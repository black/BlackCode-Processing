import websockets.*;

WebsocketServer ws;
float x, y;

void setup() {
  size(200, 200);
  ws= new WebsocketServer(this, 8025, "/any");
  x=0;
  y=0;
}

void draw() {
  background(0);
  fill(0, 255, 0);
  ellipse(mouseX, mouseY, 10, 10);
  fill(0, 255, 255);
  ellipse(x, y, 10, 10); 
  ws.sendMessage(mouseX+"\n"+mouseY);
}

void webSocketServerEvent(String msg) {
  String[] q = splitTokens(msg, "\n");
  printArray(q);
  x=float(q[0]);
  y=float(q[1]);
}