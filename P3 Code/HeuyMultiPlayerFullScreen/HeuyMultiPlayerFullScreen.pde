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
SimpleOpenNI context;
import java.awt.Toolkit;
import processing.net.*;
import fullscreen.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import jcifs.util.Base64;
import java.util.*;
import processing.opengl.*;

// Intialization

    
         PVector      handMin = new PVector();
         PVector      handMax = new PVector();
         //PVector      handVec = new PVector();
         PVector      endPosClick;
         PVector      currHand, prevHand;
         
         float handThresh = 85;
         float openThresh = 190;
         float slowness = 1.0;
         float [] ellipseX = new float [7500];
         float [] ellipseY = new float [7500];
         float seed = 0.0;
         float transparency = 255;
         float mm;
         float rds, r, splash, max = 2;
         float t,l;

         int   flag=0;
         int   rad =12;
         int   g;
         int   b;
         int   handArr[];
         int   handVecListSize = 20;
         int   blob_array[];
         int   minv, maxv;
         int   iconx, pos;
         int   counter = 0;
         int   losthand=0;
         int   splashArray[]={10,15,20};
         int   val=30, tol1=120, tol2=70, tol3=10, tol4=20;
         int   count; 
         int   cont_length;
         int   userCurID;
         int   countTrack=0;
         int   cont=0;
         
         boolean  loadsg=false;
         boolean  wave, handup,drip=false;
         boolean  start=false;
         boolean  leftSwipe=false;
         boolean  rightSwipe=false;
         boolean  click=false;
         boolean  handsTrackFlag = false;

         HashMap<Integer, String> hm;
         HashMap<Integer, String> handColorMap;
         Map<Integer,Boolean> openPalm= new HashMap<Integer,Boolean>();
         Map<Integer, ArrayList<PVector>>  handPathList = new HashMap<Integer, ArrayList<PVector>>();
         
         ArrayList<PVector> handPositions, prevHandPositions;
         ArrayList<SplashC> splashVector;
         ArrayList<DrippingC> dripCount;

         color final_color;
         color finalc;
         color sc;
         color usericon=color(0, 255, 0);
         color dc;
         color picker;
         color handC;
         //red                          //green       //blue                     //yellow                //purple                            //cyan
         color[] userColors = { 
         color(255, 255, 0), color(0, 255, 0), color(0, 0, 255), color(255, 0, 0), color(255, 0, 255), color(0, 255, 255)
         }; 
         
         PImage edgeImg;
         PImage sg;
         PImage back;
         PImage handImg;
         PrintWriter output;

         PGraphics pg;
         PGraphics layer;


         Client c;
         Server server;

         SplashC scss;
         Draggable d; 

         

    
	

    void setup()
    {
    
     //make sure you resized this according to your window size 
     size(1920,1080);
     //drawing layer. 
     pg = createGraphics(width,height);
     //all drawing stack into this layer except background. 
     layer=createGraphics(width, height);
     // enabling server 
     server=new Server(this,5234);
     // create a client instant if you are already started make sure you press a mouse click so it'll start to create a new client instant change client ip address.
     c = new Client(this, "172.21.168.42", 5001);
     //dripping Vector
     dripCount=new ArrayList<DrippingC>();
     //Splashing vector
     splashVector=new ArrayList<SplashC>();
     endPosClick=new PVector();
     
     hm=new HashMap<Integer, String>();
     handColorMap=new HashMap<Integer, String>();
     handPositions=new ArrayList<PVector>();
     prevHandPositions=new ArrayList<PVector>();
     
     //kinect context
     context = new SimpleOpenNI(this);
 
     //background image. 
     back=loadImage("back.jpg");
     //singapore background
     sg=loadImage("sg.jpg");
     //hand cursor
     handImg=loadImage("hand.png");
     
     handArr=new int[3600];
     //edgeImg = createImage(640, 480, ALPHA);
     //kinect configurations 
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
    
}




 void draw() {
       
      //starting to create a new layer for network drawing
      layer.beginDraw();
      layer.smooth();


      handC=color(255,255,255);
      context.update();
      int[] depthValues = context.depthMap();
      
      //loading background 
      image(back, 0, 0);
      //adding singapore thumnail to layer
     layer.image(sg,10, 20,width/8,height/8);
     
     
      PImage rgbImage = context.rgbImage();
     // prepare the color pixels
      rgbImage.loadPixels();

     //load singapore background if you grabbed singpore thumbnail 
    if(loadsg){
      layer.image(sg,0,0,width,height);
      }
     
     
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
  
  

  //coloring user boundary outlines according to user coordinates gatherd above. 
  
          for (int y=0; y<context.depthHeight(); y++) {
          for (int x=2; x<context.depthWidth()-2; x++) {
      

              int index = x + y * context.depthWidth();
 
 
             if (userMap != null && userMap[index] > 0) {
            int cindex=userMap[index]%userColors.length;
            usericon=userColors[cindex];
      
      // if neibhouring points are equal to 0 and 255 there is user edge so we color the user edge according to user icon color. 
      
            if (blob_array[index-1]==0&&blob_array[index]==255) {

             layer.fill(usericon);
             float xx=map(x,0,640,0,width);
             float yy=map(y,0,480,0,height);
            layer.ellipse(xx,yy,2,2);
  
            }
            else    if (blob_array[index]==255&&blob_array[index+1]==0) {

            float xx=map(x,0,640,0,width);
            float yy=map(y,0,480,0,height);
            layer.ellipse(xx,yy,2,2);
           }

           }
 
           }

           }
  

            // filling user icons
            if (hm.size()>0) {
            for (String c:hm.values()) {
            layer.fill(unhex(c));
            layer.ellipse(iconx, height-20, 16, 16);
            iconx=iconx+30;
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
            int sizex,sizey=20;
            
            
            // loop for each and every hand availble in the context
            for(int j=0;j<keys.size();j++){
            
            int handId =  (Integer)keys.get(j);
            //get handPositions for each hands
            ArrayList<PVector> vecList = (ArrayList<PVector>)values.get(j);
            PVector handPos=new PVector();;
            PVector handPosNxt;
            PVector handPosRealWorld=new PVector();
            PVector handPosNxtRealWorld=new PVector();
            PVector lastP;
            

           if(vecList!=null)
           if (vecList.size()>0)
  

          //get the latest hand poistions 
          handPos = (PVector) vecList.get(0);
          handPosNxt=handPos;
          lastP=handPos;
          if(vecList!=null)
           if(vecList.size()>1)
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
    
        if (hDist > openThresh) {
     
 
          //flag=0;
          openPalm.put(handId,false);
            }
          else {

         // flag=1;
          openPalm.put(handId,true);
      }
   
        } 
  
       // mapping hand positions to real world values. 
     
       layer.pushStyle();
       context.convertRealWorldToProjective(handPosNxt,handPosNxtRealWorld);
       context.convertRealWorldToProjective(handPos,handPosRealWorld);
       
       
       // starting drawing layer
       
         pg.beginDraw();
         pg.noStroke();
         pg.strokeWeight(40);
         pg.fill(sc);
 
 // if a right Swipe gesture executed and if the palm status is open for one particular hand ID we start drawing
 
 
  //////-------------------------Drawing Mode On and Off-----------------------------------------------////
      if(rightSwipe){
      if(openPalm.get(handId)!=null){
      if (openPalm.get(handId)) {
       pg.stroke(sc);
       float linex=map(handPosRealWorld.x,0,640,0,width);
       float liney=map(handPosRealWorld.y,0,480,0,height);
       float linepx=map(handPosNxtRealWorld.x,0,640,0,width);
       float linepy=map(handPosNxtRealWorld.y,0,480,0,height);
        pg.line(linex, liney, linepx,linepy);
      }
      }
      }
  
        //t = handVec.x;
       // l = handVec.y;

       pg.endDraw();
      //adding line draw to layer stack
       layer.image(pg, 0, 0);
       layer.popStyle();
      
      
      //////-------------------------Driping Gesture-----------------------------------------------////
          //check whether hand is in a drip gesture
          if(handPosNxt.y-handPos.y>150){
            DrippingC dpc=new DrippingC();
            
            context.convertRealWorldToProjective(handPosNxt,dripC);
            dpc.setPosition(dripC);
                 if(handColorMap.get(handId)!=null ){
         dpc.setColor(unhex(handColorMap.get(handId)));
            }else{
            dpc.setColor(color(255,0,0));
            }
             dripCount.add(0,dpc);

             drip=true;
          
          }
 

             if(flag==1&&handPosRealWorld.x>10&&handPosRealWorld.x<90&&handPosRealWorld.y>20&&handPosRealWorld.y<80){
               println("image grabbed");
             if(!loadsg){
             loadsg=true;
             }else{
             loadsg=false;
             }
               }
   
     
    

            // if hand positions quiclky swipe to left it'll identify as a left swipe gesture and this stop the drawing mode
                        if(handPosNxt.x-handPos.x>150){
             
           
             leftSwipe=true;
             rightSwipe=false;
             ArrayList<PVector> vecLista = handPathList.get(handId);
             vecLista.clear();   
           }
           if(handPos.x-handPosNxt.x>150){
           
       
               
            leftSwipe=false;
           rightSwipe=true;
           }
           
           
          // setting drawing mode status
            if(rightSwipe){
              cont++;
             if(cont<60){
               layer.pushStyle();
               layer.fill(0, 102, 153);
               layer.text("Drawing Mode", 1000, 100);
               layer.popStyle();
 
             }else{
             cont=0;
             }
          }
          

   
                int cur_x_y=int(handPosRealWorld.x+handPosRealWorld.y);
                int pre_x_y=int(handPosNxtRealWorld.x+handPosNxtRealWorld.y);
 
                int pointcz=int(handPosRealWorld.z);
                int pointpz=int(handPosNxtRealWorld.z);
        
   
    
     
      
              //---------color picking-----------------------------------------------------------------------------///
              /// we checked our hand is on a stable mode for few seconds if our hand is stable if tracked that locations rbg image pixel color
               if(((int(handPosRealWorld.x+20))+int(handPosRealWorld.y-30)*context.depthWidth())>0){
                 picker= rgbImage.pixels[(int(handPosRealWorld.x+20))+int(handPosRealWorld.y-30)*context.depthWidth()];
                 if(abs(pointcz-pointpz)<10){
                countTrack++;
              }else{
                 countTrack=0;
                  }
      
     
         
                // if hand is stable then we put this color to a map so its store color details against to hand IDs
                if(countTrack>30){
        
                 handColorMap.put(handId,hex(picker));
     
          
                }
          }else{
          picker=color(255,255,255);
          }
          
          
          ///----------------------Splashing--------------------------------//
           ////---------------- We checked hands nearest z coordinates if there is a huge gap we identified it as a splashing gesture........
           // if there is splash we creaete new SplashC object which contect position details and color details about a splash then we add that to our Splash Vector. 
                color finalsc;
               if(pointcz<pointpz-110){
                  SplashC sc= new SplashC();
                  sc.setPosition(handPosRealWorld) ;
                 
          
                  if(handColorMap.get(handId)!=null ){
                  sc.setColor(unhex(handColorMap.get(handId)));
                  }else{
                  sc.setColor(color(255,0,0));
                  }
                  splashVector.add(sc);
                   }
            if( handColorMap.get(handId)!=null){
             layer.fill( unhex(handColorMap.get(handId)));
             finalc= unhex(handColorMap.get(handId));
     
       }
           else{
           layer.fill(255,255,255);
           finalc=color(255,255,255);
           }
       
       
       /////--------------------------------------Update Hand Cursor Color-----------------------------------------//
       
       // We remove the background color of the handimage first then we add our picked color to our hand cursor image........ 
       float handImgx=map(handPosRealWorld.x,0,640,0,width);
       float handImgy=map(handPosRealWorld.y,0,480,0,height);
          layer.image(handImg,handImgx,handImgy);  
           handImg.loadPixels();
          int ccc=0;
           for (int y = 0;y < handImg.height;y++) {
           for(int x=0;x<handImg.width;x++){

            int i=x+y*handImg.width;

            //println(img.get(x,y));
            if(handImg.get(x,y)==-1){
            handArr[i]=255;
            }
            if ( handArr[i]==255){
  
            handImg.pixels[i] =finalc;
       
         
     
              }
              }
              }
 
        handImg.updatePixels();
 
        /// preview of color picking in here we create a preview of our color picking are so user can have clear idea where we picked the colors. 
        layer.copy(rgbImage,int(handPosRealWorld.x+20)-50, int(handPosRealWorld.y-30)-50, 50, 50, 1600, sizey, 200, 200);

        layer.pushMatrix();
        layer.pushStyle();

        layer.fill(255,0,0);
         float  x1 = map(handImgx+20, 0, width, 1600,1800);
         float  y1 = map(handImgy-30, 0, height,sizey, sizey+100);
          sizey+=220;
         layer.popStyle();
         layer.popMatrix();

          
           
             
 
           layer.fill(picker);
           layer.ellipse(int(handImgx),int(handImgy)-30,20,20);     
           layer.fill(255,0,0);
           layer.ellipse(int(handImgx+20),int(handImgy-30),8,8);
           layer.ellipse(int(x1),int(y1),4,4);
           layer.fill(usericon);

           layer.ellipse(int(handImgx-50),int(handImgy)+50,6,6);
          layer.ellipse(int(handImgx+50),int(handImgy)+50,6,6);




///////----------------------------- Color Mixing ---------------------------------------------------/////
      // when there are more than one hands. 
      for(int k=0;k<keys.size();k++){
        //get the next hand ID
      int handIdNxt =  (Integer)keys.get(k);
      if(handIdNxt!=handId){
      ArrayList<PVector> vecListNxt = (ArrayList<PVector>)values.get(k);
       PVector lastPNxt=new PVector();
       if(vecListNxt!=null&&vecListNxt.size()>0)
      lastPNxt = vecListNxt.get(0);
       PVector last2dNxt = new PVector();
        context.convertRealWorldToProjective(lastPNxt,last2dNxt);
          int curr_x=int(handPosRealWorld.x);
        int next_x=int(last2dNxt.x);
         int curr_y=int(handPosRealWorld.y);
        int next_y=int(last2dNxt.y);
    
   // we checked whether nearest two hands x and y coorinates are overlapped if overlapped then there is color mixed we update each hands color according to mixed color
        if(abs(next_x-curr_x)<80){
           if(abs(next_y-curr_y)<10){
          color mixing1;
          color mixing2;
          if(handColorMap.get(handId)!=null){
          mixing1=unhex(handColorMap.get(handId));
          }else{
          mixing1=color(255,255,255);
          }
          
              if(handColorMap.get(handIdNxt)!=null){
          mixing2=unhex(handColorMap.get(handIdNxt));
          }else{
          mixing2=color(255,255,255);
          }
         
          color finalMix=mixing1+mixing2;
          
          handColorMap.put(handId,hex(finalMix));
           handColorMap.put(handIdNxt,hex(finalMix));
          
         
         println("hand ID - "+handId + "hand ID Next -"+handIdNxt);
         
        println("Color mixed");
        }
        }
      
      }
      }
          if(handColorMap.get(handId)!=null){
        sc=unhex(handColorMap.get(handId));
        
          
      }
   
    }

  }

   //////-------------------------........Print Splash Vector...........-----------------------------------------------////
   // this will print all the splashes according to the splash gesture exectued. 
         for(int i=0;i<splashVector.size();i++){
           int index = int(random(splashArray.length));
           int splash_num=splashArray[index];
  //   pushStyle();
           layer.fill(splashVector.get(i).getColor());
           float splashx=map(splashVector.get(i).getPosition().x,0,640,0,width);
           float splashy=map(splashVector.get(i).getPosition().y,0,480,0,height);
           drawSplash(true,int(splashx),int(splashy),15,300,300);
      
  }
  count++;
   for(int i=1; i <=count; i++)
  {
    if(count>7400){
    count=5000;
    }
   
   
   
   //////-------------------------........Print Drips..........-----------------------------------------------////
    if(drip){
      float dripx=map(dripCount.get(0).getPosition().x,0,640,0,width);
      ellipseX[count] = dripx;
      ellipseY[count] = dripCount.get(0).getPosition().x;
    }
     
    seed += 0.05;
    float n = noise(seed);
     ///Color c=sc;
     
     
   
    layer.noStroke();
    layer.pushStyle();
     if(drip){
    layer.fill(dripCount.get(0).getColor());
     }
    if(ellipseX[i]!=0)
    layer.ellipse(ellipseX[i], ellipseY[i], rad*n, rad*n);
    
    ellipseY[i]++;
    layer.fill(255,0,0);
     layer.popStyle();
  }
   layer.endDraw();
   
 
   //////------------------------------------------------------------------End of the Drawing.-----------------------------------------------////  
   
      //////------------------------------------------------------------------Starting Mulitplayer-----------------------------------------------////  



 //////------------------------------------------------------------------Acting as a client-----------------------------------------------////  
                 PImage imga=null;

                 int[] val={0,0};

             // we checked is our client is availble
                if (c.available() > 0) {
              // we read the string which our server is sends
              // we manapulate our string according to 64bit image bit streams
          String decodes=c.readStringUntil('*');
          String in = c.readStringUntil('$');
          if(decodes!=null){
          String[] vals = splitTokens(decodes, "*" ); 
          BufferedImage bimg=new BufferedImage( width,height, BufferedImage.TYPE_INT_ARGB);
          byte[] imageByte;
          imageByte = Base64.decode(vals[0]);
          ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
        imageByte=null;
 
       try {
       bimg = ImageIO.read(bis);
       bis.close(); 
      } catch( IOException ioe ) {
      }
       if(bimg!=null){
       PImage img=new PImage(bimg.getWidth(),bimg.getHeight(),PConstants.ARGB);
       bimg.getRGB(0, 0, img.width, img.height, img.pixels, 0, img.width);
      img.updatePixels();
          // we update our reading as a image to our drawing sketch.
       image(img,0,0);
  
  }
   }
 decodes="";

  }
  
  //////------------------------------------------------------------------Acting as a Server-----------------------------------------------////  
 
  // we convert our sketch drawing ito 64bit encoded string then we transfer into our clients. 
  BufferedImage buffimga = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB);
  buffimga.setRGB( 0, 0,width,height, layer.pixels, 0, width);
  
  ByteArrayOutputStream baosa = new ByteArrayOutputStream();
  try {
      ImageIO.write( buffimga, "jpg", baosa );
    baosa.close();
    } catch( IOException ioe ) {
    }
  String b64imagea = Base64.encode( baosa.toByteArray() );
  if(start){
  server.write(b64imagea+"*");
  }
  b64imagea="";
  image(layer, 0, 0);
  layer.clear();
 
  }

////////--------------------------------------- End of Draw Function.------------------------------


////////--------------------------------------- other methods.------------------------------


        void onNewUser(int userId) {
          hm.put(userId, hex(userColors[userId]));
        
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


 

// you need to pressed mouse for start network sharing
    void mousePressed() {
     // d.clicked(mouseX,mouseY);
      start=true;
      }

      void mouseReleased() {
     // d.stopDragging();
        c = new Client(this, "172.21.168.42", 5001);
       start=true;
        }



    // splashing object create...................................
      void drawSplash(boolean lights,int x,int y,int splash_num,int sizex,int sizey) {
  
      randomSeed(0);
      rds = map(sizex, 0, width, 20, 50);
      splash = map(sizey, 0, height,1, 10);
     layer.pushMatrix();
    layer.translate(x,y);
     
      // le splash
    
      layer.beginShape();
      for (int i = 0 ; i < 360; i+=5) {
        if (i == 0) {
     
        layer.curveVertex(rds*cos(radians(0)), rds*sin(radians(0)));}
       
        if (i %15== 0) {   
          r = rds * random(0.9, max);
          float w = map(r, rds, max*rds, 1, splash);
          layer.noStroke();
      layer.smooth();
         layer.curveVertex(r*cos(radians(i-w)), r*sin(radians(i-w)));
         layer.curveVertex(r*cos(radians(i+w)), r*sin(radians(i+w)));
         //ellipse(r*cos(radians(i)), r*sin(radians(i)), 30, 30);
        } else {
          r = rds;
          
         layer.curveVertex(r*cos(radians(i)),  r*sin(radians(i)));
        }
        
      }
    
      layer.curveVertex(rds*cos(radians(360)), rds*sin(radians(360)));
      layer.endShape();
     drawLights();
   
      
    }
    
    
    void drawLights() {
    layer.translate(-1, -1);
    layer.scale(0.95);
    layer.beginShape();
      for (int i = 0 ; i < 360; i+=5) {
        if (i == 0) {layer.curveVertex(rds*cos(radians(0)), rds*sin(radians(0)));}
       
        if (i %15==0) {   
          r = rds * random(0.9, max);
          float w = map(r, rds, max*rds, 1, splash);
          layer.curveVertex(r*cos(radians(i-w)), r*sin(radians(i-w)));
          layer.curveVertex(r*cos(radians(i+w)), r*sin(radians(i+w)));
        } else {
          r = rds;
          layer.curveVertex(r*cos(radians(i)),  r*sin(radians(i)));
        }
       
      }
      layer.curveVertex(rds*cos(radians(360)), rds*sin(radians(360)));
      layer.endShape(); 
         layer.popMatrix();
    }
    
    
    
    void serverEvent(Server someServer, Client someClient) {

               println("New client: " + someClient.ip());
        }
