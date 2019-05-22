/*
Module to check 1A/4D requirement
 */
setMod = require('./setTheory.js');

// returns boolean saying whether took 3rd (extra) math
function didMthExtra(pastCourses, requiresCalc, requiresDiscrete) {
    mthOver200 = requiresCalc.concat(requiresDiscrete);
    hasTakenRequiresCalc = (setMod.intersect(pastCourses, requiresCalc).length > 0);
    hasTakenRequiresDiscrete = (setMod.intersect(pastCourses, requiresDiscrete).length > 0);
    mthAdv = setMod.intersect(pastCourses, mthOver200);
    numAdv = mthAdv.length;
    if (hasTakenRequiresCalc) {
        numAdv = numAdv - 1;
    }
    if (hasTakenRequiresDiscrete) {
        numAdv = numAdv - 1;
    }
    return (numAdv > 0);
}

// returns boolean saying whether took non-111 into course 
function didIntroExtra(pastCourses, non_111_intro) {
    introExtra = setMod.intersect(pastCourses, non_111_intro);
    return (introExtra.length > 0);
}

// returns boolean saying whether took extra CSC-200 (or more) course
// electList is list of all CSC electives taken  
function didCscExtra(electList, checkList) {
    cats = ["theory","sys","prog","sem"];
    for (cat of cats) {
        course = checkList[cat];
        if (course != "not completed") {
            electList.splice(electList.indexOf(course), 1);
        }
    }
    return (electList.length > 0);
}

exports.checkSat = function(pastCourses, electList, checkList, non_111_intro, requiresCalc, requiresDiscrete) {
    completed = (didMthExtra(pastCourses, requiresCalc, requiresDiscrete) || didIntroExtra(pastCourses, non_111_intro) || didCscExtra(electList, checkList));
    if (didMthExtra(pastCourses, requiresCalc, requiresDiscrete)) {
	return "completed, MTH adv";
    } else if (didIntroExtra(pastCourses, non_111_intro)) {
	return "completed, CSC intro"
    } else if (didCscExtra(electList, checkList)) { 
	return "completed, CSC adv"
    } else {
	return "not completed";
    }
}