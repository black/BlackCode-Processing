import neurosky.*;
import org.json.*;
import java.util.*;
import java.net.*;
//-------------------------------------------
ThinkGearSocket neuroSocket;
//-------------------------------------------
ArrayList<Points> HighA = new ArrayList();
ArrayList<Points> LowA = new ArrayList();
ArrayList<Points> HighB = new ArrayList();
ArrayList<Points> LowB = new ArrayList();
ArrayList<Points> MidG = new ArrayList();
ArrayList<Points> LowG = new ArrayList();
ArrayList<Points> DeltaX = new ArrayList();
ArrayList<Points> ThetaX = new ArrayList();
//--------------------------------------------
float med, attain;
int max_AH=0, max_AL=0, max_BH=0, max_BL=0, max_GM=0, max_GL=0, max_D=0, max_T=0; // max value cutoff
int min_AH=0, min_AL=0, min_BH=0, min_BL=0, min_GM=0, min_GL=0, min_D=0, min_T=0; // min value cutoff
//--------------------------------------------
void setup() {
  size(600, 400);
  ThinkGearSocket neuroSocket = new ThinkGearSocket(this);
  try {
    neuroSocket.start();
  } 
  catch (ConnectException e) {
  }
}

void draw() {
  background(-1);
  BrainWave();
}


class Points {
  float x, y;
  Points(float x, float y) {
    this.x = x;
    this.y = y;
  }
}
//-------------------------------------------
void poorSignalEvent(int sig) {
  println("SignalEvent "+sig);
}

public void attentionEvent(int attentionLevel) {
  println("Attention Level: " + attentionLevel);
  // attention = attentionLevel;
}


void meditationEvent(int meditationLevel) {
  println("Meditation Level: " + meditationLevel);
  // meditation = meditationLevel;
}

void blinkEvent(int blinkStrength) {

  println("blinkStrength: " + blinkStrength);
}

public void eegEvent(int delta, int theta, int low_alpha, int high_alpha, int low_beta, int high_beta, int low_gamma, int mid_gamma) {
  //--------------------------------------------------
  if (high_alpha > max_AH) {
    max_AH = high_alpha;
  }
  else if (high_alpha < min_AH) {
    min_AH = high_alpha;
  }
  Points HA = new Points(width, high_alpha);
  HighA.add(HA);
  //--------------------------------------------------
  if (low_alpha > max_AL) {
    max_AL = low_alpha;
  }
  else if (low_alpha < min_AL) {
    min_AL = low_alpha;
  }
  Points LA = new Points(width, low_alpha);
  LowA.add(LA);
  //--------------------------------------------------
  if (high_beta > max_BH) {
    max_BH = high_beta;
  }
  else if (high_beta < min_BH) {
    min_BH = high_beta;
  }
  Points HB = new Points(width, high_beta);
  HighB.add(HB);
  //--------------------------------------------------
  if (low_beta > max_BL) {
    max_BL = low_beta;
  }
  else if (low_beta < min_BL) {
    min_BL = low_beta;
  }
  Points LB = new Points(width, low_beta);
  LowB.add(LB);
  //--------------------------------------------------
  if (mid_gamma > max_GM) {
    max_GM = mid_gamma;
  }
  else if (mid_gamma < min_GM) {
    min_GM = mid_gamma;
  }
  Points MG = new Points(width, mid_gamma);
  MidG.add(MG);
  //--------------------------------------------------
  if (low_gamma > max_GL) {
    max_GL = low_gamma;
  }
  else if (low_gamma < min_GL) {
    min_GL = low_gamma;
  }
  Points LG = new Points(width, low_gamma);
  LowG.add(LG);
  //--------------------------------------------------
  if (delta > max_D) {
    max_D = delta;
  }
  else if (delta < min_D) {
    min_D = delta;
  }
  Points del = new Points(width, delta);
  DeltaX.add(del);
  //--------------------------------------------------
  if (theta > max_T) {
    max_T = theta;
  }
  else if (theta < min_T) {
    min_T = theta;
  }
  Points the = new Points(width, theta);
  ThetaX.add(the);
  //--------------------------------------------------
  println(" MAX of all " + max_AH + " " + max_AL + " " +  max_BH + " " +  max_BL + " " +  max_GM + " " +  max_GL + " " +  max_D + " " +  max_T);
  //--------------------------------------------------
}

void rawEvent(int[] raw) {
}

void stop() {
  neuroSocket.stop();
  super.stop();
}

