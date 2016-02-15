/**
* Audio synthesis from Minim is controlled by SoundCipher and
* playback is Synced with SounDCypher notes from the JavaSound synth.
* A SoundCipher score has events that trigger changes in 
* the Minim synthesis.
*
* Minim has many more audio features than SoundCipher, such as 
* recording, synthesis and more detailed audio handling. While
* SoundCipher has more detailed note-based musical features and
* stronger timing and structure elements from its SCScore class. Minim
* and SoundCipher can be combined to provide comprehensive music and 
* sound capabilities for Processing.
*
* A SoundCipher library example by Andrew R. Brown
*/

import arb.soundcipher.*;
import ddf.minim.*;
import ddf.minim.signals.*;

SoundCipher sc = new SoundCipher(this);
SCScore score;
Minim minim = new Minim(this);
AudioOutput out;
SineWave sine;
int pitch = 60;

void setup() {
  noLoop();
  score = new SCScore();
  score.addCallbackListener(this);
  for(int i=0; i<16; i+=2) {
    score.addCallback(i, 1);
    score.addCallback(i+1.5, 0);
  }

  out = minim.getLineOut(Minim.STEREO);
  sine = new SineWave(440, 0.0, out.sampleRate());
  out.addSignal(sine);
  
  score.play();
}

void draw() {
  // do nothing but keep Processing 'alive'.
}

public void handleCallbacks(int callbackID) {
  if(callbackID == 0) {
    sine.setAmp(0.0);
  }
  if(callbackID == 1) {
    sine.setFreq(SoundCipher.midiToFreq(pitch));
    sine.setAmp(1.0);
    sc.playNote(pitch++ + 4, 80, 1);
  }
}

void stop() {
  out.close();
  minim.stop();
  super.stop();
}
