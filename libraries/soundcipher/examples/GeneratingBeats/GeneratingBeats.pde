/**
* A drum pattern generator that creates a 4 beat pattern,
* playtes it then generates another 4 beat pattern, and so on.
* SoundCipher's callback facility is used to provide the loop
* regeneration notification at the end of the pattern.
* Simple drawing in time with the music is triggered by callbacks also.
*
* A SoundCipher example by Andrew R. Brown
*/

import arb.soundcipher.*;

SCScore score = new SCScore();
float[] r = new float[4];

void setup() {  
  noLoop();
  score.tempo(112);
  score.addCallbackListener(this);
  makeMusic();
}

void makeMusic() {
  score.empty();
  for (float i=0; i<16; i++) {
    if (i%8 == 0 || i%16 == 14) {
      score.addNote(i/4, 9, 0, 36, 100, 0.25, 0.8, 64);
      score.addCallback(i/4, 1);
    } else if (random(10) < 1) score.addNote(i/4, 9, 0, 36, 70, 0.25, 0.8, 64);
    if (i%8 == 4) {
      score.addNote(i/4, 9, 0, 38, 100, 0.25, 0.8, 64);
      score.addCallback(i/4, 2);
    } else if (random(10) < 2) score.addNote(i/4, 9, 0, 38, 60, 0.25, 0.8, 64);
    if(random(10) < 8) {
      score.addNote(i/4, 9, 0, 42, random(40) + 70, 0.25, 0.8, 64);
    } else score.addNote(i/4, 9, 0, 46, 80, 0.25, 0.8, 64);
  }
  score.addCallback(4, 0);
  score.play();
}

void handleCallbacks(int callbackID) {
  switch (callbackID) {
    case 0:
      score.stop();
      makeMusic();
      break;
    case 1:
      float w = random(20);
      r = new float[] {50-w, 50-w, w*2, w*2};
      redraw();
      break;
    case 2:
      r = new float[] {20, 20, 60, 60};
      redraw();
      break;
  }
}
  
void draw() {
  background(120);
  rect(r[0], r[1], r[2], r[3]);
}

void stop() {
  score.stop();
}
