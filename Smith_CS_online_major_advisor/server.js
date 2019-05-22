// Server application to search and add documents in a MongoDB library database

var http = require("http");
var url = require("url");
var mongoose = require("./createDB.js");
var fileServer = require("./fileServer.js");
var htmlGenerator = require("./generateHTML.js")

function serverCallBackFn(req, res) {
    myURL = url.parse(req.url, true);
    pathname = myURL.pathname.substring(1);
    qdata = myURL.query;
    console.log(qdata);
    if (myURL.search === "") {
        if ((pathname.startsWith("advisor")||pathname.startsWith("student"))&&pathname.endsWith(".html")) {
              identity = pathname.split(":")[0];
              name = pathname.split(":")[1].replace("%", " ");
              name = name.split(".")[0];
              console.log(name);
              mongoose.searchPerson(identity, name, "none", res);
        } else {
            if (pathname == "")
                pathname = "index.html"
            fileServer.serveFile(pathname, res);
        }
    } else {
        // mongoose.connect();
        if (qdata.request === "filter") {
            mongoose.searchPerson("advisor", qdata.userID, qdata.key, res);
        }

        // mongoose.endConnection();

    }
}

var myServer = http.createServer(serverCallBackFn);
myServer.listen("8080");
