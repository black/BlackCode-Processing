/**
* Play back a sound file loaded into memory as an audio clip.
* This more efficient than re-reading the file each time using playAudioFile().
* Schedule the playback using a SoundCiper Score.
*
* SoundCipher library example by Andrew R. Brown
*/

import arb.soundcipher.*;

SoundCipher sc = new SoundCipher(this);
SCScore score;
AudioClip ac;

void setup() {
  score = new SCScore();
  ac = sc.loadAudioClip("plink.aif");
  score.addCallbackListener(this);  
  score.tempo(100);

  for(int i=0; i<4; i++) {
    score.addCallback(i / 4.0, 3);
  }
  score.play();
}

// Parse the callback message triggered during score playback.
public void handleCallbacks(int callbackID) {
  if(callbackID == 3) {
    sc.playAudioClip(ac);
  }
}
