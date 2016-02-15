/**
* Initialise SoundCipher and play one note many times.
*
* A SoundCipher library example by Andrew R. Brown
*/

import arb.soundcipher.*;

SoundCipher sc = new SoundCipher(this);

sc.repeat(7);

sc.playNote(60, 100, 0.5);

