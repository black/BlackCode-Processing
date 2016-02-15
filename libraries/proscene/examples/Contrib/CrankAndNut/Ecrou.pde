class Ecrou {
  float positionZ, pas;

  Ecrou() {
    positionZ=-180;
    pas=15;
  }

  void draw() {
    pushStyle();  
    pushMatrix();
    translate( 0, 0, positionZ-pas*angleVis/TWO_PI);
    fill(155);
    box(100, 100, 20);
    popMatrix();
    popStyle();
  }
}
