import processing.net.*;
Server myServer;
Client c;
int port = 80;
String val;
int data[];
//String text1 = " ";
int x, y;
void setup() {
  size(400, 400);
  smooth();
  myServer = new Server(this, port);
}

void draw() {
  if (mousePressed == true) {
    noStroke();
    fill(0);
    ellipse(mouseX, mouseY, 5, 5);
    myServer.write(mouseX + " " + mouseY + "\n");
  }
  c = myServer.available();
  if (c != null) {
    val = c.readString();
    val = val.substring(0, val.indexOf("\n"));
    data = int(split(val, ' '));
    noStroke();
    fill(255);
    ellipse(data[0], data[1], 5, 5);
  }
}
/*
void keyPressed() {
 if (key == BACKSPACE) {
 if (text1.length() > 0) {
 text1 = text1.substring(0, text1.length() - 1);
 }
 }
 else {
 text1 += key;
 }
 }
 */
