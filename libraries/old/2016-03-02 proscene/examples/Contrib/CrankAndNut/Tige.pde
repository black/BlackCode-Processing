class Tige {
  float rayon, hauteur, pas;
  Tige() {
    rayon=16;
    hauteur=200;
    pas=15;
  }
  
  void draw() {
    pushMatrix();
    pushStyle();
    fill(155);
    scene.drawCone(20, 0, 0, rayon-3, rayon-3, hauteur);

    noFill();
    stroke(170, 50, 50);
    for (int i=0;i<200;i++) {
      rotateZ(PI/10);
      strokeWeight(9);
      line(rayon, 0, pas*i/20, rayon*cos(PI/10), rayon*sin(PI/10), pas*(i+1)/20);
    }
    popStyle();
    popMatrix();
  }
}
