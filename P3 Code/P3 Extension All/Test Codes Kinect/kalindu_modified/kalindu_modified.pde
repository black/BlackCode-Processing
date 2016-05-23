import SimpleOpenNI.*;
SimpleOpenNI context;
//--------------------------------------
int rad = 50;
//float speed = 25.0;
float slowness = 1.0;
import processing.opengl.*;
//red                          //green       //blue                     //yellow                //purple                            //cyan
color[] userColors = {     
  color(255, 0, 0), color(0, 255, 0), color(0, 0, 255), color(255, 255, 0), color(255, 0, 255), color(0, 255, 255)
};
int val=30, tol1=120, tol2=70, tol3=10, tol4=20; 
int count;
color usericon=color(0, 255, 0);
int blob_array[];
int minv, maxv;
int iconx, pos;
int cont_length;
int userCurID;
color handC;

void setup()
{
  context = new SimpleOpenNI(this);
  maxv=0;
  minv=context.depthWidth()*100;
  context.setMirror(true);
  context.enableDepth(1024, 768, 30);
  context.enableRGB();
  context.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);
  context.enableHands();
  context.enableGesture();
  context.addGesture("RaiseHand");
  context.addGesture("Wave");
  context.addGesture("Click");

  cont_length=context.depthWidth()*context.depthHeight();
  size(context.depthWidth(), context.depthHeight());
  blob_array=new int[cont_length];
  println("width-- = "+context.depthWidth()+"height-- = "+context.depthHeight());
}

void draw() {
  handC=color(255, 255, 255);
  context.update();
  int[] depthValues = context.depthMap();
  background(255);
  grids();
  PImage rgbImage = context.rgbImage();
  // prepare the color pixels
  rgbImage.loadPixels();
  int[] userMap = null;
  int userCount = context.getNumberOfUsers();
  if (userCount > 0) {
    userMap = context.getUsersPixels(SimpleOpenNI.USERS_ALL);
  }

  loadPixels();
  translate(0, 0, -1000);
  //println("width-- = "+context.depthWidth()+"height-- = "+context.depthHeight());
  for (int y=0; y<context.depthHeight(); y++) {
    for (int x=0; x<context.depthWidth(); x++) {
      int index = x + y * context.depthWidth();
      //  println("index ss ="+ index+" x  ss= "+x);
      if (userMap != null && userMap[index] > 0) {
        //  println("index ="+ index+" x = "+x);
        int colorIndex = userMap[index] % userColors.length;
        userCurID= userMap[index];
        // picker=rgbImage.pixels[index];;
        pixels[index] = rgbImage.pixels[index]; //userColors[colorIndex];
        blob_array[index]=255;
        usericon=userColors[colorIndex];
        // println("index i ="+index);
      }
      else {
        blob_array[index]=0;
        // pixels[index]=color(255,0,0);
      }
    }
  }

  for (int a=5;a<cont_length-5;a++) {
    if (userMap != null && userMap[a] > 0) {
      int cindex=userMap[a]%userColors.length;
      usericon=userColors[cindex];
      if (blob_array[a-1]==0&&blob_array[a]==255) {
        pixels[a-1]=usericon;
        pixels[a-2]=usericon;
        pixels[a-3]=usericon;
        pixels[a-4]=usericon;
        pixels[a-5]=usericon;
      }
      else    if (blob_array[a]==255&&blob_array[a+1]==0) {
        pixels[a+1]=usericon;
        pixels[a+2]=usericon;
        pixels[a+3]=usericon;
        pixels[a+4]=usericon;
        pixels[a+5]=usericon;
      }
    }
  }

  updatePixels();
}

//-------------------------------------------------------------


int dodge =10;
void mouseDragged()
{
  dodge = dodge + (mouseX-pmouseX);
}
void grids() {  
  for (int i=0;i <width;i+=dodge) { 
    for (int j=0;j<height;j+=dodge) {  
      fill(0);    
      textSize(8);      
      text("+", i, j);
    }
  }
}
//----------------------------------------------------
void onNewUser(int userId) {

  //iconx=iconx+30;
  println("detected" + userId);
  //  users.add(userId);//a new user was detected add the id to the list
}
void onLostUser(int userId) {

  println("lost: " + userId);
  //  iconx=iconx-30;
  //not 100% sure if users.remove(userId) will remove the element with value userId or the element at index userId
  //users.remove((Integer)userId);//user was lost, remove the id from the list
}
// hand events

void onCreateHands(int handId, PVector position, float time) {
  println("onNewHand - handId: " + handId + ", pos: " + position);

  ArrayList<PVector> vecList = new ArrayList<PVector>();
  vecList.add(position);
}

void onUpdateHands(int handId, PVector position, float time) {
}

void onDestroyHands(int handId, float time) {
  ArrayList<PVector> vecList = new ArrayList<PVector>();
  println("onLostHand - handId: " + handId);


  context.addGesture("RaiseHand");
}

void onRecognizeGesture(String strGesture, PVector idPosition, PVector endPosition) {
  {
    if (strGesture.equals("RaiseHand")) {
      context.startTrackingHands(endPosition);
      context.removeGesture("RaiseHand");
    }
    context.convertRealWorldToProjective(endPosition, endPosition);
    if (strGesture.equals("Click")) {
      println("Click gestures executed");
    }
  }
}

