
void cilinder(Vec3D p1, Vec3D p2, float rad1, float rad2, color col, float frac){
  int steps = 100;
  float ang = TWO_PI/float(steps);
  noStroke();
  fill(col);

  Vec3D v = p2.sub(p1).normalize();
  float a1 = atan2(v.z,sqrt(sq(v.x)+sq(v.y)));
  float a2 = atan2(v.y,v.x);
  Vec3D per = new Vec3D(-cos(a2)*sin(a1),-sin(a2)*sin(a1),cos(a1));
  Vec3D per1 = per.scale(rad1);
  Vec3D per2 = per.scale(rad2);
  Vec3D step = p2.sub(p1).scale(frac);

  beginShape(QUAD_STRIP);
    for(int i = 0; i <= steps; i++) {
      vertex(p1.x+step.x+per1.x,p1.y+step.y+per1.y,p1.z+step.z+per1.z);
      vertex(p2.x-step.x+per2.x,p2.y-step.y+per2.y,p2.z-step.z+per2.z);
      per1.rotateAroundAxis(v,ang);
      per2.rotateAroundAxis(v,ang);
    }
  endShape();
}

void connector(Vec3D p1, Vec3D p2, Vec3D p3, float rad1, float rad2, color col, float frac){
  int steps = 100;
  float ang = TWO_PI/float(steps);
  float angSecond = 0;
  noStroke();
  fill(col);

  Vec3D v1 = p2.sub(p1).normalize();
  float a1 = atan2(v1.z,sqrt(sq(v1.x)+sq(v1.y)));
  float a2 = atan2(v1.y,v1.x);
  Vec3D per1 = new Vec3D(-cos(a2)*sin(a1),-sin(a2)*sin(a1),cos(a1));
  per1.scaleSelf(rad1);
  Vec3D step1 = p2.sub(p1).scale(frac);

  Vec3D v2 = p3.sub(p2).normalize();
  a1 = atan2(v2.z,sqrt(sq(v2.x)+sq(v2.y)));
  a2 = atan2(v2.y,v2.x);
  Vec3D per2 = new Vec3D(-cos(a2)*sin(a1),-sin(a2)*sin(a1),cos(a1));
  per2.scaleSelf(rad2);
  Vec3D step2 = p3.sub(p2).scale(frac);

  float minDist = 100000;
  for(int i = 0; i <= steps; i++) {
    float d = per1.distanceTo(per2);
    if(d < minDist){
      minDist = d;
      angSecond = float(i)*ang;
    } 
    per2.rotateAroundAxis(v2,ang);
  }  
  
  per2 = new Vec3D(-cos(a2)*sin(a1),-sin(a2)*sin(a1),cos(a1));
  per2.scaleSelf(rad2);
  per2.rotateAroundAxis(v2,angSecond);
  
  beginShape(QUAD_STRIP);
    for(int i = 0; i <= steps; i++) {
      vertex(p2.x-step1.x+per1.x,p2.y-step1.y+per1.y,p2.z-step1.z+per1.z);
      vertex(p2.x+step2.x+per2.x,p2.y+step2.y+per2.y,p2.z+step2.z+per2.z);
      per1.rotateAroundAxis(v1,ang);
      per2.rotateAroundAxis(v2,ang);
    }
  endShape();
}

