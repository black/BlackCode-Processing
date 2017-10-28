using UnityEngine;
using System;
using System.Collections;

/**
 * This class is responsible for handling recording of brainwave data synchronized to
 * music.
 *
 * Dispatched events:
 *    OnRecorderStateChanged(RecorderState) - Dispatched when the state of the recorder
 *        has changed
 */
public class DataRecorderEventController: MonoBehaviour {
  
  BrainMusicData data; 
    
  DataController client;
  Equalizer eq;

  public Texture2D swatch;

  private bool isRecording = false;
  private bool isPlaying = false;

  private double elapsedTime = 0.0;


  void Start(){
    client = (DataController)GameObject.Find("ThinkGear").GetComponent(typeof(DataController));
    eq = (Equalizer)GameObject.Find("Equalizer").GetComponent(typeof(Equalizer));

    string path = "";

    // store the song data in different locations depending on the platform
    if(Application.platform == RuntimePlatform.WindowsPlayer){
      path = Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData);
      path = System.IO.Path.Combine(path, "NeuroSky");
      path = System.IO.Path.Combine(path, "Brainwave Visualizer");
    }
    else {
      path = Environment.GetFolderPath(Environment.SpecialFolder.Personal);
      path = System.IO.Path.Combine(path, "Library/Application Support/NeuroSky/Brainwave Visualizer");
    }

    // make sure the data directory exists before using it
    if(!System.IO.Directory.Exists(path))
      System.IO.Directory.CreateDirectory(path); 

    UserStatus.dataPath = path;
  }

  void Update(){
    elapsedTime += Time.deltaTime;
  }

  /*
   * VIEW EVENTS
   */

  void OnRecordClicked(Song song){
    iTunesConnector.backTrack();

    data = new BrainMusicData(song);

    InvokeRepeating("AddHeadsetData", 0.0f, 1.0f);
    elapsedTime = 0.0;

    if(isPlaying)
      OnStopPlayClicked();

    DispatchStartedRecording();
  }

  void OnStopRecordClicked(){
    CancelInvoke("AddHeadsetData");

    MessageBox.Show(Localizer.Content["datarecorder"]["stoprecord"], MessageBox.MessageType.Message);

    DispatchStoppedRecording();
  }

  void OnPlayClicked(Song song){
    iTunesConnector.backTrack();

    BrainMusicData b = BrainMusicData.Load(System.IO.Path.Combine(UserStatus.dataPath, song.DataFileName));

    if(isRecording)
      OnStopRecordClicked();
 
    // TODO: if null, output popup message
    if(b == null){

    }
    else {
      GameHelper.SendMessageToAll("OnLoadBrainMusicData", b, SendMessageOptions.DontRequireReceiver);
      DispatchStartedPlaying();
    }
  }

  void OnStopPlayClicked(){
    MessageBox.Show(Localizer.Content["datarecorder"]["stopplay"], MessageBox.MessageType.Disappearing);

    GameHelper.SendMessageToAll("OnUnloadBrainMusicData", null, SendMessageOptions.DontRequireReceiver);
    DispatchStoppedPlaying();
  }

  /*
   * MUSIC PLAYER EVENTS
   */

  void OnMediaAppStateChanged(MediaAppState state){
    switch(state){
      /*
       * if the media player has finished its playlist or has suddenly closed, then
       * handle by dispatching the correct events to the other components.
       */
      case MediaAppState.NotRunning:
      case MediaAppState.Stopped:
        if(isRecording)
          OnStopRecordClicked();

        if(isPlaying)
          OnStopPlayClicked();

        break;
      default:
        break;
    }
  }

  void OnSongChanged(Song song){
    // TODO: do some checks to see whether it was a transition (the old song was finished
    // playing) or a user-invoked skip.
    // for now, just stop recording the current track and start recording the new one
    
    if(isRecording){
      // stop existing invocations of AddHeadsetData, then save the data
      CancelInvoke("AddHeadsetData"); 

      // determine the highest and next highest bar, and their colors
      int highestIndex = 0;
      int secondIndex = 0;
      int highestValue = 0;

      if(eq.bars[0].topBarCounter / 4 >= eq.bars[1].topBarCounter){
        highestValue = eq.bars[0].topBarCounter / 4;
        highestIndex = 0;
        secondIndex = 1;
      }
      else {
        highestValue = eq.bars[1].topBarCounter;
        highestIndex = 1;
        secondIndex = 0;
      }

      for(int i = 1; i < eq.bars.Length; i++){
        GraphBar b = eq.bars[i];
         
        if(b.topBarCounter > highestValue){
          highestValue = b.topBarCounter;
          secondIndex = highestIndex;
          highestIndex = i;
        }
      }

      Color c = (0.6f * eq.bars[highestIndex].color) + (0.4f * eq.bars[secondIndex].color);

      //MessageBox.Show(Localizer.Content["datarecorder"]["saveddata"], MessageBox.MessageType.Message);
      MessageBox.ShowSwatch(Localizer.Content["datarecorder"]["saveddata"],
                            c, 
                            swatch);

      eq.ResetBarCounters();

      data.Write(System.IO.Path.Combine(UserStatus.dataPath, data.song.DataFileName));

      // create a new data container, and start tossing in data
      data = new BrainMusicData(song);
      InvokeRepeating("AddHeadsetData", 0.0f, 0.9f);
      elapsedTime = 0.0;
    }

    // if a new song is played, then dispatch the relevant events
    if(isPlaying){
      BrainMusicData b = BrainMusicData.Load(System.IO.Path.Combine(UserStatus.dataPath, song.DataFileName));
  
      if(b == null){
        MessageBox.Show(Localizer.Content["datarecorder"]["playnodata"], MessageBox.MessageType.Message);

        DispatchStoppedPlaying();
        GameHelper.SendMessageToAll("OnUnloadBrainMusicData", null, SendMessageOptions.DontRequireReceiver);
      }
      else {
        GameHelper.SendMessageToAll("OnLoadNextBrainMusicData", b, SendMessageOptions.DontRequireReceiver);
      }
    }
  }

  /*
   * INTERNAL HELPER METHODS
   */

  private void AddHeadsetData(){
    ThinkGearData headsetData = new ThinkGearData();
    
    headsetData.elapsedTime = elapsedTime;
    headsetData.attention = (int)client.headsetData.attention;
    headsetData.meditation = (int)client.headsetData.meditation;
    headsetData.delta = client.headsetData.delta;
    headsetData.theta = client.headsetData.theta;
    headsetData.lowAlpha = client.headsetData.lowAlpha;
    headsetData.highAlpha = client.headsetData.highAlpha;
    headsetData.lowBeta = client.headsetData.lowBeta;
    headsetData.highBeta = client.headsetData.highBeta;
    headsetData.lowGamma = client.headsetData.lowGamma;
    headsetData.highGamma = client.headsetData.highGamma;

    data.Add(headsetData);
  }

  private void DispatchStartedRecording(){
    isRecording = true;
    GameHelper.SendMessageToAll("OnStartedRecording", null, SendMessageOptions.DontRequireReceiver);
  }

  private void DispatchStoppedRecording(){
    isRecording = false;
    GameHelper.SendMessageToAll("OnStoppedRecording", null, SendMessageOptions.DontRequireReceiver);
  }

  private void DispatchStartedPlaying(){
    isPlaying = true;
    GameHelper.SendMessageToAll("OnStartedPlaying", null, SendMessageOptions.DontRequireReceiver);
  }

  private void DispatchStoppedPlaying(){
    isPlaying = false;
    GameHelper.SendMessageToAll("OnStoppedPlaying", null, SendMessageOptions.DontRequireReceiver);
  }
}
