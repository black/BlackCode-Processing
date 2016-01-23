#pragma strict

var enableAudio : boolean = true;
var enableDescriptions : boolean = true;
var enableMouseHover : boolean = false;

var overlay : Texture;

var overlayColor : Color;

private var translatedBounds : Rect[];
private var initialBounds : Rect[];
private var scalingFactor : float;

private var enableHelpMode : boolean = false;

private var activeBoundIndex : int = 0;

private var audioSource : AudioSource;

private var helpRect : Rect = Rect(20, 40, 450, 400);

private var realOverlayColor : Color;
private var textColor : Color;

private var tempSkin : GUISkin;

private var titleStyle : GUIStyle;
private var descriptionStyle : GUIStyle;

private var controller : HelpController;

function Awake(){
  audioSource = gameObject.AddComponent(AudioSource);
  controller = GetComponent(HelpController);
}

function Start(){
  OnLanguageChanged();

  initialBounds = new Rect[controller.components.Length];
  
  for(var i = 0; i < controller.components.Length; i++){
    var bound : Rect = controller.components[i].bounds;
    
    initialBounds[i] = new Rect(bound.x * LayoutHelper.ORIGINAL_WIDTH, 
                                bound.y * LayoutHelper.ORIGINAL_HEIGHT,
                                bound.width * LayoutHelper.ORIGINAL_WIDTH, 
                                bound.height * LayoutHelper.ORIGINAL_HEIGHT);
  }
  
  InvokeRepeating("Relayout", 0, 0.5);
}

function OnLanguageChanged(){
  tempSkin = Localizer.Skins["main"];

  titleStyle = tempSkin.GetStyle("header");
  descriptionStyle = tempSkin.GetStyle("Label");
}

function Relayout(){
  translatedBounds = ResizeBounds();
}

function ResizeBounds() : Rect[] {
  var tempBounds : Rect[] = new Rect[controller.components.Length];
  
  for(var i = 0; i < controller.components.Length; i++)
    tempBounds[i] = ResizeBound(controller.components[i].bounds);
    
  return tempBounds;
}

function ResizeBound(normalizedRect : Rect){
  var aspectRatioFactor : float = (Screen.width + 0.0) / (Screen.height * 1.77778);
 
  var shiftedScreenHeight : float = Screen.height * aspectRatioFactor;
  var offset : float = (Screen.height - shiftedScreenHeight) / 2.0;
  
  return Rect(normalizedRect.x * Screen.width, 
              offset + (shiftedScreenHeight * normalizedRect.y),
              normalizedRect.width * Screen.width, 
              normalizedRect.height * shiftedScreenHeight);
}

/**
 * This method checks to see whether the user clicked on an active region.
 */
function CursorRegion(mousePosition : Vector3){
  var translatedMousePosition : Vector3 = Vector3(Input.mousePosition.x, Screen.height - Input.mousePosition.y, 0);
  
  for(var i = 0; i < translatedBounds.Length; i++){
    if(translatedBounds[i].Contains(translatedMousePosition))
      return i;
  }
  
  return -1;
}

function FadeIn(waitTime : float, fadeTime : float) : IEnumerator {
  yield WaitForSeconds(waitTime);
  realOverlayColor = Color.clear;
  textColor = Color.clear;
  
  var elapsedTime : float = fadeTime;
  
  while(elapsedTime > 0.0){
   elapsedTime -= Time.deltaTime;
   realOverlayColor = Color.Lerp(overlayColor, Color.clear, elapsedTime / fadeTime);
   textColor = Color.Lerp(Color.white, Color.clear, elapsedTime / fadeTime);
   yield; 
  }
}

function FadeOut(fadeTime : float) : IEnumerator {
  realOverlayColor = overlayColor;
  textColor = Color.white;
  
  var elapsedTime : float = fadeTime;
  
  while(elapsedTime > 0.0){
   elapsedTime -= Time.deltaTime;
   realOverlayColor = Color.Lerp(Color.clear, overlayColor, elapsedTime / fadeTime);
   textColor = Color.Lerp(Color.clear, Color.white, elapsedTime / fadeTime);
   yield; 
  }
}

/**
 * This method performs all the handling for when the user clicks on 
 * a region (e.g. displaying the description text and starting up the
 * audio).
 */
function StartHelp(region : int){
  if(!enableHelpMode)
    FadeIn(0.0, 0.5);
  
  enableHelpMode = true;
  activeBoundIndex = region;
  
  if(enableAudio){
    // start up the audio
    audioSource.Stop();
    audioSource.clip = controller.components[region].audio;
    audioSource.Play();
  }
  
  // we force a delay in here so that the ModeSelector buttons can
  // receive any click events that might otherwise have been ignored
  // due to disabling them (via isHelpEnabled = true)
  yield WaitForSeconds(0.2);
  UserStatus.isHelpEnabled = true;
}

/**
 * Clean up any help-related behaviors.
 */
function EndHelp(){
  FadeOut(0.5);
  
  enableHelpMode = false;
  
  if(enableAudio)
    audioSource.Stop();
  
  UserStatus.isHelpEnabled = false;
}

function OnClicked(position : Vector3){
  // figure out which region the cursor is over
  var i = CursorRegion(position);
  
  // if a click event was invoked, handle accordingly
  if(UserStatus.hasBannerDisplayed && !UserStatus.isMenuDisplayed && 
     Event.current && Event.current.type == EventType.MouseDown && Event.current.button == 0){
    
    // they clicked on a new active region
    if(i != -1 && !enableHelpMode)
      StartHelp(i);
    // check to see if the user clicked completely outside any active regions, or if 
    // they clicked on the same region that's currently activated
    else if(enableHelpMode) 
      EndHelp();
  }
  // handle hovers
  else if(enableMouseHover && !UserStatus.isMenuDisplayed && !enableHelpMode && !UserStatus.isMouseIdle){
    DrawExclusion(Color(0, 0, 0, 0.6), translatedBounds[i]);
  }
  else {
    if(UserStatus.isMenuDisplayed && enableAudio && audioSource.isPlaying)
      audioSource.Stop();
  }
}

function OnGUI(){
  GUI.depth = 3;
  
  /*
   * now draw the *exclusion* of the bounds
   */
   
  // and determine the region bounds
  var bound : Rect = translatedBounds[activeBoundIndex];
  var initialBound : Rect = initialBounds[activeBoundIndex];
   
  if(UserStatus.hasBannerDisplayed && !UserStatus.isMenuDisplayed){

    DrawExclusion(realOverlayColor, bound);

    // now let's figure out where to draw the text
    if(enableDescriptions){
      var component : HelpComponent = controller.components[activeBoundIndex];
      var title : String = Localizer.Content["helpheaders"][component.key];
      var description : String = Localizer.Content["helpcontent"][component.key];

      GUI.color = textColor;

      // store the original transformation matrix
      var oldMatrix = GUI.matrix;
      GUI.matrix = LayoutHelper.PlacementMatrix();

      // set some padding to make things look nicer
      var padding : int = 20;

      var titleHeight : int = titleStyle.CalcHeight(GUIContent(title), helpRect.width);
      var descHeight : int = descriptionStyle.CalcHeight(GUIContent(description), helpRect.width);

      // set the text area height to be the calculated combined total of the title and description height
      helpRect.height = titleHeight + descHeight + 50;

      // let's figure out positioning
      helpRect.y = initialBound.y + initialBound.height - helpRect.height;
      helpRect.x = helpRect.width >= initialBound.x ? initialBound.x + initialBound.width + padding : initialBound.x - helpRect.width - padding;

      // now draw everything
      GUILayout.BeginArea(helpRect);
      GUILayout.Label(title, titleStyle);
      GUILayout.Label(description, descriptionStyle);
      GUILayout.EndArea();

      GUI.matrix = oldMatrix;
    }
  }
}

function DrawExclusion(overlayColor : Color, bound : Rect){
  GUI.color = overlayColor;
   
  // draw the top rectangle
  GUI.DrawTexture(Rect(0, 0, bound.x + bound.width, bound.y), overlay);

  // draw the left rectangle
  GUI.DrawTexture(Rect(0, bound.y, bound.x, Screen.height - bound.y), overlay);

  // draw the bottom rectangle
  GUI.DrawTexture(Rect(bound.x, bound.y + bound.height, Screen.width - bound.x, Screen.height - (bound.y - bound.height)), overlay);

  // drop the right rectangle
  GUI.DrawTexture(Rect(bound.x + bound.width, 0, Screen.width - (bound.x + bound.width), bound.y + bound.height), overlay);
  
}

@script RequireComponent(HelpController)
