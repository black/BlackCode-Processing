ArrayList<Rail> poop = new ArrayList();
ArrayList<Drop> rain = new ArrayList();
int xt, yt, xmt, ymt;
boolean mouseUp, mouseDown;
void setup() {
  size(300, 450);
  rain();
}

void draw() {
  background(-1);
  if (mouseDown) {
    xmt = mouseX;
    ymt = mouseY;
    stroke(0);
    line(xt, yt, xmt, ymt);
  } 
  boolean hit = false;
  for (int j=0; j<rain.size (); j++) {
    Drop D = rain.get(j);
    for (int i=0; i<poop.size (); i++) {
      Rail R = poop.get(i);
    }
  }

  for (int k=0; k<poop.size (); k++) {
    Rail R = poop.get(k);
    for (int j=0; j<rain.size (); j++) {
      Drop D = rain.get(j);
      hit = detectCollision(D.loc, R.p1, R.p2, D.r);
      if (hit)D.update(true);
      else D.update(false);
      if (D.loc.y>height) {
        rain.remove(j);
        rain.add(new Drop(random(0, width), 0));
      }
    }
  }


  for (int i=0; i<poop.size (); i++) {
    Rail R = poop.get(i);
    R.show();
  }

  for (int i=0; i<rain.size (); i++) {
    Drop D = rain.get(i);
    D.show();
  }
}

void mousePressed() {
  xt = mouseX;
  yt = mouseY;
}
void mouseDragged() {
  mouseDown = true;
}

void mouseReleased() {
  Rail K = new Rail(xt, yt, xmt, ymt);
  poop.add(K);
  mouseDown = false;
}

void rain() {
  for (int i=0; i<5; i++) {
    rain.add(new Drop(random(0, width), 0));
  }
}

