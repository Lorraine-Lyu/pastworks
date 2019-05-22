const MongoClient = require('mongodb').MongoClient;
const generate = require('./generateHTML.js');
const suggestions = require('./suggestions.js');

const url = 'mongodb://localhost:27017/majorAdvisor';

client = new MongoClient(url,{ useNewUrlParser: true });
exports.initConnection = function() {
    client = new MongoClient(url,{ useNewUrlParser: true });
}

exports.disconnect = function() {
    client.close();
}

function sendResponse(response,res) {
    // console.log(response);
    res.writeHead(200,'application/json');
    res.end(JSON.stringify(response));
}

function sendMessage(code,message,responseObj) {
    console.log(message);
    responseObj.writeHead(code,'txt/html');
    responseObj.end(message);
}

exports.searchPerson = function (identity,input,keyword,httpRes) {
    client.connect(function(err) {
        if (err) {
            console.log("add connect error");
            sendMessage(500,"Database error. Can't connect to database");
            return;
        }
        //retireve the database of the MongoClient
        db = client.db();
        db.collection("students", function(err,students) {
            if (err) {
                console.log("add collection error ");
                sendMessage(500,"Database error. Can't retrieve database collection ",httpRes);
                return;
            }
            //convert the values in the input object into regex format
            if (identity === "student") {
                q = {"name" : new RegExp(input)}
            } else if (identity === "advisor") {
                console.log(input);
                q = {"advisor" : new RegExp(input)}
            }
            //find all objects in the collection which matches the input and convert them into an array
            students.find(q).toArray(function(err,docs) {
                if (err) {
                        console.log("add find error");
                        sendMessage(500,"Database error. Can't retrieve documents",httpRes);
                        return;
                    }
                    if (docs.length == 0)
                        sendMessage(500,"No results found",httpRes);
                    // If there's one or more than one result found, call sendResponse function to send the array back
                    else
                        if (identity === "student") {
                            // call the suggestedCourses function in the suggestions mudule to add the course suggestions to the student object
                            generate.generateStudentPage(suggestions.suggestedCourses(docs[0]),httpRes);
                        } else {
                            if (keyword === "none") {
                                generate.generateProfessorPage({name:input, students: docs},httpRes);
                            } else if (keyword === "c") {
                                docs.sort((a,b) => (a.year > b.year) ? 1 : ((b.year > a.year) ? -1 : 0));
                                sendResponse(docs, httpRes);
                            } else if (keyword === "f") {
                                sendResponse(docs.filter(function(student){
                                    return suggestions.isOnFire(student);
                                }), httpRes);
                            } else if (keyword === "h") {
                                sendResponse(docs.filter(function(student){
                                    return student.onhold;
                                }), httpRes);
                            }
                        }
            }); //toArray
    }); //collection
}); //connect
} //search
