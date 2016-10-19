//Log test snippet
/*var Log = Java.type('org.jaggeryjs2.Log');
var log = new Log("test.js");
log.info("test info log..");*/

//URIMatcher test snippet
/*var URIMatcher = Java.type('org.jaggeryjs2.URIMatcher');
var uriMatcher = new URIMatcher('/sample/test.jag');

if(uriMatcher.match('/{dir0}/{page}')) {
    //If pattern matches, elements can be accessed from their keys
    print("dir0 element is : " + uriMatcher.elements().dir0)
}*/

//XHR test snippet
/* var XMLHttpRequest = Java.type('org.jaggeryjs2.xhr.XMLHttpRequest');
 var url = 'http://jaggeryjs.org/documentation.jag?api=Log';
 var xhr = new XMLHttpRequest();
 xhr.open("GET", url);
 xhr.setRequestHeader("user" , "udarar");
xhr.send();

 print("ReadyState : "+xhr.readyState());
 print("<br>ResponseStatus : "+xhr.status());
 print("<br>ResponseText : "+xhr.responseText());*/


 //File test snippet
/* var File = Java.type('org.jaggeryjs2.file.File');
 var path = "foo.txt";
 var file = new File(path);
 file.open("r");
 var message = "";

 message = file.readAll();
 print(message);
 file.close();*/

 var url  = "https://api.twitter.com/1/statuses/user_timeline.json";
 var data = { include_entities:true,include_rts:true,screen_name:"wso2",count:1 };
 var twitterJson = get(url, data ,"json");
 print(twitterJson);


