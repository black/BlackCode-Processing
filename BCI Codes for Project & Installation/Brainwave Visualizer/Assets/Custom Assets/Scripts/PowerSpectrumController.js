private var powerSpectrum : PowerSpectrumGraph;

private var thinkGearClient : DataController;

function Start(){
  thinkGearClient = GameObject.Find("ThinkGear").GetComponent(DataController);
  
  powerSpectrum = GetComponent(PowerSpectrumGraph);
  
  InvokeRepeating("UpdateGraph", 0.0, 1.0);
}

function UpdateGraph(){
  if(!thinkGearClient.IsOffHead && thinkGearClient.IsHeadsetInitialized){
    powerSpectrum.values = [Mathf.Log10(thinkGearClient.headsetData.delta),
                            Mathf.Log10(thinkGearClient.headsetData.theta),
                            Mathf.Log10(thinkGearClient.headsetData.lowAlpha),
                            Mathf.Log10(thinkGearClient.headsetData.highAlpha),
                            Mathf.Log10(thinkGearClient.headsetData.lowBeta),
                            Mathf.Log10(thinkGearClient.headsetData.highBeta),
                            Mathf.Log10(thinkGearClient.headsetData.lowGamma),
                            Mathf.Log10(thinkGearClient.headsetData.highGamma)];
  }
  else {
    powerSpectrum.values = [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0];
  }
}

