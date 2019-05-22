/* 
Module with set-theoretic array methods
*/

// returns elements of array a that are NOT in array b                                                                                          
exports.diff = function(a, b) {
    arr = [];
    for (item of a) {
        if (!b.includes(item)) {
            arr.push(item);
        }
    }
    return arr;
}

// function to return difference of two arrays                                                                                                  
exports.symDiff = function(a, b) {
    return diff(a, b).concat(diff(b, a));
}

// function to return intersection of two arrays                                                                                                
// taken from: https://stackoverflow.com/questions/16227197/compute-intersection-of-two-arrays-in-javascript                                    
exports.intersect = function(a, b) {
    var t;
    if (b.length > a.length) t = b, b = a, a = t; // indexOf to loop over shorter                                                               
    return a.filter(function (e) {
            return b.indexOf(e) > -1;
        });
}