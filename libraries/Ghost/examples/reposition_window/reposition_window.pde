/*
 * Reposition window example 
 * This example shows how to move a transparent window. 
 * Please note that we need to clear the screen every time
 * we set a new window position, even if you called 
 * ghost.clearBackground(false) before. 
 */

import de.timpulver.ghost.*;

Ghost ghost;
int x = 400;
int y = 400;

void setup(){
  //window_position_x, window_position_y, width, height
  ghost = new WindowedGhost(this, x, y, 600, 600);
}

void draw(){
  ellipse(mouseX, mouseY, 30, 30);
}

void keyPressed(){
  if(key == CODED){
    switch(keyCode){
      case UP:
        y--;
        break;
      case DOWN:
        y++;
        break;
      case LEFT:
        x--;
        break;
      case RIGHT:
        x++;
        break;
    }
    ghost.setPosition(x, y);
  }
}
