var target : Transform;
var distance = 10.0;

var xSpeed = 250.0;
var ySpeed = 120.0;

var yMinLimit = -20;
var yMaxLimit = 80;

private var x = 0.0;
private var y = 0.0;

private var yPos : int;

private var yVelocity : float;

private var smoothedYPosition : float;

@script AddComponentMenu("Camera-Control/Mouse Orbit")

function Start () {
    var angles = transform.eulerAngles;
    x = angles.y;
    y = angles.x;

	// Make the rigid body not change rotation
   	if (rigidbody)
		  rigidbody.freezeRotation = true;
		
		if(target)
		  ReorientCamera(true);
}

function LateUpdate () {
  // cast to an int so we can compare approximate values
  var targetYPos : int = target ? target.position.y * 100 : 0;
  
  // only rotate if the right mouse button is clicked or if there was a y displacement
  if((target && Input.GetMouseButton(1)) || (target && (yPos != targetYPos)))
    // if the mouse button is not held down, just reposition the camera vertically without
    // interpreting mouse input
    ReorientCamera(Input.GetMouseButton(1));
    
  yPos = target ? smoothedYPosition * 100 : 0;
}

function ReorientCamera(receiveMouseInput : boolean){
  if(receiveMouseInput){
    x += Input.GetAxis("Mouse X") * xSpeed * 0.02;
    y -= Input.GetAxis("Mouse Y") * ySpeed * 0.02;
  }
	
	y = ClampAngle(y, yMinLimit, yMaxLimit);
	   
  var rotation = Quaternion.Euler(y, x, 0);
  var position = rotation * Vector3(0.0, 0.0, -distance) + target.position;
  
  smoothedYPosition = Mathf.SmoothDamp(transform.position.y, position.y, yVelocity, 0.35);
  
  transform.rotation = rotation;
  
  // use the smoothed y position only if it's on "auto control" (i.e. the user isn't clicking
  // the right mouse button to pan the camera)
  transform.position = Vector3(position.x, 
                               receiveMouseInput ? position.y : smoothedYPosition, 
                               position.z);
}

static function ClampAngle (angle : float, min : float, max : float) {
	if (angle < -360)
		angle += 360;
	if (angle > 360)
		angle -= 360;
	return Mathf.Clamp (angle, min, max);
}