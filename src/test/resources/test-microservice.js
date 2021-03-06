var http = require('http'),
    url = require('url');

var attempts = 0;

var server = http.createServer(function(req, res) {

    if(req.method == 'POST'){

        console.log("POST to " + url.parse(req.url).pathname);

        console.log("Headers: " + JSON.stringify(req.headers));

        if (url.parse(req.url).pathname == '/generate-renewal-success') {

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


                callback(request.SignalInstanceInfo.ContainerId,request.SignalInstanceInfo.ProcessInstanceId,request.SignalInstanceInfo.SignalName,JSON.stringify(response));
            });

            res.writeHead(200, {'Content-Type': 'text/html'});

            res.end('post received');
        }

        else if (url.parse(req.url).pathname == '/generate-renewal-error') {

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
                Errors.push(
                    {
                        ErrorID : "ERROR12345",
                        Severity: "Critical",
                        Description: "This is an error!"
                    }
                );

                response.WorkerCallState.Errors = Errors;

                callback(request.SignalInstanceInfo.ContainerId,request.SignalInstanceInfo.ProcessInstanceId,request.SignalInstanceInfo.SignalName,JSON.stringify(response));
            });

            res.writeHead(200, {'Content-Type': 'text/html'});

            res.end('post received');
        }
        else if (url.parse(req.url).pathname.startsWith("/signal")) {
            console.log("callback received at path " + url.parse(req.url).pathname);

            var body = ""

            req.on('data', function (data) {
                body+=data;
            });

            req.on('end', function () {
                console.log("Response: " + body);
            });
        }

        else {
            res.writeHead(404, {'Content-Type': 'text/plain'});
            res.write("Looked everywhere, but couldn't find that page at all!\n");
            res.end();
        }
    }

});

function callback(containerId,processInstanceId,signalName,payload){
    var postOptions = {
      host: "localhost",
      port: "3000",
      path: "/signal/"+containerId+"/"+processInstanceId+"/"+signalName,
      method: 'POST',
      headers: {
          'Content-Type': 'application/json',
      }
    };

    var req = http.request(postOptions, function(response){
        console.log(response.statusCode)
    });

    req.write(payload);
    req.end();
}

server.listen(3000);
console.log("Test server listening on port 3000")