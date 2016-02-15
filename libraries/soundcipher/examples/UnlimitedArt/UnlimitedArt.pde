/**
* Notes and drawing are synchronised by the Draw framerate.
* Three SoundChiper instances are used to enable independent parts.
* Constrained randomness maintains a balance of order and variety.
* The music will continue as long as the drawing does.
*
* SoundCipher library example by Andrew R. Brown
*/

import arb.soundcipher.*;

SoundCipher sc = new SoundCipher(this);
SoundCipher sc2 = new SoundCipher(this);
SoundCipher sc3 = new SoundCipher(this);
float[] pitchSet = {57, 60, 60, 60, 62, 64, 67, 67, 69, 72, 72, 72, 74, 76, 79};
float setSize = pitchSet.length;
float keyRoot = 0;
float density = 0.8;


void setup() {
  frameRate(8);
  sc3.instrument(49);
}

void draw() {
  if (random(1) < density) {
    sc.playNote(pitchSet[(int)random(setSize)]+keyRoot, random(90)+30, random(20)/10 + 0.2);
    fill(color(random(256), random (256), random(256)));
    rect(random(100), random(100), random(40), random(40));
  }                                                                                                                                                      
  if (frameCount%32 == 0) {
    keyRoot = (random(4)-2)*2;
    density = random(7) / 10 + 0.3;
    sc2.playNote(36+keyRoot, random(40) + 70, 8.0);
  }
  if (frameCount%16 == 0) {
    float[] pitches = {pitchSet[(int)random(setSize)]+keyRoot-12, pitchSet[(int)random(setSize)]+keyRoot-12};
    sc3.playChord(pitches, random(50)+30, 4.0);
   }
}
