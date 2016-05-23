//----------- Heuy Source Code-----------
// @Author - Kalindu Priyadarshana 
// @ Created  Date - 01 - JUL -13
// @ Modified Date - 01 - NOV -13
// Modification in the latest version - Full Screen Multiplayer
// Drabacks- Rendering is very slow. 
// 

// Necessary Imports. 
import SimpleOpenNI.*;
import java.util.Map;
import java.awt.Toolkit;
import processing.net.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import jcifs.util.Base64;
import java.util.*;
import processing.opengl.*;

//----Abhinav  MINIM IMPORTS ---------------
import ddf.minim.*;  
import ddf.minim.signals.*; 
//----------------------------------

SimpleOpenNI context;
Client c;
Server server;

//----------Abhinav audio objects ---------
Minim minim; 
AudioPlayer player, buttonplayer, userpop, handSound;
//-----------------------------------------

// Intialization

PVector      handMin = new PVector();
PVector      handMax = new PVector();
PVector      currHand, prevHand;

float handThresh = 85;
float openThresh = 190;

float transparency = 255;
float mm;

int   rad =12;
int   g;
int   b;
int   handVecListSize = 20;
int   blob_array[];
int   minv, maxv;
int   iconx, pos;
int   counter = 0;
int   losthand=0;
int   splashArray[]= {
  10, 15, 20
};

int   count; 
int   cont_length;
int   userCurID;
int   countTrack=0;
int   cont=0;
int   tk=0; // Abhinav 

boolean  loadsg=false;
boolean  wave, handup, drip=false;
boolean  start=false;
boolean  leftSwipe=false;
boolean  rightSwipe=false;
boolean  click=false;
boolean  handsTrackFlag = false;
boolean wasOutside = true; // abhinav

HashMap<Integer, String> hm;
HashMap<Integer, String> handColorMap;
Map<Integer, Boolean> openPalm= new HashMap<Integer, Boolean>();
Map<Integer, ArrayList<PVector>>  handPathList = new HashMap<Integer, ArrayList<PVector>>();

ArrayList<PVector> handPositions, prevHandPositions;
ArrayList<PVector> poop = new ArrayList<PVector>();

color final_color;
color finalc = color(#000000);
color sc;
color usericon=color(0, 255, 0);
color dc;
color picker;
color handC;
//red                          //green       //blue                     //yellow                //purple                            //cyan
color[] userColors = { 
  #00C1FA, #FA9600, #8DFA00, #FA004F, #A602D8, #02D888 // Abhinav changed the colors
}; 

PImage edgeImg;
PImage handopen, handclose; // abhinav

PGraphics pg;
PGraphics layer;

boolean sketchFullScreen() { // switch to fullscreen mode // java references
  return true;
}

void setup()
{
  //make sure you resized this according to your window size 
  size(displayWidth, displayHeight, P3D);
  pg = createGraphics(width, height); //drawing layer. 
  layer=createGraphics(width, height);  //all drawing stack into this layer except background. 
  server=new Server(this, 5234);  // enabling server 
  c = new Client(this, "172.21.168.42", 5001); // create a client instant if you are already started make sure you press a mouse click so it'll start to create a new client instant change client ip address.

  hm=new HashMap<Integer, String>();
  handColorMap=new HashMap<Integer, String>();
  handPositions=new ArrayList<PVector>();
  prevHandPositions=new ArrayList<PVector>();

  //kinect context
  context = new SimpleOpenNI(this);

  handopen=loadImage("handopen.png");
  handopen.resize(59, 59);

  handclose=loadImage("handclose.png");
  handclose.resize(49, 49);
  maxv=0;
  minv=context.depthWidth()*100;
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

  //other intialization 
  handPositions=new ArrayList<PVector>();
  prevHandPositions=new ArrayList<PVector>();
  cont_length=640*480;
  blob_array=new int[cont_length];

  //-------ABHINAV -----------------------
  GUIimages(); // GUIimages 
  minim = new Minim(this);
  buttonplayer = minim.loadFile("button.mp3");
  player = minim.loadFile("2.mp3");
  userpop = minim.loadFile("pop.mp3");
  handSound = minim.loadFile("handSound.mp3");
  //-------- MUSIC PAINT -------------------------
  out = minim.getLineOut(Minim.STEREO);
  sine = new SineWave(440, 0.5, out.sampleRate()); // here the sine is generated it has F (frequency) = 440Hz , 0.5 amplitude and sample rate for line out
  sine.portamento(200); //set the portamento speed on the oscillator to 200 milliseconds
  out.addSignal(sine);
  out.mute();
  //-------ABHINAV----------------------------
}


void draw() {
  background(-1);
  //starting to create a new layer for network drawing
  layer.beginDraw();
  handC=color(255, 255, 255);
  context.update();
  int[] depthValues = context.depthMap();

  PImage rgbImage = context.rgbImage();
  // prepare the color pixels
  rgbImage.loadPixels();

  // for user boundary identification 
  int[] userMap = null;
  int userCount = context.getNumberOfUsers();
  if (userCount > 0) {
    userMap = context.getUsersPixels(SimpleOpenNI.USERS_ALL);
  }

  loadPixels();

  PVector realWorldPoint;

  //////-------------------------........User Boundaries..........-----------------------------------------------////
  //marked user pixels 
  for (int y=0; y<context.depthHeight(); y++) {
    for (int x=0; x<context.depthWidth(); x++) {
      int index = x + y * context.depthWidth();
      //  println("index ss ="+ index+" x  ss= "+x);
      if (userMap != null && userMap[index] > 0) {
        //  println("index ="+ index+" x = "+x);

        int colorIndex = userMap[index] % userColors.length;
        userCurID= userMap[index];

        blob_array[index]=255;
        usericon=userColors[colorIndex];
      }
      else {
        blob_array[index]=0;
      }
    }
  }

  //------------------ take out the contour coordinates ---
  for (int x=0; x<context.depthWidth(); x++) {
    for (int y=0; y<context.depthHeight(); y++) {
      int i = x + y*context.depthWidth();
      if (userMap != null && userMap[i] > 0) {
        int cindex = userMap[i]%userColors.length;
        usericon = userColors[cindex];
        if ( i > 0 && blob_array[i-1]==0 && blob_array[i]==255) {
          //pixels[i-1] = usericon;
          float xx=map(x, 0, 640, 0, width);
          float yy=map(y, 0, 480, 0, height);
          PVector p1 = new PVector(xx, yy);
          poop.add(p1);
        }
        else if ( i+1 < cont_length && blob_array[i]==255 && blob_array[i+1]==0) {
          //pixels[i+1] = usericon;
          float xx=map(x, 0, 640, 0, width);
          float yy=map(y, 0, 480, 0, height);
          PVector p2 = new PVector(xx, yy);
          poop.add(p2);
        }
        //-------------------
        for (int l=0;l<poop.size();l++) {
          sortEdgeCoordinates(l);
        }
        //-------------------
      }
    }
  }

  for (int k=0; k<path.size(); k+=10) {
    PVector P1 = (PVector) path.get(k);
    color Col = (color) random(#000000);
    if ( userCount < userColors.length ) { 
      Col = userColors[userCount];
    }
    else userCount = 0;
    for (int j=k+1; j<path.size(); j+=10) {
      PVector P2 = (PVector) path.get(j);
      if (P1.dist(P2)<30) {
        stroke(Col, 10);
        strokeWeight(10);
        line(P1.x, P1.y, P2.x, P2.y);
        stroke(Col, 90);
        strokeWeight(2);
        line(P1.x, P1.y, P2.x, P2.y);
      }
    }
  }
  path.clear();
  poop.clear(); 

  //-----THIS IS FOR USER COLOR ICON ---------------------------------------------------

  if (hm.size()>0) {
    for (String c:hm.values()) { //  Abhinav ------------------------------------
      Kids.disableStyle();
      layer.fill(unhex(c));
      layer.noStroke();
      layer.shape(Kids, iconx, height-2*Kids.height/3, Kids.width/2, Kids.height/2);
      iconx=iconx+40;
    }
  }
  iconx=30;

  //////-------------------------Hand Tracking-----------------------------------------------////

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
      ;
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
      // multiple hand grabbing same as single hand grab the thing is in here we iterate thoruhg each and every hand availble in the context
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

        if (hDist > openThresh && !wasOutside) {
          //flag=0;
          openPalm.put(handId, false);
          wasOutside = true;
        }
        else if (hDist < openThresh) {
          // flag=1;
          openPalm.put(handId, true);
          wasOutside = false;
        }
      } 

      // mapping hand positions to real world values. 
      layer.pushStyle();
      context.convertRealWorldToProjective(handPosNxt, handPosNxtRealWorld);
      context.convertRealWorldToProjective(handPos, handPosRealWorld);

      // starting drawing layer
      pg.beginDraw();
      pg.noStroke();
      pg.strokeWeight(40);
      pg.fill(sc);

      float linex=map(handPosRealWorld.x, 0, 640, 0, width);
      float liney=map(handPosRealWorld.y, 0, 480, 0, height);
      float linepx=map(handPosNxtRealWorld.x, 0, 640, 0, width);
      float linepy=map(handPosNxtRealWorld.y, 0, 480, 0, height);
      //------------------------------------- ENABLE BUTTONS FOR LOOP ---------------------------------

      for (int iconPos=0;iconPos<2*width;iconPos+=width) {
        if (openPalm.get(handId)!=null && openPalm.get(handId) && dist(int(linex-20), int(liney-20), (iconPos==width)?width-MusicB.width:MusicB.width/2, height/2-MusicB.height/2-50 ) < MusicB.width/2 ) { // check if Icon gets selected or not
          musicmode =!musicmode;
          buttonplayer.play();
          if (musicmode) buttonplayer.rewind(); // only play music when buttons is getting on
        }

        if (openPalm.get(handId)!=null && openPalm.get(handId) && dist(int(linex-20), int(liney-20), (iconPos==width)?width-BrushB.width:BrushB.width/2, height/2 ) < BrushB.width/2 ) { // check if Icon gets selected or not
          drawmode =!drawmode;
          buttonplayer.play();
          if (drawmode) buttonplayer.rewind(); // only play music when buttons is getting on
        }

        if (openPalm.get(handId)!=null && openPalm.get(handId) && dist(int(linex-20), int(liney-20), (iconPos==width)?width-SplashB.width:SplashB.width/2, height/2+50+SplashB.height/2 ) < SplashB.width/2 ) { // check if Icon gets selected or not
          splashmode =!splashmode;
          buttonplayer.play();
          if (splashmode) buttonplayer.rewind(); // only play music when buttons is getting on
        }
      }

      //-------------------------------------MUSIC MODE ---------------------------------------
      if (openPalm.get(handId)!=null && openPalm.get(handId) &&  dist(int(linex-20), int(liney-20), width/2-MusicB.width/2-50, height-MusicB.height-10) < 50 ) { // check if Icon gets selected or not
        musicmode =!musicmode; 
        buttonplayer.play();
        if (musicmode)buttonplayer.rewind(); // only play music when buttons is getting on
      }

      if (musicmode) { 
        if (openPalm.get(handId)!=null) {
          if (openPalm.get(handId)) {
            if (liney < ( width-100))
            { 
              float freq = map(linex, 0, width, 1500, 60); // mapping mouseX --> (width value) position with freq varible
              sine.setFreq(freq); // the generated sine frequency as calculated in the previous line
            }
          }
        }
      }

      out.mute();

      //-------------------------------------DRAW MODE  ---------------------------------------
      if (openPalm.get(handId)!=null && openPalm.get(handId)  &&  dist(int(linex-20), int(liney-20), width/2-BrushB.width/2, height-BrushB.height-10) < 50 ) { // check if Icon gets selected or not
        drawmode =!drawmode;
        buttonplayer.play();
        if (drawmode) buttonplayer.rewind(); // only play music when buttons is getting on
      }

      if (drawmode) { // draw mode activated
        if (openPalm.get(handId)!=null) {
          if (openPalm.get(handId)) {
            if (!networkmode && !rootmode && !erasemode ) {
              pg.stroke(sc);
              pg.strokeWeight(2);
              if (musicmode) out.unmute();
              pg.line(linex, liney, linepx, linepy);
            }
          }
        }
        // --------------- enable root brush ------------------------------
        if (openPalm.get(handId)!=null && openPalm.get(handId)  &&  dist(int(linex-20), int(liney-20), width/2-BrushB.width/2-200, height-BrushB.height-10) < 50 ) { // check if Icon gets selected or not
          rootmode =!rootmode;
          networkmode = false;
          buttonplayer.play();
          if (rootmode) buttonplayer.rewind(); // only play music when buttons is getting on
        }

        // --------------- enable network brush ---------------------------
        if (openPalm.get(handId)!=null && openPalm.get(handId)  &&  dist(int(linex-20), int(liney-20), width/2-BrushB.width/2+200, height-BrushB.height-10) < 50 ) { // check if Icon gets selected or not
          networkmode =!networkmode;
          rootmode = false;
          buttonplayer.play();
          if (networkmode) buttonplayer.rewind(); // only play music when buttons is getting on
        }
        // --------------- enable Eraser  ---------------------------
        // --------------- Left Eraser  ------
        if (openPalm.get(handId)!=null && openPalm.get(handId)  &&  dist(int(linex-20), int(liney-20), BrushB.width/2, height/2) < 50 ) { // check if Icon gets selected or not
          erasemode =!erasemode;
          buttonplayer.play();
          if (erasemode) buttonplayer.rewind(); // only play music when buttons is getting on
        }
        // --------------- Right Eraser  ------
        if (openPalm.get(handId)!=null && openPalm.get(handId)  &&  dist(int(linex-20), int(liney-20), width-BrushB.width/2, height/2) < 50 ) { // check if Icon gets selected or not
          erasemode =!erasemode;
          buttonplayer.play();
          if (erasemode) buttonplayer.rewind(); // only play music when buttons is getting on
        }

        if (erasemode) { // draw mode activated
          if (openPalm.get(handId)!=null) {
            if (!openPalm.get(handId)) {
              layer.noFill(); 
              layer.stroke(0, 255, 0);
              eraseFunction(linex, liney);
            }
          }
        }
        //----------------------------------------------------------
        //----------------------------- root brush -----------
        pg.beginDraw();
        if (rootmode && openPalm.get(handId)!=null && openPalm.get(handId)) {
          //if () {
          //  if () { /////////////////////////////
          if (nodes.size() == 0)
            nodes.add(new Node(linex, liney));
          for (int i = 0; i < 10; ++i)
            grow(linex, liney);
          if (nodes.size() - lastRedraw > 50)
          {
            // redraw_all();  
            lastRedraw = nodes.size();
          }
          // }///////////////////////////////////////////////////////
          // }
        }
        pg.endDraw();
        // ------------------------ network brush-------------
        if (networkmode) {
          if (openPalm.get(handId)!=null) {
            if (openPalm.get(handId)) {
              pg.stroke(sc);
              pg.strokeWeight(2);
              PVector d = new PVector(linex, liney, 0);
              hist.add(0, d);
              for (int p = 0; p < hist.size(); p++) {
                PVector v = (PVector) hist.get(p);
                float joinChance = p/hist.size() +
                  d.dist(v)/joinDist;
                if (joinChance < random(0.4))
                  pg.line(d.x, d.y, v.x, v.y);
              }
            }
          }
        }
      }

      pg.endDraw();
      //adding line draw to layer stack
      layer.image(pg, 0, 0);
      layer.popStyle();

      //-------------------------------------SPLASH MODE ---------------------------------------

      if (openPalm.get(handId)!=null && openPalm.get(handId) && dist(int(linex)-20, int(liney)-20, width/2+50, height-SplashB.height-10 ) < 50 ) { // check if Icon gets selected or not
        splashmode =!splashmode;
        buttonplayer.play();
        if (splashmode) buttonplayer.rewind();
      }

      int cur_x_y=int(handPosRealWorld.x+handPosRealWorld.y);
      int pre_x_y=int(handPosNxtRealWorld.x+handPosNxtRealWorld.y);

      int pointcz=int(handPosRealWorld.z);
      int pointpz=int(handPosNxtRealWorld.z);

      //---------color picking-----------------------------------------------------------------------------///
      /// we checked our hand is on a stable mode for few seconds if our hand is stable if tracked that locations rbg image pixel color
      if (((int(handPosRealWorld.x+20))+int(handPosRealWorld.y-30)*context.depthWidth())>0) {
        picker= rgbImage.pixels[(int(handPosRealWorld.x+20))+int(handPosRealWorld.y-30)*context.depthWidth()];
        if (abs(pointcz-pointpz)<10) {
          countTrack++;
        }
        else {
          countTrack=0;
        }

        // if hand is stable then we put this color to a map so its store color details against to hand IDs
        if (countTrack>30) {

          handColorMap.put(handId, hex(picker));
        }
      }
      else {
        picker=color(255, 255, 255);
      }

      ///----------------------Splashing--------------------------------//
      ////---------------- We checked hands nearest z coordinates if there is a huge gap we identified it as a splashing gesture........
      // if there is splash we creaete new SplashC object which contect position details and color details about a splash then we add that to our Splash Vector. 
      color finalsc;
      if (splashmode) { // Abhinav 
        if (pointcz<pointpz-110) {
          //          SplashC sc= new SplashC();
          //          sc.setPosition(handPosRealWorld) ;
          for (float i=3; i<29; i+=.35) {
            Splash sp = new Splash((int)linex, (int)liney, i, splc);
            splatpoop.add(sp);
          }     

          if (handColorMap.get(handId)!=null ) {
            splc = unhex(handColorMap.get(handId));
          }
          else {
            splc = color(255, 0, 0);
          }
          //          splashVector.add(sc);
          player.play();  // abhinav add splash sound
          player.rewind();
          if (!player.isPlaying()) {
            player.pause();
            player.rewind();
          }
        }
      }
      pg.beginDraw();
      for (int i=0; i<splatpoop.size(); i++) { // abhinav Splash 
        Splash s = (Splash) splatpoop.get(i);
        s.display();
        s.update();
        if (s.y>height) splatpoop.remove(i);
      }

      pg.endDraw();
      if ( handColorMap.get(handId)!=null) {
        layer.fill( unhex(handColorMap.get(handId)));
        finalc= unhex(handColorMap.get(handId));
      }
      else {
        layer.fill(255, 255, 255);
        finalc=color(255, 255, 255);
      }

      /////--------------------------------------Update Hand Cursor Color-----------------------------------------//

      // We remove the background color of the handimage first then we add our picked color to our hand cursor image........ 
      float handImgx=map(handPosRealWorld.x, 0, 640, 0, width);
      float handImgy=map(handPosRealWorld.y, 0, 480, 0, height);
      layer.pushStyle();
      layer.imageMode(CENTER);
      if (openPalm.get(handId)!=null && !openPalm.get(handId)) layer.image(handopen, handImgx, handImgy+10);  
      if (openPalm.get(handId)!=null && openPalm.get(handId)) layer.image(handclose, handImgx, handImgy+10); 
      layer.popStyle(); 
      //      handopen.loadPixels();
      //      handclose.loadPixels();
      //--------------------Abhinav Modifications ----------------
      for (int x=0;x<handopen.width;x++) {
        for (int y=0;y<handopen.height;y++) {
          int i = x + y*handopen.width;
          if (handopen.get(x, y)<1) // I tried both black and white  img.get(x,y)==0
            handopen.pixels[i]=finalc;
        }
      }
      for (int x=0;x<handclose.width;x++) {
        for (int y=0;y<handclose.height;y++) {
          int i = x + y*handclose.width;
          if (handclose.get(x, y)<1) // I tried both black and white  img.get(x,y)==0
            handclose.pixels[i]=finalc;
        }
      }
      handopen.updatePixels();
      handclose.updatePixels();

      /// preview of color picking in here we create a preview of our color picking are so user can have clear idea where we picked the colors. 
      layer.copy(rgbImage, int(handPosRealWorld.x+20)-50, int(handPosRealWorld.y-30)-50, 50, 50, 1600, sizey, 200, 200);

      layer.pushMatrix();
      layer.pushStyle();

      layer.fill(255, 0, 0);
      float  x1 = map(handImgx+20, 0, width, 1600, 1800);
      float  y1 = map(handImgy-30, 0, height, sizey, sizey+100);
      sizey+=220;
      layer.popStyle();
      layer.popMatrix();

      layer.fill(picker);
      layer.ellipse(int(handImgx), int(handImgy)-30, 20, 20);     
      //      layer.fill(255, 0, 0);
      //      layer.ellipse(int(handImgx+20), int(handImgy-30), 8, 8);
      //      layer.ellipse(int(x1), int(y1), 4, 4);
      layer.fill(usericon);
      layer.ellipse(int(handImgx-50), int(handImgy)+50, 6, 6);
      layer.ellipse(int(handImgx+50), int(handImgy)+50, 6, 6);


      ///////----------------------------- Color Mixing ---------------------------------------------------/////
      // when there are more than one hands. 
      for (int k=0;k<keys.size();k++) {
        //get the next hand ID
        int handIdNxt =  (Integer)keys.get(k);
        if (handIdNxt!=handId) {
          ArrayList<PVector> vecListNxt = (ArrayList<PVector>)values.get(k);
          PVector lastPNxt=new PVector();
          if (vecListNxt!=null&&vecListNxt.size()>0)
            lastPNxt = vecListNxt.get(0);
          PVector last2dNxt = new PVector();
          context.convertRealWorldToProjective(lastPNxt, last2dNxt);
          int curr_x=int(handPosRealWorld.x);
          int next_x=int(last2dNxt.x);
          int curr_y=int(handPosRealWorld.y);
          int next_y=int(last2dNxt.y);

          // we checked whether nearest two hands x and y coorinates are overlapped if overlapped then there is color mixed we update each hands color according to mixed color
          if (abs(next_x-curr_x)<80) {
            if (abs(next_y-curr_y)<10) {
              color mixing1;
              color mixing2;
              if (handColorMap.get(handId)!=null) {
                mixing1=unhex(handColorMap.get(handId));
              }
              else {
                mixing1=color(255, 255, 255);
              }

              if (handColorMap.get(handIdNxt)!=null) {
                mixing2=unhex(handColorMap.get(handIdNxt));
              }
              else {
                mixing2=color(255, 255, 255);
              }

              color finalMix=mixing1+mixing2;

              handColorMap.put(handId, hex(finalMix));
              handColorMap.put(handIdNxt, hex(finalMix));


              println("hand ID - "+handId + "hand ID Next -"+handIdNxt);

              println("Color mixed");
            }
          }
        }
      }
      if (handColorMap.get(handId)!=null) {
        sc=unhex(handColorMap.get(handId));
      }
    }
  }
  layer.endDraw();
  //////------------------------------------------------------------------End of the Drawing.-----------------------------------------------////  

  //////------------------------------------------------------------------Starting Mulitplayer-----------------------------------------------////  

  //////------------------------------------------------------------------Acting as a client-----------------------------------------------////  
  PImage imga=null;

  int[] val= {
    0, 0
  };

  // we checked is our client is availble
  if (c.available() > 0) {
    // we read the string which our server is sends
    // we manapulate our string according to 64bit image bit streams
    String decodes=c.readStringUntil('*');
    String in = c.readStringUntil('$');
    if (decodes!=null) {
      String[] vals = splitTokens(decodes, "*" ); 
      BufferedImage bimg=new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB);
      byte[] imageByte;
      imageByte = Base64.decode(vals[0]);
      ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
      imageByte=null;

      try {
        bimg = ImageIO.read(bis);
        bis.close();
      } 
      catch( IOException ioe ) {
      }
      if (bimg!=null) {
        PImage img=new PImage(bimg.getWidth(), bimg.getHeight(), PConstants.ARGB);
        bimg.getRGB(0, 0, img.width, img.height, img.pixels, 0, img.width);
        img.updatePixels();
        // we update our reading as a image to our drawing sketch.
        image(img, 0, 0);
      }
    }
    decodes="";
  }

  //////------------------------------------------------------------------Acting as a Server-----------------------------------------------////  

  // we convert our sketch drawing ito 64bit encoded string then we transfer into our clients. 
  BufferedImage buffimga = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB);
  buffimga.setRGB( 0, 0, width, height, layer.pixels, 0, width);

  ByteArrayOutputStream baosa = new ByteArrayOutputStream();
  try {
    ImageIO.write( buffimga, "jpg", baosa );
    baosa.close();
  } 
  catch( IOException ioe ) {
  }
  String b64imagea = Base64.encode( baosa.toByteArray() );
  if (start) {
    server.write(b64imagea+"*");
  }
  b64imagea="";
  GUI();
  image(layer, 0, 0);
  layer.clear();
  image(GUILAYER, 0, 0);
}

////////--------------------------------------- End of Draw Function.------------------------------


////////--------------------------------------- other methods.------------------------------
void onNewUser(int userId) {
  hm.put(userId, hex(userColors[userId]));
  userpop.play();
  userpop.rewind();
  println("detected" + userId);
  //  users.add(userId);//a new user was detected add the id to the list
}

void onLostUser(int userId) {
  hm.remove(userId);
  println("lost: " + userId);
}

// hand events
void onCreateHands(int handId, PVector position, float time) {
  println("onNewHand - handId: " + handId + ", pos: " + position);
  handSound.play();
  handSound.rewind();
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
  losthand++;
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
      println("Click gestures executed");
      //context.startTrackingHands(endPosition);
      //context.removeGesture("RaiseHand");
    }

    if (strGesture.equals("Wave")) {
      println("Wave gestures executed");
      //context.startTrackingHands(endPosition);
      //context.removeGesture("RaiseHand");
    }
  }
  // -----------------------------------------------------------------
}

// you need to pressed mouse for start network sharing
void mousePressed() {
  start=true;
}

void mouseReleased() {
  c = new Client(this, "172.21.168.42", 5001);
  start=true;
}

void serverEvent(Server someServer, Client someClient) {
  println("New client: " + someClient.ip());
}

PImage Logo;
PImage Brush, Music, Splash, Grid, BrushB, MusicB, SplashB, GridB, Network, NetworkB, Root, RootB, Erase, EraseB;
PGraphics GUILAYER;
boolean drawmode = false, splashmode = false, musicmode = false, gridonoff = false, rootmode = false, networkmode = false, erasemode = false ;
PShape Kids;
AudioOutput out;
SineWave sine;
//--------Splash -------------------------
ArrayList splatpoop = new ArrayList();
//--------network Brush variable----------
ArrayList hist = new ArrayList();
float joinDist = 100;
//--------root Brush variable----------
ArrayList<Node> nodes = new ArrayList<Node>();
int lastRedraw;
color splc;
//----------------------------------------
void GUIimages() {
  GUILAYER = createGraphics(displayWidth, displayHeight, P3D);
  //---------NEUTRAL------------------------
  BrushB = loadImage("Brushb.jpg");
  MusicB = loadImage("Musicb.jpg");
  SplashB = loadImage("splashb.jpg");
  GridB = loadImage("GridB.jpg");
  RootB = loadImage("rootb.jpg");
  NetworkB = loadImage("networkb.jpg");
  EraseB = loadImage("EraserB.jpg");
  //---------ACTIVE-------------------------
  Brush = loadImage("Brush.jpg");
  Music = loadImage("Music.jpg");
  Splash = loadImage("splash.jpg");
  Grid = loadImage("Grid.jpg");
  Root = loadImage("root.jpg");
  Network = loadImage("network.jpg");
  Erase = loadImage("Eraser.jpg");
  Logo = loadImage("logo.png");
  Kids = loadShape("kids.svg");
  //---------Resing Images-------------------
  MusicB.resize(int(width*0.04), int(width*0.04));
  Music.resize(int(width*0.04), int(width*0.04));
  Brush.resize(int(width*0.04), int(width*0.04));
  BrushB.resize(int(width*0.04), int(width*0.04));
  Splash.resize(int(width*0.04), int(width*0.04));
  SplashB.resize(int(width*0.04), int(width*0.04));
}

void GUI() {
  //--------------------------START MAIN GUI ---------------------------------
  GUILAYER.beginDraw();
  GUILAYER.background(-1, 0);
  GUILAYER.smooth();
  GUILAYER.beginShape();
  GUILAYER.noStroke();
  GUILAYER.fill(#FF005E);
  GUILAYER.vertex(0, 0);//1
  GUILAYER.vertex(width, 0);//2
  GUILAYER.vertex(width, 30);//3
  GUILAYER.vertex(7*width/10+25, 30);//4
  GUILAYER.vertex(7*width/10, 45);//5
  GUILAYER.vertex(3*width/10, 45);//6
  GUILAYER.vertex(3*width/10-25, 30);//7
  GUILAYER.vertex(0, 30);//8
  GUILAYER.endShape();
  GUILAYER.stroke(0);
  GUILAYER.line(0+10, height-BrushB.height-20, width-10, height-BrushB.height-20);
  GUILAYER.noStroke();
  //----------------- Music -----------------------------------------------
  for (int iconPos=0;iconPos<2*width;iconPos+=width) {
    if (!musicmode) GUILAYER.image(MusicB, (iconPos==width)?width-MusicB.width:0, height/2-MusicB.height-50 );
    if (musicmode) GUILAYER.image(Music, (iconPos==width)?width-MusicB.width:0, height/2-MusicB.height-50);
    //----------------- Brush -----------------------------------------------
    if (!drawmode) GUILAYER.image(BrushB, (iconPos==width)?width-BrushB.width:0, height/2-BrushB.height/2);
    if (drawmode) GUILAYER.image(Brush, (iconPos==width)?width-BrushB.width:0, height/2-BrushB.height/2);
    //  //----------------- Splash -----------------------------------------------
    if (!splashmode) GUILAYER.image(SplashB, (iconPos==width)?width-SplashB.width:0, height/2+50 );
    if (splashmode) GUILAYER.image(Splash, (iconPos==width)?width-SplashB.width:0, height/2+50);
  }
  //----------------- Brsuh Network -----------------------------------------
  //  Network.resize(int(width*0.04), int(width*0.04));
  //  NetworkB.resize(int(width*0.04), int(width*0.04));
  //  if (drawmode && !networkmode) GUILAYER.image(NetworkB, width/2-NetworkB.width/2+200, height-NetworkB.height-10);
  //  if (drawmode && networkmode) GUILAYER.image(Network, width/2-Network.width/2+200, height-Network.height-10);
  //  //----------------- Brush Root-----------------------------------------------
  //  Root.resize(int(width*0.04), int(width*0.04));
  //  RootB.resize(int(width*0.04), int(width*0.04));
  //  if (drawmode && !rootmode) GUILAYER.image(RootB, width/2-RootB.width/2-200, height-RootB.height-10);
  //  if (drawmode && rootmode ) GUILAYER.image(Root, width/2-Root.width/2-200, height-Root.height-10);
  //  //----------------- ERASER Left -----------------------------------------------
  //  Erase.resize(int(width*0.04), int(width*0.04));
  //  EraseB.resize(int(width*0.04), int(width*0.04));
  //  if (drawmode && !erasemode) GUILAYER.image(EraseB, 0, height/2);
  //  if (drawmode && erasemode) GUILAYER.image(Erase, 0, height/2);
  //  //----------------- ERASER Right-----------------------------------------------
  //  Erase.resize(int(width*0.04), int(width*0.04));
  //  EraseB.resize(int(width*0.04), int(width*0.04));
  //  if (drawmode && !erasemode) GUILAYER.image(EraseB, width-EraseB.width, height/2);
  //  if (drawmode && erasemode) GUILAYER.image(Erase, width- Erase.width, height/2);
  //------------------ LOGO -----------------------------------------------
  Logo.resize(int(width*0.04), int(width*0.04));
  GUILAYER.ellipse(Logo.width + 40, Logo.height/2, Logo.width+10, Logo.width+10);
  GUILAYER.pushStyle();
  GUILAYER.imageMode(CENTER);
  GUILAYER.image(Logo, Logo.width + 40, Logo.height/2);
  GUILAYER.popStyle();
  GUILAYER.endDraw();
  //--------------------------END MAIN GUI ---------------------------------
  //image(GUILAYER, 0, 0);
}

//-------------------ERASER FUNCTION ------------------

void eraseFunction(float linex, float liney) {
  color c = color(0, 0);
  pg.beginDraw();
  pg.loadPixels();
  for (int x=0; x<pg.width; x++) {
    for (int y=0; y<pg.height; y++ ) {
      float distance = dist(x, y, linex, liney);
      if (distance <= 25) {
        int loc = x + y*pg.width;
        pg.pixels[loc] = c;
      }
    }
  }
  pg.updatePixels();
  pg.endDraw();
}

//-------------------root brush functions ------------
Node findNearest(Node p)
{
  float minDist = 1e10;
  int minIdx = -1;
  for (int i = 0; i < nodes.size(); ++i)
  {
    float d = p.dist(nodes.get(i));
    if (d < minDist)
    {
      minDist = d;
      minIdx = i;
    }
  }
  return nodes.get(minIdx);
}


void grow(float linex, float liney)
{
  float x, y;
  do
  {
    x = random(-40, 40);
    y = random(-40, 40);
  } 
  while (sq (x) + sq(y) > sq(40));
  x += linex;
  y += liney;

  Node sample = new Node(x, y);
  Node base = findNearest(sample);
  if (base.dist(sample) < 10.0)
    return;
  Node newNode = new Node(base, sample);
  nodes.add(newNode);
  newNode.display();
}

void updateWeights()
{
  for (int i = 0; i < nodes.size(); ++i)
    nodes.get(i).weight = 1;
  for (int i = nodes.size()-1; i >= 0; --i)
  {
    Node node = nodes.get(i);
    if (node.parent != null)
      node.parent.weight += node.weight;
  }
}

//void redraw_all()
//{
//  //pg.background(192);
//  updateWeights();
//  for (Node node : nodes)
//    node.display();
//}

class Node
{
  PVector pos;
  Node parent;
  int weight;

  Node(float x, float y)
  {
    pos = new PVector(x, y);
    weight = 1;
  }

  Node(Node base, Node sample)
  {
    PVector step = PVector.sub(sample.pos, base.pos);
    step.limit(5.0);
    pos = PVector.add(base.pos, step);
    parent = base;
    weight = 1;
  }

  float dist(Node other)
  {
    return PVector.dist(pos, other.pos);
  }

  void display()
  {
    if (parent == null)
      return;
    pg.strokeWeight(1+log(weight)*0.5);  
    pg.line(parent.pos.x, parent.pos.y, pos.x, pos.y);
  }
}

class Splash {
  int x, y;
  float rad, angle, i;
  color c;
  Splash(int _x, int _y, float _i, color splc) {
    x = _x;
    y = _y;
    i = _i;
    rad = random(2, 50);
    angle = random(0, TWO_PI);
    c = splc;
  }

  void display() {
    pg.fill(c);
    pg.noStroke();
    float spotX = x + cos(angle)*2*i;
    float spotY = y + sin(angle)*3*i;
    pg.ellipse(spotX, spotY, rad-i, rad-i+1.8);
  }
  void update() {
    y = y+1;
  }
}

ArrayList <PVector> path = new ArrayList <PVector> ();
PVector walker;
PVector PV = new PVector(0, 0);
void sortEdgeCoordinates(int A) {
  walker = poop.get(A);
  path.add(walker);
  while (poop.size () > 0) {
    float nearestDistance = width*2;
    int index = -1;
    for (int i=0; i<poop.size(); i++) {
      PVector p = (PVector) poop.get(i);
      float distance = p.dist(walker);
      if (distance < nearestDistance) {
        nearestDistance = distance;
        index = i;
      }
    }
    walker = poop.get(index);
    poop.remove(index);
    path.add(walker);
  }
}

