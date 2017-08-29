#pragma strict

var visualizationDisplay : GUIText;

var showCenterPoint : boolean;

var powerSpectrumGraph : PowerSpectrumGraph;

var normalizedRect : Rect;

// use this to store data from the headset and get information like 
// average value and average amplitude 
private var data : GraphData[];

// the number of axes we need
private var axesCount : int = 8;                // the number of anchor points

private var segmentCount : float = 20.0;        // the resolution of the curve joining the two anchor points

// the line that's drawn
private var line : LineRenderer;

// the target anchor points
private var targetAnchor : Vector3[];

// the current control and anchor points
private var currentControlLeft : Vector3[];
private var currentControlRight : Vector3[];
private var currentAnchor : Vector3[];

// the velocities of the anchor points (for smooth currentVisualization.lineDamping)
private var anchorVelocityX : float[];
private var anchorVelocityY : float[];

// the main curve rendered on the screen (that moves)
private var mainCurve : Vector3[];

// the number of vertices in the curve
private var vertexCount : int;

// the range of the curve
private var extent : float;

// the center point of the screen
private var centerPoint : Vector3;

private var offset : Vector3;

// the distance of the object from the camera (affects scaling / size)
private var cameraDistanceVector : Vector3;

// target color
private var targetColor : Color;

// current color
var currentColor : Color;

// keep track of old curves (the frozen, expanding ones)
private var oldCurves : Array;
private var oldLines : Array;
private var oldCurveColors : Array;
private var oldLineObjects : Array;

// keep track of all visualizations
private var visualizations : VisualizationParameters[];
private var visualizationIndex : int = 0;
private var currentVisualization : VisualizationParameters;

private var centerPointObject : GameObject;

private var client : DataController;

private var mainCamera : Camera;

function Awake(){
  data = new GraphData[axesCount];

  for(var j : int = 0; j < data.Length; j++)
    data[j] = new GraphData(20);

  // find all the possible parameters
  var paramComponents = GetComponentsInChildren(VisualizationParameters);
  
  visualizations = new VisualizationParameters[paramComponents.Length];
  
  // a copy is done into a typed array to enforce types, so we don't get any
  // weird weak-typing behavior
  for(var i = 0; i < paramComponents.Length; i++)
    visualizations[i] = paramComponents[i];

  // set up the line
  var lineObject = GameObject("MainLine");
  line = lineObject.AddComponent(LineRenderer);
  
  currentControlLeft = new Vector3[axesCount];
  currentControlRight = new Vector3[axesCount];
  currentAnchor = new Vector3[axesCount];

  targetAnchor = new Vector3[axesCount];
  
  anchorVelocityX = new float[axesCount];
  anchorVelocityY = new float[axesCount];
  
  vertexCount = axesCount * segmentCount + 1;
  
  mainCurve = new Vector3[vertexCount];
  
  oldCurves = new Array();
  oldLines = new Array();
  oldCurveColors = new Array();
  oldLineObjects = new Array();

  client = GameObject.Find("ThinkGear").GetComponent(DataController);

  mainCamera = Camera.main;
  
  Relayout();
}

function Start(){
  // set the number of line segments
  line.SetVertexCount(vertexCount);
  
  // initialize the vectors
  for(var i = 0; i < axesCount; i++){
    currentControlLeft[i] = Vector3.zero;
    currentControlRight[i] = Vector3.zero;
  }
  
  StartVisualization();
  
  // set up the target and current points
  SetTargetPoints();
  InitializeCurrentPoints();  
  SetCurrentPoints();
  
  InvokeRepeating("Relayout", 0.0, 0.5);
}

function Relayout(){
  centerPoint = Vector3((normalizedRect.width - normalizedRect.x) * Screen.width / 2.0, 
                        Screen.height - ((normalizedRect.height - normalizedRect.y) * Screen.height / 2.0), 0);
  
  extent = 200;
  /*
  extent = (Screen.height * normalizedRect.height) / (currentVisualization != null ? currentVisualization.lineViewportReduction : 2.5);
  */

  offset = centerPoint + cameraDistanceVector;
}

function Update(){
  SetCurrentPoints();
  CreateCurve(mainCurve, currentAnchor, currentControlLeft, currentControlRight);
  
  if(currentVisualization.growCapturedCurves)
    for(var j = 0; j < oldLines.length - 1; j++)
      ExpandCurve(oldCurves[j], Time.deltaTime * currentVisualization.growthSpeed);

  // lerp the current color to the target color, and then set the color
  currentColor = Color.Lerp(currentColor, targetColor, Time.deltaTime * 2);

  // render the main curve
  RenderCurve(mainCurve, line, currentColor);
  
  // render all the sub-curves
  for(var i = 0; i < oldLines.length - 1; i++)
    RenderCurve(oldCurves[i], oldLines[i], oldCurveColors[i]);
    
  // display the name of the visualization
  if(visualizationDisplay)
    visualizationDisplay.text = currentVisualization.visualizationName;
}

/**
 * Transition over to the next visualizatoon
 */
function NextVisualization(){
  CleanupVisualization();
  
  visualizationIndex = (visualizationIndex + 1) % visualizations.Length;
  
  StartVisualization();
}

/**
 * Transition over to the previous visualization
 */
function PreviousVisualization(){
  CleanupVisualization();
  
  visualizationIndex = (visualizationIndex + visualizations.Length - 1) % visualizations.Length;
  
  StartVisualization();
}

/**
 * Start the visualization
 */
function StartVisualization(){
  // set the default visualization
  currentVisualization = visualizations[visualizationIndex];
  
  // set up the current line color
  currentColor = currentVisualization.lineColors[0];
    
  // set up the material / width of the lines
  line.material = currentVisualization.lineMaterial;
  line.SetWidth(currentVisualization.lineWidth, currentVisualization.lineWidth);
  line.SetColors(currentColor, currentColor);
  
  // set up the distance from the camera
  cameraDistanceVector = Vector3(0, 0, currentVisualization.cameraDistance);
  
  InvokeRepeating("SetTargetPoints", 0, 1.0);
	InvokeRepeating("CaptureCurveState", 0, currentVisualization.captureInterval);
	
	if(powerSpectrumGraph){
	  for(var i = 0; i < 8; i++){
	    var newColor = currentVisualization.lineColors[i];
	    newColor.a = newColor.a * 0.7;
	    
	    powerSpectrumGraph.barColors[i] = newColor;
	  }
	}
}

/**
 * Perform visualization cleanup
 */
function CleanupVisualization(){
  CancelInvoke("SetTargetPoints");
  CancelInvoke("CaptureCurveState");
}

/**
 * Expand the curve by the specified amount.
 */
function ExpandCurve(curve: Vector3[], amount : float){
  for(var i = 0; i < curve.Length; i++)
    curve[i] += curve[i].normalized * amount;
}

/**
 * Render the curve specified by the Vector3[], using the LineRenderer object.
 */
function RenderCurve(curve : Vector3[], lineObject : LineRenderer, color : Color){
	lineObject.SetColors(color, color);
	lineObject.enabled = true;
	
	for(var i = 0; i < curve.Length; i++){
	  lineObject.SetPosition(i, 0.01 * curve[i]);
  }
}

/**
 * Sets up the curve from the current anchor and control points. The curve is calculated
 * piecewise using two anchor points (the start and the end) and two control points (affecting
 * the nature of the curve), which is passed into a method that calculates a cubic Bezier
 * curve. We iterate over every pair of consecutive anchor points to generate the full curve.
 *
 * For each anchor point, the -/+ control points always lie on the same line, so as to ensure
 * smoothness between each piece of the Bezier curve.
 */
function CreateCurve(curvePoints : Vector3[], anchorPoints : Vector3[], controlLeft : Vector3[], controlRight : Vector3[]){
  var anchorIndex = 0;
  var leftControlIndex = 0;
  var rightControlIndex = 1;
  var curveIndex = 0;
  
  for(var i = 0; i < anchorPoints.Length; i++){
    // set up the anchor point
    curvePoints[curveIndex++] = anchorPoints[anchorIndex];
    
    var targetAnchorIndex = (anchorIndex + 1) % axesCount;
    
    // now draw all the intermediary points, using the anchors and controls
    for(var j : float = 1.0; j < segmentCount; j++){
      curvePoints[curveIndex++] = CustomTransform.Bezier(anchorPoints[anchorIndex], 
                                                   controlLeft[leftControlIndex],
                                                   controlRight[rightControlIndex], 
                                                   anchorPoints[targetAnchorIndex], 
                                                   j / segmentCount);
    }
    
    // increment indices
    leftControlIndex = targetAnchorIndex;
    rightControlIndex = (targetAnchorIndex + 1) % axesCount;
    anchorIndex = targetAnchorIndex;
  }
  
  // join the tail to the head
  curvePoints[curveIndex] = anchorPoints[0];
}

/**
 * Sets up the current anchor points, so the curve is ready to go at application
 * execution (i.e., no need to expand from the origin point).
 */
function InitializeCurrentPoints(){
  for(var i = 0; i < axesCount; i++)
    currentAnchor[i] = targetAnchor[i];
}

/**
 * Sets up the target anchor points around an origin, where the distance of
 * the anchor point from the origin is determined by a random value. This is 
 * called every second via a callback method.
 */
function SetTargetPoints(){
  var angleIncrement : float = 2.0 * Mathf.PI / axesCount;
  var angle : float = 0.0;
  
  var maxIndex : int = 0;
  var max : float = 0;

  // grab the values
  var useValues : float[] = [Mathf.Log10(client.headsetData.delta),
                             Mathf.Log10(client.headsetData.theta),
                             Mathf.Log10(client.headsetData.lowAlpha),
                             Mathf.Log10(client.headsetData.highAlpha),
                             Mathf.Log10(client.headsetData.lowBeta),
                             Mathf.Log10(client.headsetData.highBeta),
                             Mathf.Log10(client.headsetData.lowGamma),
                             Mathf.Log10(client.headsetData.highGamma)];
    
  for(var i = 0; i < axesCount; i++){
    data[i].Add(useValues[i]);

    // freeze the shape outwards if we've detected off-head
    var scaleFactor = client.IsOffHead ? 1.0 : Mathf.Clamp((data[i].NormalizedValue * 0.4) + 0.6, 0.2, 1.0); 
    
    // figure out the axis on which the maximum value lies
    if(scaleFactor > max){
      max = scaleFactor;
      maxIndex = i;
    }
    
    // rotate about the origin
    targetAnchor[i] = CustomTransform.Rotate(angle, Vector3(0, extent * scaleFactor, 0));
    angle += angleIncrement;
  }
  
  targetColor = currentVisualization.lineColors[maxIndex];
}

/**
 * Capture the state of the curve, and put it into storage. Used for the radiating "frozen" lines.
 * This method also performs cleanup of the curve history.
 */
function CaptureCurveState(){
  // clean up curves if there are too many
  while(oldCurves.length >= currentVisualization.captureHistorySize){
    var oldLineRenderer : LineRenderer = oldLines.Pop();
    oldLineRenderer.enabled = false;
    
    oldCurves.Pop();
    Destroy(oldLineObjects.Pop());
    oldCurveColors.Pop();
  }
  
  // generate a new GameObject to represent the frozen curve
	var lineObject = GameObject("Line" + Time.time);
	var line : LineRenderer = lineObject.AddComponent(LineRenderer);
  
	line.material = currentVisualization.lineMaterial;
  line.SetWidth(currentVisualization.lineWidth, currentVisualization.lineWidth);
  line.SetColors(currentColor, currentColor);
  line.SetVertexCount(vertexCount);
	
	// plop it into the list of objects
	oldLineObjects.Unshift(lineObject);
	
	// push a clone of the curve onto the list
	oldCurves.Unshift(mainCurve.Clone());
	
	// keep track of the LineRenderer used
	oldLines.Unshift(line);
	
	// keep track of the color of the line
	oldCurveColors.Unshift(currentColor * Color(1, 1, 1, currentVisualization.captureColorMultiplicative));
	
	// finally, disable the line until we start rendering it
	// this is to clamp down on rendering any weird "artifacted" lines
	line.enabled = false;
}

/**
 * Sets up the current anchor points by damping the movement of the target anchor points. The
 * left and right control points are then calculated from the current anchor points.
 */
function SetCurrentPoints(){
  var smoothness : float = (mainCamera.pixelWidth / 1200.0) * currentVisualization.lineSmoothness;
  
  for(var i = 0; i < axesCount; i++){    
    // figure out the position of the current anchor points
    currentAnchor[i].x = Mathf.SmoothDamp(currentAnchor[i].x, 
                                          targetAnchor[i].x, 
                                          anchorVelocityX[i], 
                                          currentVisualization.lineDamping);

    currentAnchor[i].y = Mathf.SmoothDamp(currentAnchor[i].y, 
                                          targetAnchor[i].y, 
                                          anchorVelocityY[i], 
                                          currentVisualization.lineDamping);
    
    var anchor = currentAnchor[i];
    
    // now figure out the position of the current control points by determining the
    // vector perpendicular to the anchor vector, and then dropping points on either
    // side
    currentControlRight[i] = anchor + (Vector3(-anchor.y, anchor.x, 0) * (smoothness / anchor.magnitude));
    currentControlLeft[i] = anchor + (Vector3(anchor.y, -anchor.x, 0) * (smoothness / anchor.magnitude));
  }
}

/**
 * Draw a sphere (effectively a dot) at the specified (screen) position.
 */
function DrawPoint(point : Vector3){
  var newPoint = GameObject.CreatePrimitive(PrimitiveType.Sphere);
  newPoint.transform.localScale = Vector3(0.1, 0.1, 0.1);
  newPoint.transform.localPosition = mainCamera.ScreenToWorldPoint(point);
  
  return newPoint;
}
