PVector comb(float t1, PVector v1, float t2, PVector v2) {
  PVector res=PVector.add(PVector.mult(v1, t1), PVector.mult(v2, t2));
  return res;
}

PVector comb(float t1, PVector v1, float t2, PVector v2, float t3, PVector v3) {
  PVector res=PVector.add(PVector.mult(v1, t1), PVector.mult(v2, t2));
  res=PVector.add(res, PVector.mult(v3, t3));
  return res;
}

float angleEntre(PVector u, PVector v) {
  u.normalize();
  v.normalize();
  float sinus=u.y*v.z-u.z*v.y;

  return asin(sinus);
}

PVector centreGravite(PVector u, PVector v, PVector r) {
  PVector gr= comb(0.5f, u, 0.5f, v);
  gr= comb(1.0f/3.0f, r, 2.0f/3.0f, gr);
  return gr;
}

PVector barycentre(float lamb, PVector u, PVector v) {
  return comb(lamb, u, 1-lamb, v);
}

float barycentre(float lamb, float u, float v) {
  return lamb*u+(1-lamb)*v;
}

void ligne(PVector a, PVector b) {
  line(a.x, a.y, a.z, b.x, b.y, b.z);
}

void afficher(PVector u) {
  println("vecteur = "+u.x+"    "+u.y+"   "+u.z);
}

void afficher(Quat q) {
  println("quaternion = x  "+q.x()+"  y  "+q.y()+" z  "+q.z()+"... w  "+q.z());
}

void triangle3d(PVector a, PVector b, PVector c) {
  beginShape();
  fill(255, 200, 0, 200);
  vertex( a.x, a.y, a.z);
  fill(255, 255, 0, 200);        
  vertex( b.x, b.y, b.z);
  fill(155, 50, 250, 200);
  vertex( c.x, c.y, c.z);
  endShape();
}

void triangle3d(PVector a, PVector b, PVector c, color couleur) {
  stroke(0, 100, 255);
  beginShape();
  fill(couleur);
  vertex( a.x, a.y, a.z);
  vertex( b.x, b.y, b.z);
  vertex( c.x, c.y, c.z);
  endShape();
}     

void unText1(String tex, PVector v) {
  float leX = screenX(v.x, v.y, v.z);
  float leY = screenY(v.x, v.y, v.z);

  pushMatrix();
  scene.beginScreenDrawing();
  fill(0);
  text(tex, leX+10, leY, 10);
  scene.endScreenDrawing(); 
  popMatrix();
}
