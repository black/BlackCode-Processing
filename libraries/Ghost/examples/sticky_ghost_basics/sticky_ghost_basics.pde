/*
 * StickyGhost example 
 * This example shows how to create a transparent window 
 * at one of the screen boarders (top, right, bottom or left).
 * The third parameter is the window heights / width
 * Move the mouse around to locate the window 
 */

import de.timpulver.ghost.*;

Ghost ghost;

void setup(){
  ghost = new StickyGhost(this, "top", 300);
  // uncomment if you don't want background clearing
  //ghost.clearBackground(false);
}

void draw(){
  ellipse(mouseX, mouseY, 20, 20);
}
