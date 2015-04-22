var fs = require('fs');
var http = require('http');
var fileserver = require('fileserver')('../ui', true, function(res404) {
	fs.readFile('404.html', function(err, data) {
		if (err) throw err;
		else {
			res404.setHeader('content-type', 'text/html');
			res404.end(data);
		}
	});
});

http.createServer(function(req, res) {
	console.log(req.url);
	fileserver(res, req.url);
}).listen(8080);

console.log('Listening on port 8080');