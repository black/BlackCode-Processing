ArrayList<Num> poop = new ArrayList();
int START = 10000;
void setup() {
  size(400, 200, P3D);
  for (int i=1; i<START; i++) {
    poop.add(new Num(i));
  }
}
int j=0;
void draw() {
  background(-1);
  if (j<poop.size()) {
    Num N = poop.get(j);
    // println(N.start);
    N.calculate();
    if (N.temp==1) {
      j++;
    }
  } 
  noFill();
  stroke(0);
  beginShape();
  for (int i=0; i<poop.size (); i++) {
    Num M = poop.get(i); 
    float w = map(M.start, 1, START, 0, width); 
    float h = map(M.step, 0, 4000, 0, height); 
    vertex( w, height-h); 
    if (dist(w, height-h, mouseX, mouseY)<5) {
      fill(0); 
      text(M.step + "  " + M.start, mouseX, mouseY);
    }
  }
  endShape();
}


class Num {
  int start, temp; 
  int step; 
  Num(int start) {
    this.start = start; 
    this.temp = start; 
    step = 0;
  }

  void calculate() { 
    if (temp%2==0 && temp>=1) {
      temp = temp/2; 
      step++;
    } else if (temp%2!=0 && temp>=1) {
      temp = temp*3+1; 
      step++;
    }
  }
}

