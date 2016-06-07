/*
This is a sound file player. 
*/

import processing.sound.*;

SoundFile soundfile;
Delay delay;

void setup() {
    size(640,360);
    background(255);
        
    //Load a soundfile
    soundfile = new SoundFile(this, "vibraphon.aiff");
    
    // create a Delay Effect
    delay = new Delay(this);
    
    // These methods return useful infos about the file
    println("SFSampleRate= " + soundfile.sampleRate() + " Hz");
    println("SFSamples= " + soundfile.frames() + " samples");
    println("SFDuration= " + soundfile.duration() + " seconds");

    // Play the file in a loop
    soundfile.loop();
    
    // Patch the delay
    delay.process(soundfile, 5);
}      


void draw() { 
  
  // Map mouseY from 0.2 to 1.0 for amplitude  
  soundfile.amp(map(mouseY, 0, height, 0.2, 1.0)); 
 
  // Map mouseY from -1.0 to 1.0 for left to right 
  soundfile.pan(map(mouseY, 0, height, -1.0, 1.0));  
  
  // Map mouseY from 0.001 to 2.0 seconds for the delaytime 
  delay.time(map(mouseY, 0, height, 0.001, 2.0));
  
  // Map mouseX from 0 to 0.8 for the delay feedback 
  delay.feedback(map(mouseX, 0, width, 0.0, 0.8));
}