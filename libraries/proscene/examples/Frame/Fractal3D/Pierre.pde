class Pierre extends Interpolation {
  float longueur;

  Pierre(Vec b, Quat qb, float lng) {
    super(b, qb);
    this.longueur=lng;
  }
  
  void actualiser() {
    super.dessin(longueur);
  }
}
