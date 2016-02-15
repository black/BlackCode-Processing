/**
* Controls music by sending individual MIDI messages.
* Drag around the sketch window to control pitch bend and volume.
*
* A SoundCipher library example by Andrew R. Brown
*/

import arb.soundcipher.*;

SoundCipher sc = new SoundCipher(this);

void draw() {
  background(180);
  ellipse(mouseX, mouseY, 10, 10); 
}

void mousePressed() {
  sc.sendMidi(sc.PROGRAM_CHANGE, 0, random(100), 0);
  sc.sendMidi(sc.NOTE_ON, 0, 60, 100);
}

void mouseDragged() {
  sc.sendMidi(sc.PITCH_BEND, 0, 0, 100 - mouseY + 14);
  sc.sendMidi(sc.CONTROL_CHANGE, 0, 7, mouseX);
}

void mouseReleased() {
  sc.sendMidi(sc.NOTE_OFF, 0, 60, 0);
}
