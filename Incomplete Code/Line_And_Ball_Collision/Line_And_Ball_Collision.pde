ArrayList<Segment> segmentList = new ArrayList();
ArrayList<Ball> ballList = new ArrayList();

int xt, yt, xmt, ymt;
boolean mouseUp, mouseDown;
void setup() {
  size(300, 500);
  addBalls();
}

void draw() {
  background(-1);
  /*----Segment Drawing start-----*/
  if (mouseDown) {
    xmt = mouseX;
    ymt = mouseY;
    stroke(0);
    line(xt, yt, xmt, ymt);
  }
  for (int i=0; i<segmentList.size (); i++) {
    Segment S = segmentList.get(i);
    S.show();
  }
  /*----Segment Drawing end-----*/
  /*----Balls Generation start-----*/
  for (int j=0; j<ballList.size (); j++) {
    Ball B = ballList.get(j);
    B.show();
    if (B.y>height) {
      ballList.remove(j);
      ballList.add(new Ball(random(0, width), 0));
    }
  }
  /*----Balls Generation end-----*/
  /*----Collision Detection Starts-----*/
  for (int k=0; k<ballList.size (); k++) {
    Ball B = ballList.get(k);
    for (int l=0; l<segmentList.size (); l++) {
      Segment S = segmentList.get(l);
      if ((S.x<B.x && B.x<S.xm) || (S.xm<B.x && B.x<S.x)) {
        float tempy = S.getY(B.x);
        if ((int)abs(tempy-B.y)<6) {
          B.update(true, 0.5f);
        }
      }
    }
    B.update(false, 0);
  }
}


void addBalls() {
  for (int i=0; i<5; i++) {
    ballList.add(new Ball(random(0, width), 0));
  }
}

