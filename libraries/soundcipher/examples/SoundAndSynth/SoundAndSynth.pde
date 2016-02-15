/**
* Combining JavaSound soundbank notes with audio file events in the one score.
* Drums use samples and bass uses the JavaSound synthesizer soundbank.
* The score is repeated several times on playback.
*
* SoundCipher library example by Andrew R. Brown
*/

import arb.soundcipher.*;

SoundCipher sc = new SoundCipher(this);
SCScore score;
SCScore score2;
AudioClip kick;
AudioClip snare;
AudioClip hats;
// alpha channels for drawing ellipses
int a = 255;
int b = 255;

void setup() {
  score = new SCScore();
  score2 = new SCScore();
  score.addCallbackListener(this);  
  score.tempo(130);
  score2.tempo(130);
  // load samples
  kick = sc.loadAudioClip("kick.wav");
  snare = sc.loadAudioClip("snare.wav");
  hats = sc.loadAudioClip("hats.wav");
  // compose drum sequence
  score.addCallback(0.0, 1);
  score.addCallback(1.0, 2);
  score.addCallback(2.0, 1);
  score.addCallback(3.0, 2);
  score.addCallback(3.5, 1);
  for(double i=0.0; i<4; i += 0.5) {
    score.addCallback(i, 3);
  }
  // compose bass part
  score.addNote(0.0, 0, 39, 36, 100, 1.0, 0.6, 64);
  score.addNote(1.0, 0, 39, 38, 100, 1.0, 0.6, 64);
  score.addNote(2.0, 0, 39, 40, 100, 1.0, 0.6, 64);
  score.addNote(3.0, 0, 39, 43, 100, 0.5, 0.6, 64);
  score.addNote(3.5, 0, 39, 31, 100, 0.5, 0.6, 64);
  // play back score with 2 repeats
  score.play(2);
  // add an ending note
  score2.addNote(12.0, 0, 39, 36, 100, 2.0, 1.0, 64);
  score.addCallback(12.0, 1);
  score2.play();
}

void draw() {
    fill(255, 255, 255);
    rect(-1, -1, 101 , 101);
    fill(90, 20, 60, a);
    ellipse(35, 50, 50, 50);
    a = a - 3;
    fill(30, 90, 20, b);
    ellipse(65, 50, 50, 50);
    b = b-3;
}

// Parse the callbacks trigged during the score playbcak
public void handleCallbacks(int callbackID) {
  if(callbackID == 1) {
    a = 200;
    redraw();
    sc.playAudioClip(kick);
  }
  if(callbackID == 2) {
    b = 200;
    redraw();
    sc.playAudioClip(snare);
  }
  if(callbackID == 3) {
    sc.playAudioClip(hats);
  }
}
