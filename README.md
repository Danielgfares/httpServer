# httpServer
http server 
-	Main Class: make sure the input arguments are correct and start a server on the given port.
-	ServerAppliaciton Class:  is the application that start when a client connects with the server. Start a new thread, so 
  the main thread can continue listening for new connections. Also set to the new socket created for the client a time out
  limit, so the server closes the connections with those clients who stop communicating with the server, which this way the 
  server can receive others new connections, in other words this way, the server can survive against this type of DDOS attacks.
-	ServerHttp Class: have two main behaviors which are read requests and write a response. This class makes sure that the
  received requests are HTTP GET, also, if the received request is HTTP/1.1 keep the connection with the client otherwise 
  the connection is closed after sending the response.
