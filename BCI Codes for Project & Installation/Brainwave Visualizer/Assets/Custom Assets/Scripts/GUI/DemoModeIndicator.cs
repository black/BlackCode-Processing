using UnityEngine;
using System.Collections;

public class DemoModeIndicator : MonoBehaviour {

  GUISkin skin;
  GUIStyle demomodeIndicator;

  DataController controller;

  public void Start(){
    OnLanguageChanged();
    demomodeIndicator = skin.GetStyle("demomode");

    controller = (DataController)GameObject.Find("ThinkGear").GetComponent(typeof(DataController));
  }

  public void OnLanguageChanged(){
    skin = Localizer.Skins["main"];
    demomodeIndicator = skin.GetStyle("demomode");
  }

  public void OnGUI(){
    GUI.skin = skin;

    GUI.depth = 6;
    GUI.color = Color.white;

    if(!UserStatus.isMenuDisplayed && controller.IsDemo){
      GUILayout.Label(Localizer.Content["demomode"]["text"], demomodeIndicator, GUILayout.Width(Screen.width), GUILayout.Height(31));
    }
  }
}
