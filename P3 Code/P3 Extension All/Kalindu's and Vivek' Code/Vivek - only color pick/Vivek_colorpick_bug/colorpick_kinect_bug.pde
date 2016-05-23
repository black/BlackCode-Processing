 /*--------LIBRARY--------------------*/
import SimpleOpenNI.*;
import java.awt.Toolkit;
import java.util.Iterator;
/*--------GLOBAL VARIABLE------------*/
SimpleOpenNI kinect;
ArrayList<PVector> handPositions;
ArrayList<Integer> c;
PVector currentHand;
int x,y,z,i,j,A,B,flag;
int[] position=new int[4];
int val=50;int tol=100;    //change parameter
float mm;int pos;
/*----------SETUP--------------------*/
void setup() {
  size (640, 480);
  kinect = new SimpleOpenNI(this);
  kinect.setMirror(true);
  kinect.enableDepth();
  kinect.enableRGB();
  kinect.enableGesture();
  kinect.enableHands();
  kinect.addGesture("Wave");
  handPositions=new ArrayList<PVector>();
  c=new ArrayList<Integer>();
}
/*-------------DRAW--------------------*/
void draw() {
  kinect.update();
  PImage rgbValues=kinect.rgbImage();
  image(rgbValues, 0, 0);
  /*--------------Initialise---------------------*/
  rgbValues.loadPixels();
  int[] depthValues = kinect.depthMap();
  for(A=0;A<handPositions.size();A++){
    flag=0;  
    currentHand = handPositions.get(A);
    if(currentHand.mag()!=0){
      if(c.size()<A+1)c.add(A,#FFFFFF); 
      x=(int)currentHand.x;
      y=(int)currentHand.y;
      z=(int)currentHand.z;
      /*------------------ Color Mixing-----------------*/
      for(B=0;B<handPositions.size();B++){
        if(PVector.dist(handPositions.get(A),handPositions.get(B))<tol && A!=B){
          flag=1;
          Toolkit.getDefaultToolkit().beep();
          if(c.get(A)==#FFFFFF)c.set(A,c.get(B));
          else if(c.get(B)==#FFFFFF)c.set(B,c.get(A));
          else {
            c.set(A,(c.get(A)+c.get(B))/2);
            c.set(B,(c.get(A)+c.get(B))/2);
          }
        }
      }
      if(flag==0){
        /*-----------------One Point-------------------*/
        /*pos = (x+val) + (y+val)*640;
        mm = depthValues[pos];
        println("depth " + mm + " // " + " z " + z);
        if (abs(z-mm)<tol){
          println("Color Picked!");
          Toolkit.getDefaultToolkit().beep();
          c = rgbValues.pixels[pos];
          delay(100);
        }
        strokeWeight(8);
        stroke(255,255,0);
        point(x+val, y+val);*/
        /*----------------Four Points------------------*/
        int count=0;
        for (i=0;i<2;i++){
          for (j=0;j<2;j++){
             mm=depthValues[x+(int)pow(-1,i)*val+(y+(int)pow(-1,j)*val)*640];
             print(mm+"  ");
             if(abs(z-mm)<tol){
               count=count+1;
               pos=x+(int)pow(-1,i)*val+(y+(int)pow(-1,j)*val)*640;
             }
             strokeWeight(8);
             stroke(255,255,0);
             point(x+(int)pow(-1,i)*val,y+(int)pow(-1,j)*val); 
          }
        }
        println(z+"//"+count);
        if(count>2){                //change parameter
          c.set(A,rgbValues.pixels[pos]);
          Toolkit.getDefaultToolkit().beep();
          println("Color Picked!");
          delay(100);
        }
      }
      /*--------------------Draw--------------------*/  
      strokeWeight(8);
      stroke(255,0,0);
      point(x, y);
      strokeWeight(2);
      stroke(255);
      fill(c.get(A));
      rect((A*150)%640, 0, 100, 100);
    }
  }
}
/*---------------------HAND RECOGNITION-----------------*/
void onCreateHands(int handId, PVector position, float time) {
  kinect.convertRealWorldToProjective(position, position);
  handPositions.add(handId-1,position);
  Toolkit.getDefaultToolkit().beep();
  kinect.addGesture("Wave");
  handId++;
  delay(100);
}

void onUpdateHands(int handId, PVector position, float time) {
  kinect.convertRealWorldToProjective(position, position);
  handPositions.set(handId-1,position);
}

void onDestroyHands(int handId, float time) {
  println("Destroyed!");
  //PImage rgbValues=kinect.rgbImage();
  //rgbValues.loadPixels();
  //c=rgbValues.pixels[(int)handPositions.x+val+(int)(handPositions.y+val)*640];
  Toolkit.getDefaultToolkit().beep();
  handPositions.set(handId-1, new PVector(0,0,0));
  kinect.addGesture("Wave");
  delay(100);
}

void onRecognizeGesture(String strGesture, PVector idPosition, PVector endPosition) {
  kinect.startTrackingHands(endPosition);
  kinect.removeGesture("Wave");
}
/*--------------------------------------------------*/
