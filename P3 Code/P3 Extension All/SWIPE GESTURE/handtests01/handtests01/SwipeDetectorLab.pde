class SwipeDetectorLab extends XnVSwipeDetector {
  SwipeDetectorLab() {    
    RegisterSwipeRight(this);
    RegisterSwipeLeft(this);
    RegisterSwipeUp(this);
    RegisterSwipeDown(this);
  }
  
  void onSwipe(float vel, float angle) {
    println("SWIPE v:"+vel+" a:"+angle); 
    
  }
  void onSwipeUp(float vel, float angle) {
    println("SWIPE UP v:"+vel+" a:"+angle);
    
  }
    void onSwipeDown(float vel, float angle) {
    println("SWIPE DOWN v:"+vel+" a:"+angle);
  }
    void onSwipeLeft(float vel, float angle) {
    println("SWIPE LEFT v:"+vel+" a:"+angle);
    if(vel>0.2)
    {
      robot.keyPress(KeyEvent.VK_A);
      robot.keyRelease(KeyEvent.VK_A);
    }
  }
    void onSwipeRight(float vel, float angle) {
    println("SWIPE RIGHT v:"+vel+" a:"+angle);
    if(vel>0.2)
    {
      robot.keyPress(KeyEvent.VK_B);
      robot.keyRelease(KeyEvent.VK_B);
    }
  }
}

