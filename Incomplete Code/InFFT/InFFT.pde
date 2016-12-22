import processing.serial.*;

import ddf.minim.analysis.*;
import ddf.minim.*;
import ddf.minim.effects.*;
import ddf.minim.ugens.*;


Serial myPort;
Minim       minim;
AudioInput in;
FFT         fft;
IIRFilter filt;

void setup()
{
  size(1200, 500);

  minim = new Minim(this); 
  in = minim.getLineIn(Minim.MONO, 4096, 44100);
  fft = new FFT( in.bufferSize(), in.sampleRate());//in.sampleRate()
  filt = new BandPass(400, 100, in.sampleRate());

  printArray(Serial.list());
  myPort = new Serial(this, Serial.list()[0], 9600);
}

float amp, hamp;
void draw() {
  background(-1);

  fft.forward( in.mix );
  float bw = fft.getBandWidth();
  println(bw);
  stroke(0);
  for (int i = 0; i < 20; i++) {
    // draw the line for frequency band i, scaling it up a bit so we can see it
    float x = map(i, 0, fft.specSize(), 0, width);
    line( x, height, x, height - fft.getBand(i)*8 );
    amp = fft.getBand(i)*4;//fft.getFreq(i);
    if (amp>height/2) { 
      println(amp + "  START MOTOR");
      myPort.write(80);
    } else {
      myPort.write(10);
      println(amp + "  STOP MOTOR");
    }
  }
}

