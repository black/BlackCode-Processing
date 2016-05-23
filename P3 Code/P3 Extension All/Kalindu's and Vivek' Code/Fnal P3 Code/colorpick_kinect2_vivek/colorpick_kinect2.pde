 /*--------LIBRARY---------HIS-----------*/
import SimpleOpenNI.*;
import java.awt.Toolkit;
import java.util.Iterator;
/*--------GLOBAL VARIABLE------------*/
PGraphics topLayer; 
SimpleOpenNI kinect;
ArrayList<PVector> handPositions;
ArrayList<Integer> c;
PVector currentHand;
//color c = color(255);
int x,y,z,i,j;
int[] position=new int[4];
int val=30;
int tol=100;    //change parameter
float mm;
int pos;
/*----------SETUP--------------------*/
void setup() {
  size (640, 480);
  kinect = new SimpleOpenNI(this);
  kinect.setMirror(false);
  kinect.enableDepth();
  kinect.enableRGB();
  kinect.enableGesture();
  kinect.enableHands();
  kinect.addGesture("RaiseHand");       //Wave
  handPositions=new ArrayList<PVector>();
  c=new ArrayList<Integer>();
  //handPositions=new PVector();
  topLayer = createGraphics(width, height, g.getClass().getName());
}
/*-------------DRAW--------------------*/
void draw() {
  kinect.update();
 // background(255);
  PImage rgbValues=kinect.rgbImage();
  image(rgbValues, 0, 0);
  /*--------------Initialise---------------------*/
  rgbValues.loadPixels();
  int[] depthValues = kinect.depthMap();
  for(int A=0;A<handPositions.size();A++){  
    currentHand = handPositions.get(A);
    if(currentHand.mag()!=0){ 
      x=(int)currentHand.x;
      y=(int)currentHand.y;
      z=(int)currentHand.z;
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
      if(count>2){    //change parameter
        c.set(A,rgbValues.pixels[pos]);
        Toolkit.getDefaultToolkit().beep();
        text("Color Picked!",x+val,y+val);
      }
      /*--------------------Draw--------------------*/  
      if(c.size()<A+1){
      c.add(A,#FFFFFF);
      }
      fill(c.get(A));
      rect(0, 0,50, 50);
      /*---------------*/
      strokeWeight(8);
      stroke(c.get(A));
      point(x, y);
      
      if(z>2000){
      topLayer.beginDraw();
      topLayer.noStroke();
      topLayer.fill(c.get(A));
      topLayer.ellipse(x,y,10,10);
      topLayer.endDraw();
      image(topLayer,0,0);
    }
  }

}
}
/*---------------------HAND RECOGNITION-----------------*/
void onCreateHands(int handId, PVector position, float time) {
  kinect.convertRealWorldToProjective(position, position);
  handPositions.add(handId-1,position);
  //handPositions=position.get();
  //Toolkit.getDefaultToolkit().beep();
 // println("handId: "+handId);
  kinect.addGesture("RaiseHand");
  handId++;
  delay(100);
}

void onUpdateHands(int handId, PVector position, float time) {
  kinect.convertRealWorldToProjective(position, position);
  handPositions.set(handId-1,position);
 // handPositions=position.get();
}

void onDestroyHands(int handId, float time) {
 // println("Destroyed!");
  //PImage rgbValues=kinect.rgbImage();
  //rgbValues.loadPixels();
  //c=rgbValues.pixels[(int)handPositions.x+val+(int)(handPositions.y+val)*640];
  //Toolkit.getDefaultToolkit().beep();
  handPositions.set(handId-1, new PVector(0,0,0));
  kinect.addGesture("RaiseHand");
  delay(100);
}

void onRecognizeGesture(String strGesture, PVector idPosition, PVector endPosition) {
  kinect.startTrackingHands(endPosition);
  kinect.removeGesture("RaiseHand");
}
/*--------------------------------------------------*/
