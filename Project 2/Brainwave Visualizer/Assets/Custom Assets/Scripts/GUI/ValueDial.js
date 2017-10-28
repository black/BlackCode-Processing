#pragma strict

// Dial assets
var glowTexture : Texture2D;
var pointerTexture : Texture2D;
var numberRingTexture : Texture2D;
var buttonOverlayTexture : Texture2D;
var labelTexture : Texture2D;
var dialCenter : Texture2D;

// position of dial
var normalizedRect : Rect;

// pulse rate
var pulseRate : float = 1.5;

// value that the dial should display
var value : int;

// camera linked to the dial (optional)
var linkedCamera : Camera;

// color of the dial glow
var glowColor : Color;

var thresholdColor : Color;
var threshold : int = 90;

var dialCenterThreshold : int = 80;

private var scaling : float;

private var glowRect : Rect;
private var pointerRect : Rect;
private var numberRingRect : Rect;
private var buttonOverlayRect : Rect;
private var labelRect : Rect;
private var dialCenterRect : Rect;

private var pointerAngle : float;
private var lerpedValue : float;

private var pointerVelocity : float;

private var activeColor : Color;

private var currentColor : Color;

private var dialCenterColor : Color;

function Awake(){
  usedCamera = (linkedCamera != null ? linkedCamera : Camera.main);
}

function Start(){
  InvokeRepeating("Relayout", 0.0, 0.5);
}

function Relayout(){
  scaling = (Screen.width * normalizedRect.width) / numberRingTexture.width;
  
  glowRect = RectForTexture(glowTexture);
  
  pointerRect = RectForTexture(pointerTexture);
  pointerRect.x = pointerRect.x + (22 * scaling);
  pointerRect.y = pointerRect.y + (133 * scaling);
  
  labelRect = RectForTexture(labelTexture);
  labelRect.x = labelRect.x + (65 * scaling);
  labelRect.y = labelRect.y + (263 * scaling);
  
  numberRingRect = RectForTexture(numberRingTexture);
  buttonOverlayRect = RectForTexture(buttonOverlayTexture);
  
  dialCenterRect = RectForTexture(dialCenter);
  dialCenterRect.x = dialCenterRect.x + (95 * scaling);
  dialCenterRect.y = dialCenterRect.y + (99 * scaling);
}


function Update(){
  lerpedValue = Mathf.SmoothDamp(lerpedValue, value, pointerVelocity, 1.2);
  
  pointerAngle = ((lerpedValue / 100.0) * 300.0) - 57.0;
  
  if(lerpedValue > threshold){
    // perform flashing
    if(currentColor.a < 0.01)
      activeColor = thresholdColor;
    else if(currentColor.a >= (Color(0, 0, 0, 0.99) * activeColor).a)
      activeColor = Color(1, 1, 1, 0);
  }
  else {
    activeColor = glowColor;
  }
  
  // when the threshold is exceeded, have the rate of flashing increase
  currentColor = Color.Lerp(currentColor, activeColor, 
                            pulseRate * Time.deltaTime * (lerpedValue > threshold ? 5.0 + (lerpedValue - threshold): 1.0));
  
  // if the eSense value is between 80 and 90, then lerp it between there using the glow color
  // if it's above 90, just use the glow color (flashing)
  dialCenterColor = lerpedValue >= threshold ?
                      currentColor :
                      (lerpedValue >= dialCenterThreshold && lerpedValue < threshold ? 
                        Color.Lerp(Color.black, glowColor, (lerpedValue - dialCenterThreshold + 0.0) / (threshold - dialCenterThreshold + 0.0)) : 
                        Color.black);
}

function OnGUI(){
  GUI.depth = 5;
  
  // draw the glow
  GUI.color = currentColor * Color(1, 1, 1, 0.1 + (Mathf.Clamp(lerpedValue - 35, 0, 65) * 0.9 / 65.0));
  GUI.DrawTexture(glowRect, glowTexture);
  
  GUI.color = Color.white;
  
  // draw the (rotated) pointer
  var tempMatrix = GUI.matrix;
  GUIUtility.RotateAroundPivot(pointerAngle, Vector2(glowRect.x + (glowRect.width / 2.0), pointerRect.y));
  GUI.DrawTexture(pointerRect, pointerTexture);
  GUI.matrix = tempMatrix;
  
  // draw the number ring
  GUI.DrawTexture(numberRingRect, numberRingTexture);
  
  // draw the colored texture at the center of the dial
  GUI.color = dialCenterColor;
  GUI.DrawTexture(dialCenterRect, dialCenter);
  
  // draw the dial itself
  GUI.color = Color.white;
  GUI.DrawTexture(buttonOverlayRect, buttonOverlayTexture);
  GUI.DrawTexture(labelRect, labelTexture);
}

function OnApplicationQuit(){
  CancelInvoke("Relayout");
}

function RectForTexture(texture : Texture2D){
  return Rect(Screen.width * normalizedRect.x, 
              Screen.height * normalizedRect.y,
              texture.width * scaling, texture.height * scaling);
}
