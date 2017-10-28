var timeBeforeIdle : float = 2.5;
var lastMousePosition : Vector3;
var lastMouseMoveTime : float;

function Update () {
  if(Time.time > lastMouseMoveTime + timeBeforeIdle)
    UserStatus.isMouseIdle = true;
  else
    UserStatus.isMouseIdle = false;
}

function OnGUI(){
  var currentMousePosition = Event.current.mousePosition;
  
  if(currentMousePosition != lastMousePosition)
    lastMouseMoveTime = Time.time;
    
  lastMousePosition = currentMousePosition;
}