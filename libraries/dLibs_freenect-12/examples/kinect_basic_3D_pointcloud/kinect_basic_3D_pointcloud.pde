
//---------------------------------------------
//
// author: thomas diewald
// date:   31.03.2012
// 
// desc: basic example to get a simple pointcloud from kinect
//
//---------------------------------------------

// note: sometimes, the kinects depth/video (consequently there is no 3d-data) wont start 
//       at the very first run, in this case, just re-start the sketch!


import processing.opengl.*;

import peasy.*; // peasycam

import dLibs.freenect.toolbox.*;
import dLibs.freenect.constants.*;
import dLibs.freenect.interfaces.*;
import dLibs.freenect.*;


//-------------------------------------------------------------------
Kinect kinect_;                     // main kinect-object
KinectFrameVideo kinect_video_;     // video frame
KinectFrameDepth kinect_depth_;     // depth frame

Kinect3D k3d_;

// get width/height --> actually its always 640 x 480
int kinectFrame_size_x = VIDEO_FORMAT._RGB_.getWidth();   // width of kinect frame
int kinectFrame_size_y = VIDEO_FORMAT._RGB_.getHeight();  // height of kinect frame



PeasyCam cam;   


//-------------------------------------------------------------------
void setup(){
  size(kinectFrame_size_x, kinectFrame_size_y, OPENGL);
  kinect_ = new Kinect(0);  //create a main kinect instance with index 0

  kinect_video_ = new KinectFrameVideo(VIDEO_FORMAT._RGB_);    // create a video instance
  kinect_depth_ = new KinectFrameDepth(DEPTH_FORMAT._11BIT_);  // create a depth instance
  
  k3d_ = new Kinect3D(); // generate a 3d instance
  k3d_.setFrameRate(30); // set framerate
  
  
  kinect_video_.connect(kinect_);  //connect the created video instance to the main kinect
  kinect_depth_.connect(kinect_);  //connect the created depth instance to the main kinect
  k3d_.connect(kinect_);


  initPeasyCam();
}


//---------------------------------------------------------------------------------------------------- 
void initPeasyCam(){
  cam = new PeasyCam(this, 0, 0, 0, 600);
  cam.setMinimumDistance(1);
  cam.setMaximumDistance(100000);
  cam.setDistance(400);
  cam.setRotations(0,0,0);
}

//-------------------------------------------------------------------
void draw(){
  scale(100); // to scale the scene (original units are in meters)
  background(0);
  drawPointCloud();
}





//-------------------------------------------------------------------
void drawPointCloud(){  
  // get the kinects 3d-data (by reference)
  KinectPoint3D kinect_3d[] = k3d_.get3D();
  
  int jump = 5; // resolution, ... use every fifth point in 3d
  
  int cam_w_ = kinectFrame_size_x;
  int cam_h_ = kinectFrame_size_y;
  
  strokeWeight(3); 

  for(int y = 0; y < cam_h_-jump ; y+=jump){
    for(int x = 0; x< cam_w_-jump*2 ; x+=jump){
      int index1 = y*cam_w_+x;
 
      if (kinect_3d[index1].getColor() == 0 )
        continue;

      // do some simple color mapping
      // for accurate mapping see the pther examples!!
      stroke(kinect_3d[index1].getColor() ); //get color from video frame
      
      float cx = kinect_3d[index1].x;
      float cy = kinect_3d[index1].y;
      float cz = kinect_3d[index1].z;
      point(cx, cy, cz);
      
    }
  }
} 




//-------------------------------------------------------------------
// this is maybe not necessary, but is the proper way to close everything
void dispose(){
  Kinect.shutDown(); 
  super.dispose();
}
