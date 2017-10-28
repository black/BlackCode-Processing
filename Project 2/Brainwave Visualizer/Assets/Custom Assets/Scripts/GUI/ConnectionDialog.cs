using UnityEngine;
using System.Collections;

public class ConnectionDialog : MonoBehaviour {
  private bool isConnected = false;

  private enum State {
    Disconnected,
    Scanning,
    Connected
  }

  private State state = State.Disconnected;
  private bool enableDemoMode = false;

  private string currentPort = "";

  void OnPortScanStarted(){
    state = State.Scanning;
  }

  void OnPortScanFailed(){
    state = State.Disconnected;
  }

  void OnPortQueryStarted(string portName){
    currentPort = portName;
  }

  void OnPortQueryFailed(string portName){
    Debug.Log("Query of " + portName + " failed");
  }

  void OnPortQuerySuccessful(string portName){
    Debug.Log("Query of " + portName + " successful");
  }

  void OnPortScanSuccessful(){
    Debug.Log("Port scan successful."); 
  }

  void OnHeadsetConnected(string portName){
    currentPort = portName;
    state = State.Connected;
  }

  void OnHeadsetDisconnected(){
    state = State.Disconnected;
  }

  void OnGUI(){
    GUILayout.Label("Time: " + Time.time);

    switch(state){
      case State.Disconnected:
        GUILayout.BeginHorizontal();

        if(GUILayout.Button("Connect")){
          string initialPort = enableDemoMode ? "DemoMode" : "";
          GameHelper.SendMessageToAll("OnRequestPortScan", initialPort, SendMessageOptions.DontRequireReceiver);
        }
      
        enableDemoMode = GUILayout.Toggle(enableDemoMode, " Enable demo mode");

        GUILayout.EndHorizontal();
        
        break;

      case State.Scanning:
        GUILayout.Label("Scanning " + currentPort);
        break;

      case State.Connected:
        GUILayout.BeginHorizontal();
        GUILayout.Label("Connected to " + currentPort);

        GUILayout.Space(20);

        if(GUILayout.Button("Disconnect")){
          GameHelper.SendMessageToAll("OnRequestHeadsetDisconnect", null, SendMessageOptions.DontRequireReceiver);
        }

        GUILayout.EndHorizontal();

        break;
    }
  }
}
