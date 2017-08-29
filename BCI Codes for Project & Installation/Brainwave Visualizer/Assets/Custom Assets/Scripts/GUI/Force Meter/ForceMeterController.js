var meterBackgroundTexture : Texture2D;
var forceTypesEn : Texture2D[];
var forceTypesKo : Texture2D[];

var signalsEn : Texture2D[];
var signalsKo : Texture2D[];

var meterStartTexture : Texture2D;
var meterSurroundTexture : Texture2D;
var meterEndTexture : Texture2D;
var barTexture : Texture2D;

var offHeadColor : Color;
var lookingForEsenseColor : Color;

var language : String = "en";

private var barWidth : int = 784;
private var barHeight : int = 32;

private var forceType : int = 0;
private var headsetValue : int = 0;

private var xSpacing : int = 4;

private var meterWidth : int = 560;

private var playerAttributes : Attributes;
private var thinkGear : DataController;

private var meterBackgroundRect : Rect;
private var meterTypeRect : Rect;
private var barBeginRect : Rect;
private var barSurroundRect : Rect;
private var barRect : Rect;
private var barEndRect : Rect;

private var forceTypes : Texture2D[];
private var signals : Texture2D[];

private var signalRect : Rect;

private var targetColor : Color = Color.white;
private var currentColor : Color = Color.white;

private var barVelocity : float = 0.0;

function Awake() {
  switch(language){
    case "en":
      forceTypes = forceTypesEn;
      signals = signalsEn;
      break;
    case "ko":
      forceTypes = forceTypesKo;
      signals = signalsKo;
      break;
    default:
      forceTypes = forceTypesEn;
      signals = signalsEn;
  }
  
  playerAttributes = GameObject.Find("Player").GetComponent(Attributes);
  thinkGear = GameObject.Find("ThinkGear").GetComponent(DataController);
  
  var box = Rect((Camera.main.pixelWidth - barWidth) / 2, Camera.main.pixelHeight - 40, barWidth, barHeight);

  signalRect = Rect((Camera.main.pixelWidth - signals[0].width) / 2, box.y, signals[0].width, signals[0].height);

  /*
   * Figure out the bounding boxes for each of the force meter elements
   */

  meterBackgroundRect = Rect((Camera.main.pixelWidth - meterBackgroundTexture.width) / 2, 
                             Camera.main.pixelHeight - meterBackgroundTexture.height,
                             meterBackgroundTexture.width, 
                             meterBackgroundTexture.height);
   
  meterTypeRect = Rect(box.x + xSpacing, box.y, forceTypes[forceType].width, forceTypes[forceType].height);
                                 
  box.x += xSpacing + forceTypes[forceType].width;
               
  barBeginRect = Rect(box.x + xSpacing, box.y, meterStartTexture.width, meterStartTexture.height);
   
  box.x += xSpacing + meterStartTexture.width;
     
  barSurroundRect = Rect(box.x, box.y, meterWidth, meterStartTexture.height);
  
  barRect = Rect(box.x, box.y, 0, meterStartTexture.height);
                  
  box.x += meterWidth;

  barEndRect = Rect(box.x, box.y, meterEndTexture.width, meterEndTexture.height);
}

function Update () {
  if(!thinkGear.IsHeadsetInitialized || thinkGear.IsOffHead || !thinkGear.IsESenseReady){
    targetColor = Color(1, 1, 1, 0.1);
  }
  else
    targetColor = Color.white;
  
  // dampen the movements between the current value and the target value
  barRect.width = Mathf.SmoothDamp(barRect.width, (playerAttributes.eSenseValue / 100.0) * meterWidth, barVelocity, 1.1);
  
  // figure out which label (attn / meditation) to display
  forceType = playerAttributes.eSenseType;
  
  currentColor = Color.Lerp(currentColor, targetColor, Time.deltaTime * 3.0);
}

function OnGUI(){
  GUI.color = Color.white;
  
  // draw the background
  GUI.DrawTexture(meterBackgroundRect, meterBackgroundTexture);

  GUI.color = currentColor;
       
  // draw force type
  GUI.DrawTexture(meterTypeRect, forceTypes[forceType]);
  
  // draw the bar itself
  GUI.DrawTexture(barBeginRect, meterStartTexture);
  GUI.DrawTexture(barSurroundRect, meterSurroundTexture);
  GUI.DrawTexture(barRect, barTexture);
  GUI.DrawTexture(barEndRect, meterEndTexture);
  
  GUI.color = Color.white;
  
  // draw the "disconnected" label
  if(!thinkGear.IsHeadsetInitialized){
    GUI.color = offHeadColor;
    GUI.DrawTexture(signalRect, signals[0]);
  }
  // draw the "looking for signal" label
  else if(thinkGear.IsOffHead){
    GUI.color = offHeadColor;
    GUI.DrawTexture(signalRect, signals[1]);
  }
  // draw the "analyzing signal" (on-head, but waiting for eSense) label
  else if(!thinkGear.IsESenseReady){
    GUI.color = lookingForEsenseColor;
    GUI.DrawTexture(signalRect, signals[2]);
  }
}
