var http = require('http'),
    url = require('url');

var server = http.createServer(function(req, res) {

    if(req.method == 'POST'){

        if (url.parse(req.url).pathname == '/worker-success') {

            console.log("POST to /worker-success");

            var body = "";
            req.on('data', function (data) {

                body+=data;
            });

            req.on('end', function(){

                var request = JSON.parse(body);
                console.log("Request: " + JSON.stringify(request));
                var response = {};

                response.Data = request.Data;
                response.SignalInstanceInfo = request.SignalInstanceInfo;
                response.Data.pId = "abcdef";
                response.Message = "SUCCESS"
                response.WorkerName = request.WorkerName;
                response.WorkerCallState = {};
                response.WorkerCallState.Completed = true;

                var payload = JSON.stringify(marshaller(response));

                callback(request.SignalInstanceInfo.ContainerId,request.SignalInstanceInfo.ProcessInstanceId,request.SignalInstanceInfo.SignalName,payload);
            });

            res.writeHead(200, {'Content-Type': 'text/html'});

            res.end('post received');
        }

        else if (url.parse(req.url).pathname == '/worker-error') {

            console.log("POST to /worker-error");

            var body = "";
            req.on('data', function (data) {

                body+=data;
            });

            req.on('end', function(){

                var request = JSON.parse(body);
                console.log("Request: " + JSON.stringify(request));
                var response = {};

                response.Data = request.Data;
                response.Message = "ERROR"
                response.WorkerName = request.WorkerName;
                response.WorkerCallState = {};
                response.WorkerCallState.Completed = false;
                var Errors = [];
                Errors.push({
                        ErrorID : "ERROR12345",
                        Severity: "Critical",
                        Description: "This is an error!"});
                response.WorkerCallState.Errors = Errors;

                callback(request.SignalInstanceInfo.ContainerId,request.SignalInstanceInfo.ProcessInstanceId,request.SignalInstanceInfo.SignalName,JSON.stringify(response));
            });

            res.writeHead(200, {'Content-Type': 'text/html'});

            res.end('post received');
        }
        else {
            res.writeHead(404, {'Content-Type': 'text/plain'});
            res.write("Couldn't find that worker!!\n");
            res.end();
        }
    }

});

function callback(containerId,processInstanceId,signalName,payload){
    var postOptions = {
      host: "localhost",
      port: "8081",
      path: "/kie-server/services/rest/server/containers/"+containerId+"/processes/instances/"+processInstanceId+"/signal/"+signalName,
      method: 'POST',
      headers: {
          'Content-Type': 'application/json',
          "Authorization": "Basic a2llc2VydmVyOmtpZXNlcnZlcjEh"
      }
    };

    console.log("Calling back with request: " + JSON.stringify(postOptions));

    var req = http.request(postOptions, function(response){
        console.log(response.statusCode)
    });

    req.write(payload);
    req.end();
}

function marshaller(payload){
    var marshalledPayload = {};
    marshalledPayload["org.rhc.workflow.common.ServiceResponse"] = payload;
    return marshalledPayload;
}

server.listen(3000);
console.log("Test server listening on port 3000")