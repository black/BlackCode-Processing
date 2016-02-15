import processing.serial.*;


import pt.citar.diablu.processing.mindset.*;



MindSet r;
int attention;
int strength;

int numSamples = 60;
ArrayList attSamples;

void setup() {
  size(512, 512);
  attSamples = new ArrayList();
  r = new MindSet(this, "/dev/cu.MindSet-DevB");
}


void draw() {
  background(0);
  
  
  stroke(255);
  if (attSamples.size() != 0) {
    float w = width*1.0/numSamples;
    int sum = 0;
    for (int i = 0; i< attSamples.size(); i++) {
    
      int v = ((Integer)attSamples.get(i)).intValue();
      sum += v;
      line(i*w, height, i*w, height-v*height/100.0);
    }
    float avg = sum/attSamples.size();
    fill(255, 100);
    noStroke();
    rect(0, height-avg*height/100.0, width, avg*height/100.0);
    stroke(255);
    line(0, height/2, width, height/2);
  }
  
  fill(255, 0, 0, 100);
  stroke(255, 100);
  rect(width/2-50, height-attention*height/100.0, 100, attention*height/100.0);
 
 //signal strength
 stroke(255);
 line(0, 50, 50, 50); 
 fill(0, 255,0);
 rect(12, 50, 25, -(50-strength*50/200.0));
}


void keyPressed() {
  if (key == 'q') {
    r.quit();
  }
}


public void poorSignalEvent(int sig) {
  strength = sig;
  println(sig);
}

public void attentionEvent(int attentionLevel) {
  //println("Attention Level: " + attentionLevel);
  attention = attentionLevel;
  attSamples.add(new Integer(attention));
  if (attSamples.size() > numSamples) {
    attSamples.remove(0);
  }
}

   
