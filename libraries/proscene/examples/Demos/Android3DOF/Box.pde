import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;

public class Box {
  Scene scene;
  public InteractiveFrame iFrame;
  float w, h, d;
  int c;

  public Box(Scene scn, InteractiveFrame iF) {
    scene = scn;
    iFrame = iF;
    iFrame.setGrabsInputThreshold(25);
    setSize();
    setColor();
  }

  public Box(Scene scn) {
    scene = scn;
    iFrame = new InteractiveFrame(scn);
    iFrame.setGrabsInputThreshold(25);
    setSize();
    setColor();    
    setPosition();
  }

  public void draw() {
    draw(false);
  }

  public void draw(boolean drawAxis) {
    pushMatrix();
    iFrame.applyWorldTransformation();

    noStroke();
    if (scene.grabsAnyAgentInput(iFrame))
      fill(255, 0, 0);
    else
      fill(getColor());
    //Draw a box    
    box(w, h, d);

    popMatrix();
  }

  public void setSize() {
    w = random(10, 40);
    h = random(10, 40);
    d = random(10, 40);
  }

  public void setSize(float myW, float myH, float myD) {
    w=myW; 
    h=myH; 
    d=myD;
  }  

  public int getColor() {
    return c;
  }

  public void setColor() {
    c = color(random(0, 255), random(0, 255), random(0, 255));
  }

  public void setColor(int myC) {
    c = myC;
  }

  public Vec getPosition() {
    return iFrame.position();
  }  

  public void setPosition() {
    float low = -100;
    float high = 100;
    iFrame.setPosition(new Vec(random(low, high), random(low, high), random(low, high)));
  }

  public void setPosition(Vec pos) {
    iFrame.setPosition(pos);
  }

  public Quat getOrientation() {
    return (Quat)iFrame.orientation();
  }

  public void setOrientation(Vec v) {
    Vec to = Vec.subtract(v, iFrame.position()); 
    iFrame.setOrientation(new Quat(new Vec(0, 1, 0), to));
  }
}
