import SimpleOpenNI.*;
import java.util.*;
import processing.video.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import processing.net.*;
import processing.pdf.PGraphicsPDF;
//Globals

//Server server;
import muthesius.net.*;
import org.webbitserver.*;
import jcifs.util.Base64;
SimpleOpenNI  context;
//WebSocketP5 socket;
import java.awt.Toolkit;
import fullscreen.*; 
PVector      handMin = new PVector();
PVector      handMax = new PVector();
float        handThresh = 85;
float        openThresh = 190;
int          flag=0; 
FullScreen fs;
boolean start=false;
boolean loadsg=false;
PVector      handVec = new PVector();
HashMap<Integer, String> hm;
ArrayList<PVector> handPositions, prevHandPositions;
ArrayList<SplashC> splashVector;
boolean wave, handup, drip=false;
color final_color;
int rad = 12;
//float speed = 25.0;
float slowness = 1.0;
float [] ellipseX = new float [7500];
float [] ellipseY = new float [7500];
int counter = 0;
Draggable d;
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
int handVecListSize = 20;
color sc;
Map<Integer, ArrayList<PVector>>  handPathList = new HashMap<Integer, ArrayList<PVector>>();
import processing.opengl.*;
//red                          //green       //blue                     //yellow                //purple                            //cyan
color[] userColors = { 
  color(255, 255, 0), color(255, 0, 0), color(0, 0, 255), color(255, 0, 0), color(255, 0, 255), color(0, 255, 255)
};
int val=30, tol1=120, tol2=70, tol3=10, tol4=20; 
PImage back;
int count;
color usericon=color(0, 255, 0);
int blob_array[];
int minv, maxv;
int iconx, pos;
PVector currHand, prevHand;
int cont_length;
float mm;
color picker;
int userCurID;
color handC;
int countTrack=0;
boolean      handsTrackFlag = false;
PGraphics pg;
PGraphics layer;
PImage handImg;
Client c;
Server server;
void setup()
{

  size(640, 480);
  server=new Server(this, 5234);
  //socket = new WebSocketP5( this, 8080 );
  pg = createGraphics(640, 480);
  layer=createGraphics(640, 480);
  c = new Client(this, "172.21.170.119", 5234);
  dripCount=new ArrayList<DrippingC>();
  splashVector=new ArrayList<SplashC>();
  endPosClick=new PVector();
  hm=new HashMap<Integer, String>();
  handColorMap=new HashMap<Integer, String>();
  handPositions=new ArrayList<PVector>();
  prevHandPositions=new ArrayList<PVector>();
  context = new SimpleOpenNI(this);
  //output = createWriter("positions.txt");
  // size(1024,768, OPENGL);
  back=loadImage("back.jpg");
  sg=loadImage("sg.jpg");
  handArr=new int[3600];
  //edgeImg = createImage(640, 480, ALPHA);
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
  handPositions=new ArrayList<PVector>();
  prevHandPositions=new ArrayList<PVector>();
  cont_length=640*480;
  //size(1920, 1280);
  blob_array=new int[cont_length];
  println("width-- = "+context.depthWidth()+"height-- = "+context.depthHeight());
  fs = new FullScreen(this); 
  handImg=loadImage("hand.png");

  d = new Draggable(200, 200, 100, 50);
  // enter fullscreen mode
  //fs.enter();
}
float t, l;
int cont=0;
void draw() {
  if (c.available()<0) {
    c = new Client(this, "172.21.171.160", 5100);
  }
  layer.beginDraw();
  layer.smooth();
  //pushs
  //  stroke(255);
  //  if (mousePressed == true) {
  //    line(mouseX, mouseY, pmouseX, pmouseY);
  //  }
  //  
  handC=color(255, 255, 255);
  context.update();
  int[] depthValues = context.depthMap();
  //background(0);
  image(back, 0, 0);
  layer.image(sg, 10, 20, width/8, height/8);
  PImage rgbImage = context.rgbImage();
  // prepare the color pixels
  rgbImage.loadPixels();


  if (loadsg) {
    layer.image(sg, 0, 0, width, height);
  }

  int[] userMap = null;
  int userCount = context.getNumberOfUsers();
  if (userCount > 0) {
    userMap = context.getUsersPixels(SimpleOpenNI.USERS_ALL);
  }

  loadPixels();

  PVector realWorldPoint;


  //  translate(0, 0, -1000);
  //println("width-- = "+context.depthWidth()+"height-- = "+context.depthHeight());
  for (int y=0; y<context.depthHeight(); y++) {
    for (int x=0; x<context.depthWidth(); x++) {
      int index = x + y * context.depthWidth();
      //  println("index ss ="+ index+" x  ss= "+x);
      if (userMap != null && userMap[index] > 0) {
        //  println("index ="+ index+" x = "+x);

        int colorIndex = userMap[index] % userColors.length;
        userCurID= userMap[index];
        // picker=rgbImage.pixels[index];;
        // pixels[index] = rgbImage.pixels[index]; //userColors[colorIndex];
        blob_array[index]=255;
        usericon=userColors[colorIndex];

        //             realWorldPoint = context.depthMapRealWorld()[index];
        //          if (realWorldPoint.dist(handVec) < handThresh) {
        //            point(realWorldPoint.x, realWorldPoint.y, realWorldPoint.z); 
        //            if (realWorldPoint.x < handMin.x) handMin.x = realWorldPoint.x;
        //            if (realWorldPoint.y < handMin.y) handMin.y = realWorldPoint.y;
        //            if (realWorldPoint.z < handMin.z) handMin.z = realWorldPoint.z;
        //            if (realWorldPoint.x > handMax.x) handMax.x = realWorldPoint.x;
        //            if (realWorldPoint.y > handMax.y) handMax.y = realWorldPoint.y;
        //            if (realWorldPoint.z > handMax.z) handMax.z = realWorldPoint.z;
        //          }
        // println("index i ="+index);
      }
      else {
        blob_array[index]=0;
        // pixels[index]=color(255,0,0);
      }
      // edgeImg.pixels[index]=color(0);
    }


    //println("index minv="+ minv);
    //   println("index maxv="+ maxv);
  }





  for (int y=0; y<context.depthHeight(); y++) {
    for (int x=2; x<context.depthWidth()-2; x++) {


      int index = x + y * context.depthWidth();


      if (userMap != null && userMap[index] > 0) {
        int cindex=userMap[index]%userColors.length;
        usericon=userColors[cindex];

        if (blob_array[index-1]==0&&blob_array[index]==255) {

          //pixels[index-1]=usericon;
          //  pixels[index-2]=usericon;
          //  text("x coordinates- "+x+" y coordinates- "+y,122,122);
          layer.fill(usericon);
          layer.ellipse(x, y, 2, 2);
        }
        else    if (blob_array[index]==255&&blob_array[index+1]==0) {
          //pixels[index+1]=usericon;
          // pixels[index+2]=usericon;

          //ellipse(x,y,3,3);
          // stroke(usericon);
          //line(x,y,x+1,y+1);
          layer.ellipse(x, y, 2, 2);
        }


        // fill(usericon);
        //  ellipse(iconx,height-20,16,16);
      }
    }


    //println("index minv="+ minv);
    //   println("index maxv="+ maxv);
  }

  if (handsTrackFlag)  
  {
    handMin = handVec.get();
    handMax = handVec.get();
    int[]   depthMap = context.depthMap();
    int     steps   = 5;  // to speed up the drawing, draw every third point
    int     index;
    //   PVector realWorldPoint;
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
    //line(handMin.x,handMin.y,handMin.z,handMax.x,handMax.y,handMax.z);
    float hDist = handMin.dist(handMax);

    if (hDist > openThresh) {

      //println("palm open, dist: " + hDist);
      flag=0;
    }
    else {
      // println("palm close, dist: " + hDist);
      flag=1;
    }
  } 


  //   d.display();

  //edgeImg.resize(100, 50);

  //updatePixels();
  // image(edgeImg, 0, 0);

  if (hm.size()>0) {
    for (String c:hm.values()) {
      layer.fill(unhex(c));
      layer.ellipse(iconx, height-20, 16, 16);
      iconx=iconx+30;
    }
  }
  iconx=30;





  if (handPathList.size() > 0)  
  {    
    PVector pa2d = new PVector();
    PVector pr2d = new PVector();
    PVector last2d = new PVector();
    PVector dripC=new PVector();
    //PVector pr2d=new PVector();

    Collection<ArrayList<PVector>> value=handPathList.values();
    List<ArrayList<PVector>> values=new ArrayList<ArrayList<PVector>>(value);
    Set<Integer> keySet=handPathList.keySet();
    List<Integer> keys=new ArrayList<Integer>(keySet);
    Iterator itr = handPathList.entrySet().iterator();    

    for (int j=0;j<keys.size();j++) {

      int handId =  (Integer)keys.get(j);
      ArrayList<PVector> vecList = (ArrayList<PVector>)values.get(j);
      PVector p=new PVector();
      ;
      PVector pv;
      PVector pvx;
      PVector pvy;
      PVector lastP;
      pvx=new PVector();
      pvy=new PVector();
      if (vecList!=null)
        if (vecList.size()>0)



          p = (PVector) vecList.get(0);
      pv=p;
      lastP=p;
      if (vecList!=null)
        if (vecList.size()>1)
          pv=(PVector) vecList.get(1);

      layer.pushStyle();
      context.convertRealWorldToProjective(pv, pvy);
      context.convertRealWorldToProjective(p, pvx);
      pg.beginDraw();
      // pg.perspective(radians(45), float(width)/float(height), 10.0f, 150000.0f);
      // pg.translate(width/2, height/2, 0);
      // pg.rotateX(radians(180));

      // pg.rotateY(radians(0));
      //pg.smooth();
      pg.noStroke();

      pg.strokeWeight(8 );
      pg.fill(sc);
      if (rightSwipe)
        if (flag==1) {
          pg.stroke(sc);
          pg.line(pvx.x, pvx.y, pvy.x, pvy.y);
        }
      t = handVec.x;
      l = handVec.y;

      pg.endDraw();
      layer.image(pg, 0, 0);
      layer.popStyle();


      if (pv.y-p.y>150) {
        DrippingC dpc=new DrippingC();

        context.convertRealWorldToProjective(pv, dripC);
        dpc.setPosition(dripC);
        if (handColorMap.get(handId)!=null ) {
          dpc.setColor(unhex(handColorMap.get(handId)));
        }
        else {
          dpc.setColor(color(255, 0, 0));
        }
        dripCount.add(0, dpc);
        //dripCount.set(0,dripC);
        drip=true;
        println("color drip");
      }


      //  println("p.x = "+p.x +" p.y = "+p.y);

      if (flag==1&&pvx.x>10&&pvx.x<90&&pvx.y>20&&pvx.y<80) {
        println("image grabbed");
        if (!loadsg) {
          loadsg=true;
        }
        else {
          loadsg=false;
        }
      }





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



      if (rightSwipe) {
        cont++;
        if (cont<60) {
          layer.pushStyle();
          layer.fill(0, 102, 153);
          layer.text("Drawing Mode", 500, 100);
          layer.popStyle();
        }
        else {
          cont=0;
        }
      }



      //          if(rightSwipe){
      //               for(int i=0;i<vecList.size();i++){
      //       
      //             
      //         //if(vecList.size()-2>0)
      //            p = (PVector) vecList.get(i);
      //           // pv=p;
      //           
      //           if(vecList.size()>i+1)
      //           pv=(PVector) vecList.get(i+1);
      //           
      //           int pointcz=int(pa2d.z);
      //           int pointpz=int(pr2d.z);
      //           
      //           context.convertRealWorldToProjective(p,pa2d);
      //          context.convertRealWorldToProjective(pv,pr2d);
      //       //if(abs(pointcz-pointpz)<10)
      //       
      //           strokeWeight(5); 
      //          if(handColorMap.get(handId)!=null )
      //          stroke(unhex(handColorMap.get(handId)));
      ////          pg.beginDraw();
      ////           if(flag==1){
      ////            
      ////               
      //// // pg.background(102);
      //////  pg.stroke(255);
      ////  pg.line(pr2d.x, pr2d.y, pa2d.x,pa2d.y);;
      ////
      ////  image(pg,0, 0); 
      ////            
      ////           }
      ////             pg.endDraw();
      //          }
      //          }

      //strokeWeight(1);  


      // println("current hand x=" +p.x);
      // println("prev hand x=" +pv.x);
      context.convertRealWorldToProjective(p, pa2d);
      context.convertRealWorldToProjective(pv, pr2d);
      int cur_x_y=int(pa2d.x+pa2d.y);
      int pre_x_y=int(pr2d.x+pr2d.y);
      // context.convertRealWorldToProjective(lastP, last2d);
      int pointcz=int(pa2d.z);
      int pointpz=int(pr2d.z);


      if (vecList!=null&&vecList.size()>0)
        lastP = vecList.get(0);

      context.convertRealWorldToProjective(lastP, last2d);
      //        point(p2d.x,p2d.y);
      if (((int(last2d.x+20))+int(last2d.y-30)*context.depthWidth())>0) {
        picker= rgbImage.pixels[(int(last2d.x+20))+int(last2d.y-30)*context.depthWidth()];
        if (abs(pointcz-pointpz)<10) {
          countTrack++;
        }
        else {
          countTrack=0;
        }


        //  println(countTrack);

        //output.println("current hand ID- "+handId);
        if (countTrack>30) {
          // if(flag==1)
          handColorMap.put(handId, hex(picker));
          //output.println()
        }
      }
      else {
        picker=color(255, 255, 255);
      }//rgbImage.pixels[int(pa2d.x+30)+int(pa2d.y) * context.depthWidth()];
      color finalsc;
      if (pointcz<pointpz-110) {
        SplashC sc= new SplashC();
        sc.setPosition(pa2d);
        if (handColorMap.get(handId)!=null ) {
          sc.setColor(unhex(handColorMap.get(handId)));
        }
        else {
          sc.setColor(color(255, 0, 0));
        }
        splashVector.add(0, sc);
      }
      if ( handColorMap.get(handId)!=null) {
        layer.fill( unhex(handColorMap.get(handId)));
        //output.println("hand ID CC - "+handId);
      }
      else {
        layer.fill(255, 255, 255);
      }
      layer.triangle(int(last2d.x), int(last2d.y)-20, int(last2d.x)-20, int(last2d.y)+20, int(last2d.x)+20, int(last2d.y)+20);
      layer.image(handImg, int(last2d.x)-20, int(last2d.y)-20);
      handImg.loadPixels();

      for (int y = 0;y < handImg.height;y++) {
        for (int x=0;x<handImg.width;x++) {

          int i=x+y*handImg.width;
          //println(img.get(x,y));
          if (handImg.get(x, y)==-1) {
            handArr[i]=255;
          }
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

      // println(last2d.x);
      if (flag==1) {
        d.clicked((int)last2d.x, (int)last2d.y);
      }
      else   if (flag==0) {
        d.stopDragging();
      }

      d.drag((int)last2d.x, (int)last2d.y);

      layer.fill(picker);
      layer.ellipse(int(last2d.x), int(last2d.y)-30, 10, 10);     
      layer.fill(255, 0, 0);
      layer.ellipse(int(last2d.x), int(last2d.y-40), 4, 4);
      layer.fill(usericon);
      //     ellipse(int(pa2d.x),int(pa2d.y)-40,8,8);
      layer.ellipse(int(last2d.x-25), int(last2d.y)+25, 3, 3);
      layer.ellipse(int(last2d.x+25), int(last2d.y)+25, 3, 3);

      //output.println("number of hands = " +keys.size());
      for (int k=0;k<keys.size();k++) {
        int handIdNxt =  (Integer)keys.get(k);
        if (handIdNxt!=handId) {
          ArrayList<PVector> vecListNxt = (ArrayList<PVector>)values.get(k);
          PVector lastPNxt=new PVector();
          if (vecListNxt!=null&&vecListNxt.size()>0)
            lastPNxt = vecListNxt.get(0);
          PVector last2dNxt = new PVector();
          context.convertRealWorldToProjective(lastPNxt, last2dNxt);
          int curr_x=int(last2d.x);
          int next_x=int(last2dNxt.x);
          int curr_y=int(last2d.y);
          int next_y=int(last2dNxt.y);
          // output.println("hand ID - "+handId+" x = "+curr_x+" y= "+curr_y + " hand ID Next -"+handIdNxt+" x=  "+next_x+" y= "+next_y);
          println("hand ID - "+handId+" x = "+curr_x+" y= "+curr_y + " hand ID Next -"+handIdNxt+" x=  "+next_x+" y= "+next_y);
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

              //output.println("hand ID - "+handId + "hand ID Next -"+handIdNxt);
              println("hand ID - "+handId + "hand ID Next -"+handIdNxt);

              println("Color mixed");
            }
          }
          // println("Next hand x values ="+last2dNxt.x);
        }
      }
      if (handColorMap.get(handId)!=null) {
        sc=unhex(handColorMap.get(handId));
      }
    }
  }
  //println("splash size= "+splashVector.size());
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
  // transparency-=.5;
  layer.endDraw();
  //  BufferedImage buffimg = new BufferedImage( 640, 480, BufferedImage.TYPE_INT_RGB);
  //	buffimg.setRGB( 0, 0,640, 480, layer.pixels, 0, 640 );
  //  
  //	ByteArrayOutputStream baos = new ByteArrayOutputStream();
  //	try {
  //    	ImageIO.write( buffimg, "png", baos );
  //     baos.close();
  //  	} catch( IOException ioe ) {
  //    
  //	}
  //String b64image = Base64.encode( baos.toByteArray() );
  //
  ////server.write(b64image+"*");
  PImage imga=null;
  //Client client = server.available();

  // We should only proceed if the client is not null
  int[] val= {
    0, 0
  };
  //  if (client != null) {
  //    // Receive the message
  //    String incomingMessage = client.readStringUntil('$'); 
  //    
  //     if(incomingMessage !=null){
  //     String[] vals = splitTokens(incomingMessage, "$" ); 
  //     println("vals size -" +incomingMessage.length());
  //  BufferedImage bimg=new BufferedImage( 300, 300, BufferedImage.TYPE_INT_RGB);
  //  byte[] imageByte;
  //            imageByte = Base64.decode(vals[0]);
  //           
  //            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
  //        //  bimg = ImageIO.read(bis);
  //  
  //    try {
  //  bimg = ImageIO.read(bis); 
  //  bis.close();
  //    } catch( IOException ioe ) {
  //	}
  //if(bimg!=null){
  //  imga =new PImage(bimg.getWidth(),bimg.getHeight(),PConstants.ARGB);
  //    bimg.getRGB(0, 0, imga.width, imga.height, imga.pixels, 0, imga.width);
  //    imga.updatePixels();
  ////println( "Client says");
  //}
  ////println( "Client says:" + incomingMessage);
  ////    // Write message back out (note this goes to ALL clients)
  //  //server.write(incomingMessage);
  // }
  //// val= int(splitTokens(incomingMessage, ",#" )); 
  ////
  ////    // Render an ellipse based on those values
  ////    fill(0,100);
  ////    noStroke();
  ////
  ////    // Print to Processing message window
  //
  //  }
  //socket.broadcast( b64image );

  if (c.available() > 0) {
    String decodes=c.readStringUntil('*');
    String in = c.readStringUntil('$');
    if (decodes!=null) {
      String[] vals = splitTokens(decodes, "*" ); 
      BufferedImage bimg=new BufferedImage( 640, 480, BufferedImage.TYPE_INT_ARGB);
      byte[] imageByte;
      imageByte = Base64.decode(vals[0]);
      //println(vals[0]);
      // println("--------------------------------------------------");
      ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
      imageByte=null;
      //  bimg = ImageIO.read(bis);

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


        //image(back,0,0);
        image(img, 0, 0);
      }
    }
    decodes="";
    //image(layer,0,0);
    //if(in!=null){


    //   int[] val = int(splitTokens(in, ",#" )); 
    //
    //    // Render an ellipse based on those values
    //    fill(0,100);
    //    noStroke();
    //    ellipse(val[0],val[1],25,25);
    //}
  }
  BufferedImage buffimga = new BufferedImage( 640, 480, BufferedImage.TYPE_INT_ARGB);
  buffimga.setRGB( 0, 0, 640, 480, layer.pixels, 0, 640 );

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
  image(layer, 0, 0);
  layer.clear();
  //  ellipse(val[0],val[1],16,16);
  //     if(imga!=null){
  //     image(imga,40,40,300,300);
  //     }
}

//void stop(){
//	socket.stop();
//}
//
//void websocketOnMessage(WebSocketConnection con, String msg){
//	println(msg);
//}
//
//void websocketOnOpen(WebSocketConnection con){
//	println("A client joined");
//}
//
//void websocketOnClosed(WebSocketConnection con){
//	println("A client left");
//}
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

void mousePressed() {
  d.clicked(mouseX, mouseY);
}

void mouseReleased() {
  start=true;
  // d.stopDragging();
}
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

void serverEvent(Server someServer, Client someClient) {

  println("New client: " + someClient.ip());
}

