var http = require('http'),
    url = require('url');

var attempts = 0;

var server = http.createServer(function(req, res) {

    attempts++;

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

        else if (url.parse(req.url).pathname == '/timeout') {
            setTimeout((function() {
              res.writeHead(200, {'Content-Type': 'text/plain'});
              res.end("Hello I am awake");
            }), 5000);
        }

        else if(url.parse(req.url).pathname == '/try-again' && attempts>2){
            attempts = 0;
            res.writeHead(200, {'Content-Type': 'text/plain'});
            res.end("3rd try's the charm");
            console.log("3rd try's the charm");
        }

        else {
            res.writeHead(404, {'Content-Type': 'text/plain'});
            res.write("Looked everywhere, but couldn't find that page at all!\n");
            res.end();
        }
    }

});

server.listen(3000);
console.log("Test server listening on port 3000")