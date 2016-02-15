import remixlab.bias.core.*;

public enum ClickAction implements Action<GlobalAction> {
  CHANGE_COLOR(GlobalAction.CHANGE_COLOR), 
  CHANGE_STROKE_WEIGHT(GlobalAction.CHANGE_STROKE_WEIGHT);

  @Override
  public GlobalAction referenceAction() {
    return act;
  }

  @Override
  public String description() {
    return "A simple click action";
  }

  @Override
  public int dofs() {
    return 0;
  }

  GlobalAction act;

  ClickAction(GlobalAction a) {
    act = a;
  }
}
