class Manivelle {
  float hauteur, rayon;
  
  Manivelle() {
    hauteur=50;
    rayon =50;
  }

  void draw() {
    pushMatrix();
    fill(200);
    noStroke();
    scene.drawCone(20, 0, 0, 40, 20, 80);
    translate(0, 0, 100);

    sphere(30);
    rotateY(HALF_PI);
    translate(0, 0, -90);
    scene.drawCone(20, 0, 0, 10, 10, 180);
    translate(0, 0, -10);
    sphere(25);
    translate(0, 0, 200);
    sphere(25);
    rotateY(-HALF_PI);
    translate(0, 0, 20);
    scene.drawCone(20, 0, 0, 10, 15, 50);
    popMatrix();
  }
}
