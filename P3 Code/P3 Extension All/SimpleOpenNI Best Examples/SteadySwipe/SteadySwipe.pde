import java.util.ArrayList;
import SimpleOpenNI.*;

import processing.opengl.*;

// Kinect
SimpleOpenNI      context;

XnVSessionManager sessionManager;

XnVSwipeDetector swipeDetector;
XnVFlowRouter     flowRouter;
PointDrawer       pointDrawer;

XnVSteadyDetector steadyDetector;

PVector handCursor;


void setup() {
    size (512, 668,OPENGL);
    smooth();
    
    
    // Kinect  
    context = new SimpleOpenNI(this);

    // mirror is by default enabled
    context.setMirror(true);

    // enable depthMap generation 
    if(context.enableDepth() == false)
    {
        println("Can't open the depthMap, maybe the camera is not connected!"); 
        exit();
        return;
    }

    // enable the hands + gesture
    context.enableGesture();
    context.enableHands();

    // setup NITE
    
    sessionManager = context.createSessionManager("Click,Wave", "RaiseHand");
    
    // swipe    
    swipeDetector = new XnVSwipeDetector(true);  
    swipeDetector.RegisterSwipeLeft(this); 
    swipeDetector.RegisterSwipeRight(this); 
    swipeDetector.RegisterPrimaryPointCreate(this);
    swipeDetector.RegisterPrimaryPointDestroy(this);
    swipeDetector.RegisterPointUpdate(this);
    swipeDetector.SetSteadyDuration(300);
    swipeDetector.SetYAngleThreshold(0.1);
    swipeDetector.SetMotionTime(500);
    sessionManager.AddListener(swipeDetector);
   
   //steadyDetector
   
   
    
   //http://kinectcar.ronsper.com/docs/nite/classXnVSteadyDetector.html#a862cac4c01134f496f0daf49d8156b9e
    // [in] nCooldownFrames Minimal number of frames after input start that steady is valid
    // [in] nDetectionDuration  Minimal number of frames to constitute steady
    // [in] fMaximumStdDevForSteady Standard deviation of points that is considered 'steady'
    // [in] strName Name of the control, for log purposes.
    
    steadyDetector = new XnVSteadyDetector(1,1,100);
    steadyDetector.SetDetectionDuration(200);
    // steadyDetector.SetMaximumStdDevForSteady(XnFloat fStdDev);
    // steadyDetector.SetMinimumStdDevForNotSteady(XnFloat fStdDev);
    
    
    sessionManager.AddListener(steadyDetector);   
    
    // wave
    pointDrawer = new PointDrawer();
    flowRouter = new XnVFlowRouter();
    flowRouter.SetActive(pointDrawer);
    sessionManager.AddListener(flowRouter);

}

void draw() {   
    // Kinect
     
    // update the cam
    context.update();
    // update nite
    context.update(sessionManager);
    // draw depthImageMap
    // image(context.depthImage(),0,0);
   
    background(255);
   
    pointDrawer.draw();
      
}

////////////////////////////////////////////////////////////////////////////////////////////
// session callbacks

void onStartSession(PVector pos){
    println("onStartSession: " + pos);
}

void onEndSession(){
    println("onEndSession: ");
}

void onFocusSession(String strFocus,PVector pos,float progress){
    println("onFocusSession: focus=" + strFocus + ",pos=" + pos + ",progress=" + progress);
}

////////////////////////////////////////////////////////////////////////////////////////////
// XnV callbacks

void onSwipeLeft(float fVelocity,float fAngle){
    println();
    println("--------------------------------------------------------------------------------"); 
    println("onSwipeLeft: fVelocity=" + fVelocity + " , fAngle=" + fAngle); 
}

void onSwipeRight(float fVelocity,float fAngle){
    println();
    println("--------------------------------------------------------------------------------"); 
    println("onSwipeRight: fVelocity=" + fVelocity + " , fAngle=" + fAngle); 
}

void onPrimaryPointCreate(XnVHandPointContext pContext,XnPoint3D ptFocus){
    println("onPrimaryPointCreate:");
}

void onPrimaryPointDestroy(int nID){
    println("onPrimaryPointDestroy: " + nID);
}

void onPointUpdate(XnVHandPointContext pContext){
    print("onPU");
}

// broadcasts the event to all listeners
 void OnSteadyDetected(int nId, float fStdDev){
     println("OnSteadyDetected");
 };
 void OnNotSteadyDetected(int nId, float fStdDev){
     println("OnNotSteadyDetected");
 };
