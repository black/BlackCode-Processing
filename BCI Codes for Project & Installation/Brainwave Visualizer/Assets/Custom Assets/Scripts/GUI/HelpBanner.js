var overlay : Texture2D;
var overlayColor : Color;

var position : Vector3 = Vector3(256, 128, 0);

private var headerHeight : int = 0;
private var headerStyle : GUIStyle;
private var descriptionHeight : int = 0;
private var descriptionStyle : GUIStyle;
private var langButtonStyle : GUIStyle;
private var buttonStyle : GUIStyle;
private var boxWidth : int = 512;
private var realOverlayColor : Color;
private var textColor : Color = Color.white;

private var boxPadding : int = 10;
private var textPadding : int = 10;

private var tempSkin : GUISkin;

private var header : String;
private var description : String;

private var languageIndex : int;
private var lastLanguageIndex : int;

function Awake(){
  realOverlayColor = overlayColor;
}

function Start(){
  OnLanguageChanged();

  languageIndex = Localizer.appLanguage;
  lastLanguageIndex = languageIndex;
}

function OnLanguageChanged(){
  tempSkin = Localizer.Skins["main"];

  headerStyle = tempSkin.GetStyle("header");
  descriptionStyle = tempSkin.GetStyle("Label");

  header = Localizer.Content["opening"]["header"];
  description = Localizer.Content["opening"]["content"];

  headerHeight = headerStyle.CalcHeight(GUIContent(header), boxWidth);
  descriptionHeight = descriptionStyle.CalcHeight(GUIContent(description), boxWidth);

  langButtonStyle = tempSkin.GetStyle("langbutton");
  buttonStyle = tempSkin.GetStyle("Button");

  langButtonStyle.normal.background = buttonStyle.normal.background;
  langButtonStyle.hover.background = buttonStyle.hover.background;
  //langButtonStyle.active.background = buttonStyle.active.background;
  //langButtonStyle.focused.background = buttonStyle.focused.background;
}

function DisableBanner(){
  yield FadeOut(0.0, 0.5);
  UserStatus.hasBannerDisplayed = true;
}

function Update(){
  if(languageIndex != lastLanguageIndex){
    var language : Localizer.Language = languageIndex;

    lastLanguageIndex = languageIndex;

    Localizer.SetLanguage(language);
  }
}

function OnGUI(){
  GUI.depth = 1;
  GUI.skin = tempSkin;
 
  if(!UserStatus.hasBannerDisplayed && !UserStatus.isMenuDisplayed){
    // store the original transformation matrix
    var oldMatrix = GUI.matrix;
    GUI.matrix = LayoutHelper.PlacementMatrix();

    GUI.color = realOverlayColor;

    GUI.DrawTexture(Rect(0, 0, 1024, 640), overlay);

    GUI.color = textColor;
    GUI.Label(Rect(position.x, position.y, boxWidth, headerHeight), 
              header, headerStyle);
    GUI.Label(Rect(position.x, position.y + headerHeight + textPadding, boxWidth, descriptionHeight), 
              description, descriptionStyle);

    // the "continue" button
    if(GUI.Button(Rect(position.x, position.y + headerHeight + descriptionHeight + textPadding + 20, 100, 30), Localizer.Content["opening"]["continue"])){
      DisableBanner();
      Event.current.Use();
    }

    if(Localizer.isFirstTimeLoading){
      // language selection buttons
      GUI.Label(Rect(position.x + boxWidth - 400, position.y + headerHeight + descriptionHeight + textPadding + 25,
                     boxWidth, 30), Localizer.Content["opening"]["language"] + ":", descriptionStyle);

      languageIndex = GUI.SelectionGrid(Rect(position.x + boxWidth - 320, position.y + headerHeight + descriptionHeight + textPadding + 20, 320, 60), 
                                        languageIndex, 
                                        [Localizer.Content["opening"]["optionen"], 
                                         Localizer.Content["opening"]["optionjp"], 
                                         Localizer.Content["opening"]["optionzh-cn"],
                                         Localizer.Content["opening"]["optionzh-tw"],
                                         Localizer.Content["opening"]["optionko"]], 3, langButtonStyle);
    }
  
    GUI.matrix = oldMatrix;
  }
}

function FadeOut(waitTime : float, fadeTime : float) : IEnumerator {
  yield WaitForSeconds(waitTime);
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
