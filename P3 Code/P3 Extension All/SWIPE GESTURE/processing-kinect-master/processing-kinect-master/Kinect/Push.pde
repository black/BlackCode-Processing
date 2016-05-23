class Push 
{
  XnVPushDetector pushDetector;
  Server server;

  public Push(XnVSessionManager sessionManager, Server server)
  {
    this.server = server;
    pushDetector = new XnVPushDetector();
    pushDetector.RegisterPush(this);
    sessionManager.AddListener(pushDetector);
  }

  void onPush(float velocity, float angle)
  {
    server.send("PUSH "+velocity+" "+angle);
  }

}
