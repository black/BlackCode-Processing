class RecognitionRegion {
  RecognitionArea[] areas;
  float threshold;
  int[] testResults;
  int maxSize;
  int spacing;


  private int indexOfTopEstimate;


  RecognitionRegion(PApplet parent, SVM model, int numClasses, int startX, int startY, int numAreas, int spacing, int maxSize) {
    threshold = 0.5;
    testResults = new int[numAreas];
    this.maxSize = maxSize;
    this.spacing = spacing;
    areas = new RecognitionArea[numAreas];


    /* 
     // GRID-BASED CONVOLUTION
     int i = 0; 
     for(int row = 0; row < numAreas; row++){
     for(int col = 0; col < numAreas; col++){
     int rectSize = maxSize;
     int x = startX + row*spacing;
     int y = startY + col*spacing;
     RecognitionArea area = new RecognitionArea(parent, model, numObjectsToRecognize, x, y, rectSize, rectSize);
     areas[i] = area;
     i++;
     }
     }*/
    // CONCENTRIC CONVOLUTION
    for (int i = 0; i < areas.length; i++) {
      int rectSize = maxSize - (i*spacing*2);  
      int x = startX + i*spacing;
      int y = startY + i*spacing;
      RecognitionArea area = new RecognitionArea(parent, model, numClasses, x, y, rectSize, rectSize);
      areas[i] = area;
    }
  }

  void updateSize(int s) {
    for (int i = 0; i < areas.length; i++) {
      areas[i].updateSize(s);
    }
  }

  void setPosition(int x, int y) {
    for (int i = 0; i < areas.length; i++) {
      int areaSize = maxSize - (i*spacing*2);  
      int areaX = x + i*spacing;
      int areaY = y + i*spacing;

      areas[i].setPosition(areaX, areaY);
    }
  }

  void draw() {
    pushStyle();
    for (int i = 0; i < areas.length; i++) {
      pushMatrix();
      if (i == int(areas.length/2)) {
        stroke(255, 0, 0);
        strokeWeight(3);
      } 
      else {
        stroke(255, 0, 0);
        strokeWeight(0.5);
      }
      //translate(areas[i].area.x, areas[i].area.y);
      areas[i].drawRect();
      popMatrix();
    }
    popStyle();
  }

  int getNumAreas() {
    return areas.length;
  }

  void setThreshold(float t) {
    threshold = t;
  }

  float getThreshold() {
    return threshold;
  }

  int[] test(Capture vid) {
    testResults = new int[areas.length];

    for (int i = 0; i < areas.length; i++) {
      testResults[i] = (int)areas[i].test(vid);
    }

    return testResults;
  }

  int[] getAllTestResults() {
    return testResults;
  }

  int getBestMatch() {
    getTopEstimate(); // for side effect of setting indexOfTopEstimate
    return testResults[indexOfTopEstimate];
  }

  double getTopEstimate() {
    //double[] topEstimates = new double[5];
    double topEstimate = 0;
    for (int i = 0; i < areas.length; i++) {
      if (areas[i].getTopEstimate() > topEstimate) {
        topEstimate  = areas[i].getTopEstimate();
        indexOfTopEstimate = i;
      }
    }
    return topEstimate;
  }

  boolean objectMatched() {
    return getTopEstimate() > threshold;
  }

  PImage getTestImage(int i) {
    return areas[i].getTestImage();
  }

  void drawHog(int i) {
    areas[i].drawHog();
  }
}


class RecognitionArea {
  Rectangle area;
  SVM model;
  float threshold;
  PImage testImage;
  double[] estimates;
  PixelGradientVector[][] pixelGradients; // for drawHog();
  PApplet parent;
  int numClasses;


  RecognitionArea(PApplet parent, SVM model, int numClasses, int x, int y, int w, int h) {
    this.parent = parent;
    area = new Rectangle(x, y, w, h);
    this.model = model;
    this.numClasses = numClasses;

    testImage = createImage(w, h, RGB);
  }

  int test(Capture cam) {
    testImage.copy(cam, area.x, area.y, area.width, area.height, 0, 0, 50, 50);
    testImage.resize(50, 50);
    estimates = new double[numClasses];
    return (int)model.test(buildVector(testImage), estimates);
  }

  boolean objectIsMatched() {
    return (getTopEstimate() > threshold);
  }

  double getTopEstimate() {
    Arrays.sort(estimates);
    return (estimates[estimates.length-1]*estimates[estimates.length-1]);
  }

  double[] getEstimates() {
    return estimates;
  }

  void setPosition(int x, int y) {
    area.x = x;
    area.y = y;
  }

  void updateSize(int s) {
    area.width += s;
    area.height += s;
  }

  void setThreshold(float threshold) {
    this.threshold = threshold;
  }

  float getThreshold() {
    return threshold;
  }

  void drawRect() {
    rect(area.x, area.y, area.width, area.height);
  }

  PImage getTestImage() {
    return testImage;
  }


  float[] buildVector(PImage img) {
    // float[] result = new float[324];


    // resize the images to a consistent size:
    img.resize(50, 50);
    // settings for Histogram of Oriented Gradients
    // (probably don't change these)
    int window_width=64;
    int window_height=128;
    int bins = 9;
    int cell_size = 8;
    int block_size = 2;
    boolean signed = false;
    int overlap = 0;
    int stride=16;
    int number_of_resizes=5;

    // a bunch of unecessarily verbose HOG code
    HOG_Factory hog = HOG.createInstance();
    GradientsComputation gc=hog.createGradientsComputation();
    Voter voter=MagnitudeItselfVoter.createMagnitudeItselfVoter();
    HistogramsComputation hc=hog.createHistogramsComputation( bins, cell_size, cell_size, signed, voter);
    Norm norm=L2_Norm.createL2_Norm(0.1);
    BlocksComputation bc=hog.createBlocksComputation(block_size, block_size, overlap, norm);
    pixelGradients = gc.computeGradients(img, parent);
    Histogram[][] histograms = hc.computeHistograms(pixelGradients);
    Block[][] blocks = bc.computeBlocks(histograms);
    Block[][] normalizedBlocks = bc.normalizeBlocks(blocks);
    DescriptorComputation dc=hog.createDescriptorComputation();  

    return dc.computeDescriptor(normalizedBlocks);
  }


  void drawHog() {

    int threshold = 16;

    pushMatrix();
    pushStyle();
    strokeWeight(0.5);
    scale(2);
    for (int i = 0; i < testImage.height; i+=2) {
      for (int j = 0; j < testImage.width; j+=2) {
        PixelGradientVector v = pixelGradients[i][j];

        pushMatrix();
        translate(j, i);
        rotate(radians(v.getAngle()));
        if (v.getMagnitude() > threshold) {
          stroke(255, 125);
          line(0, 0, 0, 5);
        }
        popMatrix();
      }
    }
    popStyle();
    popMatrix();
  }
}

