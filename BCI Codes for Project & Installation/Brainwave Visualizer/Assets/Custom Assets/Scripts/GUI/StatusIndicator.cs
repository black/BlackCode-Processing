using UnityEngine;
using System;
using System.Collections;

public class StatusIndicator : MonoBehaviour {
  public Texture play;
  public Texture record;
  public Texture playRecord;
  public Texture pending;

  private GUISkin statusBarSkin;

  private GUIStyle headerStyle;
  private GUIStyle textStyle;

  private bool isPlaying = false;
  private bool isRecording = false;

  private Color iconColor;

  private float targetAlpha;
  private float alphaVelocity = 0.0f;

  private DateTime date;

  void Start(){
    OnLanguageChanged();

    iconColor = new Color(1, 1, 1, 1);

    InvokeRepeating("PingPong", 0.0f, 4.0f);

    date = DateTime.Now;
  }

  void OnLanguageChanged(){
    statusBarSkin = Localizer.Skins["mediabar"];

    headerStyle = statusBarSkin.GetStyle("StatusBar Header");
    textStyle = statusBarSkin.GetStyle("StatusBar Normal");
  }

  void PingPong(){
    targetAlpha = targetAlpha == 0.0f ? 1.0f : 0.0f;  
  }

  void Update(){
    iconColor.a = Mathf.SmoothDamp(iconColor.a, targetAlpha, ref alphaVelocity, 2.0f);
  }

  void OnGUI(){
    if(isPlaying || isRecording){
      GUILayout.Space(10);
      GUILayout.BeginHorizontal(GUILayout.Width(Screen.width));
      GUILayout.FlexibleSpace();

      if(isPlaying){
        DrawStatusLabel(play, 
                        Localizer.Content["mediastatus"]["playheader"],
                        Localizer.Content["mediastatus"]["playmsg"] + date.ToString("G"));
      }
      else if(isRecording){
        DrawStatusLabel(record,
                        Localizer.Content["mediastatus"]["recordheader"],
                        Localizer.Content["mediastatus"]["recordmsg"]);
      }

      GUILayout.FlexibleSpace();
      GUILayout.EndHorizontal();
    }
  }

  private void DrawStatusLabel(Texture texture, string header, string label){
     GUI.color = iconColor;
     GUILayout.Label(texture);
     GUI.color = Color.white;
     GUILayout.BeginVertical();
     GUILayout.Label(header, headerStyle);
     GUILayout.Space(-10);
     GUILayout.Label(label, textStyle);
     GUILayout.EndVertical();
  }

  void OnStartedRecording(){
    isRecording = true;
  }

  void OnStoppedRecording(){
    isRecording = false;
  }

  void OnStartedPlaying(){
    isPlaying = true;
  }

  void OnStoppedPlaying(){
    isPlaying = false;
  }

  void OnLoadBrainMusicData(BrainMusicData b){
    date = b.createdAt;
  }

  void OnLoadNextBrainMusicData(BrainMusicData b){
    date = b.createdAt; 
  }
}
