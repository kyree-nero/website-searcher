

The results use the sample input.  Each attempt uses 20 threads as suggested.

Here are the results using the default term ('the') 

x@xu:~/test$ java -jar website-searcher-0.0.1-SNAPSHOT.jar 
...
2018-03-25 23:40:14.145  INFO 27850 --- [           main] o.k.wss.services.WebSiteSearcherService  : processed :500/500 has-term:  391  not readable: 8  timed out: 1  unknown host: 12


*  Any tests based on latency are most likely subject to environmental changes.
*  There are a couple different scenarios this attempts to catch and report including unknown hosts, ssl handshake errors, connection refusal and timeouts.

You can find the integration tests here (WebsiteSearcherIT).  The integration tests do real calls and they are also subject to the environment they are run in.

