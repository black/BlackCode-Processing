/*----------------------------LIBRARIES-----------------------------*/
import SimpleOpenNI.*;
import java.awt.Toolkit;
import java.util.Iterator;
import java.awt.Frame;
/*-------------------------GLOBAL VARIABLES-------------------------*/
SimpleOpenNI kinect;
ArrayList<PVector> handPositions,prevHandPositions;
ArrayList<Integer> c,trans;
ArrayList<Integer> lcount,rcount;
PVector currHand,prevHand;
float mm;
int i,j,A,B,C,D,pos,flag,count;
int val=20,tol1=120,tol2=70;    //change parameters
PFrame f;
secondApplet s;
/*-----------------------------SETUP---------------------------------*/
void setup() {
  size (640, 480);
  kinect = new SimpleOpenNI(this);
  PFrame f=new PFrame();
  kinect.setMirror(true);
  kinect.enableDepth();
  kinect.enableRGB();
  kinect.enableGesture();
  kinect.enableHands();
  kinect.addGesture("RaiseHand");
  kinect.addGesture("Wave");
  kinect.addGesture("Click");
  handPositions=new ArrayList<PVector>();
  prevHandPositions=new ArrayList<PVector>();
  c=new ArrayList<Integer>();
  trans=new ArrayList<Integer>();
  rcount=new ArrayList<Integer>();
  lcount=new ArrayList<Integer>();
  for(D=0;D<100;D++){
    c.add(D,#FFFFFF);     //limit #hands to 100
    trans.add(D,0);
    rcount.add(D,0);
    lcount.add(D,0);
  }
}
/*------------------------------DRAW----------------------------------*/
void draw() {
  kinect.update();
  PImage rgbValues=kinect.rgbImage();
  image(rgbValues, 0, 0);
  rgbValues.loadPixels();
  int[] depthValues = kinect.depthMap();
  
  for(A=0;A<handPositions.size();A++){
    flag=0;
    prevHand=prevHandPositions.get(A);
    currHand=handPositions.get(A);
    if(currHand.mag()!=0){ 
      colorMix();
      if(flag==0)
        colorPick(depthValues,rgbValues.pixels);
      if(trans.get(A)==0)
        rightSwipe();
      else {
        leftSwipe();
        paint();
      }
      display();
    }
  }
}
/*-------------------------MULTIPLE HAND TRACKING----------------------*/
void onCreateHands(int handId, PVector position, float time) {
  kinect.convertRealWorldToProjective(position, position);
  prevHandPositions.add(handId-1,new PVector(0,0,0));
  handPositions.add(handId-1,position);
  Toolkit.getDefaultToolkit().beep();
  kinect.addGesture("RaiseHand");
  println("Created! HandId: "+handId);
}

void onUpdateHands(int handId, PVector position, float time) {
  kinect.convertRealWorldToProjective(position, position);
  prevHandPositions.set(handId-1,handPositions.get(handId-1));
  handPositions.set(handId-1,position);
}

void onDestroyHands(int handId, float time) {
  //PImage rgbValues=kinect.rgbImage();
  //rgbValues.loadPixels();
  //c=rgbValues.pixels[(int)handPositions.x+val+(int)(handPositions.y+val)*640];
  Toolkit.getDefaultToolkit().beep();
  prevHandPositions.set(handId-1,handPositions.get(handId-1));
  handPositions.set(handId-1, new PVector(0,0,0));
  kinect.addGesture("RaiseHand");
  println("Destroyed! HandId: "+handId);
}

void onRecognizeGesture(String strGesture, PVector idPosition, PVector endPosition) {
  if(strGesture.equals("RaiseHand")){
    kinect.startTrackingHands(endPosition);
    kinect.removeGesture("RaiseHand");
  }
  kinect.convertRealWorldToProjective(endPosition,endPosition);
  if(strGesture.equals("Wave"))
    colorRemove(endPosition);
  else if(strGesture.equals("Click"))
    colorSplash(endPosition);
}
/*----------------------------SECOND FRAME----------------------------*/
public class PFrame extends Frame {
    public PFrame() {
        setBounds(100,100,640,480);
        s = new secondApplet();
        add(s);
        s.init();
        show();
    }
}
public class secondApplet extends PApplet {
    public void setup() {
        size(640, 480);
        s.background(255);
        noLoop();
    }
    public void draw() {
    }
}
/*------------------------------FUNCTIONS-----------------------------*/
void colorMix(){
  for(B=0;B<handPositions.size();B++){
    if(PVector.dist(handPositions.get(A),handPositions.get(B))<tol2 && A!=B){
      flag=1;
      println("Color Mixed!");
      Toolkit.getDefaultToolkit().beep();
      if(c.get(A)==#FFFFFF)
        c.set(A,c.get(B));
      else if(c.get(B)==#FFFFFF)
        c.set(B,c.get(A));
      else {
        c.set(A,lerpColor(c.get(A),c.get(B),0.5));
        c.set(B,lerpColor(c.get(A),c.get(B),0.5));
      }
      //delay(100);
    }
  }
}
/*----------------------------------------------------*/
void colorPick(int[] depthValues, color[] pix){
  /*-------------One Point-----------*/
  /*pos = (int)currHand.x+val + ((int)currHand.y+val)*640;
  mm = depthValues[pos];
  //println("depth " + mm + " // " + " z " + currHand.z);
  if (abs(currHand.z-mm)<tol1){
    println("Color Picked!");
    Toolkit.getDefaultToolkit().beep();
    c.set(A,rgbValues.pixels[pos]);
    //delay(100);
  }
  strokeWeight(8);
  stroke(255,255,0);
  point((int)currHand.x+val,(int)currHand.y+val);*/
  /*------------Four Points-----------*/
  count=0;
  for (i=0;i<2;i++){
    for (j=0;j<2;j++){
       mm=depthValues[(int)(currHand.x+pow(-1,i)*val)+(int)(currHand.y+pow(-1,j)*val)*640];
       //print(mm+"  ");
       if(abs(currHand.z-mm)<tol1){
         count=count+1;
         pos=(int)(currHand.x+pow(-1,i)*val)+(int)(currHand.y+pow(-1,j)*val)*640;
       }
       strokeWeight(8);
       stroke(255,255,0);
       point((int)(currHand.x+pow(-1,i)*val),(int)(currHand.y+pow(-1,j)*val)); 
    }
  }
  //println(currHand.z+"//"+count);
  if(count>2){                //change parameter
    c.set(A,pix[pos]);
    Toolkit.getDefaultToolkit().beep();
    println("Color Picked!");
    //delay(100);
  }
}
/*----------------------------------------------------*/
void rightSwipe(){
  if(currHand.x>prevHand.x+20)     //change parameter
    rcount.set(A,rcount.get(A)+1);
  else
    rcount.set(A,0);
  if(rcount.get(A)>10){          //change parameter
    println("Transfer Started!");
    rcount.set(A,0);
    trans.set(A,1);
    Toolkit.getDefaultToolkit().beep();
    //delay(100);
  }
}
/*---------------------------------------------------*/
void leftSwipe(){
  if(currHand.x<prevHand.x-20)      //change parameter
    lcount.set(A,lcount.get(A)+1);
  else
    lcount.set(A,0);
  if(lcount.get(A)>10){         //change parameter
    println("Transfer Stopped!");
    lcount.set(A,0);
    trans.set(A,0);
    Toolkit.getDefaultToolkit().beep();
    //delay(100);
  }
}
/*---------------------------------------------------*/
void paint(){
  s.strokeWeight(2);
  s.stroke(c.get(A));
  s.line(prevHand.x,prevHand.y,currHand.x,currHand.y);
  s.redraw();
}
/*---------------------------------------------------*/
void display(){
  strokeWeight(8);
  stroke(255,0,0);
  point((int)currHand.x,(int)currHand.y);
  strokeWeight(2);
  stroke(255);
  fill(c.get(A));
  rect((A*150)%640, 0, 100, 100);
}
/*---------------------------------------------------*/
void colorRemove(PVector endPosition){
  Toolkit.getDefaultToolkit().beep();
  for(C=0;C<handPositions.size();C++){
    if(PVector.dist(handPositions.get(C),endPosition)<50){    //change parameter
      c.set(C,#FFFFFF);
      println("Color Removed!");
    }
  }
  //delay(100);
}
/*---------------------------------------------------*/
void colorSplash(PVector endPosition){
  Toolkit.getDefaultToolkit().beep();
  for(C=0;C<handPositions.size();C++){
    if(PVector.dist(handPositions.get(C),endPosition)<50){    //change parameter
      s.noStroke();
      s.fill(c.get(C));
      s.ellipse(handPositions.get(C).x,handPositions.get(C).y,10,30);
      s.redraw();
      println("Color Splashed!");
      splash(handPositions.get(C).x,handPositions.get(C).y);
    }
  }
  //delay(100);
}
/*--------------------------------------------------------------------*/

void splash(float _x, float _y){
  float xl=handPositions.get(C).x;
  float yl=handPositions.get(C).y; 
  stroke(255, 0, 0);
  fill(255, 0, 0);
  star((int)random(2, 8), xl-random(100), yl-random(100), random(100), random(100), -PI / 2.0, random(0, 1.5));
  star((int)random(2, 8), xl+random(200), yl+random(140), random(100), random(100), -PI / 2.0, random(0, 3.0));
  star((int)random(2, 8), xl+random(50), yl-random(78), random(100), random(100), -PI / 2.0, random(0, 2));
  star((int)random(2, 8), xl-random(120), yl+random(100), random(100), random(100), -PI / 2.0, random(0, 0.50));
//  ellipse(mouseX + 50, mouseY + 50,random(40),random(40));
}
 
void star(int n, float cx, float cy, float r, float proportion)
{
  star(n, cx, cy, 2.0 * r, 2.0 * r, 0.0, proportion);
}
 
void star(int n, float cx, float cy, float w, float h,
  float startAngle, float proportion)
{
  if (n > 2)
  {
    float angle = TWO_PI/ (random(2,6) *n);
    float dw; // draw width
    float dh; // draw height
     
    w = w / 2.0;
    h = h / 2.0;
     
    s.beginShape();
    for (int i = 0; i < random(4, 12) * n; i++)
    {
      dw = w;
      dh = h;
      if (i % 2 == 1) // for odd vertices, use short radius
      {
        dw = w * proportion;
        dh = h * proportion;
      }
      s.curveVertex(cx + dw * cos(startAngle + angle * i) + random(10), cy + dh * sin(startAngle + angle * i) + random(10));
    }
    s.endShape(CLOSE);
  }
}


