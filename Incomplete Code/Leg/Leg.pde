int shoulderX = 190;  // pos (fix) 
int shoulderY = 111;
int lenUpperArm = 50;   // length
int angleShoulder = 90+0;    // angle 

// the lower arm -----

float  elbowX;  // pos 
float  elbowY;

int lenLowerArm = 34;   // length
int angleElbow = 97;   // angle  

// the hand
float handX;  // pos 
float handY;

void setup()
{
  size(500, 500);
} 

void draw() 
{ 
  background(255);
  fill(0);
  text("S", shoulderX, shoulderY);
  elbowX = lenUpperArm * cos(radians(angleShoulder)) + shoulderX;
  elbowY = lenUpperArm * sin(radians(angleShoulder)) + shoulderY;
  text("E", elbowX, elbowY);
  stroke(255, 0, 0);
  line (shoulderX, shoulderY, elbowX, elbowY);

  handX = lenLowerArm * cos(radians(angleElbow + angleShoulder)) + elbowX;
  handY = lenLowerArm * sin(radians(angleElbow + angleShoulder)) + elbowY;
  text("H", handX, handY);
  line (elbowX, elbowY, handX, handY);

} 

void keyPressed() {

  if (key == CODED) {
    if (keyCode == UP) {
      angleShoulder++;
    } 
    else if (keyCode == DOWN) {
      angleShoulder--;
    }

    if (keyCode == LEFT) {
      angleElbow++;
    } 
    else if (keyCode == RIGHT) {
      angleElbow--;
    }
    else {
    } // else
  } //  if (key == CODED) {
} // func

