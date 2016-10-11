//addUrlToClasspath('/home/udara/dev/wso2/git/repos/jaggery2/log/target/org.jaggeryjs2.log-1.0.0-SNAPSHOT.jar');

var Jaggery2Log = Java.type('org.jaggeryjs2.log.Jaggery2Log');
//var log = new Jaggery2Log('test.js');
Jaggery2Log.info("First log !!!");

/*
function addUrlToClasspath(pathName){
	var*/
/*java.net.URLClassLoader*//*
 sysloader = */
/*(java.net.URLClassLoader) *//*
 java.lang.ClassLoader.getSystemClassLoader();
  var*/
/*java.lang.Class*//*
 sysclass = java.net.URLClassLoader.class;
     var ClassArray = Java.type("java.lang.Class[]");
     var parameters = new ClassArray(1);
     parameters[0]= java.net.URL.class;
     var*/
/*java.lang.reflect.Method*//*
 method = sysclass.getDeclaredMethod("addURL", parameters);
     method.setAccessible(true);
     var ObjectArray = Java.type("java.lang.Object[]");
     var array = new ObjectArray(1);
  var*/
/*java.io.File*//*
 f = new java.io.File(pathName);
  if(f.isFile()){
	var*/
/*java.net.URL*//*
 u = f.toURL();
    array[0]=u;
    //if(u.toString().endsWith(".jar"))
      method.invoke(sysloader, array);
  }else{
  	var*/
/*File[]*//*
 listOfFiles = f.listFiles();
  	if(listOfFiles !=null)
  	for (var i = 0; i < listOfFiles.length; i++) {
      if (listOfFiles[i].isFile()) {
        var*/
/*java.net.URL*//*
 u = listOfFiles[i].toURL();
    	array[0]=u;
      	method.invoke(sysloader, array);
      }
    }
  }
}*/
