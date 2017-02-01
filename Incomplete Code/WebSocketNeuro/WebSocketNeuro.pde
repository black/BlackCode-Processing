import websockets.*;

WebsocketClient wsc;
int now;
boolean newEllipse;

void setup() {
  size(200, 200);

  newEllipse=true;

  //Here I initiate the websocket connection by connecting to "ws://localhost:8025/john", which is the uri of the server.
  //this refers to the Processing sketch it self (you should always write "this").
  wsc= new WebsocketClient(this, "ws://localhost:8025/john");
  now=millis();
}

void draw() {
  //Here I draw a new ellipse if newEllipse is true
  if (newEllipse) {
    ellipse(random(width), random(height), 10, 10);
    newEllipse=false;
  }

  //Every 5 seconds I send a message to the server through the sendMessage method
  if (millis()>now+5000) {
    wsc.sendMessage("Client message");
    now=millis();
  }
}

//This is an event like onMouseClicked. If you chose to use it, it will be executed whenever the server sends a message 
void webSocketEvent(String msg) {
  println(msg);
  newEllipse=true;
}

