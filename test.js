/*var Log = Java.type('org.jaggeryjs2.Log');
var log = new Log("test.js");
log.info("test info log..");*/

var URIMatcher = Java.type('org.jaggeryjs2.URIMatcher');
var uriMatcher = new URIMatcher('/sample/test.jag');

if(uriMatcher.match('/{dir0}/{page}')) {
    //If pattern matches, elements can be accessed from their keys
    print("dir0 element is : " + uriMatcher.elements().dir0)
}

