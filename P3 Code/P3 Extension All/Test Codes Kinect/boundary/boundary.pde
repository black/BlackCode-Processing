import processing.opengl.*;
import SimpleOpenNI.*;
import java.util.Locale;
import diewald_CV_kit.libraryinfo.*;
import diewald_CV_kit.utility.*;
import diewald_CV_kit.blobdetection.*;

SimpleOpenNI  kinect;
PImage  userImage;
int userID;
int[] userMap;
PImage rgbImage;
Boolean tracking=false;
void setup() {
  size(640, 480, OPENGL);
  kinect = new SimpleOpenNI(this);
  kinect.enableDepth();
  kinect.enableUser(SimpleOpenNI.SKEL_PROFILE_NONE);
}

void draw() {
  background(0);
  kinect.update();
  if (kinect.getNumberOfUsers()>0) {
    // find out which pixels have users in them
    userMap = kinect.getUsersPixels(SimpleOpenNI.USERS_ALL);
    // populate  the pixels array
    // from the sketch's current contents
    loadPixels();
    for (int i = 0; i < userMap.length; i++) {
      // if the current pixel is on a user
      if (userMap[i] == 0) {
        // make it green
        pixels[i] = color(0, 255, 0);
      }
    }
    // display the changed pixel array
    updatePixels();
  }
}
void onNewUser(int uID) {
  userID = uID;
  tracking = true;
  println("tracking");
}

