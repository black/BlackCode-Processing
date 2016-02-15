/*
 * WindowedGhost example 
 * This example shows how to create a transparent window 
 * with specific width, height and position.
 * Move the mouse around to locate the window 
 */

import de.timpulver.ghost.*;

Ghost ghost;

void setup(){
  //window_position_x, window_position_y, width, height
  ghost = new WindowedGhost(this, 400, 400, 600, 600);
  // uncomment if you don't want background clearing
  //ghost.clearBackground(false);
}

void draw(){
  ellipse(mouseX, mouseY, 30, 30);
}
