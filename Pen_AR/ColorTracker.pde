void colorTracker() {
  float worldRecord = 500; 
  int closestX = 0; 
  int closestY = 0; 
  for (int i=0; i<video.width; i++) {
    for (int j=0; j<video.height; j++) {
      int loc = i + j*video.width; 
      color currentColor = video.pixels[loc];
      float r1 = red(currentColor); 
      float g1 = green(currentColor); 
      float b1 = blue(currentColor); 
      float r2 = red(trackColor); 
      float g2 = green(trackColor); 
      float b2 = blue(trackColor); 
      float d = dist(r1, g1, b1, r2, g2, b2); 
      if (d < worldRecord) { 
        worldRecord = d; 
        closestX = i; 
        closestY = j; 
        PVector V = new PVector(closestX-random(50), closestY+random(-20, 20));
        smoke.add(new Dot(V, trackColor));
      }
    }
  }
}

int trackColor;
void mousePressed() { 
  trackColor = video.get(mouseX, mouseY);
  println(trackColor);
}

