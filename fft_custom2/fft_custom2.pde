
private FFT fft;
private FFT fft2;
private AudioInput input;
private AudioOutput output;
private Minim minim;

private int SAMPLERATE = 44100;
private int BUFFERSIZE = 512;
void setup() {
  size(300, 200);

  minim = new Minim(this);



  fft = new FFT(BUFFERSIZE, SAMPLERATE);
  //fft.window(FFT.HAMMING);





  input = minim.getLineIn(Minim.STEREO, BUFFERSIZE, SAMPLERATE);
  input.addListener(new Listener());

  output = minim.getLineOut(Minim.STEREO, BUFFERSIZE, SAMPLERATE);
  output.addSignal(new MySignal());
}

class Listener implements AudioListener {
  public void  samples(float[] sample) {
    fft.forward(sample);
  }
  public void samples(float[] left, float[] right) {
    samples(left);
  }
}


private class MySignal implements AudioSignal {
  public void generate(float[] out) {

    fft2 = new FFT(BUFFERSIZE, SAMPLERATE);

    // out = new float[BUFFERSIZE];
    int u=2;
    if (u==1) {//method 1
      //FFT imaginary part used to reconstruct time info i think.
      fft.inverse(out);
    } else {
      //only use real part of the FFT. no time information i think.
      fft2.window(FFT.HAMMING);
      for (int i=0; i<BUFFERSIZE/2; i++) {
        fft2.setBand(i, fft.getBand(i));
      }                
      fft2.inverse(out);
    }

    for (int i=0; i<out.length; i++) {
      out[i]=out[i];
    }
  }

  public void generate(float[] left, float[] right) {
    generate(right);
  }
}
