import SimpleOpenNI.*;
import java.util.Map;
SimpleOpenNI context;
import java.awt.Toolkit;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Iterator;
//--------------------------------------

HashMap<Integer, String> hm;
ArrayList<PVector> handPositions, prevHandPositions;
ArrayList<PVector> splashVector;
boolean wave, handup, drip=false;
color final_color;
int rad = 50;
//float speed = 25.0;
float slowness = 1.0;
float [] ellipseX = new float [5000];
float [] ellipseY = new float [5000];
int counter = 0;
int g;
int b;
float rds, r, splash, max = 2;
int losthand=0;
int splashArray[]= {
  10, 15, 20
};	

PrintWriter output;
float seed = 0.0;
float transparency = 255;
boolean leftSwipe=false;
boolean rightSwipe=false;
HashMap<Integer, String> handColorMap;
ArrayList<PVector> dripCount;
PVector endPosClick;
boolean click=false;
int handVecListSize = 20;
color sc;
Map<Integer, ArrayList<PVector>>  handPathList = new HashMap<Integer, ArrayList<PVector>>();
import processing.opengl.*;
//red                          //green       //blue                     //yellow                //purple                            //cyan
color[] userColors = { 
  color(255, 0, 0), color(0, 255, 0), color(0, 0, 255), color(255, 255, 0), color(255, 0, 255), color(0, 255, 255)
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
void setup()
{

  dripCount=new ArrayList<PVector>();
  splashVector=new ArrayList<PVector>();
  endPosClick=new PVector();
  hm=new HashMap<Integer, String>();
  handColorMap=new HashMap<Integer, String>();
  handPositions=new ArrayList<PVector>();
  prevHandPositions=new ArrayList<PVector>();
  context = new SimpleOpenNI(this);
  output = createWriter("positions.txt");
  // size(1024,768, OPENGL);
  back=loadImage("back.jpg");
  maxv=0;
  minv=context.depthWidth()*100;
  context.setMirror(true);
  context.enableDepth(1024, 768, 30);
  context.enableRGB();
  context.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);
  context.enableHands();
  context.enableGesture();
  context.addGesture("RaiseHand");
  context.addGesture("Wave");
  context.addGesture("Click");
  handPositions=new ArrayList<PVector>();
  prevHandPositions=new ArrayList<PVector>();
  cont_length=context.depthWidth()*context.depthHeight();
  size(context.depthWidth(), context.depthHeight());
  blob_array=new int[cont_length];
  println("width-- = "+context.depthWidth()+"height-- = "+context.depthHeight());
}

void draw() {
  handC=color(255, 255, 255);
  context.update();
  int[] depthValues = context.depthMap();
  image(back, 0, 0);
  PImage rgbImage = context.rgbImage();
  // prepare the color pixels
  rgbImage.loadPixels();




  int[] userMap = null;
  int userCount = context.getNumberOfUsers();
  if (userCount > 0) {
    userMap = context.getUsersPixels(SimpleOpenNI.USERS_ALL);
  }

  loadPixels();
  translate(0, 0, -1000);
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
        //pixels[index] = rgbImage.pixels[index]; //userColors[colorIndex];
        blob_array[index]=255;
        usericon=userColors[colorIndex];
        // println("index i ="+index);
      }
      else {
        blob_array[index]=0;
        // pixels[index]=color(255,0,0);
      }
    }
    //println("index minv="+ minv);
    //   println("index maxv="+ maxv);
  }

  for (int a=5;a<cont_length-5;a++) {
    if (userMap != null && userMap[a] > 0) {
      int cindex=userMap[a]%userColors.length;
      usericon=userColors[cindex];
      if (blob_array[a-1]==0&&blob_array[a]==255) {

        pixels[a-1]=usericon;
        pixels[a-2]=usericon;
        pixels[a-3]=usericon;
        pixels[a-4]=usericon;
        pixels[a-5]=usericon;
      }
      else    if (blob_array[a]==255&&blob_array[a+1]==0) {
        pixels[a+1]=usericon;
        pixels[a+2]=usericon;
        pixels[a+3]=usericon;
        pixels[a+4]=usericon;
        pixels[a+5]=usericon;
      }


      // fill(usericon);
      //  ellipse(iconx,height-20,16,16);
    }
  }

  updatePixels();

  if (hm.size()>0) {
    for (String c:hm.values()) {
      fill(unhex(c));
      ellipse(iconx, height-20, 16, 16);
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
      PVector lastP;
      if (vecList!=null)
        if (vecList.size()>0)
          p = (PVector) vecList.get(0);
      pv=p;
      lastP=p;
      if (vecList!=null)
        if (vecList.size()>1)
          pv=(PVector) vecList.get(1);
      if (pv.y-p.y>150) {
        context.convertRealWorldToProjective(pv, dripC);
        dripCount.add(0, dripC);
        //dripCount.set(0,dripC);
        drip=true;
        println("color drip");
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
        for (int i=0;i<vecList.size();i++) {
          p = (PVector) vecList.get(i);
          pv=p;
          if (i-1>0)
            pv=(PVector) vecList.get(i-1);

          int pointcz=int(pa2d.z);
          int pointpz=int(pr2d.z);
          context.convertRealWorldToProjective(p, pa2d);
          context.convertRealWorldToProjective(pv, pr2d);
          //if(abs(pointcz-pointpz)<10)

          strokeWeight(5); 
          if (handColorMap.get(handId)!=null )
            stroke(unhex(handColorMap.get(handId)));

          line(pr2d.x, pr2d.y, pa2d.x, pa2d.y);
        }
      }

      strokeWeight(1);  


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

        output.println("current hand ID- "+handId);
        if (countTrack>30) {
          handColorMap.put(handId, hex(picker));
          //output.println()
        }
      }
      else {
        picker=color(255, 255, 255);
      }//rgbImage.pixels[int(pa2d.x+30)+int(pa2d.y) * context.depthWidth()];

      if ( handColorMap.get(handId)!=null) {
        fill( unhex(handColorMap.get(handId)));
        output.println("hand ID CC - "+handId);
      }
      else {
        fill(255, 255, 255);
      }
      triangle(int(last2d.x), int(last2d.y)-30, int(last2d.x)-30, int(last2d.y)+30, int(last2d.x)+30, int(last2d.y)+30);

      fill(picker);
      ellipse(int(last2d.x), int(last2d.y)-40, 25, 25);     
      fill(255, 0, 0);
      ellipse(int(last2d.x), int(last2d.y-20), 8, 8);
      fill(usericon);
      //     ellipse(int(pa2d.x),int(pa2d.y)-40,8,8);
      ellipse(int(last2d.x-40), int(last2d.y)+40, 8, 8);
      ellipse(int(last2d.x+40), int(last2d.y)+40, 8, 8);

      output.println("number of hands = " +keys.size());
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
          output.println("hand ID - "+handId+" x = "+curr_x+" y= "+curr_y + " hand ID Next -"+handIdNxt+" x=  "+next_x+" y= "+next_y);
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
    drawSplash(true, int(splashVector.get(i).x), int(splashVector.get(i).y), 15, 300, 300);
    //splash(splashVector.get(i),sc);
  }
  count++;
  for (int i=1; i <=count; i++)
  {

    if (drip) {
      ellipseX[count] = dripCount.get(0).x;
      ellipseY[count] = dripCount.get(0).y;
    }

    seed += 0.05;
    float n = noise(seed);
    ///Color c=sc;



    noStroke();
    fill(sc);
    if (ellipseX[i]!=0)
      ellipse(ellipseX[i], ellipseY[i], rad*n, rad*n);

    ellipseY[i]++;
    fill(255, 0, 0);
  }
  // transparency-=.5;
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
  //  println("size------ = "+vecList.size());
}

void onDestroyHands(int handId, float time) {
  ArrayList<PVector> vecList = new ArrayList<PVector>();

  println("onLostHand - handId: " + handId);
  handPathList.remove(handId);
  losthand++;
  context.addGesture("RaiseHand");
}

void onRecognizeGesture(String strGesture, PVector idPosition, PVector endPosition) {
  {
    if (strGesture.equals("RaiseHand")) {
      context.startTrackingHands(endPosition);
      context.removeGesture("RaiseHand");
    }
    context.convertRealWorldToProjective(endPosition, endPosition);
    if (strGesture.equals("Click")) {
      splashVector.add(endPosition);
      endPosClick=endPosition;
      //splash(endPosition,sc);
      println("Click gestures executed");
      //context.startTrackingHands(endPosition);
      //context.removeGesture("RaiseHand");
    }
  }
}
void drawSplash(boolean lights, int x, int y, int splash_num, int sizex, int sizey) {
  randomSeed(0);
  rds = map(sizex, 0, width, 30, 70);
  splash = map(sizey, 0, height, 1, 6);
  pushMatrix();
  translate(x, y);

  // le splash

  beginShape();
  for (int i = 0 ; i < 360; i+=5) {
    if (i == 0) {

      curveVertex(rds*cos(radians(0)), rds*sin(radians(0)));
    }

    if (i %10== 0) {   
      r = rds * random(0.9, max);
      float w = map(r, rds, max*rds, 1, splash);
      noStroke();
      smooth();
      curveVertex(r*cos(radians(i-w)), r*sin(radians(i-w)));
      curveVertex(r*cos(radians(i+w)), r*sin(radians(i+w)));
      //ellipse(r*cos(radians(i)), r*sin(radians(i)), 30, 30);
    } 
    else {
      r = rds;

      curveVertex(r*cos(radians(i)), r*sin(radians(i)));
    }
    fill(sc, 190);
  }

  curveVertex(rds*cos(radians(360)), rds*sin(radians(360)));
  endShape();
  drawLights();
}
void drawLights() {
  translate(-1, -1);
  scale(0.95);
  beginShape();
  for (int i = 0 ; i < 360; i+=5) {
    if (i == 0) {
      curveVertex(rds*cos(radians(0)), rds*sin(radians(0)));
    }

    if (i %10==0) {   
      r = rds * random(0.9, max);
      float w = map(r, rds, max*rds, 1, splash);
      curveVertex(r*cos(radians(i-w)), r*sin(radians(i-w)));
      curveVertex(r*cos(radians(i+w)), r*sin(radians(i+w)));
    } 
    else {
      r = rds;
      curveVertex(r*cos(radians(i)), r*sin(radians(i)));
    }
    fill(sc);
  }
  curveVertex(rds*cos(radians(360)), rds*sin(radians(360)));
  endShape(); 
  popMatrix();
}

