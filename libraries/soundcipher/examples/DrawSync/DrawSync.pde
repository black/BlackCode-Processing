/**
* Notes are played at the framerate. Each time the draw() method
* is called a new line is drawn and a note is played.
* Note pitch, instrument, dynamic, and pan position are randomised.
* The mouse controls pitch on the virticle axis and pan position
* on the horizontal.
*
* SoundCipher library example by Andrew R. Brown
*/

import arb.soundcipher.*;

SoundCipher sc = new SoundCipher(this);

void setup() {
  frameRate(8);
}

void draw() {
  background(125);
  line(random(width), random(height), mouseX, mouseY);
  sc.instrument(random(80));
  sc.pan(mouseX);
  sc.playNote(random(40) + 60 - mouseY/2, random(50) + 70, 0.2);
}
