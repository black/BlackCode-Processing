using UnityEngine;
using System.Collections;
using System.Threading;

/**
 * The MediaBar class dispatches two events:
 *
 *   OnStartRecording(Song) - Dispatched when a user requests brainwave data to be recorded
 *                            for a particular song.
 *   OnStopRecording() - Dispatched when a user terminates a recording
 */
[RequireComponent(typeof(DataRecorderEventController))]
public class MediaBar: MonoBehaviour {
  public Rect normalizedRect;

  public Texture albumArt;
  public Texture albumArtNoData;
  public Texture record;
  public Texture stop;
  public Texture play;
  public Texture pause;

  private GUISkin iTunesSkin;

  private GUIStyle mediaBar;
  private GUIStyle songName;
  private GUIStyle songTitle;
  private GUIStyle mediaBarButton;
  private GUIStyle disabledButton;

  private Song currentSong;

  private MediaAppState state;

  private bool isRecording = false;
  private bool isPlaying = false;

  private bool hasData = false;

  private Color disabledColor;

  void Awake(){
    disabledColor = new Color(1.0f, 1.0f, 1.0f, 0.1f);
  }

  void Start(){
    OnLanguageChanged();
  }

  void OnLanguageChanged(){
    iTunesSkin = Localizer.Skins["mediabar"];

    mediaBar = iTunesSkin.GetStyle("MediaBar");
    songName = iTunesSkin.GetStyle("SongName");
    songTitle = iTunesSkin.GetStyle("SongTitle");
    mediaBarButton = iTunesSkin.GetStyle("MediaBarButton");
    disabledButton = iTunesSkin.GetStyle("DisabledButton");
  }

  /*
   * DATA RECORDER EVENTS
   */
  void OnStartedRecording(){
    isRecording = true;
  }

  void OnStoppedRecording(){
    isRecording = false;
  }

  /*
   * DATA PLAYER EVENTS
   */
  void OnStartedPlaying(){
    isPlaying = true;
  }

  void OnStoppedPlaying(){
    isPlaying = false;
  }

  /*
   * MEDIA PLAYER EVENTS
   */

  void OnMediaAppStateChanged(MediaAppState newState){
    state = newState;
  }

  void OnSongChanged(Song newSong){
    currentSong = newSong;

    // check whether the data file exists
    hasData = System.IO.File.Exists(System.IO.Path.Combine(UserStatus.dataPath, newSong.DataFileName));  
  }

  void OnGUI(){
    GUI.depth = 10;

    GUILayout.Space(normalizedRect.y * Screen.height);

    GUILayout.BeginHorizontal(GUILayout.Width(normalizedRect.width * Screen.width));
    GUILayout.Space(normalizedRect.x * Screen.width);

    GUILayout.BeginVertical(mediaBar, GUILayout.Height(normalizedRect.height * Screen.height));
  
    GUILayout.BeginHorizontal();

    // icon column (placeholder for album art)
    GUILayout.BeginVertical();
    GUILayout.FlexibleSpace();
    GUILayout.Label(hasData ? albumArt : albumArtNoData);
    GUILayout.FlexibleSpace();
    GUILayout.EndVertical();

    // song information column
    GUILayout.BeginVertical();
    GUILayout.FlexibleSpace();

    switch(state){
      case MediaAppState.NotRunning:
        GUILayout.Label(Localizer.Content["mediabar"]["startitunes"], songTitle);
        break;
      case MediaAppState.Stopped:
        GUILayout.Label(Localizer.Content["mediabar"]["startplay"], songTitle);
        break;
      default:
        int maxLabelWidth = (int)(normalizedRect.width * Screen.width) - 220;

        GUILayout.Label(currentSong.title, songTitle, GUILayout.Width(maxLabelWidth));
        GUILayout.Label(currentSong.artist, songName, GUILayout.Width(maxLabelWidth));
        GUILayout.Label(currentSong.album, songName, GUILayout.Width(maxLabelWidth));
        break;
    }

    GUILayout.FlexibleSpace();
    GUILayout.EndVertical();

    GUILayout.FlexibleSpace();

    // media control buttons
    GUILayout.BeginVertical();
    GUILayout.FlexibleSpace();
    GUILayout.BeginHorizontal();

    // if iTunes is stopped or not playing, then just grey out all the buttons
    if(state == MediaAppState.NotRunning || state == MediaAppState.Stopped){
      GUI.color = disabledColor;
      GUILayout.Label(record, disabledButton);
      GUILayout.Space(-20);
      GUILayout.Label(play, disabledButton);
    }
    else {
      // handle the record button
      if(!isRecording){
        if(GUILayout.Button(record, mediaBarButton)){
          SendMessage("OnRecordClicked", currentSong);
          Event.current.Use(); 
        }
      }
      else {
        if(GUILayout.Button(stop, mediaBarButton)){
          SendMessage("OnStopRecordClicked", null);
          Event.current.Use();
        }
      }

      GUILayout.Space(-20);

      // handle the play button
      if(!hasData){
        GUI.color = disabledColor;
        GUILayout.Label(play, disabledButton);
      }
      else if(!isPlaying){
        if(GUILayout.Button(play, mediaBarButton)){
          SendMessage("OnPlayClicked", currentSong, SendMessageOptions.DontRequireReceiver);
          Event.current.Use();
        }
      }
      else {
        if(GUILayout.Button(pause, mediaBarButton)){
          SendMessage("OnStopPlayClicked", null, SendMessageOptions.DontRequireReceiver);
          Event.current.Use();
        }
      }
    }
  
    GUILayout.Space(-10);
    GUILayout.EndHorizontal();
    GUILayout.FlexibleSpace();
    GUILayout.EndVertical();

    GUILayout.EndHorizontal();
  
    GUILayout.EndVertical();
    GUILayout.EndHorizontal();
  }
}
