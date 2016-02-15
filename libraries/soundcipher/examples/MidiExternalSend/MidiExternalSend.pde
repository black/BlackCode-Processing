/**
* Example of how MIDI events can be sent directly, or scheduled in a score.
* The program assumes you have an external MIDI device and you may need to
* change the channel and controller number settings for your device.
* Unusually, the callbackID is used as a data value for the controller.
*
* A SoundCipher example by Andrew R. Brown
*/

import arb.soundcipher.*;

SoundCipher sc = new SoundCipher(this);
SCScore score;
float channel = 0;
float controller = 81;

void setup() {
  sc.getMidiDeviceInfo();
  sc.setMidiDeviceOutput(1);
  score = new SCScore();
  score.addCallbackListener(this);
  score.addCallback(1, 0);
  score.addCallback(2, 127);
  score.addNote(1, 60, 100, 1);
  score.addNote(2, 72, 100, 1);
  score.play();
}

void draw() {
  background(180);
  rect(mouseX-5, mouseY-5, 10, 10);
}

void mouseDragged() {
  sc.sendMidi(sc.CONTROL_CHANGE, channel, controller, mouseY);
}

void handleCallbacks(int callbackID) {
  sc.sendMidi(sc.CONTROL_CHANGE, channel, controller, callbackID);
}

