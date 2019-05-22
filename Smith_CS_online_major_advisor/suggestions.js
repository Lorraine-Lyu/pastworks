/*
Module for making suggestions to student about which courses they should take.
 */

electMod = require('./electives.js');
setMod = require('./setTheory.js');
OneA4DMod = require('./1A4D.js');

//=====================  REQUIREMENTS  ========================

non_111_intro = ["CSC 102", "CSC 103", "CSC 105", "CSC 106", "CSC 107", "CSC 109", "FYS 164"];
core = ["CSC 212", "CSC 231", "CSC 250"];
math_intro = ["MTH 111", "MTH 112", "MTH 153", "LOG 100"];

theory = ["CSC 274", "CSC 205", "CSC 240", "CSC 252", "CSC 293", "CSC 334", "CSC 390", "CSC 290"];
programming = ["CSC 274", "CSC 205", "CSC 220", "CSC 240", "CSC 293", "CSC 334", "CSC 256", "CSC 290", "CSC 353"];
systems = ["CSC 262", "CSC 270", "CSC 330", "CSC 353", "CSC 249"];

requiresCalc = ["MTH 212", "MTH 220"];
requiresDiscrete = ["MTH 211",  "MTH 238", "MTH 255", "MTH 254"];
math_200 = requiresCalc.concat(requiresDiscrete);

listOfAllCourses = non_111_intro.concat(core).concat(math_intro).concat(theory).concat(programming).concat(systems).concat(math_200);
cscOver200 = listOfAllCourses.filter(course => ( (parseInt(course.split(" ")[1]) >= 200) && (course.split(" ")[0]=="CSC") ) );
mthOver200 = listOfAllCourses.filter(course => ( (parseInt(course.split(" ")[1]) >= 200) && (course.split(" ")[0]=="MTH") ) );

cscSeminars = listOfAllCourses.filter(course => ( (parseInt(course.split(" ")[1]) >= 300) && (course.split(" ")[0]=="CSC") ) );

//====================  HELPER METHODS  ============================

function checkMth(pastCourses, checkList) {
    hasTakenCalc = pastCourses.includes("MTH 112")||pastCourses.includes("MTH 111")||pastCourses.includes("LOG 100");
    hasTakenRequiresCalc = (setMod.intersect(pastCourses, requiresCalc).length > 0);
    hasTakenDiscrete = pastCourses.includes("MTH 153");
    hasTakenRequiresDiscrete = (setMod.intersect(pastCourses, requiresDiscrete).length > 0);
    if (hasTakenCalc) {
	checkList["MTH 111"] = "MTH 111";
    } else if (hasTakenRequiresCalc) {
	checkList["MTH 111"] = "required MTH 111";
    } else {
	checkList["MTH 111"] = "not complete";
    }
    if (hasTakenDiscrete) {
        checkList["MTH 153"] = "MTH 153";
    } else if (hasTakenRequiresDiscrete) {
        checkList["MTH 153"] = "required MTH 153";
    } else {
	checkList["MTH 153"] = "not complete";
    }
    return checkList;
}

function check(pastCourses, course) {
    if (pastCourses.includes(course)) {
	return course;
    } else {
	return "not completed";
    }
}

function fillCheckList(pastCourses) {
    checkList = {theory: null, prog: null, sys: null, oneA4D: null, sem: null};

    // check core requirements
    core = ["CSC 111","CSC 212","CSC 231","CSC 250"];
    for (course of core) {
	checkList[course] = check(pastCourses, course);
    }

    // check intro math requirements
    checkList = checkMth(pastCourses, checkList);

    theoryTaken = setMod.intersect(pastCourses, theory);
    progTaken = setMod.intersect(pastCourses, programming);
    sysTaken = setMod.intersect(pastCourses, systems);
    semTaken = setMod.intersect(pastCourses, cscSeminars);

    electList = theoryTaken.concat(progTaken).concat(sysTaken).concat(semTaken);

    lists = [];
    lists["theory"] = theoryTaken;
    lists["prog"] = progTaken;
    lists["sys"] = sysTaken;
    lists["sem"] = semTaken;

    // check elective requirements
    checkList = electMod.fillReqs(lists, checkList);

    // check 1A/4D requirement
    checkList["oneA4D"] = OneA4DMod.checkSat(pastCourses, electList, checkList, non_111_intro, requiresCalc, requiresDiscrete);

    return checkList;
}

// fix name of course (for integrating with Lorraine's code)
function fixNames(pastCourses) {
    for (i=0; i < pastCourses.length; i++) {
	course = pastCourses[i];
	list = course.split(" ");
	newList = [list[0], list[1]];
	newCourse = newList.join(" ");
	pastCourses[i] = newCourse;
    }
    return pastCourses;
}

//==============================  MAIN  ======================================


exports.isOnFire = function(studentObj) {
    onFire = false;
    pastCourses = fixNames(studentObj.classes);
    checkList = fillCheckList(pastCourses);
    countIncomplete = 0;
    for (field in checkList) {
	if (checkList[field] == "not completed") {
	    countIncomplete = countIncomplete + 1;
	}
    }
    if (studentObj.year == 2020 || studentObj.year == 2019) {
	if (countIncomplete > 4) {
	    onFire = true;
	}
    }
    if (studentObj["onhold"]) {
	onFire = true;
    }
    console.log(checkList);
    return onFire;
}


exports.suggestedCourses = function(studentObj) {
    // query database to get student object
    pastCourses = fixNames(studentObj.classes);

    // remove courses already taken from suggestion list
    suggestions = setMod.diff(listOfAllCourses, pastCourses);

    hasTakenCalc = pastCourses.includes("MTH 112")||pastCourses.includes("MTH 111")||pastCourses.includes("LOG 100");
    hasTakenRequiresCalc = (setMod.intersect(pastCourses, requiresCalc).length > 0);
    hasTakenDiscrete = pastCourses.includes("MTH 153");
    hasTakenRequiresDiscrete = (setMod.intersect(pastCourses, requiresDiscrete).length > 0);

    hasTakenCSC111 = pastCourses.includes("CSC 111");
    hasTakenMth200 = (setMod.intersect(pastCourses, mthOver200).length > 0);

    // refine CSC suggestions
    if (!hasTakenCSC111) {
        suggestions = setMod.diff(suggestions, cscOver200);
    } else {
	suggestions = setMod.diff(suggestions, non_111_intro);
    }

    // refine MTH suggestions
    if (!hasTakenMth200) { // must complete BOTH mandatory math requirements before doing 200-level math
	if (!hasTakenDiscrete || !hasTakenCalc) {
	    suggestions = setMod.diff(suggestions, mthOver200);
	}
    } else { 
	if (hasTakenRequiresDiscrete) {
	    suggestions = setMod.diff(suggestions, ["MTH 153"]);    
	}
	if (hasTakenRequiresCalc) {
	    suggestions = setMod.diff(suggestions, ["MTH 111", "MTH 112", "LOG 100"]);
	}
    }

    studentObj["suggestions"] = suggestions;
    return studentObj;
};
