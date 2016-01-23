import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Tool;
import com.leapmotion.leap.Vector;
import com.leapmotion.leap.processing.LeapMotion;

import ddf.minim.*;

Minim minim;
LeapMotion leap;

ConcurrentMap<Integer, Vector> fingerPositions; 

int N = 5, h=100, r=70;
AudioPlayer[] player=new AudioPlayer[N];
color[] Colors = {
  #F5B70C, #F50C47, #0AC673, #2D5C83, #EBF002
};

void setup() {
  size(800, 500);
  leap = new LeapMotion(this); 
  fingerPositions = new ConcurrentHashMap<Integer, Vector>();

  minim = new Minim(this);
  for (int i=0; i<N; i++) {
    String str = i + ".mp3";
    player[i] = minim.loadFile(str);
  }
}

void draw() {
  background(-1);

  for (int x =0; x<N; x++) {
    noStroke();
    fill(Colors[x]);
    ellipse(x*width/Colors.length+50, height-h, r, r);
  }


  for (Map.Entry entry : fingerPositions.entrySet ()) {
    Integer fingerId = (Integer) entry.getKey();
    Vector position = (Vector) entry.getValue(); 
    float xp = leap.leapToSketchX(position.getX());
    float yp = leap.leapToSketchY(position.getY());
    fill(0);
    for (int x =0; x<N; x++) {
      if (dist( xp, yp, x*width/Colors.length+50, height-h)<r/2) {
        if (!player[int(x)].isPlaying()) { 
          player[int(x)].rewind();
          player[int(x)].play();
          fill(0, 50);
        }
      }
    }
    noStroke();
    ellipse(xp, yp, 24, 24);
  }
}


void onFrame(final Controller controller) { 
  Frame frame = controller.frame();
  fingerPositions.clear();
  for (Finger finger : frame.fingers ()) {
    int fingerId = finger.id();
    color c = color(random(0, 255), random(0, 255), random(0, 255));
    fingerPositions.put(fingerId, finger.tipPosition());
  }
}

