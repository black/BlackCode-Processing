/**
* Play a sound as a key is typed.
*
* A SoundCipher example by Andrew R. Brown
*/

import arb.soundcipher.*;
import ddf.minim.*;

SoundCipher sc = new SoundCipher(this);
Minim minim = new Minim(this);
AudioSample kick;
 
void setup () {
  kick = minim.loadSample("kick.mp3");
}

void keyPressed()
{
  if (key == 's') sc.playAudioFile("snare.aif");
  if (key == 'k') kick.trigger();
}

// keep processing 'alive'
void draw() {}

void stop()
{
  // always close Minim audio classes when you are done with them
  kick.close();
  minim.stop();
  super.stop();
}
