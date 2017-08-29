class Polygon {  
  int r, edges;  
  float rY;
  PVector[] p;  
  Polygon(int edges, int r) {   
    this.edges = edges; 
    this.r = r;
    storeVertex(edges);
  }  
  void update(float speed) {    
    PVector bott = new PVector();    
    for (int i = 0; i<p.length; i++) {      
      p[i].rotate(speed);      
      if (p[i].y > bott.y) {        
        bott = p[i];
      }
    }         
    rY = - (bott.y);
  } 

  void display() {   
    pushMatrix();    
    translate(width/2, height+rY);    
    beginShape();      
    for (int i=0; i<p.length; i++) {
      vertex(p[i].x, p[i].y);
    }  
    endShape(CLOSE);    
    popMatrix();
  }

  void storeVertex(int N) {
    p = new PVector[N];
    for (int i=0; i<N; i++) {
      float xm = r*sin(i*TWO_PI/N);
      float ym = r*cos(i*TWO_PI/N);
      p[i] = new PVector(xm, ym);
    }
  }
}

