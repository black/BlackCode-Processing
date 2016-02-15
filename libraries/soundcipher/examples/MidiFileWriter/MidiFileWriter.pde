import arb.soundcipher.*;

SCScore score = new SCScore();

for(float i=0; i< 8; i++) {
  float p = score.pcRandom(60, 72, score.PENTATONIC);
  score.addNote(i, p, random(60, 100), 0.25);
  score.addNote(i+0.25, p+4, random(60, 100), 0.25);
  score.addNote(i+0.5, p+7, random(60, 100), 0.25);
  score.addNote(i+0.75, p+4, random(60, 100), 0.5);
}
score.play();
score.writeMidiFile("/Users/browna/Desktop/arpeggio.mid");

