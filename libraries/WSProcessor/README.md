WSProcessor
===========

The following describes the general structure of this Processing library and what the files are for.

Created by Victor Cheung some time in May 2014.

The 3 main files are 

1. WSProcessor.java
2. WSBoostrap.html
3. SingleClientSample.pde

### WSProcessor.java (source) ###

The class that is used by the Processing application acting as a server. Does all the connection, hand-shaking to get the connection going. Then provides a send and a receive methods to transmit Websocket packets to and from the clients. Check out the example file to see how it is used.

This is the source code file and is for reference only. The .jar is what you will actually be using.

### WSBootstrap.html ###

The html page visited by the client. Periodically sends sensor data to the server (does not have to be the same place hosting the html), whose IP address and port has to be known.

### SingleClientSample.pde ###

The example Processing sketch that acts as the Websocket server. Read the comments there for details.

## Instructions ##

The example here illustrates how the library works together with a few other files.

Since it is a Processing library, the first thing you'll need to do is to install Processing (http://processing.org/). Then you'll need to manually install this library as a Contributed Library (https://github.com/processing/processing/wiki/How-to-Install-a-Contributed-Library-%5BDRAFT%5D). Essentially it's just a matter of copying & pasting the library folder into a specific folder. Restart your Processing IDE and you are good to go. No extra libraries are required.

To set up, place the WSBootstrap.html in an http server, make this file publicly accessible.
Look for the statement "var ws = new WebSocket("ws://aaa.bbb.ccc.ddd:eeee");", 
which creates a Websocket connection between the web browser and the Processing server.
This statement should therefore contain the IP address of the server, and the port it uses for Websocket.

Start a browser window in your mobile device and start tilting it in various angles.
According to the Mozilla Developer Network device motion currently only works on 
Android, Firefox Mobile, and Safari Mobile.

The SingleClientSample sketch only supports a single client connection.
But you can extend it to multiple clients by using an ArrayList of clients, each using a WSProcessor object.

The SingleClientSample sketch also prints out what the server is receiving in plain text.
As you can see sometimes it does not print anything, probably due to Internet packet drops.
The WSProcessor mostly returns null in case an empty or an incomplete (corrupted?) packet is read.

Because of the way a Processing sketch quits, the stop() method might not get called.
In this case you might have to manually end the Java process that manages the networking,
otherwise you will not be able to run the server again as the port is being locked.
To do so in Windows, go to "Task Manager" and end the "java.exe" process.

