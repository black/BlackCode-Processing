/**
* A musical chord - several simulteneous notes - can be specified as a 
* list (array) of the pitches along wit the dynamic and duration which
* will be applied to all notes.
*
* SoundCipher library example by Andrew R. Brown
*/

import arb.soundcipher.*;

SoundCipher sc = new SoundCipher(this);

float[] pitches = {60, 64, 67, 71}; // c major 7th
  
sc.playChord(pitches, 80, 4);
