/*
 module for filling elective requirements
*/

function recount(lists) {
    counts = [];
    for (cat in lists) {
	list = lists[cat];
	if (list != null) {
	    console.log("cat is: "+cat+" length: "+list.length);
	    counts.push(list.length);
	}
    }
    return counts;
}

exports.fillReqs = function(lists, checkList) {
    counts = recount(lists);
    console.log(counts);
    while (counts.length > 0) {
	min = Math.min.apply(null, counts); // top priority ranking
	var catList;
	var cat;
	console.log("min: "+min+", cat: "+cat);
	for (i in lists) {
	    list = lists[i];
	    if ((list != null) && (list.length == min)) {
		cat = i;
		catList = list;
		console.log("catList: "+catList);
		break;
	    }
	}
	lists[cat] = null;
	if (catList.length == 0) { // haven't completed the requirement
	    checkList[cat] = "not completed";
	} else { // have competed requirement
	    course = catList[0];
	    console.log("course: "+course);
	    for (i in lists) {
		list = lists[i];
		console.log("list: "+list);
		if ((list != null) && (list.includes(course))) {
		    list.splice(list.indexOf(course), 1);
		    lists[i] = list;
		    console.log("i: "+i+", list: "+list+"\n\n");
		}
	    }
	    checkList[cat] = course;
	}
	counts = recount(lists);
    }
    return checkList;
}
