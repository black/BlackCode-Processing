// Parametric plotting tab

public void plotParametric() {
  GPointsArray array = calcParaPoints(txfParaExprY, txfParaExprX, lblParaStatus);
  if (array != null) {
    plot = getPlot();
    String title = "Parametric Plot\ny = " + txfParaExprY.getText();
    title += "\nx = " + txfParaExprY.getText();
    plot.setTitleText(title);
    plot.setPoints(array);
  }
  //parseScript(txfPolarExpr, lblPolarStatus);
}

public GPointsArray calcParaPoints(GTextField exprY, GTextField exprX, GLabel status) {
  // Test range of x
  Float lowT = parseFloat(txfLowT.getText());
  if (lowT.isNaN()) {
    txfLowT.addStyle(G4P.FOREGROUND, Color.BLACK);
    txfLowT.addStyle(G4P.BACKGROUND, Color.PINK);
  }
  Float highT = parseFloat(txfHighT.getText());
  if (highT.isNaN()) {
    txfHighT.addStyle(G4P.FOREGROUND, Color.BLACK);
    txfHighT.addStyle(G4P.BACKGROUND, Color.PINK);
  }

  // If limits are invalid then return nothing
  if (lowT.isNaN() || highT.isNaN())
    return null;

  // Parse y parametric equation
  Script scriptY = new Script(exprY.getText());
  try {
    scriptY.parse_();
  } 
  catch (ScriptException e) {
    handleException(status, exprY, e);
    return null;
  }
  // Parse x parametric equation
  Script scriptX = new Script(exprX.getText());
  try {
    scriptX.parse_();
  } 
  catch (ScriptException e) {
    handleException(status, exprX, e);
    return null;
  }


  // Parsed OK
  GPoint[] points = new GPoint[sdrParaSteps.getValueI()];
  println(points.length);

  float range = abs(highT - lowT);
  float deltaT = range / (points.length - 1);

  for (int i = 0; i < points.length; i++) {
    float x, y;
    float t = lowT + i * deltaT;

    scriptY.storeVariable("t", t);
    try {
      Result result = scriptY.evaluate_();
      y = result.answer.toFloat_();
    } 
    catch (ScriptException e) {
      handleException(status, exprY, e);
      return null;
    }
    scriptX.storeVariable("t", t);
    try {
      Result result = scriptX.evaluate_();
      x = result.answer.toFloat_();
    } 
    catch (ScriptException e) {
      handleException(status, exprX, e);
      return null;
    }
    points[i] = new GPoint(x, y);
  }
  return new GPointsArray(points);
}