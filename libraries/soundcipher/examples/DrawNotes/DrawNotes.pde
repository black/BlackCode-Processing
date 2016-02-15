/**
* Draw a piano roll score of the generated notes as they play.
* Note are all of the same dynamic and duration. Pitch is selected
* using the pitch class (pc) utilites and constants from SoundCipher.
* See the DynamicUpdating example for a more complex version of this
* example that uses SoundCipher's scheduler rather than Processing's
* draw() loop to detemine tempo.
*
* A SoundCipher example by Andrew R. Brown
*/

import arb.soundcipher.*;

SoundCipher sc = new SoundCipher(this);

float yLoc = 0.0;
float pitch = 60;

void setup() {
  frameRate(6);
}

void draw() {
  if (yLoc * 10 > width) {
    background(204);
    yLoc = 0;
  }
  line(yLoc * 10 , height-pitch, yLoc * 10 + 10, height-pitch);
  sc.playNote(pitch, 100, 1);
  yLoc += 1;
  pitch = sc.pcRandomWalk(pitch, 7, sc.MINOR);
  //pitch = sc.pcRandom(48, 72, sc.MINOR);
}


