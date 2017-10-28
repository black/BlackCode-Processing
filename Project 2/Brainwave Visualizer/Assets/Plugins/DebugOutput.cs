using UnityEngine;
using System.Collections;

public class DebugOutput : MonoBehaviour {

  public bool enableDebugOutput = false;

  private bool isConnected = false;
  private DataController d;

  void Start(){
    d = (DataController)GameObject.Find("ThinkGear").GetComponent(typeof(DataController));
  }

  void OnHeadsetConnected(string portName){
    isConnected = true;
  }

  void OnHeadsetDisconnected(){
    isConnected = false;
  }

  void OnGUI(){
    if(isConnected && enableDebugOutput){
      GUILayout.Space(10);
      GUILayout.BeginHorizontal();
      GUILayout.Space(10);
      GUILayout.Label(d.headsetData.ToString());
      GUILayout.EndHorizontal();
    }
  }
}
