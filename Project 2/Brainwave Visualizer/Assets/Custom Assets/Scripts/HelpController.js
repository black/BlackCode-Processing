
class HelpComponent {
  public var key : String;
  public var bounds : Rect;
  public var audio : AudioClip;
}

public var components : HelpComponent[];

private var gui : HelpGUI;

function Awake(){
  gui = GetComponent(HelpGUI);
}

function OnGUI(){
  GUI.depth = 11;

  if(UserStatus.hasBannerDisplayed && !UserStatus.isMenuDisplayed && 
     Event.current && Event.current.type == EventType.MouseDown && Event.current.button == 0){
    
    gui.OnClicked(Input.mousePosition); 
  }
}
@script RequireComponent(HelpGUI)
