void grid() {
  stroke(0, 10);
  noFill();
  for (int i=0; i<8; i++) {  
    for (int j=0; j<8; j++) {
      int w = width/8;
      int h = height/8;
      rect(i*w, j*h, w, h);
    }
  }
}

