using UnityEngine;
using System.Collections;
using System.Threading;
using System;

public class DataController : MonoBehaviour {

  public ThinkGearData headsetData;
  public IBrainwaveDataPlayer dataPlayer;
  public IBrainwaveDataPlayer standbyPlayer;

  private long lastPacketReadTime;

  private Thread updateThread;
  private bool updateDataThreadActive = true;

  private double elapsedTime = 0.0;
  private bool enableDemoMode = false;
  private bool isConnected = false;

  private const float TIMEOUT_INTERVAL = 1.5f;
  private const long TIMEOUT_INTERVAL_TICKS = (long)(TIMEOUT_INTERVAL * 10000000);

  public bool IsOffHead {
    get { return !isConnected || (isConnected && headsetData.poorSignalValue >= 200); }
  }

  public bool IsHeadsetInitialized {
    get { return isConnected; }
  }

  public bool IsDemo {
    get { return enableDemoMode; }
  }

  public bool IsESenseReady {
    get { return !IsOffHead && !(headsetData.attention == 0 && headsetData.meditation == 0); }
  }

  void Awake(){
    dataPlayer = new FakeMindSetOutput();
    updateThread = new Thread(UpdateDataValuesThread);
  }

  void Update(){
    elapsedTime += Time.deltaTime;
  }

  void OnHeadsetConnected(string portName){
    isConnected = true;
    
    enableDemoMode = portName == "DemoMode";

    standbyPlayer = dataPlayer;

    dataPlayer = enableDemoMode ? 
                    //(IBrainwaveDataPlayer)new FakeMindSetOutput() :
                    (IBrainwaveDataPlayer)new RecordedRawOutput(Application.dataPath + "/recorded_raw.xml") :
                    (IBrainwaveDataPlayer)new MindSetOutput(MindSetVersions.ASIC);

    lastPacketReadTime = DateTime.Now.Ticks;

    Invoke("UpdateDataValues", 0.0f); 
  }

  void OnHeadsetDisconnected(){
    isConnected = false;
    enableDemoMode = false;

    updateDataThreadActive = false;

    dataPlayer = standbyPlayer;

    if(updateThread != null && updateThread.IsAlive)
      updateThread.Abort();
  }

  void OnLoadBrainMusicData(BrainMusicData b){
    updateDataThreadActive = false;
    updateThread.Abort();

    dataPlayer = b;

    elapsedTime = 0.0;

    Invoke("UpdateDataValues", 1.0f);

    InvokeRepeating("UpdateElapsedTimeFromITunes", 0.0f, 4.0f);
  }

  void OnUnloadBrainMusicData(){
    updateDataThreadActive = false;
    updateThread.Abort();

    CancelInvoke("UpdatedElapsedTimeFromITunes");

    dataPlayer = standbyPlayer;

    Invoke("UpdateDataValues", 1.0f);
  }

  void OnLoadNextBrainMusicData(BrainMusicData b){
    updateDataThreadActive = false;
    updateThread.Abort();

    elapsedTime = 0.0;

    dataPlayer = b;

    Invoke("UpdateDataValues", 1.0f);
  }

  void UpdateElapsedTimeFromITunes(){
    elapsedTime = iTunesConnector.retrieveTrackPosition();
  }

  private void UpdateDataValues(){
    updateDataThreadActive = true;

    if(updateThread == null || (updateThread != null && !updateThread.IsAlive)){
      updateThread = new Thread(UpdateDataValuesThread);
      updateThread.Start();
    }
  }

  private void UpdateDataValuesThread(){
    while(updateThread.IsAlive && updateDataThreadActive){
      // Update/refresh the data values
      int readResult = enableDemoMode ? 0 : ThinkGear.ReadPackets(-1);

      if(readResult < 0){
        // if we haven't seen a valid packet in a while, then the headset was probably
        // disconnected. do a cleanup.
        if(DateTime.Now.Ticks - lastPacketReadTime > TIMEOUT_INTERVAL_TICKS){
          Debug.Log("Headset data receipt timed out.");
          GameHelper.SendMessageToAll("OnRequestHeadsetDisconnect", null, SendMessageOptions.DontRequireReceiver);
        }
      }
      else {
        lock(this){
          headsetData = dataPlayer.DataAt(elapsedTime);
        }

        lastPacketReadTime = DateTime.Now.Ticks;
      }

      Thread.Sleep(20);
    }
  }
}
