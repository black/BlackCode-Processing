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
ArrayList<PVector> poop = new ArrayList<PVector>();
void setup()
{
  size(displayWidth-100, displayHeight-100, P3D);
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
  //////-------------------------........User Boundaries..........-----------------------------------------------////
  //marked user pixels 
  for (int y=0; y<context.depthHeight(); y++) {
    for (int x=0; x<context.depthWidth(); x++) {
      int index = x + y * context.depthWidth();
      //  println("index ss ="+ index+" x  ss= "+x);
      if (userMap != null && userMap[index] > 0) {
        //  println("index ="+ index+" x = "+x);

        int colorIndex = userMap[index] % userColors.length;
        userCurID= userMap[index];

        blob_array[index]=255;
        usericon=userColors[colorIndex];
      }
      else {
        blob_array[index]=0;
      }
    }
  }

  //------------------ take out the contour coordinates ---
  for (int x=0; x<context.depthWidth(); x++) {
    for (int y=0; y<context.depthHeight(); y++) {
      int i = x + y*context.depthWidth();
      if (userMap != null && userMap[i] > 0) {
        int cindex = userMap[i]%userColors.length;
        usericon = userColors[cindex];
        if ( i > 0 && blob_array[i-1]==0 && blob_array[i]==255) {
          //pixels[i-1] = usericon;
          float xx=map(x, 0, 640, 0, width);
          float yy=map(y, 0, 480, 0, height);
          PVector p1 = new PVector(xx, yy);
          poop.add(p1);
        }
        else if ( i+1 < cont_length && blob_array[i]==255 && blob_array[i+1]==0) {
          //pixels[i+1] = usericon;
          float xx=map(x, 0, 640, 0, width);
          float yy=map(y, 0, 480, 0, height);
          PVector p2 = new PVector(xx, yy);
          poop.add(p2);
        }
        //-------------------
        for (int l=0;l<poop.size();l++) {
          sortEdgeCoordinates(l);
        }
        //-------------------
      }
    }
  }

  for (int k=0; k<path.size(); k+=10) {
    PVector P1 = (PVector) path.get(k);
    color Col = (color) random(#000000);
    if ( userCount < userColors.length ) { 
      Col = userColors[userCount];
    }
    else userCount = 0;
    for (int j=k+1; j<path.size(); j+=10) {
      PVector P2 = (PVector) path.get(j);
      if (P1.dist(P2)<30) {
        stroke(Col, 10);
        strokeWeight(10);
        line(P1.x, P1.y, P2.x, P2.y);
        stroke(Col, 90);
        strokeWeight(2);
        line(P1.x, P1.y, P2.x, P2.y);
      }
    }
  }
  path.clear();
  poop.clear();
}
void onNewUser(int userId) {
  println("detected" + userId);
}
void onLostUser(int userId) {
  println("lost: " + userId);
}

