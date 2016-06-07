

// Tail.pde

class Tail
{
  PVector[] pts = null;
  float segLength = 0.02f;
  PVector bmin;
  PVector bmax;

  public Tail(PVector pos, int NumPoints, float segL)
  {
    pts = new PVector[NumPoints];
    segLength = segL;
     for (int i = 0; i < pts.length;i++)
        pts[i]= new PVector(pos.x+(float)i, pos.y,pos.z);
        
     bmin = new PVector(0,0,0);
     bmax = new PVector(0,0,0);
  }
  void grow()
  {
    PVector np = new PVector();
    np.set(pts[pts.length-1]);
    np.x += segLength;
    pts = (PVector[])append(pts,np);
  }
  void AddPoint( PVector p, PVector pos )
  {
    if ( pts[pts.length-1].dist(p)>segLength)
    {
      grow();
      pts[pts.length-1].set(p);
    }
    Update( pos);
  }

  void shrink()
  {
    if ( pts.length>1)
       pts = (PVector[])shorten(pts);
  }
  boolean Empty() { return pts.length==1;}
  
  void Update( PVector head )
  {
    pts[0].set(head);
    bmin.set(pts[0]);
    bmax.set(pts[0]);
    
   
    for (int i = 1; i < pts.length;i++)
    {
      PVector d = new PVector();
      d.set( pts[i]);
      d.sub( pts[i-1]);
      d.normalize();
      d.mult( segLength);
      d.add(pts[i-1]);
      pts[i].set(d);
      
      bmin =minV(bmin, pts[i]);
      bmax =maxV(bmax, pts[i]);
    }
  }
  void Draw()
  {
    noFill();
    beginShape();
    for ( PVector p : pts)
      vertex(p.x,p.y);
     endShape();     
  }
}
