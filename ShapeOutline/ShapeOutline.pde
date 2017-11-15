ArrayList<Circle> poop = new ArrayList();
ArrayList<PVector> spots = new ArrayList();
PImage apple;
void setup() {
  size(512, 512);
  apple = loadImage("apple.png");
  apple.loadPixels();
  for (int x=0; x<apple.width; x++) {
    for (int y=0; y<apple.height; y++) {
      int i = x+y*apple.width;
      color c = apple.pixels[i];
      float b = brightness(c);
      if (b>1) {
        spots.add(new PVector(x, y));
      }
    }
  }
  println(spots.size());
}

void draw() {
  background(-1);  
 
}
 
