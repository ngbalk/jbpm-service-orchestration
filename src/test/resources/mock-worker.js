var http = require('http'),
    https = require('https'),
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

                var data = request.Data;
                response.Data = marshallerWrapper(data,request.DataType);
                response.DataType = request.DataType;
                response.SignalInstanceInfo = request.SignalInstanceInfo;
                response.Data.pId = "abcdef";
                response.Message = "SUCCESS"
                response.WorkerName = request.WorkerName;
                response.WorkerCallState = {};
                response.WorkerCallState.Completed = true;

                var payload = JSON.stringify(marshallerWrapper(response,"org.rhc.workflow.common.ServiceResponse"));

                callback(request.SignalInstanceInfo.ContainerId,request.SignalInstanceInfo.ProcessInstanceId,request.SignalInstanceInfo.SignalName,payload);
            });

            res.writeHead(200, {'Content-Type': 'text/html'});

            res.end('post received');
        }

        else if (url.parse(req.url).pathname == '/incident') {

            console.log("POST to /incident");

            var body = "";
            req.on('data', function (data) {

                body+=data;
            });

            req.on('end', function(){

                var request = JSON.parse(body);
                console.log("Request: " + JSON.stringify(request));
                var response = {};

                var data = request.Data;
                data.SupportActivityId = "def";
                
                response.Data = marshallerWrapper(data,request.DataType);
                response.DataType = request.DataType;
                response.SignalInstanceInfo = request.SignalInstanceInfo;
                response.Message = "SUCCESS"
                response.WorkerName = request.WorkerName;
                response.WorkerCallState = {};
                response.WorkerCallState.Completed = true;

                var payload = JSON.stringify(marshallerWrapper(response,"org.rhc.workflow.common.ServiceResponse"));

                callback(request.SignalInstanceInfo.ContainerId,request.SignalInstanceInfo.ProcessInstanceId,request.SignalInstanceInfo.SignalName,payload);
            });

            res.writeHead(200, {'Content-Type': 'text/html'});

            res.end('post received');
        }

        else if (url.parse(req.url).pathname == '/payment') {

            console.log("POST to /payment");

            var body = "";
            req.on('data', function (data) {

                body+=data;
            });

            req.on('end', function(){

                var request = JSON.parse(body);
                console.log("Request: " + JSON.stringify(request));
                var response = {};

                var data = request.Data;
                data.PaymentId = "abc";
                
                response.Data = marshallerWrapper(data,request.DataType);
                response.DataType = request.DataType;
                response.SignalInstanceInfo = request.SignalInstanceInfo;
                response.Message = "SUCCESS"
                response.WorkerName = request.WorkerName;
                response.WorkerCallState = {};
                response.WorkerCallState.Completed = true;

                var payload = JSON.stringify(marshallerWrapper(response,"org.rhc.workflow.common.ServiceResponse"));

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

                var data = request.Data;

                response.Data = marshallerWrapper(data,request.DataType);
                response.DataType = request.DataType;
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
            console.log("404 NOT FOUND");
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

function callbackKIEDev(containerId,processInstanceId,signalName,payload){
    var postOptions = {
      host: "kie-dev.svmintranet.com",
      port: "443",
      path: "/kie-server/services/rest/server/containers/"+containerId+"/processes/instances/"+processInstanceId+"/signal/"+signalName,
      method: 'POST',
      headers: {
          'Content-Type': 'application/json',
          "Authorization": 'eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IkF1ZHBIa0tnS2lhZ3Q2U0pxajdVcjFRX3M5USJ9.eyJhdWQiOiJodHRwOi8vMTAuNTMuNDMuODQ6ODA4MC9idXNpbmVzcy1jZW50cmFsIiwiaXNzIjoiaHR0cDovL2FkZnMuc2VydmljZW1hc3Rlci5jb20vYWRmcy9zZXJ2aWNlcy90cnVzdCIsImlhdCI6MTQ5OTk1OTExNywiZXhwIjoxNDk5OTYyNzE3LCJ3aW5hY2NvdW50bmFtZSI6Im5iYWxraXNzIiwiZ3JvdXAiOlsiRG9tYWluIFVzZXJzIiwiQXBwRHluYW1pY3NfQ3VzdG9tX0Rhc2hib2FyZF9WaWV3ZXIiLCJBcHBEeW5hbWljc19EQl9Nb25pdG9yaW5nX1VzZXIiLCJBcHBEeW5hbWljc19TZXJ2ZXJfTW9uaXRvcmluZ19Vc2VyIiwiQXBwRHluYW1pY3NfUmVhZF9Pbmx5X1VzZXIiLCJTU0RldiIsIlNTVGVzdCJdLCJhdXRoX3RpbWUiOiIyMDE3LTA3LTEzVDE0OjEyOjM0LjU4MFoiLCJhdXRobWV0aG9kIjoidXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOmFjOmNsYXNzZXM6UGFzc3dvcmRQcm90ZWN0ZWRUcmFuc3BvcnQiLCJ2ZXIiOiIxLjAiLCJhcHBpZCI6ImJ1c2luZXNzX2NlbnRyYWxfZGV2In0.RuEvq6d0I2HXU76b8C6XZYLjEKQviXYrCy6Bn4_lWjlogRFMXQDQ3GLYbafBMJwlFS3OBwbwQ4Z8BFbcNSp9JmOKlvTzVr78K_lVext3ljMwmK3jn3GBGFaKPigGS3g_lWVxJmwxpcNtTzY1HdDASVFyLiqGYqbdmdOEknchotv4s30L2wYusLYliP7SeRmLwZsSsjyVi7vO3-z8BeVcoR4aQsf83hLWsezqfpG9VYdjmGhkr15_e-B_6Ir81N7gsX0UrIzZQoPB9AUPTB40HIB89lHi923LLGWt5rStpeQa6-QoeYdhihASSXW7FPnuig3FVZkllnnw8Q3Wuo-GKw'
      }
    };

    console.log("Calling back with request: " + JSON.stringify(postOptions));

    process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";

    var req = https.request(postOptions, function(response){
        console.log(response.statusCode)
    });

    console.log("Response: "+ payload);

    req.write(payload);
    req.end();
}



function marshallerWrapper(payload, typeInfo){
    var marshalledPayload = {};
    marshalledPayload[typeInfo] = payload;
    return marshalledPayload;
}

server.listen(3000);
console.log("Test server listening on port 3000")