import ddf.minim.*;
import ddf.minim.ugens.*;

Minim         minim;
AudioOutput   out; 
Oscil       wave;
void setup() {
  size(512, 200, P3D); 
  minim = new Minim(this); 
  out = minim.getLineOut();
  wave = new Oscil( 440, 0.5f, Waves.SINE ); 
  wave.patch( out );
}

void draw() {
  background(0); 
  stroke(255); 
  for (int i = 0; i < out.bufferSize () - 1; i++)
  {
    line(i, 50  + out.left.get(i)*50, i+1, 50  + out.left.get(i+1)*50);
    line(i, 150 + out.right.get(i)*50, i+1, 150 + out.right.get(i+1)*50);
  }
}

