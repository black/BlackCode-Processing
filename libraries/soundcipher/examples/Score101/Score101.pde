/**
* Use a score object as a container for notes, phrases, chords,
* and callbacks.
* Notes can be on different channels, sounds, and beats.
*
* A SoundCipher example by Andrew R. Brown
*/

import arb.soundcipher.*;

SCScore score = new SCScore();
float[] pitches = {0, 4, 7, 9};

// kick drum
score.addNote(0, 9, 0, 36, 100, 0.5, 0.8, 64);
score.addNote(3.5, 9, 0, 36, 100, 0.5, 0.8, 64);

// hi hats
for (float i=0; i<8; i++) {
  score.addNote(i/2, 9, 0, 42, cos(i * 3.14159 * 2 * .25) * 30 + 70, 0.5, 0.8, 64);
}

// bass
float[] bassPitches = new float[4];
float[] bassDynamics = new float[4];
float[] bassDurations = new float[4];
float[] bassArticulations = new float[4];
float[] bassPans = new float[4];
for (int i=0; i<4; i++) {
  if (i<1) {
    bassDurations[i] = 1;  
    bassPitches[i] = 36;  
  } else {
    bassDurations[i] = random(1) * 0.5 + 0.5;
    bassPitches[i] = 36 + pitches[(int)(random(pitches.length))];
  }
  bassDynamics[i] = random(30) + 50;
  bassArticulations[i] = 0.8;
  bassPans[i] = 64;
}
score.addPhrase(0, 0, 34, bassPitches, bassDynamics, bassDurations, bassArticulations, bassPans);

// piano
float chordPitches[] = new float[3];
for (int i=0; i<3; i++) {
  chordPitches[i] = 60 + pitches[(int)(random(pitches.length))];
}
score.addChord(1, 1, 0, chordPitches, 60, 2, 0.8, 64);
score.addChord(3, 1, 0, chordPitches, 30, 0.25, 0.8, 64);

score.play(3);




