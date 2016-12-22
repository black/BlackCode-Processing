ArrayList<PVector> points = new ArrayList();
ArrayList<PVector> centers = new ArrayList();

void setup() {
  size(400, 300);
}

void draw() {
  background(-1); 
  strokeWeight(4);  
  if (centers.size()>0) {
    for (int i=0; i<centers.size (); i++) {
      PVector C = centers.get(i); 
      fill(0, 10);
      noStroke(); 
      ellipse(C.x, C.y, 2*C.z, 2*C.z);
      //      for (int j=0; j<centers.size (); j++) {
      //        PVector M = centers.get(j);
      //        if (M.dist(C)<50) line(C.x, C.y, M.x, M.y);
      //      }   
      stroke(0, 255, 0);
      point(C.x, C.y);
    }
  }  
  strokeWeight(4);
  stroke(0);
  if (points.size()>0)
    for (int i=0; i<points.size (); i++) {
      PVector S = points.get(i);
      point(S.x, S.y);
    }
}

void mousePressed() {
  points.add(new PVector(mouseX, mouseY)); 
  for (int i=0; i<points.size (); i++) {
    PVector I = points.get(i);
    for (int j=i+1; j<points.size (); j++) {
      PVector J = points.get(j); 
      for (int k=j+1; k<points.size (); k++) { 
        PVector K = points.get(k);
        centers.add(find_Center(I, J, K));
      }
    }
  }
}

