private var equalizer : Equalizer;

private var thinkGearClient : DataController;

function Start(){
  thinkGearClient = GameObject.Find("ThinkGear").GetComponent(DataController);
  
  equalizer = GetComponent(Equalizer);
  
  InvokeRepeating("UpdateGraph", 0.0, 1.0);
}

function UpdateGraph(){
  if(!thinkGearClient.IsOffHead && thinkGearClient.IsHeadsetInitialized){
    equalizer.AddData([Mathf.Log10(thinkGearClient.headsetData.delta),
                       Mathf.Log10(thinkGearClient.headsetData.theta),
                       Mathf.Log10(thinkGearClient.headsetData.lowAlpha),
                       Mathf.Log10(thinkGearClient.headsetData.highAlpha),
                       Mathf.Log10(thinkGearClient.headsetData.lowBeta),
                       Mathf.Log10(thinkGearClient.headsetData.highBeta),
                       Mathf.Log10(thinkGearClient.headsetData.lowGamma),
                       Mathf.Log10(thinkGearClient.headsetData.highGamma)]);
  }
  else {
    equalizer.AddData([0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]);
  }
}

