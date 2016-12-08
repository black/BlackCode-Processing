import ddf.minim.analysis.*;
import ddf.minim.ugens.*;
import ddf.minim.*;

Minim minim;
AudioInput in;
AudioOutput out;
Oscil wave;
Oscil mod;
FFT  fftin;
FFT fftout;
int feq1 = 16000;
int feq2 = 20;
void setup()
{
  size(512, 400, P3D);
  minim = new Minim(this);
  in = minim.getLineIn();
  out = minim.getLineOut();
  wave = new Oscil( feq1  /*in FREQUENCY HZ*/, 1.0f /*AMPLITUDE*/, Waves.TRIANGLE );
  mod  = new Oscil( feq2, 0.4f, Waves.SINE );
  mod.patch( wave.amplitude );
  wave.patch( out );
}

float centerFrequency = 0;

void draw()
{
  background(0);
  stroke(255);
  fftin = new FFT( in.bufferSize(), in.sampleRate() );
  fftout = new FFT( out.bufferSize(), out.sampleRate() );
  // draw the waveforms so we can see what we are monitoring
  stroke(255, 0, 0); // in  RED
  for (int i = 0; i < in.bufferSize () - 1; i++)
  {
    line( i, 50 + in.left.get(i)*100, i+1, 50 + in.left.get(i+1)*100 );
    line( i, 150 + in.right.get(i)*100, i+1, 150 + in.right.get(i+1)*100 );
  }
  stroke(0, 255, 0); // out GREEN
  for (int i = 0; i < out.bufferSize () - 1; i++)
  {
    line( i, 100 + out.left.get(i)*100, i+1, 100 + out.left.get(i+1)*100 );
    line( i, 250 + out.right.get(i)*100, i+1, 250 + out.right.get(i+1)*100 );
  }

  //----------input--------
  fftin.forward( in.mix );
  stroke(255, 0, 0); // in RED
  for (int i = 0; i < fftin.specSize (); i++)
  {
    // draw the line for frequency band i, scaling it up a bit so we can see it
    line( i, height, i, height- fftin.getBand(i)*100 );
    if (fftin.indexToFreq(i)==feq1) {
      centerFrequency = fftin.indexToFreq(i);
      println(i);
    }
  }
  //----------output--------

  fftout.forward( out.mix );
  stroke(0, 255, 0); // out GREEN
  for (int i = 0; i < fftout.specSize (); i++)
  {
    // draw the line for frequency band i, scaling it up a bit so we can see it
    line( width-i, height-100, width-i, height-100- fftout.getBand(i)*8 );
  }
}

