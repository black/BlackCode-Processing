void initPhysics() {
  physics=new VerletPhysics();
  physics.setWorldBounds(new AABB(new Vec3D(),new Vec3D(DIM,DIM,DIM)));
  if (surface!=null) {
    surface.reset();
    mesh.clear();
  }
  boundingSphere=new SphereConstraint(new Sphere(new Vec3D(),DIM),SphereConstraint.INSIDE);
  gravity=new GravityBehavior(new Vec3D(0,1,0));
  physics.addBehavior(gravity);
}

void updateParticles() {
  Vec3D grav=Vec3D.Y_AXIS.copy();
  grav.rotateX(mouseY*0.01);
  grav.rotateY(mouseX*0.01);
  gravity.setForce(grav.scaleSelf(2));
  numP=physics.particles.size();
  if (random(1)<0.8 && numP<NUM_PARTICLES) {
    VerletParticle p=new VerletParticle(new Vec3D(random(-1,1)*10,-DIM,random(-1,1)*10));
    if (useBoundary) p.addConstraint(boundingSphere);
    physics.addParticle(p);
  }
  if (numP>10 && physics.springs.size()<1400) {
    for(int i=0; i<60; i++) {
      if (random(1)<0.04) {
        VerletParticle q=physics.particles.get((int)random(numP));
        VerletParticle r=q;
        while(q==r) {
          r=physics.particles.get((int)random(numP));
        }
        physics.addSpring(new VerletSpring(q,r,REST_LENGTH, 0.0002));
      }
    }
  }
  float len=(float)numP/NUM_PARTICLES*REST_LENGTH;
  for(Iterator i=physics.springs.iterator(); i.hasNext();) {
    VerletSpring s=(VerletSpring)i.next();
    s.setRestLength(random(0.9,1.1)*len);
  }
  physics.update();
}
