class SineInstrument implements Instrument {
  Oscil wave;
  Line  ampEnv;
  int i, j;

  SineInstrument( float frequency ) {
    // make a sine wave oscillator
    // the amplitude is zero because 
    // we are going to patch a Line to it anyway
    wave   = new Oscil( frequency, 0, Waves.SINE );
    ampEnv = new Line();
    ampEnv.patch( wave.amplitude );
  }
  //  void getVal(int i, int j ) {
  //    this.i = i;
  //    this.j = j;
  //  }
  // this is called by the sequencer when this instrument
  // should start making sound. the duration is expressed in seconds.
  void noteOn( float duration) {
    // start the amplitude envelope
    ampEnv.activate( duration, 0.5f, 0 );
    // attach the oscil to the output so it makes sound
    wave.patch( out );
  }

  // this is called by the sequencer when the instrument should
  // stop making sound
  void noteOff() {
    wave.unpatch( out);
  }
}

