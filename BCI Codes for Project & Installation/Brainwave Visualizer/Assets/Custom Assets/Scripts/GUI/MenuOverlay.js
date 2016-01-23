var overlay : Texture2D;
var overlayColor : Color;

function OnGUI(){
  GUI.depth = 1;
  if(UserStatus.isMenuDisplayed){
    GUI.color = overlayColor;
    GUI.DrawTexture(Rect(0, 0, Screen.width, Screen.height), overlay);
  }
}
