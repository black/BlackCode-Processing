public class ClickButton extends Button2D {
  int path;

  public ClickButton(Scene scn, PVector p,  PFont font, int index) {
    this(scn, p, font, "", index);
  }

  public ClickButton(Scene scn, PVector p,  PFont font, String t, int index) {
    super(scn, p, font, t);
    path = index;
  }

  @Override
  public void performInteraction(ClickEvent event) {
    if (event.clickCount() == 1)
      if (path == 0)
        scene.togglePathsVisualHint();
      else
        scene.eye().playPath(path);
  }

  public void display() {
    String text = new String();
    if (path == 0)
      if (scene.pathsVisualHint())
        text = "don't edit camera paths";
      else
        text = "edit camera paths";
    else {
      if (grabsInput(scene.motionAgent())) {
        if (scene.eye().keyFrameInterpolator(path).numberOfKeyFrames() > 1)
          if (scene.eye().keyFrameInterpolator(path).interpolationStarted())
            text = "stop path ";
          else
            text = "play path ";
        else
          text = "restore position ";
      } else {
        if (scene.eye().keyFrameInterpolator(path).numberOfKeyFrames() > 1)
          text = "path ";
        else
          text = "position ";
      }
      text += ((Integer) path).toString();
    }
    setText(text);
    super.display();
  }
}