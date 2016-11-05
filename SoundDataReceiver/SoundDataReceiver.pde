import ddf.minim.analysis.*;
import ddf.minim.*;
import ddf.minim.effects.*;
import ddf.minim.ugens.*;

Minim minim;
AudioInput in;
FFT fft;

float spectrumScale =100;

IIRFilter filt;

void setup() {
  size(500, 200, P3D);
  minim = new Minim(this);
  in = minim.getLineIn(Minim.STEREO, in.bufferSize(), 4096); //Minim.MONO, buffer_size, sample_rate
  fft = new FFT( in.bufferSize(), 1024 );
  filt = new BandPass(400, 100, 4096);
}

void draw() {
  background(-1);
  stroke(0);
  //  for (int i = 0; i < in.bufferSize () - 1; i++) {
  //    line( i, 50 + in.left.get(i)*50, i+1, 50 + in.left.get(i+1)*50 );
  //    line( i, 150 + in.right.get(i)*50, i+1, 150 + in.right.get(i+1)*50 );
  //  }

  fft.forward(in.mix ); 
  for (int i = 0; i < fft.specSize (); i++) {

    line(i, height, i, height - fft.getBand(i)*spectrumScale);
  }
}


public void findFrequency() {
  double frequency;
  Complex[] cmplx = transformer.transform(doubleData, TransformType.FORWARD);
  double real;
  double im;
  double mag[] = new double[cmplx.length];

  for (int i = 0; i < cmplx.length; i++) {
    real = cmplx[i].getReal();
    im = cmplx[i].getImaginary();
    mag[i] = Math.sqrt((real * real) + (im * im));
  }

  double peak = -1.0;
  int index = -1;
  for (int i = 0; i < cmplx.length; i++) {
    if (peak < mag[i]) {
      index = i;
      peak = mag[i];
    }
  }
  frequency = (sampleRate * index) / audioFrames;
  if (frequency > 700 && frequency < 1600) {
    System.out.print("Index: " + index + ", Frequency: " + frequency + "\n");
  }
}

public void printFreqs() {
  for (int i = 0; i < audioFrames / 4; i++) {
    System.out.println("bin " + i + ", freq: " + (sampleRate * i) / audioFrames);
  }
}

public static void main(String[] args) {
  AudioInput ai = new AudioInput();
  int turns = 10000;
  while (turns-- > 0) {
    ai.readPcm();
    ai.byteToDouble();
    ai.findFrequency();
  }

  // ai.printFreqs();
}

