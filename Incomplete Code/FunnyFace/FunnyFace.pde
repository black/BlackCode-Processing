import gab.opencv.*;
import processing.video.*;
import java.awt.*;

Capture video;
OpenCV opencvFace, opencvMouth, opencvEye;

void setup() {
  size(640, 480);
  video = new Capture(this, 640/2, 480/2);
  opencvFace = new OpenCV(this, 640/2, 480/2);
  opencvMouth = new OpenCV(this, 640/2, 480/2);
  opencvEye = new OpenCV(this, 640/2, 480/2);

  opencvFace.loadCascade(OpenCV.CASCADE_FRONTALFACE);  
  opencvMouth.loadCascade(OpenCV.CASCADE_MOUTH);
  opencvEye.loadCascade(OpenCV.CASCADE_EYE);

  video.start();
}
PImage img;
void draw() {
  scale(2);
  opencvMouth.loadImage(video);
  opencvFace.loadImage(video);
  opencvEye.loadImage(video);

  image(video, 0, 0 );
  Rectangle[] faces = opencvFace.detect();
  Rectangle[] mouth = opencvMouth.detect();
  Rectangle[] eyes = opencvEye.detect();

  // FACE
  noFill();
  stroke(0, 255, 0);
  strokeWeight(3);
  for (int i = 0; i < faces.length; i++) {
    //rect(faces[i].x, faces[i].y, faces[i].width, faces[i].height);
  }

  // MOUTH
  noFill();
  stroke(255, 0, 0);
  strokeWeight(3);
  for (int j = 0; j < mouth.length; j++) {
    //if (i<1)rect(mouth[i].x, mouth[i].y, mouth[i].width, mouth[i].height);
    for (int i = 0; i < eyes.length; i++) {
      //  rect(eyes[i].x, eyes[i].y, eyes[i].width, eyes[i].height);
      if (j<1) {
        //rect(mouth[i].x, mouth[i].y, mouth[i].width, mouth[i].height);
        img = video.get(mouth[j].x, mouth[j].y-3, mouth[j].width, mouth[j].height/2);
      }
      if (i<2)image(img, eyes[i].x, eyes[i].y+5, eyes[i].width, eyes[i].height/2);
    }
  }

  // EYES
  noFill();
  stroke(0, 0, 255);
  strokeWeight(3);
}

void captureEvent(Capture c) {
  c.read();
}

