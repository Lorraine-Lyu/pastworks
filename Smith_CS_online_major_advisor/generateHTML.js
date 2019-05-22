var swig = require('swig');


function generateStudentPage(studentInfo, res) {
    swig.renderFile('./student.html', studentInfo, function(err, output){
        if (err) {
            res.writeHead(404, {'Content-Type':'text/html'});
            throw err;
            res.end();
        } else {
            res.writeHead(200, {'Content-Type':'text/html'});
            res.write(output, function(err) {
                console.log(err);
                // throw err;
                res.end();
            });
        }
    })
}

function generateProfessorPage(obj, res) {
  swig.renderFile('./prof.html', obj, function(err, output){
      if (err) {
          res.writeHead(404, {'Content-Type':'text/html'});
          throw err;
          // res.end();
      } else {
          // console.log(output);
          res.writeHead(200, {'Content-Type':'text/html'});
          res.write(output, function(err) {
              console.log(err);
              // throw err;
              res.end();
          });
          // res.end();
      }
  })
}

exports.generateProfessorPage = generateProfessorPage;
exports.generateStudentPage = generateStudentPage;
