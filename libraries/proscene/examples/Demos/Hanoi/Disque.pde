class Disque {
  float rayon;
  float hauteur=10;
  int numero, surLePiquet;
  InteractiveFrame repere;
  color c;

  Disque(int surLePiquet, int numer, InteractiveFrame ir) {
    surLePiquet=0;
    numero=numer;
    rayon=numero*10+10;
    repere=ir;
    c=color(random(50, 150), random(50, 150), random(50, 150));
  }

  void draw() {
    pushMatrix();
    repere.applyTransformation();
    noStroke();
    if (repere.grabsInput(((Scene)scene).motionAgent()))
      fill(255, 0, 0);
    else
      fill(0, 0, 255);
    sphere(5);
    fill(c);
    translate(0, 0, -3);
    ellipse(0, 0, rayon*2, rayon*2);
    translate(0, 0, -hauteur);
    scene.drawCone(rayon, rayon, hauteur);
    ellipse(0, 0, rayon*2, rayon*2);
    popMatrix();
  }

  void collerAuPiquet() {
    float  dis=sqrt(sq(repere.position().x())+sq(repere.position().y()));
    float  disd=sqrt(sq(repere.position().x()-150)+sq(repere.position().y()));
    float  disg=sqrt(sq(repere.position().x()+150)+sq(repere.position().y()));
    if (repere.position().z()<120 ) {
      if ( dis<25) {
        repere.setPosition(new Vec(0, 0, repere.position().z()));
        surLePiquet=1;
      }
      if (disd<25) {
        repere.setPosition(new Vec(150, 0, repere.position().z()));
        surLePiquet=2;
      }
      if (disg<25) {
        repere.setPosition(new Vec(-150, 0, repere.position().z()));
        surLePiquet=0;
      }
    }
  }
}