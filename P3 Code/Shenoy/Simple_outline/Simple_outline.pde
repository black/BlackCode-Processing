import SimpleOpenNI.*;
import java.util.*;
SimpleOpenNI context;
//--------------------------------------
color[] userColors = { 
  color(255, 0, 0), color(0, 255, 0), color(0, 0, 255), color(#FFC400), color(#8B00FF), color(#FF0D00)
};
color usericon = color(0, 255, 0);
int blob_array[];
int userCurID;
int cont_length = 640*480;
void setup()
{
  size(640, 480); //window size
  context = new SimpleOpenNI(this); // start and enable kinect object  
  context.setMirror(true); // set mirroring object
  context.enableDepth(); // enable depth  camera
  context.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL); // enable skeleton generation for all joints
  context.enableScene(); // enable scene 
  blob_array=new int[cont_length]; // initialize blob_array size
}

void draw() {
  background(-1); //set background to white color
  context.update(); // update kinect in each frame
  int[] depthValues = context.depthMap(); // save all depth values in a array
  int[] userMap = null; // initalize array to null
  int userCount = context.getNumberOfUsers(); // get number of user in scene
  if (userCount > 0) { // check if number of user is more than zero
    userMap = context.getUsersPixels(SimpleOpenNI.USERS_ALL); // get user pixles of all the users
  }
  loadPixels();
  for (int y=0; y<context.depthHeight (); y++) {
    for (int x=0; x<context.depthWidth (); x++) {
      int index = x + y * context.depthWidth();
      if (userMap != null && userMap[index] > 0) {
        int colorIndex = userMap[index] % userColors.length;
        userCurID = userMap[index];
        blob_array[index] = 255;
        usericon=userColors[colorIndex];
      } else {
        blob_array[index]=0;
      }
    }
  }
  for (int i=0; i<cont_length; i++) {
    if (userMap != null && userMap[i] > 0) {
      int cindex = userMap[i]%userColors.length;
      usericon = userColors[cindex];
      if ( i > 0 && blob_array[i-1]==0 && blob_array[i]==255) {
        pixels[i-1] =  color(255, 0, 0);
        //        pixels[i-2] = usericon;
        //        pixels[i-3] = usericon;
        //        pixels[i-4] = usericon;
        //        pixels[i-5] = usericon;
      } else if ( i+1 < cont_length && blob_array[i]==255 && blob_array[i+1]==0) {
        pixels[i+1] =color(0, 0, 255);
        //        pixels[i+2] = usericon;
        //        pixels[i+3] = usericon;
        //        pixels[i+4] = usericon;
        //        pixels[i+5] = usericon;
      }
    }
  }
  updatePixels();
}
void onNewUser(int userId) {
  println("detected" + userId);
}
void onLostUser(int userId) {
  println("lost: " + userId);
}

