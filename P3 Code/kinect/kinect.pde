/* OpenProcessing Tweak of *@*http://www.openprocessing.org/sketch/60316*@* */
/* !do not delete the line above, required for linking your tweak if you re-upload */

//static public void main(String args[]){
//  Frame frame = new Frame("testing");
//  frame.setUndecorated(true);
//  // The name "sketch_name" must match the name of your program
//  PApplet applet = new kinect();
//  frame.add(applet);
//  applet.init();
//  frame.setBounds(0, 0, 1440, 900); 
//  frame.setVisible(true);
//}

import SimpleOpenNI.*;
import controlP5.*;
import toxi.geom.*;

public float   xmin = -1000;
public float   xmax = 1000;
public float   ymin = -1000;
public float   ymax = 1000;
public float   zmin = -1000;
public float   zmax = 1000;
public int     resolution = 2;
public int     bandsResolution;
public int     pixelsResolution;
public int     bandsSeparation = 0;
public int     pixelSize = resolution;
public boolean drawBands = true;
public boolean drawPixels = false;
public boolean realColor = true;
public boolean explode = false;
public boolean disolve = false;
public boolean follow =  false;
public boolean sandEffect = false;
public boolean drawSculpture = false;
public boolean drawSphere = false;
public boolean drawBalls = false;
public boolean drawGrid = false;
public boolean drawFloor = false;

SimpleOpenNI context;

float zoomF = 0.35;
float rotX  = PI;
float rotY  = 0;

Particles      par;
Bands          band;
ConnectedLines connectedLine;
ArrayList      particlesList = new ArrayList();
ArrayList      bandsList = new ArrayList();
Spline3D       splinePoints = new Spline3D();
ArrayList      balls = new ArrayList();
Ball           sph = new Ball(new PVector(0,0,2000),new PVector(0,0,0),100);

void setup(){
  //size(1440, 900,P3D);
  size(1024,768,P3D);
  frameRate(300);
  perspective(radians(45),float(width)/float(height),10.0,150000.0);
                         
  context = new SimpleOpenNI(this);
  context.setMirror(true);
  context.enableDepth();  
  context.enableRGB();
  context.alternativeViewPointDepthToImage();
  context.enableHands();
  context.enableGesture();
  context.addGesture("RaiseHand");
  context.update();

  calculateLimits(context.depthMap(),context.depthMapRealWorld());
  
  controlPanel();
}

void draw(){
  context.update();
  int[] depthMap = context.depthMap();
  PVector[] realWorldMap = context.depthMapRealWorld();
  PImage rgbImage = context.rgbImage();

  int[] resDepth = resizeDepth(depthMap,resolution);
  PVector[]resMap3D = resizeMap3D(realWorldMap,resolution);
  PImage resRGB = resizeRGB(rgbImage,resolution);
  boolean[] constrainedImg = constrainImg(resDepth,resMap3D,xmin,xmax,ymin,ymax,zmin,zmax);
  int resXsize = context.depthWidth()/resolution;
  int resYsize = context.depthHeight()/resolution;
  
  background(10);
  translate(width/2,height/2,0);
  rotateX(rotX);
  rotateY(rotY);
  scale(zoomF);
  translate(0,0,-1500);
  
  if(drawFloor){
    drawFloor(color(150),xmin,xmax,ymin,ymax,zmin,zmax);
  }

  if(drawGrid){
    drawGrid(color(255),xmin,xmax,ymin,ymax,zmin,zmax);
  }
  
  if(!realColor){
    directionalLight(255,255,255,0,-0.2,1); 
  }

  if(drawBands){
    if(!explode && !disolve){
      band = new Bands(resMap3D,resRGB,constrainedImg,resXsize,resYsize);
    }
    else if(explode){
      band.explode();
    }
    else if(disolve){
      band.disolve(ymin);
    }
    
    if(realColor){
      band.paint(bandsSeparation);
    }
    else{
      band.paint(color(50,50,255),bandsSeparation);
    }
    
    if(follow){
      bandsList.add(band);
      if(bandsList.size() > 30){
        Bands band1 = (Bands) bandsList.get(15);
        Bands band2 = (Bands) bandsList.get(0);
        if(realColor){
          band1.paint(bandsSeparation);
          band2.paint(bandsSeparation);
        }
        else{
          band1.paint(color(255,50,50),bandsSeparation);
          band2.paint(color(50,255,50),bandsSeparation);
        }
        bandsList.remove(0);
      }
    }
    else{
      bandsList.clear();
    }
  }

  if(drawPixels){
    par = new Particles(resMap3D,resRGB,constrainedImg);
    //connectedLine = new ConnectedLines(resMap3D,constrainedImg,resXsize,resYsize);
    if(realColor){
      par.paint(pixelSize);
      //connectedLine.paint(color(200));
    }
    else{
      par.paint(pixelSize,color(200));
      //connectedLine.paint(color(200));
    }

    if(follow){
      particlesList.add(par);
      if(sandEffect){
        for(int i = 0; i < particlesList.size()-1; i++){
          par = (Particles) particlesList.get(i);
          par.paint(pixelSize,color(200));
          par.update(ymin);
        }
        if(particlesList.size() > 30){
          particlesList.remove(0);
        }
      } 
      else{
        if(particlesList.size() > 30){
          Particles par1 = (Particles) particlesList.get(15);
          Particles par2 = (Particles) particlesList.get(0);
          if(realColor){
            par1.paint(pixelSize);
            par2.paint(pixelSize);
          }
          else{
            par1.paint(pixelSize,color(200));
            par2.paint(pixelSize,color(200));
          }
          particlesList.remove(0);
        }
      }
    }
    else{
      particlesList.clear();
    }
  }

  if(realColor){
    directionalLight(255,255,255,0,-0.2,1); 
  }

  if(drawSculpture){
    if(splinePoints.getPointList().size() > 2){
      splinePoints.setTightness(0.25);
      java.util.List vertices = splinePoints.computeVertices(4);
      for(int i = 0; i < vertices.size()-2; i++){
        Vec3D p1 = (Vec3D) vertices.get(i);
        Vec3D p2 = (Vec3D) vertices.get(i+1);
        Vec3D p3 = (Vec3D) vertices.get(i+2);
        float rad1 = 60; // + 20*noise(float(i)*0.1);
        float rad2 = 60; // + 20*noise((float(i)+0.5)*0.1);
        float rad3 = 60; // + 20*noise(float(i+1)*0.1);
        color col = color(255);
        float frac = 0.2;
      
        cilinder(p1,p2,rad1,rad2,col,frac);
        connector(p1,p2,p3,rad2,rad3,col,frac);
      }
    }
  }
  
  if(drawBalls){
    for(Iterator i = balls.iterator(); i.hasNext(); ){
      Ball b = (Ball) i.next();
      b.paint(color(255));
      b.update(new PVector(0,-15,0),1);
      if(b.pos.y < ymin){
        i.remove();
      }
    }  
  }
  
  if(drawSphere){
    color col = color(0,50+200*noise(frameCount*0.05),50+200*noise(200+frameCount*0.05));
    sph.paint(col);
    sph.contact(resMap3D,constrainedImg,resXsize);
    sph.update(new PVector(0,0,0),0.5);
  }
  
}

