var dragBox : Vector3;
var dragIndicator : Texture2D;
var dragIndicatorHover : Texture2D;

var minimumSize : Vector2 = Vector2(800, 600);

private var dragRect : Rect;

private var width : int;
private var height : int;

private var delta : Vector3;
private var referenceY : int;

private var isDraggingWindow : boolean = false;

private var isDisplayed : boolean = false;

private var activeIndicator : Texture2D;

function Awake(){
}

function Start(){
  activeIndicator = dragIndicator;
  
  width = Screen.width;
  height = Screen.height;
}

function Update(){
  isDisplayed = !(UserStatus.isMouseIdle || UserStatus.isMenuDisplayed || UserStatus.isHelpEnabled);
  
  if(!Screen.fullScreen){
    var p = Vector3(Input.mousePosition.x, Screen.height - Input.mousePosition.y, 0);

    if((p.y > (Screen.height - dragBox.y)) && 
       (p.x > (Screen.width - dragBox.x))){
    
      activeIndicator = dragIndicatorHover;
    
      if(Event.current != null && Event.current.type == EventType.MouseDown){
        isDraggingWindow = true;
        delta = Vector3(Screen.width - p.x, Screen.height - p.y, 0);
      }
         
    }
    else
      activeIndicator = dragIndicator;

    if(Event.current != null && Event.current.type == EventType.MouseUp){
      isDraggingWindow = false;
    }

    if(isDraggingWindow && Event.current.type == EventType.MouseDrag){
      var targetResolution : Vector2 = Vector2(p.x + delta.x, p.y + delta.y);
      
      if(targetResolution.x < minimumSize.x)
        targetResolution.x = minimumSize.x;
        
      if(targetResolution.y < minimumSize.y)
        targetResolution.y = minimumSize.y;
      
      Screen.SetResolution(targetResolution.x, targetResolution.y, false);
    }
  }
}

function OnGUI(){
  if(!Screen.fullScreen && isDisplayed){
    dragRect = Rect(Screen.width - dragBox.x, Screen.height - dragBox.y, dragBox.x, dragBox.y);
    GUI.DrawTexture(dragRect, activeIndicator);
  }
}