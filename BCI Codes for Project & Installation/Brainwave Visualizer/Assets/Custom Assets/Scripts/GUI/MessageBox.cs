using UnityEngine;
using System.Collections;

public class MessageBox: MonoBehaviour {

  public enum MessageType {
    Error,
    Message,
    Disappearing,
    ColorSwatch
  }

  private static bool displayMessage = false;

  private static Texture2D texture;
  private static Color color = Color.white;

  private static string messageText = "";
  private static MessageType messageType = MessageType.Message;

  private static bool invokeDisappearing = false;

  private GUISkin mainSkin;

	// Use this for initialization
	void Start () {
    OnLanguageChanged();
	}

  void OnLanguageChanged(){
    mainSkin = Localizer.Skins["main"];	
  }

  void Update(){
    if(invokeDisappearing){
      Invoke("HideMessageBox", 6.0f);
      invokeDisappearing = false;
    }
  }

  public static void Show(string message, MessageType m){
    displayMessage = true;
    messageText = message;
    messageType = m;

    if(messageType == MessageType.Disappearing)
      invokeDisappearing = true;
  }

  public static void ShowSwatch(string message, Color c, Texture2D t){
    displayMessage = true;
    messageText = message;
    texture = t;
    color = c;

    messageType = MessageType.ColorSwatch;
  }

  void HideMessageBox(){
    displayMessage = false;
  }

  void OnGUI(){
    GUI.skin = mainSkin;
    GUI.depth = 1;

    if(displayMessage){
      GUILayout.BeginVertical(GUILayout.Height(Screen.height));
      GUILayout.FlexibleSpace();
      GUILayout.BeginHorizontal(GUILayout.Width(Screen.width));
      GUILayout.FlexibleSpace();

      GUILayout.BeginVertical("MessageBox");

      GUILayout.Label(messageText, GUI.skin.GetStyle("MessageBoxLabel"));

      Color oldColor = GUI.color;
      GUI.color = color;

      if(messageType == MessageType.ColorSwatch){
        GUILayout.BeginHorizontal(GUILayout.Width(200));
        GUILayout.FlexibleSpace();
        GUI.color = color;
        GUILayout.Label(texture);
        GUILayout.FlexibleSpace();
        GUILayout.EndHorizontal();

        GUILayout.Space(20);
      }

      GUI.color = oldColor;

      if(messageType != MessageType.Disappearing){
        if(GUILayout.Button("Okay"))
          displayMessage = false;
      }

      GUILayout.EndVertical();
        
      GUILayout.FlexibleSpace();
      GUILayout.EndHorizontal();
      GUILayout.FlexibleSpace();
      GUILayout.EndVertical();
    }
  }
}
