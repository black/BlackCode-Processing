var background : Texture2D;
var bar : Texture2D;
var normalizedRect : Rect;
var linkedCamera : Camera;

var values : float[];

var barColors : Color[];

private var scaling : float;
private var usedCamera : Camera;
private var cameraRect : Rect;

private var lerpedValues : float[];

private var backgroundRect : Rect;
private var graphRect : Rect;

private var barWidth : float;
private var barGap : float;
private var startGap : float;

function Awake(){
  usedCamera = (linkedCamera != null ? linkedCamera : Camera.main);
  
  lerpedValues = new float[Mathf.Clamp(values.length, 0, 8)];
}

function Start(){
  InvokeRepeating("Relayout", 0.0, 0.5);
}

function Relayout(){
  cameraRect = usedCamera.pixelRect;
  scaling = cameraRect.width / 700.0;
  
  backgroundRect = RectForTexture(background);
  
  barWidth = 52.0 * scaling;
  barGap = 15.0 * scaling;
  startGap = 11.0 * scaling;
  
  graphRect = Rect(backgroundRect.x + startGap, backgroundRect.y, backgroundRect.width - startGap, backgroundRect.height - (30 * scaling));
}

function Update(){
  for(var i : int = 0; i < lerpedValues.Length; i++)
    lerpedValues[i] = Mathf.Lerp(lerpedValues[i], 0, Time.deltaTime);
}

function OnGUI(){
  GUI.depth = 10;

  GUI.DrawTexture(backgroundRect, background);
  
  // draw the bars
  for(var i : int = 0; i < Mathf.Clamp(lerpedValues.length, 0, 8); i++){
    GUI.color = barColors[i];
    // figure out the actual bar height
    var height : float = Mathf.Clamp(graphRect.height * lerpedValues[i], 0.0, graphRect.height);
    
    GUI.DrawTexture(Rect(graphRect.x + i * (barWidth + barGap), graphRect.y + (graphRect.height - height), barWidth, height), bar);
  }
}

function RectForTexture(texture : Texture2D){
  var aspectRatioFactor : float = ((Screen.width + 0.0) / (Screen.height + 0.0)) / 1.77778;
 
  var shiftedScreenHeight : float = Screen.height * aspectRatioFactor;
  var offset : float = (Screen.height - shiftedScreenHeight) / 2.0;
  
  return Rect(cameraRect.x + (cameraRect.width * normalizedRect.x), 
              (cameraRect.y * aspectRatioFactor) + ((cameraRect.height * aspectRatioFactor) * normalizedRect.y) + offset,
              texture.width * scaling, texture.height * scaling);
}
