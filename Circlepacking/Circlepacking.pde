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

  Circle newC = createCircle();
  if (newC!=null) {
    poop.add(newC);
  }

  for (int i=0; i<poop.size (); i++) {
    Circle iC = poop.get(i); 
    if (iC.edges()) {
      iC.growing = false;
    } else {
      for (int j=0; j<poop.size (); j++) {
        Circle jC = poop.get(j); 
        if (iC!=jC) {
          float d = dist(jC.x, jC.y, iC.x, iC.y);
          if (d<jC.r+iC.r) {
            iC.growing = false;
            // break;
          }
        }
      }
    }
    if (dist(mouseX, mouseY, iC.x, iC.y)<iC.r && mousePressed ) {
      poop.remove(i);
    }
    iC.grow();
    iC.show();
  }
}

Circle createCircle() { 
  int index = int(random(0, spots.size()));
  PVector spot = spots.get(index);
  float x = spot.x;
  float y = spot.y;
  boolean valid = true;
  for (int i=0; i<poop.size (); i++) {
    Circle C = poop.get(i);
    float d = dist(C.x, C.y, x, y);
    if (d<C.r) {
      valid = false;
      break;
    }
  }
  if (valid) {
    return new Circle(x, y);
  } else {
    return null;
  }
}

class Circle {
  float x, y, r, t;
  boolean growing = true;
  color c;
  Circle(float x, float y) {
    this.x =x ;
    this.y = y;
    r =1;
    c = (color) random(#000000);
  }

  void show() {
    fill(0);
    noStroke();
    pushMatrix();
    translate(x, y);
    rotate(radians(t++));
    rectMode(CENTER);
    rect(0, 0, 2*r, 2*r);
    popMatrix();
  }

  void grow() {
    if (growing)r+=2;
  }


  boolean edges() {
    return (x+r>width || x -r<0 || y+r>height || y-r<0);
  }
}

