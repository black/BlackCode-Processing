import ddf.minim.*;
import ddf.minim.ugens.*;

Minim       minim;
AudioOutput out;
Oscil       wave;

void setup() {
  size(512, 200, P3D); 
  minim = new Minim(this);   
  out = minim.getLineOut();  
  wave = new Oscil( 440, 1, Waves.SINE );// create a sine wave Oscil, set to 440 Hz, at 0.5 amplitude
  wave.patch( out );// patch the Oscil to the output
  wave.setWaveform( Waves.SQUARE );
}

void draw() {
  background(0);
  stroke(255);
  strokeWeight(1);

  // draw the waveform of the output
  for (int i = 0; i < out.bufferSize () - 1; i++) {
    line( i, 50  - out.left.get(i)*50, i+1, 50  - out.left.get(i+1)*50 );
    line( i, 150 - out.right.get(i)*50, i+1, 150 - out.right.get(i+1)*50 );
  }

  // draw the waveform we are using in the oscillator
  stroke( 128, 0, 0 );
  strokeWeight(4);
  for ( int i = 0; i < width-1; ++i ) {
    point( i, height/2 - (height*0.49) * wave.getWaveform().value( (float)i / width ) );
  }
}

//void mouseMoved() {
//  // usually when setting the amplitude and frequency of an Oscil
//  // you will want to patch something to the amplitude and frequency inputs
//  // but this is a quick and easy way to turn the screen into
//  // an x-y control for them.
//
//  float amp = map( mouseY, 0, height, 1, 0 );
//  wave.setAmplitude( amp );
//
//  float freq = map( mouseX, 0, width, 110, 880 );
//  wave.setFrequency( freq );
//}

