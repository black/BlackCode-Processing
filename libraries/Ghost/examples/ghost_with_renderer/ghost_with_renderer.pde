/*
 * GhostWithRenderer example 
 * This example shows how to create a transparent 
 * window (windowed, stickied or fullscreen) 
 * with a specific renderer. 
 * You can pass in every renderer Processing can use. 
 */

import de.timpulver.ghost.*;

Ghost ghost;

void setup(){
  ghost = new WindowedGhost(this, 400, 400, 600, 600, P3D); // window mode
  //ghost = new StickyGhost(this, "bottom", 300, P3D); // sticky mode
  //ghost = new FullscreenGhost(this, P3D); // fullscreen mode
  
  // uncomment if you don't want background clearing
  //ghost.clearBackground(false);
}

void draw(){
  ellipse(mouseX, mouseY, 30, 30);
}
