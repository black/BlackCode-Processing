/**
 * Mandelbrot Explorer 
 * This is an advanced example of using multiple windows. 
 * This sketch
 * 1) uses the double data type to give higher magnification
 *    (~3x10^13) of the Mandelbrot. 
 * 2) creats separate threads to perform the complex calculations 
 *    and create the images
 * 3) demonstrates how to create a class to hold the Mandelbrot 
 *    data for each plot
 * 
 * for Processing V3
 * 
 * Copyright (c) 2019 Peter Lager
 */

import g4p_controls.*;

// You can experiment with these values to change the color range
final int MAX_NBR_COLORS = 400;
final int MAX_COLOR_LOOPS = 3;
final int MAX_ITERATE = MAX_NBR_COLORS * MAX_COLOR_LOOPS;

// Define the maximum width and height of a plot. Must be square 
// for correct aspect ratio
final int MAX_WIDTH = 400;
final int MAX_HEIGHT = MAX_WIDTH;

// The colors to be used in the plot
int[] colors = new int[MAX_ITERATE];

// The position for the new new window
int winPosX = 100, winPosY = 100;

// The data used for the main sketch display of the full 
// Mandelbrot set
MandelbrotData fullSet;

void settings() {
  size(MAX_WIDTH, MAX_HEIGHT + 120);
}

void setup() {
  createColours();
  createInstructions();
  fullSet = new MandelbrotData(-2d, -1.25d, 0.5d, 1.25d, MAX_WIDTH, MAX_HEIGHT);
  new Thread(fullSet).start();
  registerMethod("mouseEvent", this);
}

void createInstructions() {
  GLabel lblTitle = new GLabel(this, 10, MAX_HEIGHT + 4, MAX_WIDTH - 20, 20, "MANDELBROT EXPLORER");
  lblTitle.setTextAlign(GAlign.CENTER, GAlign.MIDDLE);
  lblTitle.setTextBold();
  GLabel lblText = new GLabel(this, 10, MAX_HEIGHT + 24, MAX_WIDTH - 20, 100);
  String t = "This image shows the full Mandelbrot set. Drag a "
    + "selection box over the area you want to examine "
    + "in greater detail and it will be shown in a new "
    + "window. You can repeat this on the new window to "
    + "zoom further or on any window to view other areas.";
  lblText.setText(t);
  lblText.setTextAlign(GAlign.JUSTIFY, GAlign.TOP);
  lblText.setTextBold();
}

void draw() {
  background(200, 200, 255);
  renderPlot(this, fullSet);
}

void mouseEvent(MouseEvent event) {
  processMouseEvent(this, fullSet, event);
}

/**
 * Handles mouse events for the main display and any open
 * windows (GWindow objects)
 * 
 * @param appc main sketch of window showing the Mandelbrot plot.
 * @param data the data for the Mandelbrot plot (each plot has its own unique data)
 * @param event the mouse event to process
 */
void processMouseEvent(PApplet appc, GWinData data, MouseEvent event) {
  MandelbrotData md = (MandelbrotData)data;
  // Ignore mouse events if still making Mandelbrot image
  if (md.working) {
    return;
  }
  int mX = constrain(appc.mouseX, 0, md.w - 1);
  int mY = constrain(appc.mouseY, 0, md.h - 1);
  switch(event.getAction()) {
  case MouseEvent.PRESS:
    md.msx = md.mex = mX;
    md.msy = md.mey = mY;
    appc.loop();
    appc.frameRate(60);
    break;
  case MouseEvent.RELEASE:
    md.mex = mX;
    md.mey = mY;
    // Make sure the coordinates are top left / bottom left
    int temp;
    if (md.msx > md.mex) {
      temp = md.msx; 
      md.msx = md.mex; 
      md.mex = temp;
    }
    if (md.msy > md.mey) {
      temp = md.msy; 
      md.msy = md.mey; 
      md.mey = temp;
    }
    int deltaX = md.mex - md.msx;
    int deltaY = md.mey - md.msy;
    if (deltaX >= 1 && deltaY >= 1) {
      // Calculate the new Mandelbrot plane coordinates
      double nsx, nex, nsy, ney;
      nsx = dmap((double)md.msx, (double)0, (double)md.w, md.sx, md.ex);
      nex = dmap((double)md.mex, (double)0, (double)md.w, md.sx, md.ex);
      nsy = dmap((double)md.msy, (double)0, (double)md.h, md.sy, md.ey);
      ney = dmap((double)md.mey, (double)0, (double)md.h, md.sy, md.ey);
      makeNewBrotWindow(nsx, nex, nsy, ney);
      md.msx = md.mex = md.msy = md.mey = 0;
      appc.frameRate(1);
    } else if ( (deltaX == 0) ^ (deltaY == 0)) {
      G4P.showMessage(this, "Selection box size must be at least 1x1 pixel.", "Invalid Selection Box", G4P.WARNING);
    }
    break;
  case MouseEvent.DRAG:
    md.mex = mX;
    md.mey = mY;
    break;
  }
}

/**
 * Maps the value in the first range (1) to the equivalent position
 * in the second range (2) 
 */
double dmap(double value, double start1, double stop1, double start2, double stop2) {
  return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
}

/**
 * Attempts to create a window for a new Mandelbrot plot. If the selection 
 * box width or height are less than is MIN_WIDTH and MIN_HEIGHT then this 
 * will be ignored.
 */
void makeNewBrotWindow(double nsx, double nex, double nsy, double ney) {
  GWindow window = null;
  int w, h;
  // Using doubles for greater magnification range
  double mag = 2.5d / (nex-nsx);
  String s = String.format("%.2E", mag);
  String title = "Mandelbrot (x "+s+")";
  float ratio = (float) ((nex-nsx)/(ney-nsy));
  if (ratio > ((float)MAX_WIDTH)/MAX_HEIGHT) {
    w = MAX_WIDTH;
    h = (int)( MAX_HEIGHT / ratio);
  } else {
    h = MAX_HEIGHT;
    w = (int)( MAX_WIDTH * ratio);
  }
  MandelbrotData mm = new MandelbrotData(nsx, nsy, nex, ney, w, h);
  window = GWindow.getWindow(this, title, winPosX, winPosY, w, h, JAVA2D);
  winPosX = (winPosX + w + 20)%(displayWidth - MAX_WIDTH);
  winPosY = (winPosY + 20)%(displayHeight - MAX_HEIGHT);
  window.addData(mm);
  window.addDrawHandler(this, "renderPlot");
  window.addMouseHandler(this, "processMouseEvent");
  window.setActionOnClose(G4P.CLOSE_WINDOW);
  new Thread(mm).start();
}

/**
 * Handles drawing to the windows PApplet area
 * 
 * @param appc main sketch of window showing the Mandelbrot plot.
 * @param data the data for the Mandelbrot plot (each plot has its own unique data)
 */
void renderPlot(PApplet appc, GWinData data) {
  MandelbrotData d = (MandelbrotData)data;
  appc.image(d.pg, 0, 0);
  // Draw selection box
  if (d.msx != d.mex || d.msy != d.mey) {
    appc.strokeWeight(2);
    appc.stroke(255);
    appc.noFill();
    appc.rectMode(CORNERS);
    appc.rect(d.msx, d.msy, d.mex, d.mey);
  }
}

/**
 * Create the colors to be used in the plots.
 */
void createColours() {
  colorMode(HSB, 1.0f, 1.0f, 1.0f);
  float hue = 0.0f;
  float deltaHue = 0.95f / MAX_NBR_COLORS;
  float deltaBrightness = 0.2f / MAX_COLOR_LOOPS;
  for (int ln = 0; ln < MAX_COLOR_LOOPS; ln++) {
    for (int cn = 0; cn < MAX_NBR_COLORS; cn++) {
      colors[ln * MAX_NBR_COLORS + cn] = color(hue + cn * deltaHue, 1.0f, 1.0f - ln * deltaBrightness);
    }
  }
  colorMode(RGB, 256, 256, 256);
  colors[MAX_ITERATE - 1] = color(0);
}
