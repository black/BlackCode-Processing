/**
* Create a music keyboard from the qwerty keyboard.
* Capture key presses and map the ascii key number 
* to a MIDI pitch number and play a note immediatly.
*
* A SoundCipher example by Andrew R. Brown
*/

import arb.soundcipher.*;

SoundCipher sc = new SoundCipher(this);

void keyPressed()
{
  sc.playNote(key - 40, 100, 0.5);
}

// keep processing 'alive'
void draw() {}
