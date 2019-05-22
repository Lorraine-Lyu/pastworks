var advisor = require("./suggestions.js");

var student1 = {name:"Abigail Mathis", year:2022, id:"996844789", onhold:false, classes:["MTH 111 Calculus 1", "CSC 102 How the Internet Works", "CSC 105 Interactive Web Documents"], advisor:"Jordan Crouser"};
var student2 = {name:"Maria Nolan", year:2021, id:"990609692", onhold:false, classes:["MTH 111 Calculus 1", "MTH 153 Discrete Math", "CSC 212 Programming with Data Structures", "CSC 252 Algorithms", "CSC 290 Artificial Intelligence"], advisor:"Dominique F. Thiébaut"};
var student3 = {name:"Rachel Dickens", year:2021, id:"997227020", onhold:false, classes:["MTH 111 Calculus 1", "CSC 111 Intro Computer Science through Programming"], advisor:"Jordan Crouser"};
console.log(advisor.suggestedCourses(student1));
