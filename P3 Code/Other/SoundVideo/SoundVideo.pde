// StickyNotes
// Copyright (c) 2009 Jonathan Speicher
// jon.speicher@gmail.com
// Licensed under the MIT license: http://creativecommons.org/licenses/MIT
//
// Click a note, then click a point in the video (a bright, unchanging color
// like a sticky note on a wall works best).  Cover the sticky note with your 
// hand to play the note.
//
// The program isn't all that smart; changing lighting conditions, moving your 
// camera, etcetera will all produce disastrous results.  Chords don't work all 
// that well either.

import ddf.minim.*;
import ddf.minim.signals.*;
import processing.video.*;

Capture capture;
Minim minim;
AudioOutput lineOut;

// Triggers are the video points that, when the camera sees their color change,
// play notes.  Selectors allow the assignment of a note to a trigger.

ArrayList triggers;
ArrayList selectors;

// These are the frequencies of a few notes starting at middle C, and a 
// variable to keep track of which frequency is currently selected when 
// assigning triggers.

float[] frequencies = { 
  261.63, 293.66, 329.63, 349.23, 392.00, 440.00, 493.88
};
String[] frequencyLabels = { 
  "C", "D", "E", "F", "G", "A", "B"
};
int nextFrequencyIndex = 0;

// Setup runs once when the program begins.

void setup()
{
  // Capture 30 fps 640 x 480 video with a 50 pixel selector bar at the bottom.

  size(640, 530);
  capture = new Capture(this, width, height - 50, 30);
  capture.start();
  // 512 is the buffer size for the line output.

  minim = new Minim(this);
  lineOut = minim.getLineOut(Minim.STEREO, 512);

  // Start with no triggers.

  triggers = new ArrayList();

  // Build selectors for each frequency.

  selectors = new ArrayList();
  int cellWidth = width / frequencies.length;

  for (int i = 0; i < frequencies.length; i++)
  {
    selectors.add(new Selector((cellWidth / 2) + (i * cellWidth), height - 25, 30, 30, frequencyLabels[i]));
  }
}

// Stop runs once when the program exits.

void stop()
{
  lineOut.close();
  minim.stop();
  super.stop();
}

// mouseClicked runs when the mouse button is clicked in the window.

void mouseClicked()
{
  boolean selectorHit = false;

  // Look to see if a selector was hit and remember its frequency if it was.

  for (int i = 0; i < selectors.size (); i++)
  {
    Selector selector = (Selector) selectors.get(i);

    if (selector.hit(mouseX, mouseY))
    {
      nextFrequencyIndex = i;
      selectorHit = true;
    }
  }

  // If the mouse wasn't clicked on a selector, it's defining a new trigger 
  // with the last selected frequency.

  if (!selectorHit)
  {
    triggers.add(new Trigger(mouseX, mouseY, get(mouseX, mouseY), lineOut, frequencies[nextFrequencyIndex]));
  }
}

// Draw is called over and over again.  

void draw() 
{
  drawCapture();
  drawSelectors();
  processTriggers();
}

// Draws the captured video frame.

void drawCapture()
{
  if (capture.available())
  {
    capture.read();

    // Invert the display (like a reflection in a mirror) to make moving more
    // intuitive.

    pushMatrix();
    scale(-1.0, 1.0);
    image(capture, -capture.width, 0);
    popMatrix();
  }
}

// Draws the frequency selector buttons.

void drawSelectors()
{
  for (int i = 0; i < selectors.size (); i++)
  {
    Selector selector = (Selector) selectors.get(i);
    selector.draw();
  }
}

// Check to see if a defined trigger is triggered and play its note if it is.

void processTriggers()
{
  for (int i = 0; i < triggers.size (); i++)
  {
    Trigger trigger = (Trigger) triggers.get(i);

    if (trigger.thresholdExceeded())
    {
      trigger.trigger();
    } else
    {
      trigger.untrigger();
    }
  }
}

