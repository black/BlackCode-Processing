import processing.serial.*;
Serial myPort;  // Create object from Serial class
String myString="";  
String arrayval="";  
String[] lable = {
  "SYNC", "SYNC", "PLENGTH", "SIGNALQ", "SIGNAL", "BATTERY LEVEL", "BATTERY LEVEL", "ATTEN", "ATTENL", "MED", "MEDL", "CHKSUM"
};
byte[] inBufferfinal = new byte[12]; 
void setup() {
  size(600, 400); 
  String[] portName = Serial.list();
  for (int i=0; i<portName.length; i++) {
    if (portName[i]!=null) {
      myPort = new Serial(this, portName[i], 9600); // baudrate: 57600, 9600, 115400
      myPort.buffer(7);
      println(portName[i]  + " " + i);
    }
  }
}

void draw() {  
  background(255);
  fill(0);
  text(myString, width/2, height/2);
}

void serialEvent(Serial myPort) {
  myString = "";
  arrayval = "";   
  while (myPort.available () > 0) {
    byte[] temp = myPort.readBytes();  
    myPort.readBytes(temp); 
    if (temp != null) {
      for (int i=0; i<temp.length; i++) {
        myString = myString + temp[i];
      }
    }
  }
}

//void visualizer(int lengthofbyte, byte[] val) {
//  noStroke();
//  fill(0);
//  for (int i=0; i<lengthofbyte; i++) {
//    int h = val[i];
//    String str = lable[i];
//    rect(230, i*20+20, h, 20);
//    text(str, 50, i*20+40);
//    text(h, 20, i*20+40);
//  }
//}

