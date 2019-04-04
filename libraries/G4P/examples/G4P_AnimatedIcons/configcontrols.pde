/* =================================================================
 Everthing in this tab can be ignored it simply creates the standard
 G4P controls used in this sketch.
 ================================================================== */

GCustomSlider intervalSlider; 
GOption left, right, still;
GLabel time;
PImage sprites;

// Create the standard G4P controls
public void createGUI() {
  sprites = loadImage("spritesheets.png");
  GLabel t0, t1, t2, t3, t4;
  GWindow window = GWindow.getWindow(this, "Animation Sprite Sheets", 10, 10, 640, 600, JAVA2D);
  window.noLoop();
  window.addDrawHandler(this, "win_draw");
  window.loop();
  t0 = new GLabel(this, 10, 11, 435, 20, "ANIMATED ICONS");
  t0.setTextAlign(GAlign.CENTER, null);
  t0.setOpaque(true);
  t1 = new GLabel(this, 10, 31, 150, 20, "GButton");
  t1.setTextAlign(GAlign.CENTER, null);
  t1.setOpaque(true);
  t2 = new GLabel(this, 240, 31, 100, 20, "GLabel");
  t2.setTextAlign(GAlign.CENTER, null);
  t2.setOpaque(true);
  t3 = new GLabel(this, 10, 230, 205, 20, "GCheckbox");
  t3.setTextAlign(GAlign.CENTER, null);
  t3.setOpaque(true);
  t4 = new GLabel(this, 240, 230, 205, 20, "GOption");
  t4.setTextAlign(GAlign.CENTER, null);
  t4.setOpaque(true);

  time = new GLabel(this, 10, 170, 150, 20, "0 seconds");
  time.setTextAlign(GAlign.CENTER, null);

  intervalSlider = new GCustomSlider(this, 240, 165, 205, 30, "blue18px");
  intervalSlider.setLimits(100, 300, 25);
  intervalSlider.setNbrTicks(11);
  intervalSlider.setShowTicks(true);
  intervalSlider.setEasing(20);
  intervalSlider.setNumberFormat(G4P.INTEGER, 0);
  intervalSlider.setValue(100);

  GToggleGroup og0 = new GToggleGroup();
  left = new GOption(this, 360, 55, 80, 30, "Run left");
  right = new GOption(this, 360, 85, 80, 30, "Run right");
  still = new GOption(this, 360, 115, 80, 30, "Stand still");
  og0.addControls(left, right, still);
  left.setSelected(true);
}

public void win_draw(PApplet appc, GWinData data) {
  appc.background(sprites);
  appc.noLoop();
}