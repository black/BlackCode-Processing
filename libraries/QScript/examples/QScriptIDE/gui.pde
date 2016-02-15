// GUI tab
public void script_change(GTextArea source, GEvent event) {
  if (event == GEvent.CHANGED) {
    txaOutput.setText("");
    lblStatus.setText("");
    txaScript.clearStyles();
    codeChanged = true;
  }
}

public void delay_change(GCustomSlider source, GEvent event) {
  if (script != null) {
    script.traceDelay(source.getValueI());
  }
}

public void start_click(GButton source, GEvent event) {
  goToMode(RUNNING);
  txaScript.clearStyles();
  if (codeChanged) {
    script.setCode(txaScript.getTextAsArray());
    codeChanged = false;
  }
  lblStatus.setText("");
  output = new StringBuilder();
  setTraceMode(cbxTrace.isSelected());
  txaOutput.setText("");
  clearVars();
  Solver$.evaluate(script);
}

public void stop_click(GButton source, GEvent event) {
  script.stop();
}

public void pause_click(GButton source, GEvent event) {
  script.waitFor(0);
  source.setAlpha(120);
  btnResume.setAlpha(255);
}

public void resume_click(GButton source, GEvent event) {
  script.resume();
  source.setAlpha(120);
  btnPause.setAlpha(255);
}

public void trace_click(GCheckbox source, GEvent event) {
  setTraceMode(source.isSelected());
}

public void script1_click(GButton source, GEvent event) {
  if (currMode != RUNNING)
    setScript(1);
}

public void script2_click(GButton source, GEvent event) {
  if (currMode != RUNNING)
    setScript(2);
}

public void script3_click(GButton source, GEvent event) {
  if (currMode != RUNNING)
    setScript(3);
}

// Create all the GUI controls. 
public void createGUI() {
  G4P.messagesEnabled(false);
  G4P.setGlobalColorScheme(GCScheme.BLUE_SCHEME);
  G4P.setCursor(ARROW);
  if (frame != null)
    frame.setTitle("Sketch Window");
  txaScript = new GTextArea(this, 0, 20, 350, 450, G4P.SCROLLBARS_BOTH);
  txaScript.setPromptText("Enter your script");
  txaScript.setOpaque(true);
  txaScript.addEventHandler(this, "script_change");
  txaVars = new GTextArea(this, 350, 20, 250, 210, G4P.SCROLLBARS_BOTH);
  txaVars.setOpaque(true);
  txaOutput = new GTextArea(this, 350, 250, 250, 220, G4P.SCROLLBARS_BOTH);
  txaOutput.setLocalColorScheme(GCScheme.GREEN_SCHEME);
  txaOutput.setOpaque(false);
  lblScriptTitle = new GLabel(this, 0, 0, 350, 20);
  lblScriptTitle.setText("QScript editor");
  lblScriptTitle.setTextBold();
  lblScriptTitle.setOpaque(true);
  lblOutputTitle = new GLabel(this, 350, 230, 250, 20);
  lblOutputTitle.setText("Output");
  lblOutputTitle.setTextBold();
  lblOutputTitle.setLocalColorScheme(GCScheme.GREEN_SCHEME);
  lblOutputTitle.setOpaque(true);
  lblVariablesTitle = new GLabel(this, 350, 0, 250, 20);
  lblVariablesTitle.setText("Variables");
  lblVariablesTitle.setTextBold();
  lblVariablesTitle.setOpaque(true);
  lblStatusTitle = new GLabel(this, 10, 500, 70, 20);
  lblStatusTitle.setText("Status:");
  lblStatusTitle.setTextBold();
  lblStatusTitle.setLocalColorScheme(GCScheme.RED_SCHEME);
  lblStatusTitle.setOpaque(true);
  lblStatus = new GLabel(this, 80, 500, 510, 20);
  lblStatus.setTextAlign(GAlign.LEFT, GAlign.MIDDLE);
  lblStatus.setText(" ");
  lblStatus.setTextBold();
  lblStatus.setTextItalic();
  lblStatus.setLocalColorScheme(GCScheme.RED_SCHEME);
  lblStatus.setOpaque(true);
  sdrDelay = new GCustomSlider(this, 250, 470, 200, 20, "grey_blue");
  sdrDelay.setLimits(50.0f, 0.0f, 500.0f);
  sdrDelay.setEasing(10.0f);
  sdrDelay.setNumberFormat(G4P.INTEGER, 0);
  sdrDelay.setOpaque(true);
  sdrDelay.addEventHandler(this, "delay_change");
  btnStart = new GButton(this, 10, 470, 50, 20);
  btnStart.setText("Start");
  btnStart.setTextBold();
  btnStart.addEventHandler(this, "start_click");
  btnStop = new GButton(this, 70, 470, 50, 20);
  btnStop.setText("Stop");
  btnStop.setTextBold();
  btnStop.addEventHandler(this, "stop_click");

  btnPause = new GButton(this, 460, 470, 60, 20);
  btnPause.setText("Pause");
  btnPause.setTextBold();
  btnPause.addEventHandler(this, "pause_click");

  btnResume = new GButton(this, 530, 470, 60, 20);
  btnResume.setText("Resume");
  btnResume.setTextBold();
  btnResume.addEventHandler(this, "resume_click");

  cbxTrace = new GCheckbox(this, 130, 470, 70, 20);
  cbxTrace.setIconAlign(GAlign.RIGHT, GAlign.MIDDLE);
  cbxTrace.setText("Trace");
  cbxTrace.setTextBold();
  cbxTrace.setOpaque(true);
  cbxTrace.addEventHandler(this, "trace_click");
  lblDelayTitle = new GLabel(this, 210, 470, 50, 20);
  lblDelayTitle.setText("Delay");
  lblDelayTitle.setTextBold();
  lblDelayTitle.setOpaque(true);
  btnScript1 = new GButton(this, 10, 530, 180, 30);
  btnScript1.setText("Solve Quadratic Equation");
  btnScript1.setTextBold();
  btnScript1.setLocalColorScheme(GCScheme.ORANGE_SCHEME);
  btnScript1.addEventHandler(this, "script1_click");
  btnScript2 = new GButton(this, 210, 530, 180, 30);
  btnScript2.setText("Fibonacci Series");
  btnScript2.setTextBold();
  btnScript2.setLocalColorScheme(GCScheme.ORANGE_SCHEME);
  btnScript2.addEventHandler(this, "script2_click");
  btnScript3 = new GButton(this, 410, 530, 180, 30);
  btnScript3.setText("Prime Number Sieve");
  btnScript3.setTextBold();
  btnScript3.setLocalColorScheme(GCScheme.ORANGE_SCHEME);
  btnScript3.addEventHandler(this, "script3_click");
}

// Variable declarations 
GTextArea txaScript; 
GTextArea txaVars; 
GTextArea txaOutput; 
GLabel lblScriptTitle; 
GLabel lblOutputTitle; 
GLabel lblVariablesTitle; 
GLabel lblStatusTitle; 
GLabel lblStatus; 
GCustomSlider sdrDelay; 
GButton btnStart; 
GButton btnStop; 
GButton btnPause; 
GButton btnResume; 
GCheckbox cbxTrace; 
GLabel lblDelayTitle; 
GButton btnScript1; 
GButton btnScript2; 
GButton btnScript3;
