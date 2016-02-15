/**
 * Sprite Tracking (Shifty + Teddy)
 * by James Patterson. 
 * Feature tracking with GPUKLT added by Andres Colubri
 *
 * This tracker identifies "features" (which are by definition "corner" pixels, this is, pixels
 * with large changes in intensity along the X and Y directions) and then tracks them along 
 * successive frames. When a feature disappears it is replaced by a new one.
 */
 
import processing.opengl.*;
import codeanticode.progpuklt.*;

Animation animation1, animation2;
FeatureTracker tracker;
float xpos, ypos;
float drag = 30.0;

void setup() {
  size(200, 200, OPENGL);
  background(255, 204, 0);
  frameRate(24);
  animation1 = new Animation("PT_Shifty_", 38);
  animation2 = new Animation("PT_Teddy_", 60);
  tracker = new FeatureTracker(this, 200, 200, 100);
}

void draw() { 
  // Display the sprite at the position xpos, ypos
  if (mousePressed) {
    background(153, 153, 0);
    animation1.advanceFrame();
    animation1.display(0, 0);
    tracker.track(animation1.getImage());
  } else {
    background(255, 204, 0);
    animation2.advanceFrame();    
    animation2.display(0, 0);
    tracker.track(animation2.getImage());
  }
  
  // Parameters used to draw features and their tracks.
  stroke(200, 10, 10);
  fill(200, 10, 10);
  ellipseMode(CENTER);  
  tracker.drawTracks();    
  tracker.drawFeatures(10); 
}

void stop()
{
  // Stop the sketch by hitting the stop button, if you close the output
  // window you will get errors.
  
  // And remember to stop the tracker here.
  tracker.stop();
  
  super.stop();
}
