#pragma strict

var signalTextures : Texture[];

private var thinkGearData : DataController;

private var signalTextureIndex : int = 5;

private var portName : String = "";

private var enableDemoMode : boolean = false;

private var elementHeight : int = 25;

private var initialPortName : String = "";

private var configByte : String = "02";

enum State {
  Disconnected,
  Scanning,
  Connected
}

private var state : State = State.Disconnected;

function Awake(){
  DontDestroyOnLoad(this);
  
  thinkGearData = GameObject.Find("ThinkGear").GetComponent(DataController);
  
  InvokeRepeating("UpdateMeter", 0, 1.0);
}

function Start(){
  if(thinkGearData.IsHeadsetInitialized)
    state = State.Connected;

  GameHelper.SendMessageToAll("OnRequestPortScan", "", SendMessageOptions.DontRequireReceiver);
}

function UpdateMeter(){
  // if headset is uninitialized or off-head, display zero signal quality
  if(!thinkGearData.IsHeadsetInitialized || thinkGearData.IsOffHead)
    signalTextureIndex = 0;
  // otherwise, figure out what the poor signal value is and display
  // the appropriate signal quality icon
  else {
    var signalQuality : float = thinkGearData.headsetData.poorSignalValue;

    // no poor signal flags have been raised
    if(signalQuality < 25)
      signalTextureIndex = 5;
    // one flag has been raised
    else if(signalQuality >= 25 && signalQuality < 51)
      signalTextureIndex = 4;
    // two flags have been raised
    else if(signalQuality >= 51 && signalQuality < 78)
      signalTextureIndex = 3;
    // three flags have been raised
    else if(signalQuality >= 78 && signalQuality < 107)
      signalTextureIndex = 2;
    // four flags have been raised
    else if(signalQuality >= 107)
      signalTextureIndex = 1;
  }
}

function OnGUI(){
  GUI.depth = 5; 

  GUILayout.BeginHorizontal(GUILayout.Width(Screen.width));
  GUILayout.FlexibleSpace();

  switch(state){
    case State.Disconnected:
      if(UserStatus.isAdvancedModeEnabled){
        GUILayout.BeginVertical(GUILayout.Height(elementHeight));
        GUILayout.Space(6);
        GUILayout.BeginHorizontal();
        GUILayout.Label("Initial port");
        GUILayout.Space(5);
        
        if(Application.platform == RuntimePlatform.WindowsPlayer){ 
          GUILayout.Label("COM");
          initialPortName = GUILayout.TextField(initialPortName, 3, GUILayout.Width(25));
        }
        else {
          initialPortName = GUILayout.TextField(initialPortName, GUILayout.Width(180));
        }

        GUILayout.EndHorizontal();
        GUILayout.EndVertical();
      }

      GUILayout.BeginVertical(GUILayout.Height(elementHeight));
      GUILayout.FlexibleSpace();
      enableDemoMode = GUILayout.Toggle(enableDemoMode, "  Enable demo mode");
      GUILayout.Space(3);
      GUILayout.EndVertical();

      GUILayout.BeginVertical(GUILayout.Height(elementHeight));
      GUILayout.FlexibleSpace();

      if(GUILayout.Button("Connect", GUILayout.Width(100))){
        // add the "\\.\COM" prefix to the user-specified starting COM port number
        var formattedPortName = Application.platform == RuntimePlatform.WindowsPlayer ? "\\\\.\\COM" + initialPortName : initialPortName;

        GameHelper.SendMessageToAll("OnRequestPortScan", enableDemoMode ? "DemoMode" : formattedPortName, SendMessageOptions.DontRequireReceiver);
      }

      GUILayout.EndVertical();

      break;
    case State.Scanning:
      GUILayout.BeginVertical(GUILayout.Height(elementHeight));
      GUILayout.FlexibleSpace();
      GUILayout.Label("Scanning " + portName + "...");
      GUILayout.EndVertical();

      GUILayout.BeginVertical(GUILayout.Height(elementHeight));
      GUILayout.FlexibleSpace();

      if(GUILayout.Button("Skip", GUILayout.Width(100))){
        GameHelper.SendMessageToAll("OnRequestPortQueryCancel", null, SendMessageOptions.DontRequireReceiver);
      }

      GUILayout.EndVertical();

      break;
    case State.Connected:
      if(!thinkGearData.IsDemo){
        if(UserStatus.isAdvancedModeEnabled){
          GUILayout.BeginVertical(GUILayout.Height(elementHeight));
          GUILayout.Space(6);
          GUILayout.BeginHorizontal();
          GUILayout.Label("Config Byte");
          GUILayout.Space(5);
          GUILayout.Label("0x");
          configByte = GUILayout.TextField(configByte, 3, GUILayout.Width(25));

          if(GUILayout.Button("Send")){
            GameHelper.SendMessageToAll("OnTransmitByte", System.Byte.Parse(configByte, System.Globalization.NumberStyles.AllowHexSpecifier), SendMessageOptions.DontRequireReceiver);
          }

          GUILayout.Space(15);
          GUILayout.EndHorizontal();
          GUILayout.EndVertical();
        }

        GUILayout.BeginVertical(GUILayout.Height(elementHeight));
        GUILayout.FlexibleSpace();
        GUILayout.Label("Wave Quality");
        GUILayout.EndVertical();
        GUILayout.BeginVertical();
        GUILayout.Space(-4);
        GUILayout.Label(signalTextures[signalTextureIndex]);
        GUILayout.EndVertical();
      }

      GUILayout.BeginVertical(GUILayout.Height(elementHeight));
      GUILayout.FlexibleSpace();

      if(GUILayout.Button("Disconnect", GUILayout.Width(100)))
        GameHelper.SendMessageToAll("OnRequestHeadsetDisconnect", null, SendMessageOptions.DontRequireReceiver);

      GUILayout.EndVertical();

      break;
  }

  GUILayout.Space(20);
  GUILayout.EndHorizontal();
}

function OnPortScanStarted(){
  state = State.Scanning;
}

function OnPortScanFailed(){
  state = State.Disconnected;
}

function OnPortQueryStarted(queryPortName : String){
  portName = queryPortName;
}

function OnHeadsetConnected(portName : String){
  state = State.Connected;
}

function OnHeadsetDisconnected(){
  state = State.Disconnected;
}
