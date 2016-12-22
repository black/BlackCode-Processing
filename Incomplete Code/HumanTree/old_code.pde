//import gab.opencv.*;
//import processing.video.*;
//import java.awt.*;
//
//Capture video;
//OpenCV opencv;
//
//void setup() {
//  size(640, 480);
//  video = new Capture(this, 640/2, 480/2);
//  opencv = new OpenCV(this, 640/2, 480/2);
//  opencv.loadCascade(OpenCV.CASCADE_FRONTALFACE);  
//  video.start();
//}
//
//void draw() {
//  pushMatrix();
//  video.loadPixels();
//  scale(2);
//  opencv.loadImage(video);
//  PImage img = getFaces();
//  image(video, 0, 0 );
//  popMatrix();
//  image(img, 100, 100 );
//  updatePixels();
//}
//
//void captureEvent(Capture c) {
//  c.read();
//}
//
//
//PImage getFaces() {
//  PImage img  = createImage(100, 100, RGB);
//  noFill();
//  stroke(0, 255, 0);
//  strokeWeight(3);
//  Rectangle[] faces = opencv.detect();
//  for (int i = 0; i < faces.length; i++) {
//    img  = createImage(faces[i].width, faces[i].height, RGB);
//    rect(faces[i].x, faces[i].y, faces[i].width, faces[i].height);
//    img.copy(video, 0, 0, video.width, video.height, faces[i].x, faces[i].y, faces[i].width, faces[i].height);
//  }
//  return img;
//}

