boolean jump = false, left = false, right=false, up = false, down = false;
void keyPressed() {
  if (key==CODED) {
    if (keyCode == LEFT) left = true;
    if (keyCode== RIGHT) right = true;
    if (keyCode == UP) up = true;
    if (keyCode== DOWN) down = true;
  }
  if (key==' ') jump = true;
} 

void keyReleased() {
  left = false;
  right = false;
  up = false;
  down = false;
} 

