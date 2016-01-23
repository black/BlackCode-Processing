boolean keyright = false;
boolean keyleft = false; 

void keyPressed() {
  if (key == CODED) {
    if (keyCode == LEFT) keyleft = true;
    if (keyCode == RIGHT) keyright = true;
  }
}

void keyReleased() { 
  keyleft = false;
  keyright = false;
}


void screenFlush() {
  noStroke();
  fill(-1, 50);
  rect(0, 0, width, height);
}

