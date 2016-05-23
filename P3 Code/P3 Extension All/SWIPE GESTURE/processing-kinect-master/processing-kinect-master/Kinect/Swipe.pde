class Swipe
{
  XnVSwipeDetector swipeDetector;
  Server server;

  public Swipe(XnVSessionManager sessionManager, Server server)
  {
    this.server = server;
    swipeDetector = new XnVSwipeDetector();
    swipeDetector.RegisterSwipeLeft(this);
    swipeDetector.RegisterSwipeRight(this);
    sessionManager.AddListener(swipeDetector);
  }

  void onSwipeLeft(float velocity, float angle)
  {
    server.send("SWIPELEFT "+velocity+" "+angle);
  }

  void onSwipeRight(float velocity, float angle)
  {
    server.send("SWIPERIGHT "+velocity+" "+angle);
  }
}
