Serving json in a fat jar under 4mb (this includes sl4j, undertow webserver and gson)

Background
Starting a startup couldn't find an existing library I liked.
 
Spring is at least 16mb and I couldn't easily see what it was including. and it uses jackson, I prefer gson.
 
Jersey, is 20+ jars, 5mb+. 
 
Resteasy, ~45mb.
 
Then I realized I only needed 5 annotations and constructor injection - so here we are.

A configured project serving json (no xml) and static resources.


 
 
 