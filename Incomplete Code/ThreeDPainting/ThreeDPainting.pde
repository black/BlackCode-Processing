import processing.serial.*; 
Serial Bluetooth;  
import peasy.*;
PeasyCam cam; 
boolean Xrot = false, Yrot = false, Zrot = false;
float x=0, y=0, z=0;

ArrayList<PVector> poop = new ArrayList();
void setup() {
  size(400, 400, P3D);  
  cam = new PeasyCam(this, 100);
  cam.setMinimumDistance(0);
  cam.setMaximumDistance(1000);
  int listB = Serial.list().length;
  println(listB); 
  Bluetooth = new Serial(this, "COM5", 9600);
}
String inBuffer = " ";
void draw() {
  background(-1); 
  lights();
  textAlign(CENTER);
  fill(0);
  if ( Bluetooth.available() > 0) {  
    inBuffer = Bluetooth.readString();
    String[] list = split(inBuffer, ',');
    x = Float.parseFloat(list[2]); 
    y = Float.parseFloat(list[3]);
    z = Float.parseFloat(list[4]);
    println(list);
  }  
  pushMatrix();
  rotateX(PI+radians(x));
  rotateY(radians(y));
  rotateZ(radians(z)); 
  noStroke();
  fill(0);
  boxVetex();
  popMatrix();
} 


void boxVetex() { 
  scale(90);
  beginShape(QUADS);
  fill(255, 0, 0);
  vertex(-1, -1, 1);
  vertex( 1, -1, 1);
  vertex( 1, 1, 1);
  vertex(-1, 1, 1);
  endShape();
  // Back
  beginShape(QUADS);
  fill(255, 255, 0);
  vertex( 1, -1, -1);
  vertex(-1, -1, -1);
  vertex(-1, 1, -1);
  vertex( 1, 1, -1);
  endShape();
  // Bottom
  beginShape(QUADS);
  fill( 255, 0, 255);
  vertex(-1, 1, 1);
  vertex( 1, 1, 1);
  vertex( 1, 1, -1);
  vertex(-1, 1, -1);
  endShape();
  // Top
  beginShape(QUADS);
  fill(0, 255, 0);
  vertex(-1, -1, -1);
  vertex( 1, -1, -1);
  vertex( 1, -1, 1);
  vertex(-1, -1, 1);
  endShape();
  // Right
  beginShape(QUADS);
  fill(0, 0, 255);
  vertex( 1, -1, 1);
  vertex( 1, -1, -1);
  vertex( 1, 1, -1);
  vertex( 1, 1, 1);
  endShape();
  // Left
  beginShape(QUADS);
  fill(0, 255, 255);
  vertex(-1, -1, -1);
  vertex(-1, -1, 1);
  vertex(-1, 1, 1);
  vertex(-1, 1, -1);
  endShape();
}

