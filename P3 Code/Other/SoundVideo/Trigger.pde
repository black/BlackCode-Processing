// StickyNotes
// Copyright (c) 2009 Jonathan Speicher
// jon.speicher@gmail.com
// Licensed under the MIT license: http://creativecommons.org/licenses/MIT
//
// This class represents a very simple trigger.  It monitors the color value of
// a single pixel on the display and if the value changes by a certain 
// threshold, it generates an audio output frequency.

class Trigger
{
  int x;
  int y;
  int triggerColor;
  int threshold;
  boolean triggered;
  
  AudioOutput lineOut;
  SineWave sineWave;
  
  Trigger(int triggerX, int triggerY, int triggerC, AudioOutput out, float frequency)
  {
    x = triggerX;
    y = triggerY;
    triggerColor = triggerC;
    threshold = 25;
    triggered = false;
    
    lineOut = out;
    sineWave = new SineWave(frequency, 1.0, lineOut.sampleRate());
    
    println("Added new trigger x = " + x + " y = " + y + " color = " + triggerColor + " freq = " + frequency);
  }
  
  boolean thresholdExceeded()
  {
    color currentColor = get(x, y);
    float deltaRed = abs(red(currentColor) - red(triggerColor));
    float deltaGreen = abs(green(currentColor) - green(triggerColor));
    float deltaBlue = abs(blue(currentColor) - blue(triggerColor));
    return (deltaRed > threshold) || (deltaGreen > threshold) || (deltaBlue > threshold);
  }
  
  void trigger()
  {
    if (!triggered)
    {
      println("TRIGGER: " + sineWave.frequency());
      lineOut.addSignal(sineWave);
      triggered = true;
    }
  }
  
  void untrigger()
  {
    if (triggered)
    {
      println("UNTRIGGER: " + sineWave.frequency());
      lineOut.removeSignal(sineWave);
      triggered = false;
    }
  } 
}
