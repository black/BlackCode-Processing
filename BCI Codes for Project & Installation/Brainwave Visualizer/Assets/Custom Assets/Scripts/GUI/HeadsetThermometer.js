/**
 * Do the thermometer-type headset force strength
 */
#pragma strict

var foreground : GUITexture;
var filler : GUITexture;
var fillerBottom : GUITexture;
var fillerTop : GUITexture;
var background : GUITexture;

var attentionColor : Color;
var meditationColor : Color;

private var meterHeight : float = 0.0;
private var meterScalingFactor : float;

private var headsetValue : float;
private var position : Vector3;

private var playerAttributes : Attributes;

function Awake() {
  playerAttributes = GameObject.Find("Player").GetComponent(Attributes);
  
  // enable everything
  foreground.enabled = true;
  filler.enabled = true;
  fillerBottom.enabled = true;
  fillerTop.enabled = true;
  background.enabled = true;
  
  meterScalingFactor = (Camera.main.pixelHeight / foreground.texture.height) * 0.9;
  
  position = Vector3( Camera.main.pixelWidth * 0.05 - (Camera.main.pixelWidth / 2),
                      -(background.texture.height * meterScalingFactor) / 2,
                      0);
  
  foreground.pixelInset.x = fillerTop.pixelInset.x = filler.pixelInset.x = 
                            fillerBottom.pixelInset.x = background.pixelInset.x = position.x;
                            
  foreground.pixelInset.y = fillerBottom.pixelInset.y = background.pixelInset.y = position.y;
  
  foreground.pixelInset.width *= meterScalingFactor;
  foreground.pixelInset.height *= meterScalingFactor;
  
  background.pixelInset.width *= meterScalingFactor;
  background.pixelInset.height *= meterScalingFactor;
  
  fillerBottom.pixelInset.width *= meterScalingFactor;
  fillerBottom.pixelInset.height *= meterScalingFactor;
  
  fillerTop.pixelInset.width *= meterScalingFactor;
  fillerTop.pixelInset.height = (fillerTop.pixelInset.height * meterScalingFactor) - 1;
  
  filler.pixelInset.width *= meterScalingFactor;
  filler.pixelInset.height = 0;
    
  fillerTop.transform.localPosition.z = -6;
  fillerBottom.transform.localPosition.z = -6; 
    
  background.transform.localPosition.z = -10;
  filler.transform.localPosition.z = -5;
  foreground.transform.localPosition.z = 0;
  
  filler.pixelInset.y = position.y + fillerBottom.pixelInset.height - 1;
  
  // continuously update the color of the bar
  InvokeRepeating("SetColor", 1, 0.2);
}

function Update() {
  filler.pixelInset.height = Mathf.Lerp(filler.pixelInset.height, 900.0 * meterScalingFactor * (playerAttributes.eSenseValue / 100.0), Time.deltaTime * 1.0);
  fillerTop.pixelInset.y = position.y + fillerBottom.pixelInset.height + filler.pixelInset.height - 2;
}

function SetColor() {
  if(playerAttributes.eSenseType == Attributes.ESenseTypes.Attention)
    filler.color = fillerTop.color = fillerBottom.color = attentionColor;
  else
    filler.color = fillerTop.color = fillerBottom.color = meditationColor;
}
