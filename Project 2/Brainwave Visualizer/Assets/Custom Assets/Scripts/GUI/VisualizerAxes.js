/**
 * This script draws a texture in the center of the bounding box of
 * the camera that it's attached to. The texture is assumed to be square,
 * and no cropping is performed.
 */

var axes: Texture;

private var axesRect : Rect;
private var controller : VisualizationController;

function Start(){
  controller = GetComponent(VisualizationController);
}

function Update(){
  // figure out the size (in pixels) of the smallest side
  var width : int = Screen.width * (controller.normalizedRect.width - controller.normalizedRect.x);
  var height : int = Screen.height * (controller.normalizedRect.height - controller.normalizedRect.y);
  var minSide : int = Mathf.Min(width, height);

  if(width < height){
    axesRect.x = controller.normalizedRect.x * Screen.width;
    axesRect.y = (controller.normalizedRect.y * Screen.height) + (height - minSide) / 2.0;
  }
  else {
    axesRect.x = (controller.normalizedRect.x * Screen.width) + (width - minSide) / 2.0;
    axesRect.y = controller.normalizedRect.y * Screen.height;
  }

  axesRect.width = axesRect.height = minSide;
}

function OnGUI(){
  GUI.depth = 10;
  GUI.Label(axesRect, axes);
}

@script RequireComponent(VisualizationController)
