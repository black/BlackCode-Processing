public class ClickButton extends Button2D {
  boolean increase;
  Sensitivity sensitivity;

  public ClickButton(Scene scn, PVector p, PFont font, String t, Sensitivity sens, boolean inc) {
    super(scn, p, font, t);
    increase = inc;
    sensitivity = sens;
  }

  @Override
  public void performInteraction(ClickEvent event) {
    if (event.clickCount() == 1)
      if (increase)
        increaseSensitivity(sensitivity);
      else
        decreaseSensitivity(sensitivity);
  }
}
