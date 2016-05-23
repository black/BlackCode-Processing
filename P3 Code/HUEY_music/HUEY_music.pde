import SimpleOpenNI.*;
//-----JAVA IMPORTS--------------
import java.util.*;
import java.awt.Toolkit;
//-----NETWORK IMPORTS-----------
import processing.video.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import processing.net.*;
import muthesius.net.*;
import org.webbitserver.*;
import jcifs.util.Base64;
import ddf.minim.*;
import ddf.minim.signals.*;
//--------------------------------------------------------------------
Minim minim;
AudioPlayer player, buttonplayer;
Server server; // server variable
SimpleOpenNI  context; //kinect variable
WebSocketP5 socket; //browser sharing 
//----------------HAND GRABBING VARIABLES ----------------------------
PVector      handMin = new PVector();
PVector      handMax = new PVector();
float        handThresh = 85;
float        openThresh = 190;
int          flag=0; 
//--------------------------------------------------------------------
boolean loadsg=false;
PVector      handVec = new PVector();
HashMap<Integer, String> hm;
///used for hand tracking
ArrayList<PVector> handPositions, prevHandPositions;
//splashing array list
ArrayList<SplashC> splashVector;
boolean wave, handup, drip=false;
color final_color;
int rad = 12;
//float speed = 25.0;
float slowness = 1.0;
float [] ellipseX = new float [7500];
float [] ellipseY = new float [7500];
int counter = 0;
SplashC scss;
int g;
int b;
int handArr[];
float rds, r, splash, max = 2;
int losthand=0;
int splashArray[]= {
  10, 15, 20
};	
PImage edgeImg;
PrintWriter output;
float seed = 0.0;
float transparency = 255;
boolean leftSwipe=false;
boolean rightSwipe=false;
HashMap<Integer, String> handColorMap;
ArrayList<DrippingC> dripCount;
PVector endPosClick;
boolean click=false;
color dc;
PImage sg;
float t, l;
int cont=0;
int handVecListSize = 20;
color sc;
Map<Integer, ArrayList<PVector>>  handPathList = new HashMap<Integer, ArrayList<PVector>>();
//user icon colors.
//red                          //green       //blue                     //yellow                //purple                            //cyan
color[] userColors = { 
  color(255, 255, 0), color(0, 255, 0), color(0, 0, 255), color(255, 0, 0), color(255, 0, 255), color(0, 255, 255)
};
PImage back;
int count;
color usericon=color(0, 255, 0);

int minv, maxv;
int iconx, pos;
PVector currHand, prevHand;
int cont_length;
float mm;
color picker;
int[] blob_array;
int userCurID;
color handC;
int countTrack=0;
boolean      handsTrackFlag = false;
PGraphics pg;
PGraphics layer;
PImage handImg;
//-----------ABHINAV ADDONS-------------------
ArrayList hist = new ArrayList();
float joinDist = 130;
int tk=0; // variable for switicing between brushes 
ArrayList<PVector> poop  = new ArrayList <PVector>();
//--------------------------------
boolean sketchFullScreen() {
  return true;
}
void setup()
{
  size(displayWidth, displayHeight, P3D);
  server = new Server(this, 5100);
  socket = new WebSocketP5( this, 8080 );
  pg = createGraphics(displayWidth, displayHeight);
  layer = createGraphics(displayWidth, displayHeight);
  dripCount = new ArrayList<DrippingC>();
  splashVector = new ArrayList<SplashC>();
  endPosClick = new PVector();
  hm = new HashMap<Integer, String>();
  handColorMap = new HashMap<Integer, String>();
  handPositions = new ArrayList<PVector>();
  prevHandPositions = new ArrayList<PVector>();
  //this for analyzing data out put we do not need this
  output = createWriter("positions.txt");
  handArr=new int[3600];
  maxv=0;
  //----------------------------------
  context = new SimpleOpenNI(this);
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
  handPositions=new ArrayList<PVector>();
  prevHandPositions=new ArrayList<PVector>();
  cont_length=640*480;
  blob_array=new int[cont_length];
  println("width-- = "+context.depthWidth()+"height-- = "+context.depthHeight());
  handImg=loadImage("hand.png");
  //--------------------------------------------
  Loadimages(); // GUIimages
  minim = new Minim(this);
  buttonplayer = minim.loadFile("button.mp3");
  player = minim.loadFile("2.mp3");
  //-------- MUSIC PAINT -------------------------
  out = minim.getLineOut(Minim.STEREO);
  sine = new SineWave(440, 0.5, out.sampleRate()); // here the sine is generated it has F (frequency) = 440Hz , 0.5 amplitude and sample rate for line out
  sine.portamento(200); //set the portamento speed on the oscillator to 200 milliseconds
  out.addSignal(sine);
  out.mute();
  //--------------------------------------------
}

void draw() {
  background(-1);
  //we adding all our drawings to a one layer this is for sharing screens
  layer.beginDraw();
  layer.smooth();
  layer.background(-1);
  //-----------GRID----------------------- // removed the image because it gets blurred on full screen mode
  if (!gridonoff) {
    for (int i=0;i <displayWidth;i+=70)
    {
      for (int j=0;j<displayHeight-100;j+=70)
      {
        layer.fill(0);
        layer.textSize(8);
        layer.text("+", i, j);
      }
    }
  }
  //------------------------------------
  handC=color(255, 255, 255);
  context.update();
  int[] depthValues = context.depthMap();
  PImage rgbImage = context.rgbImage();
  rgbImage.loadPixels();  // prepare the color pixels
  //-------------------------------------------------------------------------
  int[] userMap = null;
  int userCount = context.getNumberOfUsers();
  if (userCount > 0) {
    userMap = context.getUsersPixels(SimpleOpenNI.USERS_ALL);
  }
  //------------------------------------------------------------------------
  loadPixels();
  //---------------USER BOUNDARY TRACKING ----------------------------------
  for (int y=0; y<context.depthHeight(); y++) {
    for (int x=0; x<context.depthWidth(); x++) {
      int index = x + y * context.depthWidth();
      if (userMap != null && userMap[index] > 0) {
        int colorIndex = userMap[index] % userColors.length;
        userCurID= userMap[index]; //first we found out is ther any user pixels
        // if a user pixels found we color our blob_array as 255
        blob_array[index]=255;
        usericon=userColors[colorIndex];
      }
      else {
        // if user pixels are not there we color our blob_array as 0
        blob_array[index]=0;
      }
    }
  }

  //---------------USER CONTOUR DRAWING -------------------------------------
  //------------------ take out the contour coordinates ---
  for (int x=0; x<context.depthWidth(); x++) {
    for (int y=0; y<context.depthHeight(); y++) {
      int i = x + y*context.depthWidth();
      if (userMap != null && userMap[i] > 0) {
        int cindex = userMap[i]%userColors.length;
        usericon = userColors[cindex];
        if (blob_array[i-1]==0 && blob_array[i]==255) {
          //pixels[i-1] = usericon;
          float xx=map(x, 0, 640, 0, width);
          float yy=map(y, 0, 480, 0, height);
          PVector p1 = new PVector(xx, yy);
          poop.add(p1);
        }
        else if (blob_array[i]==255 && blob_array[i+1]==0) {
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

  for (int k=0; k<path.size(); k+=20) {
    PVector P1 = (PVector) path.get(k);
    color Col = userColors[userCount];
    for (int j=k+1; j<path.size(); j+=20) {
      PVector P2 = (PVector) path.get(j);
      if (P1.dist(P2)<50) {
        stroke(Col, 10);
        strokeWeight(10);
        line(P1.x, P1.y, P2.x, P2.y);
        stroke(Col, 90);
        strokeWeight(1);
        line(P1.x, P1.y, P2.x, P2.y);
      }
    }
  }
  path.clear();
  poop.clear(); 
  //-----THIS IS FOR USER COLOR ICON ---------------------------------------------------
  if (hm.size()>0) {
    for (String c:hm.values()) {
      Kids.disableStyle();
      layer.fill(unhex(c));
      //layer.ellipse(iconx, height-20, 16, 16);
      layer.shape(Kids, iconx, height-2*Kids.height/3, Kids.width/2, Kids.height/2);
      iconx=iconx+40;
    }
  }
  iconx=30;
  //-------------------------------------------------------------------------------------
  //-------------------------------START HAND POSITION TRACKING -------------------------
  if (handPathList.size() > 0) 
  {    
    PVector pa2d = new PVector();
    PVector pr2d = new PVector();
    PVector last2d = new PVector();
    PVector dripC=new PVector();
    //Getting hand ids and positions of each
    Collection<ArrayList<PVector>> value=handPathList.values();
    List<ArrayList<PVector>> values=new ArrayList<ArrayList<PVector>>(value);
    Set<Integer> keySet=handPathList.keySet();
    List<Integer> keys=new ArrayList<Integer>(keySet);

    Iterator itr = handPathList.entrySet().iterator();    

    for (int j=0;j<keys.size();j++) {
      //get the current handID
      int handId =  (Integer)keys.get(j);
      //get the current hand position
      ArrayList<PVector> vecList = (ArrayList<PVector>)values.get(j);
      //Intializing some vector to processeing
      PVector p=new PVector();
      PVector pv;
      PVector pvx;
      PVector pvy;
      PVector lastP;
      pvx=new PVector();
      pvy=new PVector();
      if (vecList!=null)
        if (vecList.size()>0)
          p = (PVector) vecList.get(0);//get the current hand postion vector
      pv=p;
      lastP=p;
      if (vecList!=null)
        if (vecList.size()>1)//get the previous hand postion vector
          pv=(PVector) vecList.get(1);
      PVector realWorldPoint;
      //----------GRAB GESTURE / OPEN or CLOSE PALM GESTURE-----------------------
      if (handsTrackFlag)  
      {
        handMin = handVec.get();
        handMax = handVec.get();
        int[]   depthMap = context.depthMap();
        int     steps   = 5;  // to speed up the drawing, draw every third point
        int     index;
        for (int y=0;y < context.depthHeight();y+=steps)
        { 
          for (int x=0;x < context.depthWidth();x+=steps)
          {
            index = x + y * context.depthWidth();
            if (depthMap[index] > 0)
            { 
              realWorldPoint = context.depthMapRealWorld()[index];
              if (realWorldPoint.dist(handVec) < handThresh) {
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
        float hDist = handMin.dist(handMax);
        if (hDist > openThresh) { 
          flag=0; // println("HAND OPEN");
        }
        else {  
          flag=1; // println("HAND CLOSE");
        }
      } 
      //------------------------------------------------------------------------------------

      layer.pushStyle();
      //mapping hand positions in to real wolr points
      context.convertRealWorldToProjective(pv, pvy);
      context.convertRealWorldToProjective(p, pvx);
      //line drawing layer
      pg.beginDraw();
      pg.noStroke();
      pg.strokeWeight(8 );
      pg.fill(sc);
      //-------------THIS IS FOR GRABBING PROCESS---------------------------
      t = handVec.x;
      l = handVec.y;
      pg.endDraw();
      layer.image(pg, 0, 0);
      layer.popStyle();
      //----------------------START DRIPPING COLOR -------------------------
      //if the currend hand posstion y cordinates and pervious hand  position  y cordinates diffence is more 150 drip gesture identified
      if (pv.y-p.y>150) {
        DrippingC dpc=new DrippingC(); // creating a new DrippingC object
        context.convertRealWorldToProjective(pv, dripC);
        dpc.setPosition(dripC); //setting dripping postion 
        if (handColorMap.get(handId)!=null ) {
          dpc.setColor(unhex(handColorMap.get(handId)));
        }
        else {
          dpc.setColor(color(255, 0, 0)); //setting dripping color
        }
        dripCount.add(0, dpc); //add a dripiing object to the dripping list this will add to the  first postion.
        drip=true;  //dripCount.set(0,dripC);
        println("color drip");
      }
      //----------------------END DRIPPING COLOR ----------------------------

      //----------------------START SWIPE GESTURES---------------------------
      //if the currend hand posstion x cordinates and pervious hand position x cordinates diffence is more 150 left swipe identified or else it'll identified as right swipe
      if (pv.x-p.x>150) {
        println("Left swife");
        leftSwipe=true;
        rightSwipe=false;
        ArrayList<PVector> vecLista = handPathList.get(handId);
        vecLista.clear();
      }
      if (p.x-pv.x>150) {
        println("Right swife");
        leftSwipe=false;
        rightSwipe=true;
      }

      if (leftSwipe) {
        saveFrame("Frame-######.jpg");
        //println("frmae saved");
      }
      //----------------------END SWIPE GESTURES------------------------------
      //------------------------------------------------------------------------
      // mapping previous and current hand position to real wold points
      context.convertRealWorldToProjective(p, pa2d);
      context.convertRealWorldToProjective(pv, pr2d);
      //get current x+y values
      int cur_x_y=int(pa2d.x+pa2d.y);
      //get previous x y values
      int pre_x_y=int(pr2d.x+pr2d.y);
      //get  z values 
      int pointcz=int(pa2d.z);
      int pointpz=int(pr2d.z);
      if (vecList!=null&&vecList.size()>0) lastP = vecList.get(0);
      context.convertRealWorldToProjective(lastP, last2d);
      //color picking code in her we track  pixel color of the hand postion x and y from the rbb image
      if (((int(last2d.x+20))+int(last2d.y-30)*context.depthWidth())>0) {
        //picking color
        picker= rgbImage.pixels[(int(last2d.x+20))+int(last2d.y-30)*context.depthWidth()];
        //track whether hand is on hold mode or not
        if (abs(pointcz-pointpz)<10) {
          countTrack++;
        }
        else {
          countTrack=0;
        }
        //output.println("current hand ID- "+handId);
        // if color piccked and hand hold for fewseconds this will put a map. kep of the map is hand id and we are converting color into hexcode and put to the map against to the key
        if (countTrack>30) {
          handColorMap.put(handId, hex(picker));
        }
      }
      else {
        picker=color(255, 255, 255); //color set to default
      }//rgbImage.pixels[int(pa2d.x+30)+int(pa2d.y) * context.depthWidth()];
      color finalsc;
      //-----------------------------------------
      //getting the color for a hand id
      if ( handColorMap.get(handId)!=null) {
        layer.fill( unhex(handColorMap.get(handId)));
        output.println("hand ID CC - "+handId);
      }
      else {
        layer.fill(255, 255, 255);
      }
      float handX = map(last2d.x, 0, 640, 0, displayWidth);
      float handY = map(last2d.y, 0, 480, 0, displayHeight);
      //-------------------------------------MUSIC MODE ---------------------------------------
      if (flag==1 &&  dist(int(handX-20), int(handY-20), width/2-MusicB.width/2-50, height-MusicB.height-10) < 50 ) { // check if Icon gets selected or not
        musicmode =!musicmode;
        buttonplayer.play();
        if (musicmode)buttonplayer.rewind();
      }
      if (musicmode) { 
        float freq = map(pvx.x, 0, width, 1500, 60); // mapping mouseX --> (width value) position with freq varible
        sine.setFreq(freq); // the generated sine frequency as calculated in the previous line
      }

      if (flag==0) out.mute();

      //-------------------------------------DRAW MODE ---------------------------------------
      if (flag==1 &&  dist(int(handX-20), int(handY-20), width/2-BrushB.width/2, height-BrushB.height-10) < 50 ) { // check if Icon gets selected or not
        drawmode =!drawmode;
        buttonplayer.play();
        if (drawmode) buttonplayer.rewind();
        tk=tk+1;
        if (tk>1) tk=0;
      }
      if (drawmode && flag ==1 ) { // draw mode activated
        if (tk==1) {
          pg.stroke(sc);
          pg.strokeWeight(2);
          if (musicmode) out.unmute();
          float linex=map(pvx.x, 0, 640, 0, width);
          float liney=map(pvx.y, 0, 480, 0, height);
          float linepx=map(pvy.x, 0, 640, 0, width);
          float linepy=map(pvy.y, 0, 480, 0, height);
          pg.line(linex, liney, linepx, linepy);
        }
        if (tk==2) { // add new brush
        }
      }
      //-------------------------------------SPLASH MODE ---------------------------------------
      if (flag==1 && dist(int(handX)-20, int(handY)-20, width/2+50, height-SplashB.height-10 ) < 50 ) { // check if Icon gets selected or not
        splashmode =!splashmode;
        buttonplayer.play();
        if (splashmode) buttonplayer.rewind();
      }
      if (splashmode) { // splash mode activated
        int musicID=0;
        String str=" ";
        if (pointcz < pointpz-110) { //if the hand z postion coordianates difference are more than 110 splashing gesture executed
          SplashC sc= new SplashC(); //intial a new SplachC object
          float pa2dX = map(pa2d.x, 0, 640, 0, displayWidth);
          float pa2dY = map(pa2d.y, 0, 480, 0, displayHeight);
          PVector Psplash = new PVector(pa2dX, pa2dY);
          sc.setPosition(Psplash); //set the position and color for a splash
          if (handColorMap.get(handId)!=null ) {
            sc.setColor(unhex(handColorMap.get(handId)));
          }
          else {
            sc.setColor(color(255, 0, 0));
          }   
          splashVector.add(0, sc); // add splash object to splash list
          //          musicID = (int)random(1, 3);
          //          str = musicID + ".mp3";
          //          if (str!=null) player = minim.loadFile(str);
          player.play();
          player.rewind();
          if (!player.isPlaying()) {
            player.pause();
            player.rewind();
          }
        }
      }
      //------------------------ ON / OFF GRID -------------------------------------------------
      if (flag==1 && dist(int(handX)-20, int(handY)-20, 0, height/2) < 50 ) { // check if Icon gets selected or not
        gridonoff =!gridonoff;
      }
      //----------------------------------------------------------------------------------------
      //fill the color picked to the traangle
      layer.triangle(int(handX), int(handY)-20, int(handX)-20, int(handY)+20, int(handX)+20, int(handY)+20);
      layer.image(handImg, int(handX)-20, int(handY)-20);  //load hand cursor image
      handImg.loadPixels();

      for (int y = 0;y < handImg.height;y++) {
        for (int x=0;x<handImg.width;x++) {
          int i=x+y*handImg.width;
          // get the whilte color pixels from the handImage
          if (handImg.get(x, y)==-1) {
            handArr[i]=255; //marked it as 255
          }
          //color the white pixels with the hand color
          if ( handArr[i]==255) {
            if (handColorMap.get(handId)!=null ) {
              handImg.pixels[i] = unhex(handColorMap.get(handId));
            }
            else {
              handImg.pixels[i] = color(125);
            }
          }
        }
      }
      handImg.updatePixels();
      //color top circle with picked color
      layer.fill(picker);
      layer.ellipse(int(handX), int(handY)-30, 10, 10);
      //color picking point marked it as red color circle     
      layer.fill(255, 0, 0);
      layer.ellipse(int(handX), int(handY-40), 4, 4);
      //color rest two circles with usericon color
      layer.fill(usericon);
      layer.ellipse(int(handX-25), int(handY)+25, 3, 3);
      layer.ellipse(int(handX+25), int(handY)+25, 3, 3);
      //    output.println("number of hands = " +keys.size());
      //    color mixing part
      for (int k=0;k<keys.size();k++) {
        //get the next hand ID
        int handIdNxt =  (Integer)keys.get(k);
        if (handIdNxt!=handId) {
          // get the next hand ID position 
          ArrayList<PVector> vecListNxt = (ArrayList<PVector>)values.get(k);
          PVector lastPNxt=new PVector();
          if (vecListNxt!=null&&vecListNxt.size()>0)
            lastPNxt = vecListNxt.get(0);
          PVector last2dNxt = new PVector();
          //assigning those points to real world points
          context.convertRealWorldToProjective(lastPNxt, last2dNxt);
          //get the next hand and current hand x and y coordinates
          int curr_x=int(last2d.x);
          int next_x=int(last2dNxt.x);
          int curr_y=int(last2d.y);
          int next_y=int(last2dNxt.y);
          // output.println("hand ID - "+handId+" x = "+curr_x+" y= "+curr_y + " hand ID Next -"+handIdNxt+" x=  "+next_x+" y= "+next_y);
          println("hand ID - "+handId+" x = "+curr_x+" y= "+curr_y + " hand ID Next -"+handIdNxt+" x=  "+next_x+" y= "+next_y);
          // if two hand coordinates are possible to overlap we start color mixxing process
          if (abs(next_x-curr_x)<80) {
            if (abs(next_y-curr_y)<10) {
              color mixing1;
              color mixing2;
              if (handColorMap.get(handId)!=null) {
                //first color
                mixing1=unhex(handColorMap.get(handId));
              }
              else {
                mixing1=color(255, 255, 255);
              }
              if (handColorMap.get(handIdNxt)!=null) {
                //second color
                mixing2=unhex(handColorMap.get(handIdNxt));
              }
              else {
                mixing2=color(255, 255, 255);
              }
              //mixed color
              color finalMix=mixing1+mixing2;
              // if color mixed we replace both hand color with the mixed color
              handColorMap.put(handId, hex(finalMix));
              handColorMap.put(handIdNxt, hex(finalMix));
              //output.println("hand ID - "+handId + "hand ID Next -"+handIdNxt);
              println("hand ID - "+handId + "hand ID Next -"+handIdNxt);
              println("Color mixed");
            }
          }
          // println("Next hand x values ="+last2dNxt.x);
        }
      }
      // get the handcolor id unhex is used for get the relavant color for a given hexa color code
      if (handColorMap.get(handId)!=null) {
        sc=unhex(handColorMap.get(handId));
      }
    }
  }
  //-------------------------------END HAND POSITION TRACKING -------------------------------
  //drawing all the splases 
  for (int i=0;i<splashVector.size();i++) {
    int index = int(random(splashArray.length));
    int splash_num=splashArray[index];
    //   pushStyle();
    layer.fill(splashVector.get(i).getColor());
    drawSplash(true, int(splashVector.get(i).getPosition().x), int(splashVector.get(i).getPosition().y), 15, 300, 300);
    // popStyle();
    //splash(splashVector.get(i),sc);
  }
  count++;
  for (int i=1; i <=count; i++)
  {
    if (count>7400) {
      count=5000;
    }
    // color dripping thing
    if (drip) {
      ellipseX[count] = dripCount.get(0).getPosition().x;
      ellipseY[count] = dripCount.get(0).getPosition().x;
    }
    seed += 0.05;
    float n = noise(seed);
    ///Color c=sc;
    layer.noStroke();
    layer.pushStyle();
    if (drip) {
      layer.fill(dripCount.get(0).getColor());
    }
    if (ellipseX[i]!=0)
      layer.ellipse(ellipseX[i], ellipseY[i], rad*n, rad*n);

    ellipseY[i]++;
    layer.fill(255, 0, 0);
    layer.popStyle();
  }
  // in here layer end draws all our drawings map to one single image. 
  layer.endDraw();
  //convert our layer into bufferd image
  BufferedImage buffimg = new BufferedImage( 640, 480, BufferedImage.TYPE_INT_RGB);
  buffimg.setRGB( 0, 0, 640, 480, layer.pixels, 0, 640 );

  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  try {
    ImageIO.write( buffimg, "jpg", baos );
  } 
  catch( IOException ioe ) {
  }
  //encode it as 64base string
  String b64image = Base64.encode( baos.toByteArray() );
  // we adding  a delimiter to spilt our strings in clients
  server.write(b64image+"*");
  Client client = server.available();
  // We should only proceed if the client is not null
  int[] val= {
    0, 0
  };
  if (client != null) {
    // Receive the message
    String incomingMessage = client.readStringUntil('#'); 
    val= int(splitTokens(incomingMessage, ",#" )); 
    // Render an ellipse based on those values
    fill(0, 100);
    noStroke();
    // Print to Processing message window
    println( "Client says:" + incomingMessage);
    // Write message back out (note this goes to ALL clients)
    server.write(incomingMessage);
  }
  //socket.broadcast( b64image );
  image(layer, 0, 0);
  GUI();
  ellipse(val[0], val[1], 16, 16);
  image(GUILAYER, 0, 0);
}

void stop() {
  socket.stop();
}

void websocketOnMessage(WebSocketConnection con, String msg) {
  println(msg);
}

void websocketOnOpen(WebSocketConnection con) {
  println("A client joined");
}

void websocketOnClosed(WebSocketConnection con) {
  println("A client left");
}
void onNewUser(int userId) {
  hm.put(userId, hex(userColors[userId]));
  //iconx=iconx+30;
  println("detected" + userId);
  //  users.add(userId);//a new user was detected add the id to the list
}
void onLostUser(int userId) {
  hm.remove(userId);
  println("lost: " + userId);
  //  iconx=iconx-30;
  //not 100% sure if users.remove(userId) will remove the element with value userId or the element at index userId
  //users.remove((Integer)userId);//user was lost, remove the id from the list
}

// hand events
void onCreateHands(int handId, PVector position, float time) {
  println("onNewHand - handId: " + handId + ", pos: " + position);
  ArrayList<PVector> vecList = new ArrayList<PVector>();
  vecList.add(position);
  handsTrackFlag = true;
  handVec = position;
  handPathList.put(handId, vecList);
}

void onUpdateHands(int handId, PVector position, float time) {
  //  println("onTrackedHand - handId: " + handId + ", pos: " + position );
  ArrayList<PVector> vecList = handPathList.get(handId);
  if (vecList != null)
  {
    vecList.add(0, position);
    // remove the last point
    // vecList.remove(vecList.size()-1);
  }
  handVec = position;
  //  println("size------ = "+vecList.size());
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
      //splashVector.add(endPosition);
      endPosClick=endPosition;
      //splash(endPosition,sc);
      println("Click gestures executed");
      //context.startTrackingHands(endPosition);

      //context.removeGesture("RaiseHand");
    }

    if (strGesture.equals("Wave")) {
      splashVector.clear();
      //splash(endPosition,sc);
      println("Wave gestures executed");
      //context.startTrackingHands(endPosition);

      //context.removeGesture("RaiseHand");
    }
  }

  // -----------------------------------------------------------------
  // Keyboard event
}

//-----------------------------------------------------------

byte[] int2byte(int[]src) {
  int srcLength = src.length;
  byte[]dst = new byte[srcLength << 2];

  for (int i=0; i<srcLength; i++) {
    int x = src[i];
    int j = i << 2;
    dst[j++] = (byte) (( x >>> 0 ) & 0xff);           
    dst[j++] = (byte) (( x >>> 8 ) & 0xff);
    dst[j++] = (byte) (( x >>> 16 ) & 0xff);
    dst[j++] = (byte) (( x >>> 24 ) & 0xff);
  }
  return dst;
}
//------------------------------------------------------
void drawSplash(boolean lights, int x, int y, int splash_num, int sizex, int sizey) {
  randomSeed(0);
  rds = map(sizex, 0, width, 10, 18);
  splash = map(sizey, 0, height, 1, 6);
  layer.pushMatrix();
  layer.translate(x, y);
  // le splash
  layer.beginShape();
  for (int i = 0 ; i < 360; i+=5) {
    if (i == 0) {
      layer.curveVertex(rds*cos(radians(0)), rds*sin(radians(0)));
    }
    if (i %15== 0) {   
      r = rds * random(0.9, max);
      float w = map(r, rds, max*rds, 1, splash);
      layer.noStroke();
      smooth();
      layer.curveVertex(r*cos(radians(i-w)), r*sin(radians(i-w)));
      layer.curveVertex(r*cos(radians(i+w)), r*sin(radians(i+w)));
      //ellipse(r*cos(radians(i)), r*sin(radians(i)), 30, 30);
    } 
    else {
      r = rds;
      layer.curveVertex(r*cos(radians(i)), r*sin(radians(i)));
    }
  }
  layer.curveVertex(rds*cos(radians(360)), rds*sin(radians(360)));
  layer.endShape();
  drawLights();
}

//-----------------------------------------------------
void drawLights() {
  layer.translate(-1, -1);
  layer.scale(0.95);
  layer.beginShape();
  for (int i = 0 ; i < 360; i+=5) {
    if (i == 0) {
      layer.curveVertex(rds*cos(radians(0)), rds*sin(radians(0)));
    }

    if (i %15==0) {   
      r = rds * random(0.9, max);
      float w = map(r, rds, max*rds, 1, splash);
      layer.curveVertex(r*cos(radians(i-w)), r*sin(radians(i-w)));
      layer.curveVertex(r*cos(radians(i+w)), r*sin(radians(i+w)));
    } 
    else {
      r = rds;
      layer.curveVertex(r*cos(radians(i)), r*sin(radians(i)));
    }
  }
  layer.curveVertex(rds*cos(radians(360)), rds*sin(radians(360)));
  layer.endShape(); 
  layer.popMatrix();
}
//-------------------------------------------------------------
void serverEvent(Server someServer, Client someClient) {

  fill(0, 255, 0);
  text("SERVER", 10, 15);
  fill(255);
  text("New client: " + someClient.ip(), 10, 25);
}

