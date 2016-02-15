/**
* This example shows how the tempo of a score can be varied
* dynamically during playback. Callbacks are used to trigger
* tempo changes.
*
* A SoundCipher example by Andrew R. Brown
*/

import arb.soundcipher.*;

SCScore score = new SCScore();

void setup() {
  score.tempo(120);
  score.addCallbackListener(this);
  for (int i=0; i<24; i++) {
    score.addNote(i, 60, 100, 0.5);
    score.addCallback(i, 0);
  }
  score.play();
}

void handleCallbacks(int id) {
  //score.tempo(score.tempo + 8); // accellerate
  score.tempo(random(150) + 40); // randomise
  //score.tempo(score.tempo + random(30) - 15); // random walk
  println("tempo = " + score.tempo);
}
