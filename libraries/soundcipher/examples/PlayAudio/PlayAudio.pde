/**
* Play back an audio sample reading it every time it is scheduled
* This exmaple is inefficinet but simple.
* A SoundCicpher Score is created and scheduled events are added
* at the times when the audio file is to be played.
* On playback the events trigger the handleScheduledEvents() method
* which pases the event ID and triggers audio file playback.
*
* SoundCipher library example by Andrew R. Brown
*/

import arb.soundcipher.*;

SoundCipher sc = new SoundCipher(this);
SCScore score = new SCScore();

void setup() {
  score.addScheduledEventListener(this);  
  score.tempo(80);

  for(int i=0; i<5; i++) {
    score.addScheduledEvent(i / 4.0, 3);
  }
  score.play();
}

public void handleScheduledEvents(int eventID) {
  if(eventID == 3) {
    sc.playAudioFile("SD.wav");
  }
}
