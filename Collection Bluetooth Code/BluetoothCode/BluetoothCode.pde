import processing.serial.*;
import peasy.*;
boolean Xrot = false, Yrot = false, Zrot = false;
PeasyCam cam;
Serial Bluetooth; 
float x, y, z;
void setup() 
{
  size(400, 400, P3D);
  cam = new PeasyCam(this, 100);
  cam.setMinimumDistance(0);
  cam.setMaximumDistance(1000);
  println(Serial.list());
  Bluetooth = new Serial(this, Serial.list()[1], 9600);
  x = 0 ;
  y = 0;
  z = 0;
}
void draw() {
  background(0);
  lights();
  axis();
  fill(#FCB603, 50);
  rect(-width/2, -height/2, 400, 400);
  if ( Bluetooth.available() > 0) {  
    String inBuffer = Bluetooth.readString();
    println(inBuffer);
    String str1 = inBuffer; 
    str1 = str1.replaceFirst("\\>", "");  
    if (str1 != null) {
      String[] str = splitTokens(str1, ",");
      //println(str.length);
      x = Float.parseFloat(str[2]); 
      y = Float.parseFloat(str[3]);
      z = Float.parseFloat(str[4]);
      println("X: " + x + "\nY: " +y + "\nZ: " + z);
    }
  }
  // translate(x, y, z);
  pushMatrix();
  if (Xrot)rotateX(radians(x));
  if (Yrot)rotateY(radians(y));
  if (Zrot)rotateZ(radians(z));
  axis();
  noStroke();
  fill(-1);
  box(100, 200, 20);
  popMatrix();
}
void keyPressed() {
  if (key == 'x' ) Xrot =!Xrot;
  if (key == 'y' ) Yrot =!Yrot;
  if (key == 'z' ) Zrot =!Zrot;
}

void axis() {
  strokeWeight(2);
  stroke(255, 0, 0);
  text("-X", -100, 0);
  text("+X", 100, 0);
  line(-100, 0, 100, 0);
  stroke(0, 255, 0);
  text("-Y", 0, -100);
  text("+Y", 0, 100);
  line(0, -200, 0, 200);
  stroke(0, 0, 255);
  text("-Z", 0, 0, -100);
  text("+Z", 0, 0, +100);
  line(0, 0, -100, 0, 0, +100);
}

