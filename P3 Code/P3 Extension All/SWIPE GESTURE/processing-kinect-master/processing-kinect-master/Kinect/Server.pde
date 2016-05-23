import net.tootallnate.websocket.WebSocketServer;

class Server extends WebSocketServer
{  
  public Server(int port)
  {
    super(port);
  }
  
  public void send(String msg)
  {
    println("Sending: "+msg);
    try {
    sendToAll(msg);
      } catch (Exception e) {
        e.printStackTrace();
      }     
    }
  
  public void onConnect() 
  {
    println("Connecting");
    } 
    
    public void onClientOpen(WebSocket s)
    {
      println("Got client");
    }
    public void onClientMessage(WebSocket s, String m)
    {
      println("Got message but do not care: "+m);
    }
    public void onClientClose(WebSocket s)
    {
      println("Closing");
    }

}

