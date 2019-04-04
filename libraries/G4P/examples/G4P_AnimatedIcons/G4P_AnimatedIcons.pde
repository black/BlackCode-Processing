/**
 This sketch demonstrates how to use animated icons with
 text-icon controls i.e. GButton, GLabel, GCheckbox and GOption.
 
 Animated icons were introduced with G4P v4.1
 
 Note that a GIcon or GAnimIcon must only be associated with one
 control. If multiple controls, such as GOtion buttons need to use
 the same graphics and animation sequences then use a copy of the
 GAnimIcon. This is done below with the GOtion controls.
 
 for Processing V3
 (c) 2016 Peter Lager
 */

import g4p_controls.*;

// These controls will have animated icons
GButton button;
GLabel label;
GCheckbox checkbox;
GOption option0, option1, option2;

public void setup() {
  size(455, 370);
  createControlsWithAnimatedIcons();
  createGUI();
}

public void draw() {
  background(100, 100, 200);
  fill(200, 200, 255);
  noStroke();
  rect(4, 4, width-9, height-9, 6);
}

public void handleButtonEvents(GButton source, GEvent event) { 
  if (source == button)
    time.setText(millis()/1000 + " seconds");
}

public void handleSliderEvents(GValueControl source, GEvent event) {
  if (source == intervalSlider && event == GEvent.VALUE_CHANGING) {    
    label.getIcon().setInterval(intervalSlider.getValueI());
  }
}

public void handleToggleControlEvents(GToggleControl source, GEvent event) { 
  if (source == left) {
    label.getIcon().setInterval("TO LEFT", intervalSlider.getValueI());
    label.getIcon().animate("TO LEFT");
  } else if (source == right) {
    label.getIcon().setInterval("TO RIGHT", intervalSlider.getValueI());
    label.getIcon().animate("TO RIGHT");
  } else if (source == still) {
    label.getIcon().setInterval("STILL", intervalSlider.getValueI());
    label.getIcon().animate("STILL");
  }
}

public void createControlsWithAnimatedIcons() {
  GAnimIcon ai;
  button = new GButton(this, 10, 60, 150, 100);
  button.setText("How long have we been running?");
  // A 28 frame animation to be played continuously
  ai = new GAnimIcon(this, "watch.png", 7, 4, 150);
  button.setIcon(ai, GAlign.WEST, null, null);
  button.getIcon().animate();

  label = new GLabel(this, 240, 60, 100, 100, "Running Man");
  label.tag = "RUNNER";
  // A 5x3 tiled image with 11 used frames. Frames 0-4 are running to the left, frames
  // 5-9 running to right and frame 10 standing still.
  // Stores 2 animation clips of the man running continuously (looped) and a single 
  // non-looped animation of him standing.
  ai = new GAnimIcon(this, "stickman.png", 5, 3, 100);
  ai.storeAnim("TO LEFT", 0, 4, 100).storeAnim("TO RIGHT", 5, 9, 100).storeAnim("STILL", 10, 10, 100, 1);
  label.setTextAlign(GAlign.CENTER, null);
  label.setIcon(ai, GAlign.NORTH, null, null);
  label.getIcon().animate("TO LEFT");

  checkbox = new GCheckbox(this, 50, 260, 120, 80, "Temperature");
  // A 7 frame animation which runs 0-6 when the control is selected (true) 
  // and 6-0 when deselected (false).
  // This are the default SELECT and DESELECT animation sequences
  ai = new GAnimIcon(this, "thermometer.png", 7, 1, 100);
  checkbox.setIcon(ai, GAlign.EAST, null, null);
  checkbox.setSelected(true);

  GToggleGroup tg = new GToggleGroup();
  // The default animations have been replaced by 2 custom animations 
  // that run continuously.
  ai = new GAnimIcon(this, "campfire.png", 4, 4, 100);
  ai.storeAnim("SELECT", 0, 3, 100);
  ai.storeAnim("DESELECT", 8, 11, 300);
  option0 = new GOption(this, 300, 260, 80, 24, "Eat");
  option0.setIcon(ai, null, null, null);
  option0.setTextAlign(null, GAlign.BOTTOM);
  // IMPORTANT icons cannot be shared by different controls. 
  // Each option must have its own icon so for the remaining
  // option buttons we will use copies of the animated icon.
  option1 = new GOption(this, 300, 290, 80, 24, "Drink");
  option1.setIcon(ai.copy(), null, null, null);
  option1.setTextAlign(null, GAlign.BOTTOM);
  option2 = new GOption(this, 300, 320, 80, 24, "Sleep");
  option2.setIcon(ai.copy(), null, null, null);
  option2.setTextAlign(null, GAlign.BOTTOM);
  // Group the option buttons
  tg.addControls(option0, option1, option2);
  // Must set one of the options true
  option1.setSelected(true);
}