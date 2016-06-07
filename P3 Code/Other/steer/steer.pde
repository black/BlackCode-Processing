/* OpenProcessing Tweak of *@*http://www.openprocessing.org/sketch/11105*@* */
/* !do not delete the line above, required for linking your tweak if you re-upload */

// Boid.pde

class Boid
{
    float radius;
    float speed;
    
    float maxForce;
    float maxSpeed;
    
    PVector m_velocity;
    PVector m_prevVelocity;

    PVector up;

    PVector position; 
    PVector lposition;
    
    float WanderSide;
    float WanderUp; 
    
    public Boid( PVector pos, float strength )
    {
      radius=strength;speed=0;
      position = pos;
      lposition = new PVector();
     lposition.set( pos);
      
      up = new PVector(0.,0.,1.);
      maxForce = strength*.5;
      maxSpeed = strength*1.3;
     
      m_velocity = new PVector(0.,0.,0.);
     
      WanderSide = WanderUp = 0.;
       _forward = new PVector(random(-1,1),random(-1,1),0);
       _forward.normalize();
        m_prevVelocity= new PVector();
        m_prevVelocity.set(_forward);
        m_prevVelocity.mult(maxSpeed);
      
      GenBasis();
    }
    void GenBasis()
    {
       PVector f = new PVector();
       f.set(position);
       f.sub(lposition);
       if ( f.mag()>0.0001f)
       {
         f.normalize();
         _forward.set(f);
       }       
       _side = up.cross(_forward); 
       _side.normalize();
    }
    PVector velocity() {    return m_velocity; }
 
    PVector _side;
    PVector _forward;
    
    void Draw()
    {   
      float r = radius*0.75;
      triangle(_side.x*r +position.x,_side.y*r + position.y, 
              _forward.x*r*2.+position.x, _forward.y*r*2 + position.y, 
              -_side.x*r+position.x,-_side.y*r + position.y);
    }
    void Apply( PVector force, float dt )
    {
      // clip force
      PVector newAcc = truncLength(force, maxForce);      
      PVector nvel = new PVector();
      nvel.set( newAcc);
      nvel.mult(dt);
      nvel.add(m_prevVelocity);
      
      nvel =  truncLength(nvel, maxSpeed);
      speed = nvel.mag();
      
      lposition.set(position);
      m_prevVelocity.set(nvel);
      nvel.mult(dt);
      position.add(nvel);
      m_velocity.set(nvel);
      GenBasis();
    }

PVector truncLength( PVector r, float maxv)
{
  PVector res = new PVector();
  res.set(r);
  res.normalize();
  res.mult( min(r.mag(), maxv));
  return res;
}
PVector steerForTargetSpeed (float targetSpeed)
{
    float speedError = targetSpeed - speed;
    speedError = min(max (speedError, -maxForce), +maxForce);
    PVector f = new PVector();
    f.set(_forward);
    f.mult( speedError);
    return f; 
}
PVector Seek (PVector target)
{
    PVector desiredVelocity = new PVector();
    desiredVelocity.set(target);
    desiredVelocity.sub(position);
    desiredVelocity.sub( m_prevVelocity);
    desiredVelocity = truncLength( desiredVelocity, maxForce);
    return desiredVelocity;    
}
PVector Flee(PVector target)
{
    PVector desiredVelocity = new PVector();
    desiredVelocity.set(position);
    desiredVelocity.sub(target);
    desiredVelocity.sub( m_prevVelocity);
    desiredVelocity = truncLength( desiredVelocity, maxForce);
    return desiredVelocity;    
}
float scalarRandomWalk ( float initial, float walkspeed,
                         float minV, float maxV)
{
  float next = initial + random(-walkspeed, walkspeed);
  return min(max(next, minV), maxV);
}
PVector Wander (float dt)
{
   float speed = 12 * dt; // maybe this (12) should be an argument?
   WanderSide = scalarRandomWalk (WanderSide, speed, -1, +1);
   
  PVector f = new PVector();
  PVector f2 = new PVector();
  f.set(_side);
  f.mult(WanderSide);
    return f;
}


}

PVector minV( PVector a, PVector b) { return new PVector( min(a.x,b.x), min(a.y,b.y),0.); }
PVector maxV( PVector a, PVector b) { return new PVector( max(a.x,b.x), max(a.y,b.y),0.); }


class Sensor
{
  PVector[] lend;
  PVector   l0;
  PVector   bmin;
  PVector   bmax;
  
  public Sensor( Boid b)
  {
     l0 = new PVector();
     l0.add(b.position);
   
     lend= new PVector[3];
     lend[0] = new PVector();
     lend[0].set(b._forward);
     lend[0].mult(max(b.speed,1)*4. + b.radius*.5);
     lend[0].add(l0);
  
     lend[1] = new PVector();
     lend[1].set(b._forward);
     lend[1].add(b._side);
     lend[1].mult(max(b.speed,1)*4. + b.radius*.5);
     lend[1].add(l0);
  
     lend[2] = new PVector();
     lend[2].set(b._forward);
     lend[2].sub(b._side);
     lend[2].mult(max(b.speed,1)*4. + b.radius*.5);
     lend[2].add(l0);
   
     
     bmin= minV(minV(minV(l0,lend[0]),lend[1]),lend[2]);
     bmax= maxV(maxV(maxV( l0,lend[0]),lend[1]),lend[2]);
  }
};
boolean Overlap( PVector bmin, PVector bmax, PVector pmin, PVector pmax)
{
    
    return pmax.x > bmin.x && pmin.x < bmax.x &&
            pmax.y > bmin.y && pmin.y < bmax.y;
}
PVector Avoidance( Boid b, Sensor whiskers, PVector[] pts, int startPt)
{
  if ( pts.length<= startPt)
      return new PVector(0.,0.,0.);
   PVector p1 = pts[startPt];
  
   float hitS =1.;
   PVector pdir = new PVector();
   PVector ldir = new PVector();
   PVector hp = new PVector();
   for(int i=startPt; i < pts.length;i++)
  {
    PVector p2 = pts[i];
    PVector pmin= minV(p1,p2);
    PVector pmax= maxV(p1,p2);
    
    if(  Overlap( whiskers.bmin, whiskers.bmax, pmin,pmax))
    {
      // do three points
      float[] s=new float[3];
      for (int j =0; j < 3;j++)
      {
        s[j] =lineIntersects(whiskers.l0,whiskers.lend[j],p1,p2);
        if ( s[j]>0. && s[j]<hitS )
        {
           hitS = s[j];
         
          pdir.set(p2);
           pdir.sub(p1);
           pdir.normalize();
                    
           hp.set(whiskers.lend[j]);
           hp.sub(whiskers.l0);
           
           ldir.set(hp);
           hp.mult(hitS);
           hp.add(whiskers.l0);        
           
        }     
       }
    }
    p1 = p2;
  }
  if ( hitS == 1. )
     return new PVector(0.,0.,0.);
  
   // calc line normal
   // find the colsest point first
   pdir.z=0.;
   PVector force =  pdir.cross(b.up);
   force.normalize();
   // flip depending on side
   float angle = ldir.dot(force);
   if ( angle>0.0f)
   {
      force.mult(-1);
   }
   force.normalize();
   force.mult(b.maxForce*(1 - hitS)*2.);//*(abs(angle)+0.5));
  
  return force;
}
// from http://mathworld.wolfram.com/Line-LineIntersection.html
float lineIntersects( PVector x1, PVector  x2, PVector x3, PVector x4)
{
  PVector a = new PVector();
  PVector b = new PVector();
  PVector c = new PVector(); 
  a.set(x2);
  b.set(x4);
  c.set(x3);
  a.sub(x1);
  b.sub(x3);
  c.sub(x1);

  // find s
  PVector tcb = c.cross(b);
  PVector tab = a.cross(b);
  float s = tcb.dot(tab);
  s *= 1./sq(tab.mag());
  
  return s;
}  
