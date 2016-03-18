/**
 * Custom Agents.
 * by Jean Pierre Charalambos.
 * 
 * This demo shows how to implement a custom bias mouse agent with its own
 * set of actions.
 * 
 * Press ' ' (the spacebar) to toggle the mouse agent (proscene or custom).
 * Press 'h' to display the key shortcuts and mouse bindings in the console.
 */

import remixlab.bias.core.*;
import remixlab.bias.agent.*;
import remixlab.bias.agent.profile.*;
import remixlab.bias.event.*;
import remixlab.proscene.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.core.*;

public class MouseAgent extends ActionMotionAgent<MotionProfile<MotionAction>, ClickProfile<ClickAction>> {
  DOF2Event event, prevEvent;
  public MouseAgent(InputHandler scn, String n) {
    super(new MotionProfile<MotionAction>(), 
          new ClickProfile<ClickAction>(), scn, n);
    //default bindings
    clickProfile().setBinding(LEFT, 1, ClickAction.CHANGE_COLOR);
    clickProfile().setBinding(DOF2Event.META, RIGHT, 1, ClickAction.CHANGE_STROKE_WEIGHT);
    clickProfile().setBinding((DOF2Event.META | DOF2Event.SHIFT), RIGHT, 1, ClickAction.CHANGE_STROKE_WEIGHT);
    profile().setBinding(LEFT, MotionAction.CHANGE_POSITION);
    profile().setBinding(DOF2Event.SHIFT, LEFT, MotionAction.CHANGE_SHAPE);
    profile().setBinding(DOF2Event.META, RIGHT, MotionAction.CHANGE_SHAPE);
  }

  public void mouseEvent(processing.event.MouseEvent e) {      
    if ( e.getAction() == processing.event.MouseEvent.MOVE ) {
      event = new DOF2Event(prevEvent, e.getX(), e.getY(),e.getModifiers(), e.getButton());
      updateTrackedGrabber(event);
      prevEvent = event.get();
    }
    if ( e.getAction() == processing.event.MouseEvent.DRAG ) {
      event = new DOF2Event(prevEvent, e.getX(), e.getY(), e.getModifiers(), e.getButton());
      handle(event);
      prevEvent = event.get();
    }
    if ( e.getAction() == processing.event.MouseEvent.CLICK ) {
      handle(new ClickEvent(e.getX(), e.getY(), e.getModifiers(), e.getButton(), e.getCount()));
    }
  }
}

public class Ellipse extends GrabberObject {
  public float radiusX, radiusY;
  public PVector center;
  public color colour, contourColour;
  public int sWeight;

  public Ellipse(Agent agent) {
    agent.addInPool(this);
    setColor();
    setPosition();
    sWeight = 4;
    contourColour = color(0, 0, 0);
  }

  public Ellipse(Agent agent, PVector c, float r) {
    agent.addInPool(this);
    radiusX = r;
    radiusY = r;
    center = c;    
    setColor();
    sWeight = 4;
  }

  public void setColor() {
    setColor(color(random(0, 255), random(0, 255), random(0, 255), random(100, 200)));
  }

  public void setColor(color myC) {
    colour = myC;
  }

  public void setPosition(float x, float y) {
    setPositionAndRadii(new PVector(x, y), radiusX, radiusY);
  }

  public void setPositionAndRadii(PVector p, float rx, float ry) {
    center = p;
    radiusX = rx;
    radiusY = ry;
  }

  public void setPosition() {
    float maxRadius = 50;
    float low = maxRadius;
    float highX = w - maxRadius;
    float highY = h - maxRadius;
    float r = random(20, maxRadius);
    setPositionAndRadii(new PVector(random(low, highX), random(low, highY)), r, r);
  }

  public void draw() {
    draw(colour);
  }

  public void draw(int c) {
    pushStyle();
    stroke(contourColour);
    strokeWeight(sWeight);
    fill(c);
    ellipse(center.x, center.y, 2*radiusX, 2*radiusY);
    popStyle();
  }

  @Override
  public boolean checkIfGrabsInput(BogusEvent event) {
    if (event instanceof DOF2Event) {
      float x = ((DOF2Event)event).x();
      float y = ((DOF2Event)event).y();
      return(pow((x - center.x), 2)/pow(radiusX, 2) + pow((y - center.y), 2)/pow(radiusY, 2) <= 1);
    }      
    return false;
  }

  @Override
  public void performInteraction(BogusEvent event) {
    if (((BogusEvent)event).action() != null) {
      switch ((GlobalAction) ((BogusEvent)event).action().referenceAction()) {
        case CHANGE_COLOR:
        contourColour = color(random(100, 255), random(100, 255), random(100, 255));
        break;
      case CHANGE_STROKE_WEIGHT:
        if (event.isShiftDown()) {					
          if (sWeight > 1)
            sWeight--;
        }
        else			
          sWeight++;		
        break;
      case CHANGE_POSITION:
        setPosition( ((DOF2Event)event).x(), ((DOF2Event)event).y() );
        break;
        case CHANGE_SHAPE:
        radiusX += ((DOF2Event)event).dx();
        radiusY += ((DOF2Event)event).dy();
        break;
      }
    }
  }
}

int w = 600;
int h = 600;
MouseAgent agent;
Ellipse [] ellipses;
boolean drawSelectionHints = false;
Scene scene;
PFont font;

void setup() {
  size(w, h);
  scene = new Scene(this);
  scene.setAxesVisualHint(false);
  scene.setGridVisualHint(false);
  scene.setRadius(min(w,h)/2);
  scene.setCenter(new Vec(w/2,h/2));
  scene.showAll();
  agent = new MouseAgent(scene.inputHandler(), "my_mouse");
  ellipses = new Ellipse[10];
  for (int i = 0; i < ellipses.length; i++)
    ellipses[i] = new Ellipse(agent);
  scene.inputHandler().unregisterAgent(agent);
  font = loadFont("FreeSans-16.vlw");
  textFont(font);
}

void draw() {
  background(120);
  for (int i = 0; i < ellipses.length; i++) {
    if ( ellipses[i].grabsInput(agent) )
      ellipses[i].draw(color(255, 0, 0));
    else
      ellipses[i].draw();
  }
  scene.beginScreenDrawing();
  if(scene.isMotionAgentEnabled()) {
    fill(255,0,0);
    text("Proscene's default mouse agent can handle your eye, but not your custom actions", 5, 17);
  }
  else {
    fill(0,255,0);
    text("Your agent can handle your custom actions, but not your aye", 5, 17);
    text("Press 'v' to toggle the display of the circle positions displacement due to the eye", 5, 37);
  }
  fill(0,0,255);
  text("Press the spacebar to change the mouse agent", 5, 57);
  scene.endScreenDrawing();
  if(drawSelectionHints && !scene.isMotionAgentEnabled()) drawSelectionHints();
}

void keyPressed() {
  if(key == ' ') {
    if(scene.isMotionAgentEnabled()) {
      scene.disableMotionAgent();
      scene.inputHandler().registerAgent(agent);
      registerMethod("mouseEvent", agent);
    } else {
      scene.enableMotionAgent();
      scene.inputHandler().unregisterAgent(agent);
      unregisterMethod("mouseEvent", agent);
    }
  }
  if(key=='v')
    drawSelectionHints = !drawSelectionHints;
}

void drawSelectionHints() {
  scene.beginScreenDrawing();
  for (int i = 0; i < ellipses.length; i++) {
    color c = ellipses[i].colour;
    ellipses[i].draw(color(red(c), green(c), blue(c), 100));
  }
  scene.endScreenDrawing();
  pushMatrix();
  translate(w/2,h/2);
  scene.drawAxes();
  popMatrix();
}