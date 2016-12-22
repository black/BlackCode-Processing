import ddf.minim.analysis.*;
import ddf.minim.*;
Minim minim;
AudioInput in;
FFT         fft;
float spectrumScale = 4;

void setup() {
  size(800, 400, P3D);
  minim = new Minim(this); 
  in = minim.getLineIn();
  fft = new FFT( in.bufferSize(), in.sampleRate());
}

void draw() {
  background(0);
  float centerFrequency = 0;
  stroke(-1);
  fft.forward( in.mix ); 
  for (int i = 0; i < fft.specSize (); i++) { 
    int m = (int)map(i, 0, fft.specSize(), 0, width);
    if ( m == mouseX ) {
      centerFrequency = fft.indexToFreq(i);
      stroke(255, 0, 0); 
      text(centerFrequency, m, height/2 - 25);
    } else {
      stroke(255);
    }
    line( m, height, m, height - fft.getBand(i)*spectrumScale );
  }
}

