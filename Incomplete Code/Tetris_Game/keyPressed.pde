boolean right, left, up, down, rot;
void keyPressed() {
  if (key==CODED) {
    if (keyCode==UP)up=true;
    if (keyCode==DOWN)down=true;
    if (keyCode==RIGHT)right=true;
    if (keyCode==LEFT)left=true;
  }
  if (key=='m')rot = true;
}

void keyReleased() {
  up = false;
  down = false;
  right = false;
  left = false;
  rot = false;
}

