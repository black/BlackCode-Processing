/* 
How much vitamin D daily dosage is required for human ? 
 
Health Risk: Cognitive impairment, Type 1 Diabetes, Fractures, 
Colon Cancer, Breast Cancer, Ovarian Cancer, Men Kidney Cancer, Endometrial Cancer 
Dementia [Alzheimer’s ], Asthma, Schizophernia, Heart Diesase, 

Required Dosage = 1000 IU/day [IU = International Unit] 
35 nano gram 
*/
import processing.serial.*;
Serial myPort;   
int val;       

void setup() {
  size(400, 200);
  for (int i=0; i<Serial.list ().length; i++) {
    if (Serial.list()[i]!=null) {
      String portName = Serial.list()[i];
      myPort = new Serial(this, portName, 9600);
    }
  } 
  println(Serial.list());
}

void draw() {
  background(-1); 
  if ( myPort.available()!=0) {   
    val = myPort.read();
  }
  fill(0);
  text(val, width>>1, height>>1);
}

