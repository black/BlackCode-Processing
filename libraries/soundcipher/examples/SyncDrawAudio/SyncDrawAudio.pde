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
AudioClip ac;

void setup() {
  noLoop();
  score = new SCScore();
  ac = sc.loadAudioClip("droplet.aif");
  score.addCallbackListener(this);  
  score.tempo(180);
  
  for(int i=0; i<17; i++) {
    score.addCallback(i / (int)(random(2) + 1), 3);
  }
  score.play();
}

void draw() {
    rect(-1, -1, 101 , 101);
    float y = random(100);
    line(0, y, 100, y);
}

public void handleCallbacks(int callbackID) {
  if(callbackID == 3) {
    redraw();
    sc.playAudioClip(ac);
  }
}
