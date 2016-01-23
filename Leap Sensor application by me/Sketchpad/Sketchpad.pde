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

LeapMotion leap;

ConcurrentMap<Integer, Vector> fingerPositions; 

color[] Colors = {
  #F5B70C, #F50C47, #0AC673, #2D5C83, #EBF002
};
float kx, ky;
void setup() {
  size(800, 500);
  leap = new LeapMotion(this); 
  fingerPositions = new ConcurrentHashMap<Integer, Vector>();
}

void draw() {
  background(0);
  for (Map.Entry entry : fingerPositions.entrySet ()) {
    Integer fingerId = (Integer) entry.getKey();
    Vector position = (Vector) entry.getValue();
    fill(-1);
    noStroke();
    ellipse(leap.leapToSketchX(position.getX()), leap.leapToSketchY(position.getY()), 24.0, 24.0);
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



class Emitter {
  PVector loc, acc;
  color c;
  float r;
  Emitter(PVector loc1) {
    loc = loc1;
    acc = new PVector(random(-1, 1), random(-1, 1));
    c = (color) random(#000000);
    r = random(20);
  }

  void display() {
    fill(c);
    noStroke();
    ellipse(loc.x, loc.y, r, r);
  }

  void update() {
    loc.add(acc);
  }
}

