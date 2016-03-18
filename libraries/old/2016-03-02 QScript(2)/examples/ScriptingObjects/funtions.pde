public static class ResetFunction extends Operator {

  public ResetFunction(String symbol, int nbrArgs, int priority, int type) {
    super(symbol, nbrArgs, priority, type);
  }

  public Argument resolve(Script script, Token token, Argument[] args,
      Object... objects) throws EvaluationException {
    testForUninitialisedVars(script, args);
    Argument a0 = args[0]; // Helicopter wrapped up in a thing object
    Argument a1 = args[1]; // x position
    Argument a2 = args[2]; // y position
    Argument a3 = args[3]; // angle
    if(a0.isThing() && a1.isNumeric && a2.isNumeric && a3.isNumeric){
      Float px = a1.toFloat_();
      Float py = a2.toFloat_();
      Float ang = a3.toFloat_();
      script.fireEvent(ResetEvent.class, null, a0, new Object[] { a0.getValue(), px, py, ang });
      // Wait 0.2 seconds before continuing
      script.waitFor(0);
      return null;
    }

    // If we get here then the arguments are invalid
    handleInvalidArguments(script, token);
    return null;
  }
}


public static class MoveFunction extends Operator {

  public MoveFunction(String symbol, int nbrArgs, int priority, int type) {
    super(symbol, nbrArgs, priority, type);
  }

  public Argument resolve(Script script, Token token, Argument[] args,
      Object... objects) throws EvaluationException {
    testForUninitialisedVars(script, args);
    Argument a0 = args[0]; // Helicopter wrapped up in a thing object
    Argument a1 = args[1]; // distance to move
    if(a0.isThing() && a1.isNumeric){
      Float ang = a1.toFloat_();
      script.fireEvent(MoveEvent.class, null, a0, new Object[] { a0.getValue(), ang });
      // Wait until told by the user to resume
      script.waitFor(0);
      return null;
    }

    // If we get here then the arguments are invalid
    handleInvalidArguments(script, token);
    return null;
  }

}


public static class TurnFunction extends Operator {

  public TurnFunction(String symbol, int nbrArgs, int priority, int type) {
    super(symbol, nbrArgs, priority, type);
  }

  public Argument resolve(Script script, Token token, Argument[] args, Object... objects) throws EvaluationException {
    testForUninitialisedVars(script, args);
    Argument a0 = args[0]; // Helicopter wrapped up in a thing object
    Argument a1 = args[1]; // angle to turn by
    if(a0.isThing() && a1.isNumeric){
      Float ang = a1.toFloat_();
      script.fireEvent(TurnEvent.class, null, a0, new Object[] { a0.getValue(), ang });
      script.waitFor(0);
      return null;
    }

    // If we get here then the arguments are invalid
    handleInvalidArguments(script, token);
    return null;
  }

}
