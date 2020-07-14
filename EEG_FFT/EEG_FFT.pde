import ddf.minim.analysis.*;
import ddf.minim.*;

Minim       minim;
FFT         fft;
ArrayList<Float> poop = new ArrayList();
int bufferSize=512;
int highest=0;
float[] sample;
void setup() {
  size(800, 300);
  minim = new Minim(this);
  fft = new FFT( bufferSize, 50000); //  FFT(int timeSize,float sampleRate)
}

void draw() {
  background(0);
  stroke(#FF0000); 
  stroke(#009BFF);
  line(width/2, 20, mouseX, 20); 
  sample = new float[poop.size()];
  for (int i=0; i<poop.size(); i++) {
    Float f = (float) poop.get(i);
    sample[i] = f;
  }
  if (poop.size()< bufferSize) {
    float t = map(mouseX, 0, width, 0, bufferSize);
    poop.add(t);
    println(poop.size());
  } else {

    fft.window(FFT.HAMMING);
    fft.forward(sample); // apply fft on sample 
    noFill();
    stroke(0, 255, 0);
    beginShape();
    for (int i = 0; i < fft.specSize(); i++)
    {
      float x = map(i, 0, fft.specSize(), 0, width); 
      vertex(x, height - fft.getBand(i)/50.0);
    }
    endShape();
    poop.remove(0);
  }
}
