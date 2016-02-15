public class TouchAgent extends JoystickAgent {
  Scene scene;
  DOF3Event event, prevEvent;
  float histDistance;
  boolean firstPerson;
  
  public TouchAgent(Scene scn, String n) {
    super(scn, n);
    this.enableTracking();
    scene = scn;
    eyeProfile().setBinding(DOF3Action.ROTATE);
    frameProfile().setBinding(DOF3Action.ROTATE);
    eyeProfile().setBinding(DOF3Event.NOMODIFIER_MASK, CENTER, DOF3Action.TRANSLATE_XYZ);
    frameProfile().setBinding(DOF3Event.NOMODIFIER_MASK, CENTER, DOF3Action.TRANSLATE_XYZ);
  }

  public void addTouCursor(MotionEvent tcur) {
    if(tcur.getPointerCount() == 1 || firstPerson || (inputGrabber() instanceof InteractiveFrame && !(inputGrabber() instanceof InteractiveEyeFrame)) ) {
        event = new DOF3Event(prevEvent, 
              tcur.getX(), 
              tcur.getY(), 
              0, 
              DOF3Event.NOMODIFIER_MASK, 
              DOF3Event.NOBUTTON);
    }else {
      event = new DOF3Event(prevEvent, 
              tcur.getX()*-1, 
              tcur.getY()*-1, 
              0, 
              DOF3Event.NOMODIFIER_MASK, 
              DOF3Event.NOBUTTON);
    }

    if( tcur.getPointerCount() == 1) updateTrackedGrabber(event);
    prevEvent = event.get();
  }

  // called when a cursor is moved
  public void updateTouCursor(MotionEvent tcur) {
    event = new DOF3Event(prevEvent, 
    tcur.getX(), 
    tcur.getY(), 
    0, 
    DOF3Event.NOMODIFIER_MASK, 
    DOF3Event.NOBUTTON);
    handle(event);
    prevEvent = event.get();
  }

  // called when a cursor is removed from the scene
  public void removeTouCursor(MotionEvent tcur) {
    event = new DOF3Event(prevEvent, 
    tcur.getX(), 
    tcur.getY(), 
    0, 
    DOF3Event.NOMODIFIER_MASK, 
    DOF3Event.NOBUTTON);
    prevEvent = event.get();
    disableTracking();
    enableTracking();
    histDistance = 0f;
  }

  //tcur.getX(0),tcur.getY(0)
  public void transalateTouCursor(MotionEvent tcur) {
    float distance;
    if (histDistance == 0) {
      distance = 0;
    } else {
      distance = histDistance - sqrt((tcur.getX(0) - tcur.getX(1))*(tcur.getX(0) - tcur.getX(1)) + (tcur.getY(0) - tcur.getY(1))*(tcur.getY(0) - tcur.getY(1)));
    }
  
    if(firstPerson || (inputGrabber() instanceof InteractiveFrame && !(inputGrabber() instanceof InteractiveEyeFrame)) ) {
        event = new DOF3Event(prevEvent, 
                              tcur.getX(0), 
                              tcur.getY(0), 
                              distance * -1, 
                              DOF3Event.NOMODIFIER_MASK, 
                              CENTER);
    } else {
        event = new DOF3Event(prevEvent, 
                              tcur.getX(0) * -1, 
                              tcur.getY(0) * -1, 
                              distance, 
                              DOF3Event.NOMODIFIER_MASK, 
                              CENTER);
    }
    
    histDistance = sqrt((tcur.getX(0) - tcur.getX(1))*(tcur.getX(0) - tcur.getX(1)) + (tcur.getY(0) - tcur.getY(1))*(tcur.getY(0) - tcur.getY(1)));
    handle(event);
    prevEvent = event.get();
  }
  
  public boolean isAsFirstPerson() {
      return firstPerson;
  }

  public void setAsFirstPerson(boolean cameraFirstPerson) {
    firstPerson = cameraFirstPerson;
  }
}

