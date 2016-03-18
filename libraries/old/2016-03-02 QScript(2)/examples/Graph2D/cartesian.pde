// Cartesian plotting tab

public void plotCartesian() {
  GPointsArray array = calcCartPoints(txfCartExpr, lblCartStatus);
  if (array != null) {
    plot = getPlot();
    String title = "Cartesian Plot\ny = " + txfCartExpr.getText();
    plot.setTitleText(title);
    plot.setPoints(array);
  }
  //parseScript(txfPolarExpr, lblPolarStatus);
}

public GPointsArray calcCartPoints(GTextField expr, GLabel status) {
  // Test range of x
  Float lowX = parseFloat(txfLowX.getText());
  if (lowX.isNaN())
    txfLowX.setLocalColorScheme(G4P.RED_SCHEME);
  Float highX = parseFloat(txfHighX.getText());
  if (highX.isNaN())
    txfHighX.setLocalColorScheme(G4P.RED_SCHEME);
  // If limits are invalid then return nothing
  if (lowX.isNaN() || highX.isNaN())
    return null;

  Script script = new Script(expr.getText());
  try {
    script.parse_();
  } 
  catch (ScriptException e) {
    handleException(lblCartStatus, expr, e);
    return null;
  }
  // Parsed OK
  GPoint[] points = new GPoint[round(gcw)];
  float range = abs(highX - lowX);
  float deltaX = range / (gcw - 1);
  lowX = min(lowX, highX);

  for (int i = 0; i < points.length; i++) {
    float x = lowX + i * deltaX;
    script.storeVariable("x", x);
    float y;
    try {
      Result result = script.evaluate_();
      y = result.answer.toFloat_();
    } 
    catch (ScriptException e) {
      handleException(lblCartStatus, expr, e);
      return null;
    }
    points[i] = new GPoint(x, y);
  }
  return new GPointsArray(points);
}