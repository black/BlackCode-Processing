/**
* A musical chord - several simulteneous notes - is created
* at runtime by seleting 3 pitches randomly from a pitch set.
* The chord is repeated several times.
*
* To learn how to select a different chord on reach repetition
* see the UnlimitedArt example/tutorial.
*
* SoundCipher library example by Andrew R. Brown
*/

import arb.soundcipher.*;

SoundCipher sc = new SoundCipher(this);

float[] pitches = {60, 62, 64, 67, 69, 72}; // c pentatonic +
float[] chord = {pitches[(int)random(pitches.length)], 
    pitches[(int)random(pitches.length)], pitches[(int)random(pitches.length)]};

sc.repeat(3);
  
sc.playChord(chord, 80, 4);
