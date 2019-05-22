//module for serving static files
var fs = require('fs');
var mime = require('mime');

function serveFile(pathname, res) {
    if (pathname === "" || pathname === "index") {
        pathname = "index.html";
    }
    var fileExt = pathname.split('.').pop();
    // console.log(fileExt);
    var type = mime.getType(fileExt);
    // console.log(type);
    res.writeHead(200, {"Content-Type" : type});
    var fileStream = fs.createReadStream(pathname);
    fileStream.pipe(res);
    fileStream.on('end', function(){
        res.end()
        // console.log('fileStream end')
    })
    //
    fileStream.on('error', function(){
        res.writeHead(404, {"Content-Type" : "text/plain"});
        res.write('You get an error');
        res.end()
        // console.log('Error accessing the file')
    })
}
exports.serveFile = serveFile;
