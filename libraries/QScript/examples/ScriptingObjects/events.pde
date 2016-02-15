public static class ResetEvent extends ScriptEvent {

  public ResetEvent(Script script, ErrorType etype, int lineNo, int pos, int width, Object[] extra) {
    super(script, etype, lineNo, pos, width, extra);
    message = Messages.build("Reset to position [{0}, {1}] facing {2} degrees", extra[1], extra[2], extra[3]);
  }
}


public static class MoveEvent extends ScriptEvent {

  public MoveEvent(Script script, ErrorType etype, int lineNo, int pos, int width, Object[] extra) {
    super(script, etype, lineNo, pos, width, extra);
    message = Messages.build("Moving {0} pixels", extra[1]);
  }
}


public static class TurnEvent extends ScriptEvent {

  public TurnEvent(Script script, ErrorType etype, int lineNo, int pos, int width, Object[] extra) {
    super(script, etype, lineNo, pos, width, extra);
    message = Messages.build("Turning {0} degrees", extra[1]);
  }
}

