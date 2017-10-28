import processing.serial.*;
Serial myPort;  
int count =0;// The serial port
PImage[] head = new PImage[101];
int i;
void setup () {
  size(800, 600);   
  String portName = Serial.list()[1];
  myPort = new Serial(this, portName, 9600);
  myPort.clear();
  myPort.bufferUntil('\n');
    smooth();
}

void draw () {
  // nothing happens in the draw loop, 
  // but it's needed to keep the program running
        for(i=0; i<=100; i++) {
      String imageName = "head_" + nf(i, 4) + ".png";
      head[i] = loadImage(imageName);
      delay(1000);
        }
}
 
void serialEvent (Serial myPort) {
  String inString = myPort.readStringUntil('\n');
  if (inString != null) {
    inString = trim(inString);
    float val = int(inString);
    i = int(val);
    println(val);


  }
}
