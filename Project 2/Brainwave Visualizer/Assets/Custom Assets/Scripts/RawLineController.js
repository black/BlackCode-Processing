private var rawLine : RawLine;

private var thinkGearClient : DataController;

function Start(){
  thinkGearClient = GameObject.Find("ThinkGear").GetComponent(DataController);
  
  rawLine = GetComponent(RawLine);
  
  InvokeRepeating("UpdateRawLine", 0.0, 0.01);
}

function UpdateRawLine(){
  if(!thinkGearClient.IsOffHead && thinkGearClient.IsHeadsetInitialized)
    rawLine.targetValue = thinkGearClient.headsetData.raw;
  else
    rawLine.targetValue = 0.0;
}
