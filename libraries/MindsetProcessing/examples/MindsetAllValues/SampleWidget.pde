class SampleWidget {

  private int[] samples;
  private int index;
  private int maximum;
  private float average;
  private boolean autoScale;
  private int maxValue;
  
  public SampleWidget(int nSamples, boolean autoScale, int maxValue) {
    this.autoScale = autoScale;
    this.maxValue = maxValue;
    samples = new int[nSamples];

    index = 0;
  }

  public void add(int v) {
    samples[index] = v;

    index = (index+1)%samples.length;
  }

  void update() {
    maximum = -1000000;
    average = 0;
    for (int i = 0; i < samples.length; i++) {
      average += samples[i];
      if ( samples[i] > maximum ) {
        maximum = samples[i];
      }
    }
  }

  public void draw(int x, int y, int width, int height) {
    update();
    pushStyle();
    
    int max = maximum;
    if ( max == 0 ) {
      max = 100;
    }
    if (!autoScale) {
      max = maxValue;
    }
    
    float w = width*1.0/samples.length;
    noFill();
    stroke(255);
    for (int i = 0; i < samples.length; i++) {
      line(x+i*w, y+height, x+i*w, y+height-samples[i]*height/max);
    }
    popStyle();
  }
}

