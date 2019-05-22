var userIdentity;

/* When the user clicks on the button,
toggle between hiding and showing the dropdown content */
function dropDown() {
    document.getElementById("myDropdown").classList.toggle("show");
}

// Close the dropdown if the user clicks outside of it
window.onclick = function(event) {
  if (!event.target.matches('.dropbtn')) {

    var dropdowns = document.getElementsByClassName("dropdown-content");
    var i;
    for (i = 0; i < dropdowns.length; i++) {
      var openDropdown = dropdowns[i];
      if (openDropdown.classList.contains('show')) {
        openDropdown.classList.remove('show');
      }
    }
  }
}

function studentOrAdvisorLogin(identity) {
    userIdentity = identity;
    document.getElementById("wrapper").removeChild(document.getElementById("DropDown"));
    document.getElementById("wrapper").removeChild(document.getElementById("hint"));
    document.getElementById("header").innerHTML = "Please type in your userID below";
    document.getElementById("wrapper").innerHTML +=
    `<input type="text" id="USERname" name="name" placeholder="Your userID ..">
    <input type="submit" id = "submit" value="Submit" onclick="init()" href="#">
    <p id = "result"></p>`
}


function init() {
    var name = document.getElementById("USERname").value;
    name = name.replace(' ', "%");
    window.location.pathname=userIdentity+":"+name+".html";
}
