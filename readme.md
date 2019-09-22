**Serving json in a fat jar under 500kb**

Open source framework for building json serving web api's, all under 1mb (yes less than one megabyte include logging)    

Quick start : [See sample project](https://github.com/NimChimpsky/metrix-sample)

Import the metrix jar, create an Index.hmtl, define your controllers, build an instance of MetrixServer class, run ! 

Supports a static js frontend that accesses a json api.  The api is defined using _@Get_,_@Post_,_@Put_ _@Delete_ and _@Controller_ annotations, like spring and jax but without the baggage.

***

**Background**

Building an app that requires a rest-api and web front end and I could not find an existing library that I liked.
 
Spring is at least 16mb, afaik.

Jersey, is 20+ jars, 5mb+. 
 
Resteasy, ~45mb (omg).
 
I only need 5 annotations and constructor injection - so here we are : 

- Specify url endpoints easily with conventional annotations(controller, get, post etc)
- Constructor injection based on type. 
- Configured fat jar and embedded webserver.
- Logging and json serialization. 
- **Nothing else what-so-ever**.

Your fat jars, that you can run as a service will be less than 1mb, and breath out
`https://twitter.com/@Stephen_Batty`


 
 
 