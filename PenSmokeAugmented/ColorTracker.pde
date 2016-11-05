PVector colorTracker() {
  PVector position = new PVector(0, 0);
  int index = 0;
  for (int i=0; i<video.width; i++) {
    for (int j=0; j<video.height; j++) {
      int pixelValue = video.pixels[index];  

      //      int r = (pixelColor >> 16) & 0xff;
      //      int g = (pixelColor >> 8) & 0xff;
      //      int b = pixelColor & 0xff;
      //
      //      int tr = (clckd_colr >> 16) & 0xff;
      //      int tg = (clckd_colr >> 8) & 0xff;
      //      int tb = clckd_colr & 0xff;

      //if (clckd_colr-10 < pixelValue &&  pixelValue < clckd_colr+10) {
      if (clckd_colr == pixelValue) {
        position = new PVector(i, j);
      }
      index++;
    }
  }
  return position;
}

int clckd_colr;
void mousePressed() { 
  clckd_colr = video.get(mouseX, mouseY);
  println(clckd_colr);
}

