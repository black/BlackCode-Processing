public class MouseMoveAgent extends MouseAgent {
  DOF2Event event, prevEvent;
  public MouseMoveAgent(AbstractScene scn, String n) {
    super(scn, n);
    // agents creation registers it at the inputHandler.
    // we unregister it here, keeping the default mouse agent
    inputHandler().unregisterAgent(this);
    // while camera rotation requires no mouse button press:
    eyeProfile().setBinding(DOF2Action.ROTATE); // -> MouseEvent.MOVE
    // camera translation requires a mouse left button press:
    eyeProfile().setBinding(LEFT, DOF2Action.TRANSLATE); // -> MouseEvent.DRAG
    // Disable center and right button camera actions (inherited from MouseAgent):
    eyeProfile().setBinding(DOF2Event.META, RIGHT, null);
    eyeProfile().setBinding(DOF2Event.ALT, CENTER, null);
  }
  public void mouseEvent(processing.event.MouseEvent e) {
    //don't even necessary :P
    //if( e.getAction() == processing.event.MouseEvent.MOVE || e.getAction() == processing.event.MouseEvent.DRAG) {
    event = new DOF2Event(prevEvent, e.getX() - scene.originCorner().x(), e.getY() - scene.originCorner().y(), e.getModifiers(), e.getButton());
    handle(event);
    prevEvent = event.get();
    //}
  }
}
