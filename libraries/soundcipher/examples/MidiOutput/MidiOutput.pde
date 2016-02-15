/**
* This example shows how to route playback to an external
* MIDI synthesizer. The availible devices are printed, then
* the preferred device is set. Playback is controlled by the
* slowed down draw thread.
*
* A SoundCipher example by Andrew R. Brown
*/
import arb.soundcipher.*;

SoundCipher sc = new SoundCipher();

void setup() {
  SoundCipher.getMidiDeviceInfo();
  sc.setMidiDeviceOutput(1);
  frameRate(1);
}

void draw() {
  sc.playNote(sc.pcRandom(60, 72, sc.PENTATONIC), 100, 1);
}
