/* Demonstration file for AULib (Andrew's Utilities library).
* Download and documentation:
*   http://www.imaginary-institute.com/resources/AULib/AULib.php
* Show use of AULib.wave() 
* Version 1 - Andrew - Sept 6, 2014
*/

import AULib.*;

PFont Serif16, Serif32;

void setup() {
  size(750, 1100); 
  Serif16 = loadFont("Serif-16.vlw");  
  Serif32 = loadFont("Serif-32.vlw");
}

float BoxWidth, BoxHeight, GridGap, PicSize, BoxGap, PlotHeight;

void draw() {
  background(255);

  float cycleLength = 240;    // total frames for a round trip
  
  BoxWidth = .8 * (width/4.);            // side of each box 
  GridGap = (width - (4*BoxWidth))/4.0;  // gap between boxes
  PicSize = BoxWidth;                    // size of picture
  BoxGap = .1 * BoxWidth;                // space between picture and plot
  PlotHeight = .4 * BoxWidth;            // height of the plot
  BoxHeight = PicSize + BoxGap + PlotHeight;

  // a runs 0 to 1 across the grid with the mouse
  float a = constrain((mouseX-(GridGap/2.))/(width-(GridGap)), 0, 1);
 
  float t = ((frameCount-1)*1./cycleLength) % 1.;
  String waveName = "";
  int waveType = 0;

  textAlign(LEFT);
  textFont(Serif32);
  text("a="+a, (width/2.)+(BoxWidth), (GridGap/2.)+(BoxHeight/2.));
  textAlign(CENTER);
  textFont(Serif16);
  
  drawWaveBox(AULib.WAVE_SAWTOOTH, "WAVE_SAWTOOTH", t, a, (width/2.)-(BoxWidth/2.), GridGap/2.);
  
  drawWaveBox(AULib.WAVE_TRIANGLE,     "WAVE_TRIANGLE",     t, a, GridGap/2.,                          GridGap/2. +   (BoxHeight+GridGap));
  drawWaveBox(AULib.WAVE_BOX,          "WAVE_BOX",          t, a, GridGap/2. +    (BoxWidth+GridGap),  GridGap/2. +   (BoxHeight+GridGap));
  drawWaveBox(AULib.WAVE_SINE,         "WAVE_SINE",         t, a, GridGap/2. + (2*(BoxWidth+GridGap)), GridGap/2. +   (BoxHeight+GridGap));
  drawWaveBox(AULib.WAVE_COSINE,       "WAVE_COSINE",       t, a, GridGap/2. + (3*(BoxWidth+GridGap)), GridGap/2. +   (BoxHeight+GridGap));
  
  drawWaveBox(AULib.WAVE_BLOB,         "WAVE_BLOB",         t, a, GridGap/2.,                          GridGap/2. + (2*(BoxHeight+GridGap)));
  drawWaveBox(AULib.WAVE_VAR_BLOB,     "WAVE_VAR_BLOB",     t, a, GridGap/2. +    (BoxWidth+GridGap),  GridGap/2. + (2*(BoxHeight+GridGap)));
  drawWaveBox(AULib.WAVE_BIAS,         "WAVE_BIAS",         t, a, GridGap/2. + (2*(BoxWidth+GridGap)), GridGap/2. + (2*(BoxHeight+GridGap)));
  drawWaveBox(AULib.WAVE_GAIN,         "WAVE_GAIN",         t, a, GridGap/2. + (3*(BoxWidth+GridGap)), GridGap/2. + (2*(BoxHeight+GridGap)));
  
  drawWaveBox(AULib.WAVE_SYM_BLOB,     "WAVE_SYM_BLOB",     t, a, GridGap/2.,                          GridGap/2. + (3*(BoxHeight+GridGap)));
  drawWaveBox(AULib.WAVE_SYM_VAR_BLOB, "WAVE_SYM_VAR_BLOB", t, a, GridGap/2. +    (BoxWidth+GridGap),  GridGap/2. + (3*(BoxHeight+GridGap)));
  drawWaveBox(AULib.WAVE_SYM_BIAS,     "WAVE_SYM_BIAS",     t, a, GridGap/2. + (2*(BoxWidth+GridGap)), GridGap/2. + (3*(BoxHeight+GridGap)));
  drawWaveBox(AULib.WAVE_SYM_GAIN,     "WAVE_SYM_GAIN",     t, a, GridGap/2. + (3*(BoxWidth+GridGap)), GridGap/2. + (3*(BoxHeight+GridGap)));
}

void drawWaveBox(int waveType, String name, float t, float a, float left, float top) {
  fill(255);
  stroke(0);
  rect(left, top, BoxWidth, BoxHeight);  // box outline
  fill(0);
  text(name, left+(BoxWidth/2.), top+BoxHeight+(1.2*textAscent()));
  
  fill(225);
  rect(left, top, PicSize, PicSize); // background for the pendulum
  rect(left, top+PicSize+BoxGap, PicSize, PlotHeight); // background for the plot
  
  // the pendulum. stringLen and angleExtent were picked by eye to look good
  float stringLen = PicSize * .8;
  float angleExtent = PI* .18;
  float waveVal = AULib.wave(waveType, t, a); 
  pushMatrix();
    translate(left+(PicSize/2.), top+(PicSize-stringLen)/2.);
    fill(0);
    ellipse(0, 0, 10, 10);
    float angle = waveVal;
    angle = angleExtent * lerp(1, -1, angle);
    rotate(angle);
    line(0, 0, 0, stringLen);
    fill(255, 0, 0);
    ellipse(0, stringLen, 20, 20);
  popMatrix();
  
  // the plot below
  noFill();
  stroke(255, 0, 0);
  strokeWeight(2);
  beginShape();
    for (int i=0; i<(int)BoxWidth; i++) {
      float wt = norm(i, 0, BoxWidth);
      float v = AULib.wave(waveType, wt, a);
      vertex(left+i, map(v, 0, 1, top+BoxHeight, top+BoxHeight-PlotHeight));
    }
  endShape();
  
  // the little ball traveling on the plot
  strokeWeight(1);
  fill(0);
  stroke(0);
  ellipse(left+(t*BoxWidth), map(waveVal, 0, 1, top+BoxHeight, top+BoxHeight-PlotHeight), 15, 15);
}

