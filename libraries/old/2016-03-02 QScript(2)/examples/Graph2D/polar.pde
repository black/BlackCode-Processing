// Polar plotting tab

public void plotPolar() {
  GPointsArray array = calcPolarPoints(txfPolarExpr, lblPolarStatus);
  if (array != null) {
    plot = getPlot();
    String title = "Polar Plot\nr = " + txfPolarExpr.getText();
    plot.setTitleText(title);
    plot.setPoints(array);
  }
}

public GPointsArray calcPolarPoints(GTextField expr, GLabel status) {
  Script script = new Script(expr.getText());
  try {
    script.parse_();
  } 
  catch (ScriptException e) {
    handleException(lblPolarStatus, expr, e);
    return null;
  }
  // Parsed OK
  float maxA =  PI * sdrPolarHigh.getValueF();
  float deltaA = maxA / sdrPolarSteps.getValueI();
  GPoint[] points = new GPoint[1 + round(maxA/deltaA)];
  float a = 0;
  for (int i = 0; i < points.length; i++) {
    a = i * deltaA;
    script.storeVariable("a", a);
    float r;
    try {
      Result result = script.evaluate_();
      r = result.answer.toFloat_();
    } 
    catch (ScriptException e) {
      handleException(lblPolarStatus, expr, e);
      return null;
    }
    points[i] = new GPoint(r*cos(a), r*sin(a));
  }
  return new GPointsArray(points);
}