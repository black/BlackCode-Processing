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

ArrayList<Emitter> peep = new ArrayList();
ConcurrentMap<Integer, Vector> fingerPositions; 

LeapMotion leap;

void setup() {
  size(800, 500); 
  leap = new LeapMotion(this); 
  fingerPositions = new ConcurrentHashMap<Integer, Vector>();
}

void draw() {
  background(-1);
  for (Map.Entry entry : fingerPositions.entrySet ()) {
    Integer fingerId = (Integer) entry.getKey();
    Vector position = (Vector) entry.getValue(); 
    float xp = leap.leapToSketchX(position.getX());
    float yp = leap.leapToSketchY(position.getY());
    fill(0);
    noStroke();
    ellipse(xp, yp, 24, 24);
    Emitter E = new Emitter(new PVector(xp, yp));
    peep.add(E);
  }

  for (int i=0; i<peep.size (); i++) {
    Emitter E = peep.get(i);
    E.display();
  }
  for (int i=0; i<peep.size (); i++) {
    Emitter E = peep.get(i); 
    E.update();
    if (E.loc.x<0 || E.loc.x>width || E.loc.y<0 || E.loc.y>height) peep.remove(i);
  }
}


class Emitter {
  PVector loc, acc;
  color c;
  float r;
  Emitter(PVector loc1) {
    loc = loc1;
    acc = new PVector(random(-1, 1), random(-1, 1));
    colorMode(HSB);
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


void onFrame(final Controller controller) { 
  Frame frame = controller.frame();
  fingerPositions.clear();
  for (Finger finger : frame.fingers ()) {
    int fingerId = finger.id();
    color c = color(random(0, 255), random(0, 255), random(0, 255));
    fingerPositions.put(fingerId, finger.tipPosition());
  }
}

