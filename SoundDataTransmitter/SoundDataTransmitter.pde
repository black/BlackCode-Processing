import ddf.minim.*;
import ddf.minim.ugens.*;

Minim       minim;
AudioOutput out;
Oscil       wave;

void setup() {
  size(500, 200, P3D);
  minim = new Minim(this);
  out = minim.getLineOut();
  wave = new Oscil( 10000, 0.5f, Waves.SINE ); 
  wave.patch( out );
  wave.setWaveform( Waves.SINE );
  wave.setAmplitude( 1); //1 - 0
  wave.setFrequency( 10000 ); //110-880
}

void draw() {
  background(-1);
  stroke(0);  
  for (int i = 0; i < out.bufferSize () - 1; i++) {
    line( i, 50  - out.left.get(i)*50, i+1, 50  - out.left.get(i+1)*50 );
    line( i, 150 - out.right.get(i)*50, i+1, 150 - out.right.get(i+1)*50 );
  }
}

void keyPressed() {
  if (key==' ') {
    //send data
  }
}

