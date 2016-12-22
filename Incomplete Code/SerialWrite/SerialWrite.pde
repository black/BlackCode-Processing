import processing.serial.*;

Serial myPort;

void setup() {
  size(600, 300);
  printArray(Serial.list());
  myPort = new Serial(this, Serial.list()[0], 9600);
}

void draw() {
  background(-1);
  int x = (int)map(mouseX, 0, width, 0, 100);
  myPort.write(x);
}

