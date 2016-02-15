/**
* Drawing in time with the SoundCipher scheduler.
* The noLoop() command halts Processing's draw thread, and redraw()
* is called by the SoundCipher event handler to control
* drawing and make sure it is in time with the sound.
*
* A SoundCipher library example by Andrew R. Brown
*/

import arb.soundcipher.*;

SoundCipher sc = new SoundCipher(this);
SCScore score;
float y;

void setup() {
  noLoop();
  score = new SCScore();
  score.addCallbackListener(this);  
  score.tempo(180);
  sc.instrument(1);
  
  for(int i=0; i<17; i++) {
    score.addCallback(i / (int)(random(2) + 1), 3);
  }
  score.play();
}

void draw() {
    rect(-1, -1, 101 , 101);
    line(0, y, 100, y);
    sc.playNote(120 - y, 100, 0.5);
}

void handleCallbacks(int callbackID) {
  if(callbackID == 3) {
    y = random(100);
   redraw();
  }
}
