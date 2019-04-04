/*
 Demonstrates the use of the GView control to create a 3D view of 
 a toroid / helix.
 
 This sketch is based on a very old Processing example created 
 by Ira Greenberg.
 
 
 for Processing V3
 (c) 2018 Peter Lager
 
 */

import g4p_controls.*;

GLabel lblSegs, lblERad, lblPts, lblLRad;
GCustomSlider sdrSegs, sdrERad, sdrPts, sdrLRad;
GCheckbox cbxWire;
GOption optTorroid, optHelix;
GToggleGroup optShape;
GPanel p;

GView view;

Toroid t1;

void setup() {
  size(480, 680, P2D);
  view = new GView(this, 20, 20, width - 40, height - 200, P3D);
  t1 = new Toroid();
  // Create the sliders etc.
  createGUI();
}

void draw() {
  background(255, 200, 255);
  // update and render the toroid inside the view
  // The PGraphics object returned by the view must be cast to PGraphics3D
  t1.update((PGraphics3D)view.getGraphics());
}

// Create the sliders and options
void createGUI() {
  G4P.setGlobalColorScheme(G4P.PURPLE_SCHEME);
  // Create the various GUI components
  int h = height - 150;
  sdrSegs = new GCustomSlider(this, 150, h-5, width - 170, 30, "purple18px");
  sdrSegs.setLimits(60, 3, 60);
  sdrSegs.setNbrTicks(58);
  sdrSegs.setStickToTicks(true);

  sdrPts = new GCustomSlider(this, 150, h + 25, width - 170, 30, "purple18px");
  sdrPts.setLimits(32, 3, 32);
  sdrPts.setNbrTicks(30);
  sdrPts.setStickToTicks(true);

  sdrERad = new GCustomSlider(this, 150, h + 55, width - 170, 30, null);
  sdrERad.setLimits(60.0, 10.0, 100.0);  
  sdrERad.setEasing(20);

  sdrLRad = new GCustomSlider(this, 150, h + 85, width - 170, 30, null);
  sdrLRad.setLimits(140.0, 10.0, 240.0);
  sdrLRad.setEasing(20);

  // Various options
  optTorroid = new GOption(this, width / 4, h + 120, 80, 20, "Toroid?");
  optTorroid.setTextBold();
  optHelix = new GOption(this, width / 2, h + 120, 80, 20, "Helix?");
  optHelix.setTextBold();
  cbxWire = new GCheckbox(this, 3 * width / 4, h + 120, 100, 20, "Wire frame?");
  cbxWire.setTextBold();

  // Torroid / helix option group
  optShape = new GToggleGroup();
  optShape.addControl(optTorroid);
  optShape.addControl(optHelix);
  optTorroid.setSelected(true);

  lblSegs = new GLabel(this, 20, h, 120, 20, "Segment detail");
  lblSegs.setTextBold();
  t1.setSegmentDetail(sdrSegs.getValueI());
  lblPts = new GLabel(this, 20, h + 30, 120, 20, "Ellipse detail");
  lblPts.setTextBold();
  t1.setEllipseDetail(sdrPts.getValueI());
  lblERad = new GLabel(this, 20, h + 60, 120, 20, "Ellipse Radius");
  lblERad.setTextBold();
  t1.setEllipseRadius(sdrERad.getValueF());
  lblLRad = new GLabel(this, 20, h + 90, 120, 20, "Toroid Radius");
  lblLRad.setTextBold();
  t1.setLatheRadius(sdrLRad.getValueF());
}

public void handleSliderEvents(GValueControl slider, GEvent event) {
  if (slider == sdrSegs)
    t1.setSegmentDetail(sdrSegs.getValueI());
  if (slider == sdrPts)
    t1.setEllipseDetail(sdrPts.getValueI());
  if (slider == sdrERad)
    t1.setEllipseRadius(sdrERad.getValueF()); 
  if (slider == sdrLRad)
    t1.setLatheRadius(sdrLRad.getValueF());
}

public void handleToggleControlEvents(GToggleControl option, GEvent event) {
  if (option == cbxWire)
    t1.setIsWire(cbxWire.isSelected());
  if (option == optHelix)
    t1.setIsHelix(true);
  if (option == optTorroid)
    t1.setIsHelix(false);
}
