import processing.net.*; 
Client myClient;
String val;
int data[];

void setup() {
  size(400, 400);
  smooth();
  myClient = new Client(this, "10.14.11.19", 80);
}

void draw() {
  
  if (mousePressed == true) {
    noStroke();
    fill(0);
    ellipse(mouseX, mouseY, 5, 5);
    myClient.write(mouseX + " " + mouseY + "\n");
  }

  if (myClient.available() > 0) {
    val = myClient.readString();
    val = val.substring(0, val.indexOf("\n"));
    data = int(split(val, ' '));
    noStroke();
    fill(255);
    ellipse(data[0], data[1], 5, 5);
  }
}

