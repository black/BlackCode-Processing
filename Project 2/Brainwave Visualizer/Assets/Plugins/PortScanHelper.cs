using UnityEngine;
using System.Collections;
using System.Threading;

public class PortScanHelper : MonoBehaviour {

  private string portName = "";

  private enum Status {
    Idle,
    Scanning,
    Successful,
    Cancelled,
    Failed
  };

  public const float TIMEOUT_INTERVAL = 1.5f;
  
  private Status status = Status.Idle;
  private Thread scanThread;

	// Update is called once per frame
	void Update () {
    if(status == Status.Successful){
      GameHelper.SendMessageToAll("OnPortQuerySuccessful", portName, SendMessageOptions.DontRequireReceiver);
      status = Status.Idle;
    }
    else if(status == Status.Failed){
      GameHelper.SendMessageToAll("OnPortQueryFailed", portName, SendMessageOptions.DontRequireReceiver);
      status = Status.Idle;
    }
    else if(status == Status.Cancelled){
      scanThread.Abort();
      ThinkGear.FreeConnection();
      scanThread = null;

      GameHelper.SendMessageToAll("OnPortQueryFailed", portName, SendMessageOptions.DontRequireReceiver);
      status = Status.Idle;
    }
	}

  void OnRequestPortQuery(string portName){
    this.portName = portName;
    status = Status.Scanning;
    GameHelper.SendMessageToAll("OnPortQueryStarted", portName, SendMessageOptions.DontRequireReceiver);

    if(portName == "DemoMode"){
      status = Status.Successful;
      return;
    }

    if(scanThread == null || (scanThread != null && !scanThread.IsAlive)){
      // fire off the thread to scan the port
      scanThread = new Thread(ScanPort);	
      scanThread.Start(); 
    }
  }

  private void ScanPort(){
    // connect at 9600 baud and using the "packets" stream type
    Debug.Log("Getting connection ID...");
    int handleID = ThinkGear.GetNewConnectionID();

    Debug.Log("Attempting to connect to " + portName + "...");
    int connectStatus = ThinkGear.Connect(portName, 9600, 0);    
    Debug.Log("Connect() returned status " + connectStatus);

    if(connectStatus >= 0){
      Debug.Log("Sleeping thread...");
      Thread.Sleep((int)(TIMEOUT_INTERVAL * 1000.0f));

      Debug.Log("Attempting to read data from the serial port stream...");

      int packetCount = ThinkGear.ReadPackets(-1);

      if(packetCount >= 0){
        Debug.Log("Success!");
        status = Status.Successful;
        return;
      }

      Debug.Log("Connection successful, but headset data read timed out.");
    }
    else {
      // artificially slow down the port scanning process
      // otherwise we get weird behavior when it goes too fast
      Thread.Sleep(5);
    }
    
    ThinkGear.FreeConnection();
    status = Status.Failed;
  }

  // Cleanup a port scan in progress
  void OnRequestPortQueryCancel(){
    status = Status.Cancelled;
  }
}
