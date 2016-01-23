/**
 * This class implements the mode selector, which loads scenes based
 * on which button you press.
 */

// simple struct to store the contents of a mode selector button
class ModeButton {
  var key : String;
  var texture : Texture;
  var levelName : String;
}

var buttons : ModeButton[];

private var buttonStyle : GUIStyle;

private var isDisplayed : boolean = false;

private var tempSkin : GUISkin;

function Start(){
  OnLanguageChanged();
}

function OnLanguageChanged(){
  tempSkin = Localizer.Skins["main"];
  buttonStyle = tempSkin.GetStyle("dock");
}

function Update(){
  // hide the mode selector if the mouse is idle, if the help prompts are up, or
  // if the right mouse button is clicked
  if(UserStatus.isMouseIdle || UserStatus.isHelpEnabled || 
     UserStatus.isMenuDisplayed || Input.GetMouseButton(1)){
    isDisplayed = false;

    // also hide the mouse cursor while we're at it
    Screen.showCursor = UserStatus.isHelpEnabled || UserStatus.isMenuDisplayed;
  }
  else if(!UserStatus.hasBannerDisplayed){
    isDisplayed = false;
    Screen.showCursor = true;
  }
  // otherwise, draw the mode selector
  else {
    isDisplayed = true;
    Screen.showCursor = true;
  }
}

function OnGUI(){
  GUI.depth = 0;
    
  if(isDisplayed){
    GUI.color = Color.white;
    
    // set up the drawing space for the buttons.
    // vertically position the buttons 3/4 down the screen
    GUILayout.BeginVertical(GUILayout.Height(Screen.height));
    GUILayout.Space(Screen.height / 2);
    GUILayout.FlexibleSpace();
   
    // horizonally center the buttons
    GUILayout.BeginHorizontal(GUILayout.Width(Screen.width));
    GUILayout.FlexibleSpace();

    // draw each of the buttons
    for(var button : ModeButton in buttons){
      if(GUILayout.Button(GUIContent(Localizer.Content["dockmodes"][button.key], button.texture), buttonStyle))
        Application.LoadLevel(button.levelName);
    }

    GUILayout.FlexibleSpace();
    GUILayout.EndHorizontal();

    GUILayout.FlexibleSpace();
    GUILayout.EndVertical();
  }
}
