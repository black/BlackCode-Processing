/**
* Demonstrates how the callback timing can be varied at runtime. In this example the
* score callback is varied at random between 0.5 and 1.0 beats in length. On each callback
* a note is played and piano-roll score display updated. Pitch is selected
* using the pitch class (pc) utilites and constants from SoundCipher.
* See the DrawNotes example for a simpler version of this
* example that uses Processing's draw() loop to detemine tempo.
*
* A soundCipher example by Andrew R. Brown
*/

import arb.soundcipher.*;

SoundCipher sc = new SoundCipher(this);
SCScore score = new SCScore();
float beatCount = 0;
float pitch = 60;
float duration = 0.5;
float dynamic = 80;
float tempo = 140;
float beatSize = 20;

void setup() {
  noLoop();
  background(250);
  sc.instrument(sc.GUITAR);
  score.tempo(tempo);
  score.addCallbackListener(this);
  score.addCallback(0.5, 1);
  score.play(-1);
  redraw();
}

void draw() {
  if (beatCount * beatSize > width) {
    background(250);
    beatCount = 0;
  }
  stroke(255 - dynamic*2);
  line(beatCount * beatSize , height - pitch + 10, 
    beatCount * beatSize + duration * beatSize, height - pitch + 10);
  sc.playNote(pitch, dynamic, duration);
}

void handleCallbacks(int callbackID) {
  redraw();
  // compute new values
  beatCount += duration;
  if (beatCount%4 == 0 || random(10) < 2) {
    duration = 1.0;
    pitch = sc.pcRandomWalk(pitch, 5, sc.MINOR_TRIAD);
  } else  {
    duration = 0.5;
    pitch = sc.pcGaussianWalk(pitch, 3, sc.MINOR);
  }
  dynamic = cos(PI * 1 * beatCount) * 30 + 90;
  // refresh score with new values
  score.empty();
  score.addCallback(duration, 1);
}


