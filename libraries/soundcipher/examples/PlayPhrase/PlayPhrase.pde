/**
* A musical sequence - a phrase - can be specified as Lists (arrays)
* of the pitch, dynamic, and duration values for each note in the phrase.
*
* SoundCipher library example by Andrew R. Brown
*/

import arb.soundcipher.*;

SoundCipher sc = new SoundCipher(this);

float[] pitches = {70, 75, 72, 63, 65, 67, 68, 70};
float [] dynamics = {80, 100, 80, 80, 80, 100, 80, 80};
float[] durations = {0.5, 1, 0.5, 1, 0.5, 1, 0.5, 0.5};
  
sc.playPhrase(pitches, dynamics, durations);


