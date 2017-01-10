ArrayList<PVector> finalWave = new ArrayList();
int n = 3;
int N = 8;
int pn = 1;

void setup() {
  size(400, 400);
  finalWave = createWave(0, 0, 0);
  for (int i=0; i<N; i++) {
    ArrayList<PVector> wave = createWave(2*i+1, 10*N/(i+1), 0); // freq, amp, phase
    finalWave = addWave(wave);
  }
}

void draw() {
  background(-1);  
  for (int i=0; i<N; i++) { 
    ArrayList<PVector> wave = createWave(2*i+1, 10*N/(i+1), 0);
    showWave(wave, i);
  } 
  showWave(finalWave, N+1);
}


ArrayList<PVector> createWave(float freq, float amp, float phase) {
  ArrayList<PVector> wave = new ArrayList();
  for (int i=0; i<width; i++) {
    float ang = map(i, 0, width, 0, 360 );
    float y = amp*sin(radians(ang*freq)+phase);
    wave.add(new PVector(i, y));
  }
  return wave;
}

void showWave(ArrayList<PVector> wave, int h) {
  pushMatrix();
  translate(0, height/2);
  noFill();
  strokeWeight(h/4);
  stroke(#0041FF, 80);
  beginShape();
  for (int i=0; i<wave.size (); i++) {
    PVector P = wave.get(i);
    vertex(P.x, P.y);
  }
  endShape();
  //line(0, 0, width, 0);
  popMatrix();
}

ArrayList<PVector> addWave(ArrayList<PVector> wave) {
  ArrayList<PVector> tempWave = new ArrayList();
  for (int i=0; i<width; i++) {
    PVector w = wave.get(i);
    PVector fw = finalWave.get(i);
    float yy = w.y + fw.y;
    PVector finalVec = new PVector(i, yy);
    tempWave.add(finalVec);
  }
  return tempWave;
}

