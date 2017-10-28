using UnityEngine;
using System.Collections;

public class GameHelper : MonoBehaviour {
  public static void SendMessageToAll(string methodName, object argument, SendMessageOptions options){
    foreach(GameObject go in FindObjectsOfType(typeof(GameObject)))
      go.SendMessage(methodName, argument, options);
  }
}
