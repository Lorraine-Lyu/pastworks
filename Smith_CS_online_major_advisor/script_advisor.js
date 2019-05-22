//While user click on one button with student name on it, direct them to the user profile page
    function getStudentPage(name) {
        var re = /(\w+\s\w+)/;
        name = name.match(re)[0];
        name = name.replace(" ", "%");
        window.location.pathname="student"+":"+name+".html";
    }
//send ajax request to the server to query specific group of students
    function filter(key) {
        var userID = document.getElementById("advisorName").innerHTML;
        var searchXmlhttp = new XMLHttpRequest();
        searchXmlhttp.onload = filterAJAXHandler;
        msg = "?request=filter&identity=" + "advisor" +"&userID=" + userID + "&key=" + key;
        searchXmlhttp.open("GET", "http://localhost:8080/" + msg, true);
        searchXmlhttp.send();
    }

    function filterAJAXHandler() {
        if (this.status == 200) {
            var lst = JSON.parse(this.response);
            var div = document.getElementById("allStudents");
            while (div.firstChild) {
                div.removeChild(div.firstChild);
            }
            for (student of lst) {
                div.innerHTML+= '<button class="student" onclick="getStudentPage(this.textContent)">' +
                  student.name +
                '</button>'
            }
        }
        if (this.status == 500) {
            document.getElementById("result").innerHTML=this.response;
        }
    }
