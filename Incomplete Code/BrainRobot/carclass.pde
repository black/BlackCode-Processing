class Car {
  float t=0;
  PVector l, v;
  float prev, next, angle;
  Car(PVector l) {
    this.l = l;
  }
  void show() {  
    angle = prev + t*(next-prev); 
    noFill();
    stroke(0);
    pushMatrix();
    translate(l.x, l.y);
    rotate(radians(angle));
    pushStyle();
    rectMode(CENTER);
    rect(0, 0, 30, 20, 5, 5, 5, 5);
    line(0, 0, 10, 0);
    popStyle();
    popMatrix();
    if (t < 1.0) {
      t += 0.0005f;
    } else t =0;
  }
  void update(float myangle) { 
    prev = angle;
    next = myangle;
  }
}

