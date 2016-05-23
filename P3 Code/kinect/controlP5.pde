
void controlPanel(){
  int deltaX = 20;
  int deltaY = 50;
  int stepX = 93;
  int stepY = 30;
  
  ControlP5 controlP5 = new ControlP5(this);
  //controlP5.setAutoDraw(false);
  
  ControlWindow cw = controlP5.addControlWindow("Kinect control",0,0,400,160);
  cw.hideCoordinates();
  cw.setBackground(color(0));
  cw.activateTab("Constrain control");
  
  Tab t1 = controlP5.addTab(cw,"default");
  t1.setLabel("Constrain control");
  //t1.activateEvent(true);
  t1.setHeight(20);
  t1.setWidth(96);
  t1.getCaptionLabel().align(CENTER,CENTER);
    
  Range xLimsRange = controlP5.addRange("xRange",-8000,8000,xmin,xmax,deltaX,deltaY,300,20);
  xLimsRange.setCaptionLabel("X limits");
  xLimsRange.getCaptionLabel().align(ControlP5.RIGHT_OUTSIDE,CENTER).setPaddingX(10);
  xLimsRange.moveTo(t1);

  Range yLimsRange = controlP5.addRange("yRange",-8000,8000,ymin,ymax,deltaX,deltaY+stepY,300,20);
  yLimsRange.setCaptionLabel("Y limits");
  yLimsRange.getCaptionLabel().align(ControlP5.RIGHT_OUTSIDE,CENTER).setPaddingX(10);
  yLimsRange.moveTo(t1);

  Range zLimsRange = controlP5.addRange("zRange",-8000,8000,zmin,zmax,deltaX,deltaY+2*stepY,300,20);
  zLimsRange.setCaptionLabel("Z limits");
  zLimsRange.getCaptionLabel().align(ControlP5.RIGHT_OUTSIDE,CENTER).setPaddingX(10);
  zLimsRange.moveTo(t1);


  Tab t2 =  controlP5.addTab(cw,"Bands control");
  t2.activateEvent(true);
  t2.setHeight(20);
  t2.setWidth(96);
  t2.getCaptionLabel().align(CENTER,CENTER);

  Slider bandsResSlider = controlP5.addSlider("bandsResolution",1,10,resolution,deltaX,deltaY,300,20);
  bandsResSlider.setCaptionLabel("Resolution");
  bandsResSlider.getCaptionLabel().align(ControlP5.RIGHT_OUTSIDE,CENTER).setPaddingX(10);
  bandsResSlider.setNumberOfTickMarks(10);
  bandsResSlider.showTickMarks(false);
  bandsResSlider.setBehavior(new UpdateResolution());
  bandsResSlider.moveTo(t2);

  Slider bandsSepSlider = controlP5.addSlider("bandsSeparation",0,10,bandsSeparation,deltaX,deltaY+stepY,300,20);
  bandsSepSlider.setCaptionLabel("Separation");
  bandsSepSlider.getCaptionLabel().align(ControlP5.RIGHT_OUTSIDE,CENTER).setPaddingX(10);
  bandsSepSlider.setNumberOfTickMarks(11);
  bandsSepSlider.showTickMarks(false);
  bandsSepSlider.moveTo(t2);

  Toggle bandsRealToggle = controlP5.addToggle("bandsRealColor",realColor,deltaX,deltaY+2*stepY,20,20);
  bandsRealToggle.setCaptionLabel("Real color");
  bandsRealToggle.getCaptionLabel().align(ControlP5.RIGHT_OUTSIDE,CENTER).setPaddingX(10);
  bandsRealToggle.setBehavior(new UpdateRealColor());
  bandsRealToggle.moveTo(t2);

  Toggle followBandsToggle = controlP5.addToggle("followBands",follow,deltaX+stepX,deltaY+2*stepY,20,20);
  followBandsToggle.setCaptionLabel("Follow");
  followBandsToggle.getCaptionLabel().align(ControlP5.RIGHT_OUTSIDE,CENTER).setPaddingX(10);
  followBandsToggle.setBehavior(new UpdateFollow());
  followBandsToggle.moveTo(t2);
  
  Toggle explodeToggle = controlP5.addToggle("explode",explode,deltaX+2*stepX,deltaY+2*stepY,20,20);
  explodeToggle.setCaptionLabel("Explode");
  explodeToggle.getCaptionLabel().align(ControlP5.RIGHT_OUTSIDE,CENTER).setPaddingX(10);
  explodeToggle.moveTo(t2);
  
  Toggle disolveToggle = controlP5.addToggle("disolve",disolve,deltaX+3*stepX,deltaY+2*stepY,20,20);
  disolveToggle.setCaptionLabel("Disolve");
  disolveToggle.getCaptionLabel().align(ControlP5.RIGHT_OUTSIDE,CENTER).setPaddingX(10);
  disolveToggle.moveTo(t2);


  Tab t3 =  controlP5.addTab(cw,"Pixels control");
  t3.activateEvent(true);
  t3.setHeight(20);
  t3.setWidth(96);
  t3.getCaptionLabel().align(CENTER,CENTER);

  Slider pixelsResSlider = controlP5.addSlider("pixelsResolution",1,10,resolution,deltaX,deltaY,300,20);
  pixelsResSlider.setCaptionLabel("Resolution");
  pixelsResSlider.getCaptionLabel().align(ControlP5.RIGHT_OUTSIDE,CENTER).setPaddingX(10);
  pixelsResSlider.setNumberOfTickMarks(10);
  pixelsResSlider.showTickMarks(false);
  pixelsResSlider.setBehavior(new UpdateResolution());
  pixelsResSlider.moveTo(t3);

  Slider pixelSizeSlider = controlP5.addSlider("pixelSize",1,10,pixelSize,deltaX,deltaY+stepY,300,20);
  pixelSizeSlider.setCaptionLabel("Pixel size");
  pixelSizeSlider.getCaptionLabel().align(ControlP5.RIGHT_OUTSIDE,CENTER).setPaddingX(10);
  pixelSizeSlider.setNumberOfTickMarks(10);
  pixelSizeSlider.showTickMarks(false);
  pixelSizeSlider.moveTo(t3);

  Toggle pixelsRealToggle = controlP5.addToggle("pixelsRealColor",realColor,deltaX,deltaY+2*stepY,20,20);
  pixelsRealToggle.setCaptionLabel("Real color");
  pixelsRealToggle.getCaptionLabel().align(ControlP5.RIGHT_OUTSIDE,CENTER).setPaddingX(10);
  pixelsRealToggle.setBehavior(new UpdateRealColor());
  pixelsRealToggle.moveTo(t3);

  Toggle followPixelsToggle = controlP5.addToggle("followPixels",follow,deltaX+stepX,deltaY+2*stepY,20,20);
  followPixelsToggle.setCaptionLabel("Follow");
  followPixelsToggle.getCaptionLabel().align(ControlP5.RIGHT_OUTSIDE,CENTER).setPaddingX(10);
  followPixelsToggle.setBehavior(new UpdateFollow());
  followPixelsToggle.moveTo(t3);

  Toggle sandToggle = controlP5.addToggle("sandEffect",sandEffect,deltaX+2*stepX,deltaY+2*stepY,20,20);
  sandToggle.setCaptionLabel("Sand effect");
  sandToggle.getCaptionLabel().align(ControlP5.RIGHT_OUTSIDE,CENTER).setPaddingX(10);
  sandToggle.moveTo(t3);
  

  Tab t4 =  controlP5.addTab(cw,"Other options");
  //t4.activateEvent(true);
  t4.setHeight(20);
  t4.setWidth(96);
  t4.getCaptionLabel().align(CENTER,CENTER);

  Toggle sculptureToggle = controlP5.addToggle("drawSculpture",drawSculpture,deltaX,deltaY,20,20);
  sculptureToggle.setCaptionLabel("Sculpture");
  sculptureToggle.getCaptionLabel().align(ControlP5.RIGHT_OUTSIDE,CENTER).setPaddingX(10);
  sculptureToggle.moveTo(t4);

  Bang clearSculptureBang = controlP5.addBang("clearSculpture",deltaX+stepX,deltaY,20,20);
  clearSculptureBang.setCaptionLabel("Redraw sculpture");
  clearSculptureBang.getCaptionLabel().align(ControlP5.RIGHT_OUTSIDE,CENTER).setPaddingX(10);
  clearSculptureBang.moveTo(t4);
    
  Toggle sphereToggle = controlP5.addToggle("drawSphere",drawSphere,deltaX,deltaY+stepY,20,20);
  sphereToggle.setCaptionLabel("Sphere");
  sphereToggle.getCaptionLabel().align(ControlP5.RIGHT_OUTSIDE,CENTER).setPaddingX(10);
  sphereToggle.moveTo(t4);

  Bang clearSphereBang = controlP5.addBang("clearSphere",deltaX+stepX,deltaY+stepY,20,20);
  clearSphereBang.setCaptionLabel("Redraw sphere");
  clearSphereBang.getCaptionLabel().align(ControlP5.RIGHT_OUTSIDE,CENTER).setPaddingX(10);
  clearSphereBang.moveTo(t4);
    
  Toggle ballsToggle = controlP5.addToggle("drawBalls",drawBalls,deltaX,deltaY+2*stepY,20,20);
  ballsToggle.setCaptionLabel("Balls");
  ballsToggle.getCaptionLabel().align(ControlP5.RIGHT_OUTSIDE,CENTER).setPaddingX(10);
  ballsToggle.moveTo(t4);

  Toggle gridToggle = controlP5.addToggle("drawGrid",drawGrid,deltaX+stepX,deltaY+2*stepY,20,20);
  gridToggle.setCaptionLabel("Grid");
  gridToggle.getCaptionLabel().align(ControlP5.RIGHT_OUTSIDE,CENTER).setPaddingX(10);
  gridToggle.moveTo(t4);
    
  Toggle floorToggle = controlP5.addToggle("drawFloor",drawFloor,deltaX+2*stepX,deltaY+2*stepY,20,20);
  floorToggle.setCaptionLabel("Floor");
  floorToggle.getCaptionLabel().align(ControlP5.RIGHT_OUTSIDE,CENTER).setPaddingX(10);
  floorToggle.moveTo(t4);
}

class UpdateResolution extends ControlBehavior {
  public void update() {
    setValue(resolution); 
  }
}
  
class UpdateRealColor extends ControlBehavior {
  public void update() {
    setValue(int(realColor)); 
  }
}
  
class UpdateFollow extends ControlBehavior {
  public void update() {
    setValue(int(follow)); 
  }
}

void controlEvent(ControlEvent theControlEvent){
  if(theControlEvent.isFrom("xRange")){
    xmin = theControlEvent.getController().getArrayValue(0);
    xmax = theControlEvent.getController().getArrayValue(1);
  }
  if(theControlEvent.isFrom("yRange")){
    ymin = theControlEvent.getController().getArrayValue(0);
    ymax = theControlEvent.getController().getArrayValue(1);
  }
  if(theControlEvent.isFrom("zRange")){
    zmin = theControlEvent.getController().getArrayValue(0);
    zmax = theControlEvent.getController().getArrayValue(1);
  }
  if(theControlEvent.isFrom("bandsResolution") || theControlEvent.isFrom("pixelsResolution")){
    resolution = (int) theControlEvent.getController().getValue();
  }
  if(theControlEvent.isFrom("bandsRealColor") || theControlEvent.isFrom("pixelsRealColor")){
    int v = (int) theControlEvent.getController().getValue();
    realColor = boolean(v);
  }
  if(theControlEvent.isFrom("followBands") || theControlEvent.isFrom("followPixels")){
    int v = (int) theControlEvent.getController().getValue();
    follow = boolean(v);
  }
  if(theControlEvent.isFrom("Bands control")){
    drawBands = true;
    drawPixels = false;
  }
  if(theControlEvent.isFrom("Pixels control")){
    drawPixels = true;
    drawBands = false;
  }
  if(theControlEvent.isFrom("clearSculpture")){
    splinePoints = new Spline3D();
  }
  if(theControlEvent.isFrom("clearSphere")){
    PVector pos = new PVector(0,0,2000);
    PVector vel = new PVector(0,0,0);
    sph = new Ball(pos,vel,100);
  }
}


