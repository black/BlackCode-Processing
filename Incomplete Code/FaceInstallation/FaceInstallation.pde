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
    println(faces[i].x + "," + faces[i].y);
    rect(faces[i].x, faces[i].y, faces[i].width, faces[i].height);
  }

  // MOUTH
  noFill();
  stroke(255, 0, 0);
  strokeWeight(3);
  for (int i = 0; i < mouth.length; i++) {
    println(mouth[i].x + "," + mouth[i].y);
    rect(mouth[i].x, mouth[i].y, mouth[i].width, mouth[i].height);
  }

  // EYES
  noFill();
  stroke(0, 0, 255);
  strokeWeight(3);
  for (int i = 0; i < eyes.length; i++) {
    println(eyes[i].x + "," + eyes[i].y);
    rect(eyes[i].x, eyes[i].y, eyes[i].width, eyes[i].height);
  }
}

void captureEvent(Capture c) {
  c.read();
}

