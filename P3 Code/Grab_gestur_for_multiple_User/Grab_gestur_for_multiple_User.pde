import SimpleOpenNI.*;
import java.util.*;
SimpleOpenNI context;
Map<Integer, Boolean> openPalm= new HashMap<Integer, Boolean>();
Map<Integer, ArrayList<PVector>>  handPathList = new HashMap<Integer, ArrayList<PVector>>();
ArrayList<PVector> handPositions, prevHandPositions;
HashMap<Integer, String> handColorMap;
PImage handImg;
boolean  handsTrackFlag = false;
PVector      handMin = new PVector();
PVector      handMax = new PVector();
PVector      endPosClick;
PVector      currHand, prevHand;
int[] handArr;
float handThresh = 85;
float openThresh = 190;
PGraphics pg;
void setup() {
  size(displayWidth-100, displayHeight-100);
  handColorMap=new HashMap<Integer, String>();
  handPositions=new ArrayList<PVector>();
  prevHandPositions=new ArrayList<PVector>();
  context = new SimpleOpenNI(this);
  handImg=loadImage("hand.png");
  handArr=new int[3600];
  //maxv=0;
  //minv=context.depthWidth()*100;
  context.setMirror(true);
  context.enableDepth();
  context.enableRGB();
  context.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);
  context.enableHands();
  context.enableGesture();
  context.addGesture("RaiseHand");
  context.addGesture("Wave");
  context.addGesture("Click");
  context.setMirror(true);

  handPositions=new ArrayList<PVector>();
  prevHandPositions=new ArrayList<PVector>();

  pg = createGraphics(width, height);
}

void draw() {
  background(-1);
  context.update();
  //////-------------------------Hand Tracking-----------------------------------------------////
  PVector realWorldPoint;
  pg.beginDraw();
  if (handPathList.size() > 0)  
  {    
    PVector pa2d = new PVector();
    PVector pr2d = new PVector();
    PVector last2d = new PVector();
    PVector dripC=new PVector();

    // get all hand ids and hand positions 
    Collection<ArrayList<PVector>> value=handPathList.values();
    List<ArrayList<PVector>> values=new ArrayList<ArrayList<PVector>>(value);
    Set<Integer> keySet=handPathList.keySet();
    List<Integer> keys=new ArrayList<Integer>(keySet);

    Iterator itr = handPathList.entrySet().iterator();    
    int sizex, sizey=20;


    // loop for each and every hand availble in the context
    for (int j=0;j<keys.size();j++) {

      int handId =  (Integer)keys.get(j);
      //get handPositions for each hands
      ArrayList<PVector> vecList = (ArrayList<PVector>)values.get(j);
      PVector handPos=new PVector();
      PVector handPosNxt;
      PVector handPosRealWorld=new PVector();
      PVector handPosNxtRealWorld=new PVector();
      PVector lastP;


      if (vecList!=null)
        if (vecList.size()>0)
          //get the latest hand poistions 
          handPos = (PVector) vecList.get(0);
      handPosNxt=handPos;
      lastP=handPos;
      if (vecList!=null)
        if (vecList.size()>1)
          handPosNxt=(PVector) vecList.get(1);

      //////------------------------------------------------Hand Grabbing Single or Mulitple--------------------------------------------------------------------///       
      // multiple hand grabbing same as single hand grab the thing is in here we iterate thorugh each and every hand availble in the context
      if (handsTrackFlag)  
      {
        handMin = handPos.get();
        handMax = handPos.get();
        int[]   depthMap = context.depthMap();
        int     steps   = 5;  
        int     index;

        for (int y=0;y < context.depthHeight();y+=steps)
        { 
          for (int x=0;x < context.depthWidth();x+=steps)
          {
            index = x + y * context.depthWidth();
            if (depthMap[index] > 0)
            { 
              realWorldPoint = context.depthMapRealWorld()[index];
              if (realWorldPoint.dist(handPos) < handThresh) {
                point(realWorldPoint.x, realWorldPoint.y, realWorldPoint.z); 
                if (realWorldPoint.x < handMin.x) handMin.x = realWorldPoint.x;
                if (realWorldPoint.y < handMin.y) handMin.y = realWorldPoint.y;
                if (realWorldPoint.z < handMin.z) handMin.z = realWorldPoint.z;
                if (realWorldPoint.x > handMax.x) handMax.x = realWorldPoint.x;
                if (realWorldPoint.y > handMax.y) handMax.y = realWorldPoint.y;
                if (realWorldPoint.z > handMax.z) handMax.z = realWorldPoint.z;
              }
            }
          }
        }
        // we update a openPalm hash map by changing the values of handID against to openpalm true false status
        // so in here it'll update each and every hand's palm open and close status

        float hDist = handMin.dist(handMax);
        if (hDist > openThresh) {
          //flag=0;
          openPalm.put(handId, false);
          print(handId +  " open" );
        }
        else {
          // flag=1;
          openPalm.put(handId, true);
          print(handId +  " close" );
        }
      } 

      context.convertRealWorldToProjective(handPosNxt, handPosNxtRealWorld);
      context.convertRealWorldToProjective(handPos, handPosRealWorld);

      if (openPalm.get(handId)!=null) {
        if (openPalm.get(handId)) {
          pg.stroke(0);
          float linex=map(handPosRealWorld.x, 0, 640, 0, width);
          float liney=map(handPosRealWorld.y, 0, 480, 0, height);
          float linepx=map(handPosNxtRealWorld.x, 0, 640, 0, width);
          float linepy=map(handPosNxtRealWorld.y, 0, 480, 0, height);
          pg.line(linex, liney, linepx, linepy);
        }
      }




      int cur_x_y=int(handPosRealWorld.x+handPosRealWorld.y);
      int pre_x_y=int(handPosNxtRealWorld.x+handPosNxtRealWorld.y);

      int pointcz=int(handPosRealWorld.z);
      int pointpz=int(handPosNxtRealWorld.z);



      // We remove the background color of the handimage first then we add our picked color to our hand cursor image........ 
      float handImgx=map(handPosRealWorld.x, 0, 640, 0, width);
      float handImgy=map(handPosRealWorld.y, 0, 480, 0, height);
      image(handImg, handImgx, handImgy);  
      handImg.loadPixels();
      int ccc=0;

      handImg.updatePixels();
    }
  }
  pg.endDraw();
  image(pg, 0, 0);
}
////////--------------------------------------- End of Draw Function.------------------------------


////////--------------------------------------- other methods.------------------------------


void onNewUser(int userId) {

  println("detected" + userId);
  //  users.add(userId);//a new user was detected add the id to the list
}

void onLostUser(int userId) {
  println("lost: " + userId);
}



// hand events

void onCreateHands(int handId, PVector position, float time) {
  println("onNewHand - handId: " + handId + ", pos: " + position);

  ArrayList<PVector> vecList = new ArrayList<PVector>();
  vecList.add(position);
  handsTrackFlag = true;
  //handVec = position;
  handPathList.put(handId, vecList);
}

void onUpdateHands(int handId, PVector position, float time) {
  //  println("onTrackedHand - handId: " + handId + ", pos: " + position );

  ArrayList<PVector> vecList = handPathList.get(handId);
  if (vecList != null)
  {

    vecList.add(0, position);
  }
}

void onDestroyHands(int handId, float time) {
  ArrayList<PVector> vecList = new ArrayList<PVector>();

  println("onLostHand - handId: " + handId);
  handPathList.remove(handId);
  context.addGesture("RaiseHand");
}

// -----------------------------------------------------------------
// gesture events

void onRecognizeGesture(String strGesture, PVector idPosition, PVector endPosition) {
  {
    if (strGesture.equals("RaiseHand")) {
      context.startTrackingHands(endPosition);

      context.removeGesture("RaiseHand");
    }
    context.convertRealWorldToProjective(endPosition, endPosition);
    if (strGesture.equals("Click")) {
      //splashVector.add(endPosition);
      endPosClick=endPosition;
      //splash(endPosition,sc);
      println("Click gestures executed");
      //context.startTrackingHands(endPosition);

      //context.removeGesture("RaiseHand");
    }

    if (strGesture.equals("Wave")) {

      //splash(endPosition,sc);
      println("Wave gestures executed");
      //context.startTrackingHands(endPosition);

      //context.removeGesture("RaiseHand");
    }
  }

  // -----------------------------------------------------------------
  // Keyboard event
}

