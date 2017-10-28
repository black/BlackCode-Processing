var meditationDial : ValueDial;
var attentionDial : ValueDial;

private var thinkGearClient : DataController;

function Start(){
  thinkGearClient = GameObject.Find("ThinkGear").GetComponent(DataController);
  
  InvokeRepeating("UpdateDials", 0.0, 1.0);
}

function UpdateDials(){
  if(meditationDial)
    meditationDial.value = thinkGearClient.headsetData.meditation;
    
  if(attentionDial)
    attentionDial.value = thinkGearClient.headsetData.attention;
}
