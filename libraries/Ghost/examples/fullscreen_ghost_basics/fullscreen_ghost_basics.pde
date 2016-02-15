/*
 * FullscreenGhost example 
 * This example shows how to create a transparent fullscreen window.
 */

import de.timpulver.ghost.*;

Ghost ghost;

void setup(){
  ghost = new FullscreenGhost(this);
  // uncomment if you don't want background clearing
  //ghost.clearBackground(false);
}

void draw(){
  ellipse(mouseX, mouseY, 20, 20);
}