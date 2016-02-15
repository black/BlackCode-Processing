/**
* Steve Reich's composition Piano Phase requires two pianists to play the
* same phrase together, one slightly faster than the other at times. This 
* program immitates that by using two SoundCipher instances that run at 
* different tempi.
*
* A SoundCipher example by Andrew R. Brown
*/

import arb.soundcipher.*;

SoundCipher part1 = new SoundCipher(this);
SoundCipher part2 = new SoundCipher(this);

void setup() {
  part1.tempo(80);
  part2.tempo(82);
  part1.repeat(16);
  part2.repeat(16);
  part1.pan(10);
  part2.pan(120);

  float[] pitches = {64, 66, 71, 73, 74, 66, 64, 73, 71, 66, 74, 73};
  float[] dynamics = new float[pitches.length];
  float[] durations = new float[pitches.length];

  for(int i=0; i<pitches.length; i++) {
    dynamics[i] = random(40) + 70;
    durations[i] = 0.25;
  }

  part1.playPhrase(pitches, dynamics, durations);
  part2.playPhrase(pitches, dynamics, durations);
}

void draw() {}

void stop() {
  part1.stop();
  part2.stop();
}
