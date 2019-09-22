**Serving json in a fat jar under 500kb**

A fully configured project that includes sl4j, undertow, and gson.  

Quick start 
- Run the gradle fatJar task to build myFat.jar. 
- Run myFat.jar via command line, `java myFat.jar` . 
- You can then navigate to http://localhost:8080 (you can pass in port number as argument on cmd line)

A static js frontend is served that accesses a json api.  The api is defined using _@Get_,_@Post_,_@Put_ _@Delete_ and _@Controller_ annotations, like spring and jax but without the baggage.

***

**Background**

Building an app that requires a rest-api and web front end and I could not find an existing library that I liked.
 
Spring is at least 16mb, it is not simple to see what is included.

Jersey, is 20+ jars, 5mb+. 
 
Resteasy, ~45mb (omg).
 
I only need 5 annotations and constructor injection - so here we are : 

- Specify url endpoints easily with conventional annotations 
- Constructor injection based on type. 
- Configured fat jar and embedded webserver.
- Logging and json serialization. 
- **Nothing else what-so-ever**.

`https://twitter.com/@Stephen_Batty`


 
 
 