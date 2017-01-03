/**
 * A simple sample program demonstrating how the WSProcessor class is used.
 *
 * @author    Victor Cheung <victor.cheung@uwaterloo.ca>
 * @version   1.0
 * @since     2014-07-02
 */
import processing.net.*;
import net.victorcheung.WSProcessor.*;

/*****
The example here illustrates how the library works together with a few other files.

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
*****/

Server myServer;
// we are using a single client here, use an arrayList if you want multiple clients
Client myClient;
int serverPort = 1234;
boolean isConnected = false;
WSProcessor myWSProcessor;
float alpha = 0, beta = 0, gamma = 0;
PFont font;

void setup() {
  size(400, 400, P3D);
  font = createFont("Arial", 16);
  textFont(font);
  
  // Starts a Websocket server at port defined above
  myServer = new Server(this, serverPort);
}

// supposed to be a call-back function of Processing when it closes.
// not sure if it works, but if it does it stops both server and client.
@Override
void stop() {
  myServer.stop();
  myClient.stop();
  
  super.stop();
}

//check for new clients, update and draw. 
void draw() {
  
  background(0);
  fill(255);
  text("Server IP: "+Server.ip(), 10, 20);
  
  if(!isConnected) {
    //if no client is connected, wait until there is one
    myClient = myServer.available();
    
    if(myClient != null) {
      /* Here comes the golden part */
      //1. create the WProcessor object, need to pass the client so it knows whom to talk with
      myWSProcessor = new WSProcessor(myClient, true);
      //2. call the connect method, which handles all the handshaking and figuring out the masking key
      myWSProcessor.connect();
      //3. call the sendMessage to send something to the client, can call mulitple times
      myWSProcessor.sendMessage("You are connected");
      /* That's it... for the starting and sending */
      
      isConnected = true;
      println("isConnected set to true");
    } else {
      isConnected = false;
      println("isConnected set to false");
    }
  } else { //the case when a client is connected
    text("A client has connected", 10, 50);
    String unMaskedString = myWSProcessor.getMessageAsString();
    String[] data = split(unMaskedString, ':');
    if(unMaskedString!=null && data.length>=12) {
      //check the html file to see why we take those array elements
      alpha = Float.parseFloat(data[7]);
      beta = Float.parseFloat(data[9]);
      gamma = Float.parseFloat(data[11]);
      //println("received from client:"+alpha+" "+beta+" "+gamma);
    }
    text("Latest data received from client:"+alpha+" "+beta+" "+gamma, 10, 70);
    pushMatrix();
    translate(width/2, height/2);
    rotateZ(radians(beta));
    rotateX(radians(gamma+90));
    fill(100);
    stroke(255);
    box(100, 50, 10);
    popMatrix();    
    
  }
  
  //check if the client is active (i.e. still connected)
  if(myClient!=null && !myClient.active()) {
    myWSProcessor.stop();
    isConnected = false;
  }
 
}

