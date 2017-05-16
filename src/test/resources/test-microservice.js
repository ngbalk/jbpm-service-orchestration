var http = require('http'),
    url = require('url');

var server = http.createServer(function(req, res) {


    if(req.method == 'POST'){
        console.log("POST");

        if (url.parse(req.url).pathname == '/hello-microservice') {
            var body = "";
            req.on('data', function (data) {
                body += data;
            });
            req.on('end', function () {
                console.log("Body: " + body);
            });
            res.writeHead(200, {'Content-Type': 'text/html'});
            res.end('post received');
        }

        else {
            res.writeHead(404, {'Content-Type': 'text/plain'});
            res.write("Looked everywhere, but couldn't find that page at all!\n");
            res.end();
        }
    }

});

server.listen(8080);