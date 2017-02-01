import websockets.*;

WebsocketClient wsc;
float x, y;
void setup() {
  size(200, 200);  
  wsc= new WebsocketClient(this, "ws://127.0.0.1:8025/any");
  
}

void draw() {
  background(-1);
  fill(0, 255, 255);
  ellipse(mouseX, mouseY, 10, 10);
  fill(0, 255, 0);
  ellipse(x, y, 10, 10);
  wsc.sendMessage(mouseX+"\n"+mouseY);
}

void webSocketEvent(String msg) {
  String[] q = splitTokens(msg, "\n");
  printArray(q);
  x=float(q[0]);
  y=float(q[1]);
}