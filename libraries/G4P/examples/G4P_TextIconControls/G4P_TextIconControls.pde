/**
 This sketch is used show controls that use a mixture of
 text and icons i.e. GButton, GLabel, GCheckbox and GOption.
 
 This sketch allows you to experiment with 
 
 + text styling (bold, italic)
 + text alignment
 + icon alignment
 + icon positioning
 
 It also uses the droplist control extensively.

 for Processing V3
 (c) 2016 Peter Lager
 */

import g4p_controls.*;

GToggleGroup tg = new GToggleGroup();
GOption opt0, opt1, opt2;
GCheckbox cbx0;
GLabel lbl0, lbl1;
GButton btn0, btn1;

// List of puppet controls we can test tezt and alignment options
ArrayList<GTextIconBase> puppets = new ArrayList<GTextIconBase>();

int bgcol = 240;

public void settings() {
  size(560, 490);
}

public void setup() {
  G4P.setCursor(CROSS);
  makePuppets();
  makeTextIconConfigControls();
  syncPuppets();
}

public void draw() {
  background(bgcol);
  fill(227, 230, 255);
  noStroke();
  rect(width - 190, 0, 200, height);
}

public void handleSliderEvents(GValueControl slider, GEvent event) { 
  if (slider == sdrBack)
    bgcol = slider.getValueI();
}

public void handleKnobEvents(GValueControl knob, GEvent event) { 
  if (knbAngle == knob)
    for (GTextBase control : puppets)
      control.setRotation(knbAngle.getValueF(), GControlMode.CENTER);
}

public void handleButtonEvents(GButton button, GEvent event) { 
  if (button.tagNo >= 1000) {
    for (GTextBase control : puppets)
      control.setLocalColorScheme(button.tagNo - 1000);
  }
}

public void handleToggleControlEvents(GToggleControl option, GEvent event) {
  if (option == optPlain)
    for (GTextIconBase control : puppets)
      control.setTextPlain();
  else if (option == optBold)
    for (GTextIconBase control : puppets) {
      control.setTextPlain();
      control.setTextBold();
    } else if (option == optItalic)
    for (GTextIconBase control : puppets) {
      control.setTextPlain();
      control.setTextItalic();
    } else if (option == optItalic)
    for (GTextIconBase control : puppets) {
      control.setTextPlain();
      control.setTextItalic();
    } else if (option == optBoldItalic)
    for (GTextIconBase control : puppets) {
      control.setTextBold();
      control.setTextItalic();
    } else if (option == cbxOpaque)
    for (GTextIconBase control : puppets)
      control.setOpaque(cbxOpaque.isSelected());
}

public void handleDropListEvents(GDropList list, GEvent event) {
  GAlign na = GAlign.getFromText(list.getSelectedText());
  if (list == textH)
    for (GTextIconBase control : puppets)
      control.setTextAlign(na, null);
  else if (list == textV)
    for (GTextIconBase control : puppets)
      control.setTextAlign(null, na);
  else if (list == iconH)
    for (GTextIconBase control : puppets)
      control.setIconAlign(na, null);
  else if (list == iconV)
    for (GTextIconBase control : puppets)
      control.setIconAlign(null, na);
  else if (list == iconP) {
    for (GTextIconBase control : puppets)
      control.setIconPos(na);
  }
}

// Change the alignment and icon positions to match the items
// currently selected in the drop-down lists
public void syncPuppets() {
  GAlign textAlignH = GAlign.getFromText(textH.getSelectedText());
  GAlign textAlignV = GAlign.getFromText(textV.getSelectedText());
  GAlign iconAlignH = GAlign.getFromText(iconH.getSelectedText());
  GAlign iconAlignV = GAlign.getFromText(iconV.getSelectedText());
  GAlign iconPos = GAlign.getFromText(iconP.getSelectedText());
  for (GTextIconBase control : puppets) {
    control.setTextAlign(textAlignH, textAlignV);
    control.setIconAlign(iconAlignH, iconAlignV);
    control.setIconPos(iconPos);
  }
}

// Make all the dummy configurable controls using control specific default alignments etc.
public void makePuppets() {
  lbl0 = new GLabel(this, 20, 120, 150, 350);
  String[] lines = loadStrings("tisample.txt");
  String text = join(lines, '\n');
  lbl0.setText(text);
  ;
  lbl1 = new GLabel(this, 200, 120, 140, 100, "Labels can also have icons like this one");
  lbl1.setIcon("bugtest.png", 1, null, null);
  // Buttons
  btn0 = new GButton(this, 200, 230, 150, 80, "Buttons always have an opaque background");
  btn1 = new GButton(this, 200, 320, 150, 150, "As well as text buttons can also have icons");
  btn1.setIcon("smile.png", 3, null, null);
  // Options
  opt0 = new GOption(this, 16, 10, 90, 46, "Option 0");
  opt1 = new GOption(this, 136, 10, 90, 46, "Option 1");
  opt2 = new GOption(this, 256, 10, 90, 46, "Option 2");
  tg.addControls(opt0, opt1, opt2);
  opt0.setSelected(true);
  // Checkbox
  cbx0 = new GCheckbox(this, 130, 66, 100, 50, "Tick box");
  // Add them to a list so it is easier to apply changes
  puppets.add(lbl0);
  puppets.add(lbl1);
  puppets.add(btn0);
  puppets.add(btn1);
  puppets.add(opt0);
  puppets.add(opt1);
  puppets.add(opt2);
  puppets.add(cbx0);
  puppets.add(opt0);
  puppets.add(opt1);
  puppets.add(opt2);
}