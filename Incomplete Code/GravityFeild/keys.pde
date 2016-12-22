boolean keyup, keydown, keyright, keyleft, keyzpos, keyzneg;
void keyPressed() {
  if (key==CODED) {
    if (keyCode==UP) keyup = true;
    if (keyCode==DOWN) keydown = true;
    if (keyCode==LEFT) keyleft = true;
    if (keyCode==RIGHT) keyright = true;
  }
  if (key=='a' || key=='A') keyzpos = true;
  if (key=='z' || key=='Z') keyzneg = true;
}

void keyReleased() {
  keyup = false;
  keydown = false;
  keyright = false;
  keyleft = false;
  keyzpos = false;
  keyzneg = false;
}

