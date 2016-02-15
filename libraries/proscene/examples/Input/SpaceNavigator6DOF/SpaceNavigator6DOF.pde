/**
 * Space Navigator
 * by Jean Pierre Charalambos.
 *
 * This demo shows how to control your scene Eye and iFrames using a Space Navigator
 * (3D mouse), with 6 degrees-of-freedom.
 *
 * We implement the (non-conventional) user interaction mechanism as a HIDAgent
 * which provides up to 6DOFs. The Agent gathers Space Navigator input data and reduces
 * it as "bogus" 6DOF event from which the following the TRANSLATE_ROTATE proscene
 * built-in actions is bound.
 * 
 * Press 'h' to display the key shortcuts, mouse and SpaceNavigator bindings in the console.
 */

import remixlab.proscene.*;
import remixlab.bias.core.*;
import remixlab.bias.event.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.agent.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;

import procontroll.*;
import net.java.games.input.*;

PImage bg;
PImage texmap;

int sDetail = 35;  // Sphere detail setting
float rotationX = 0;
float rotationY = 0;
float velocityX = 0;
float velocityY = 0;
float globeRadius = 400;
float pushBack = 0;

float[] cx, cz, sphereX, sphereY, sphereZ;
float sinLUT[];
float cosLUT[];
float SINCOS_PRECISION = 0.5;
int SINCOS_LENGTH = int(360.0 / SINCOS_PRECISION);

Scene scene;
InteractiveFrame iFrame;
HIDAgent hidAgent;

ControllIO controll;
ControllDevice device; // my SpaceNavigator
ControllSlider sliderXpos; // Positions
ControllSlider sliderYpos;
ControllSlider sliderZpos;
ControllSlider sliderXrot; // Rotations
ControllSlider sliderYrot;
ControllSlider sliderZrot;
ControllButton button1; // Buttons
ControllButton button2;

void setup() {
  size(640, 360, P3D);
  openSpaceNavigator();
  texmap = loadImage("world32k.jpg");    
  initializeSphere(sDetail);
  scene = new Scene(this);
  scene.setGridVisualHint(false);
  scene.setAxesVisualHint(false);  
  scene.setRadius(260);
  scene.showAll();
  
  iFrame = new InteractiveFrame(scene);
  iFrame.translate(new Vec(180, 180, 0));

  hidAgent = new HIDAgent(scene, "SpaceNavigator") {
    @Override
    public DOF6Event feed() {
      return new DOF6Event(sliderXpos.getValue(), sliderYpos.getValue(), sliderZpos.getValue(), 
                           sliderXrot.getValue(), sliderYrot.getValue(), sliderZrot.getValue(), 0, 0);
    }
  };
  
  hidAgent.addInPool(iFrame);
  //declare some sensitivities for the space navigator device
  hidAgent.setSensitivities(0.01, 0.01, -0.01, 0.0001, 0.0001, 0.0001);

  smooth();
}

void draw() {    
  background(0);
  // the hidAgent sensitivities should not vary when its input grabber is the iFrame:
  if( hidAgent.inputGrabber() == iFrame )
    hidAgent.setSensitivities(0.01, 0.01, -0.01, 0.0001, 0.0001, 0.0001);
  else if(hidAgent.eyeProfile().isActionBound(DOF6Action.HINGE))
    hidAgent.setSensitivities(0.0001, 0.0001, -0.01, 0.0001, 0.0001, 0.0001);      
  renderGlobe();
  renderIFrame();
}

void keyPressed() {
  if (key == 'x') scene.flip();
  if(key == ' ')
    if( hidAgent.eyeProfile().isActionBound(DOF6Action.HINGE) ) {
      hidAgent.eyeProfile().setBinding(DOF6Action.TRANSLATE_XYZ_ROTATE_XYZ);
      hidAgent.setSensitivities(0.01, 0.01, -0.01, 0.0001, 0.0001, 0.0001);
      scene.eye().lookAt(scene.center());
      scene.showAll();
    }
    else {
      hidAgent.eyeProfile().setBinding(DOF6Action.HINGE);
      hidAgent.setSensitivities(0.0001, 0.0001, -0.01, 0.0001, 0.0001, 0.0001); 
      Vec t = new Vec(0,0,0.7*globeRadius);
      float a = TWO_PI - 2;      
      Vec tr = scene.camera().frame().rotation().rotate(t);
      scene.camera().setPosition(t);
      //For HINGE to work flawlessly we need to line up the eye up vector along the anchor and
      //the camera position:
      scene.camera().setUpVector(Vec.subtract(scene.camera().position(), scene.anchor()));
      //The rest is just to make the scene appear in front of us. We could have just used
      //the space navigator itself to make that happen too.
      Quat q = new Quat();
      q.fromEulerAngles(a,0,0);
      scene.camera().frame().rotate(q);
    }
  if ( key == 'i') {
    scene.motionAgent().setDefaultGrabber(scene.motionAgent().defaultGrabber() == iFrame ? scene.eye().frame() : iFrame);
    hidAgent.setDefaultGrabber(hidAgent.defaultGrabber() == iFrame ? scene.eye().frame() : iFrame);
  }
}

void renderGlobe() {  
  //lights();
  fill(200);
  noStroke();
  textureMode(IMAGE);  
  texturedSphere(globeRadius, texmap);
}

void renderIFrame() {
  // Save the current model view matrix
  pushMatrix();
  // Multiply matrix to get in the frame coordinate system.
  // applyMatrix(scene.interactiveFrame().matrix()) is handy but inefficient 
  iFrame.applyTransformation(); //optimum
  // Draw an axis using the Scene static function
  scene.drawAxes(20);
  // Draw a second box
  if (iFrame.grabsInput(scene.motionAgent()) || iFrame.grabsInput(hidAgent) ) {
    fill(255, 0, 0);
    box(12, 17, 22);
  }
  else {
    fill(0, 0, 255);
    box(10, 15, 20);
  }  
  popMatrix();
}

void initializeSphere(int res)
{
  sinLUT = new float[SINCOS_LENGTH];
  cosLUT = new float[SINCOS_LENGTH];

  for (int i = 0; i < SINCOS_LENGTH; i++) {
    sinLUT[i] = (float) Math.sin(i * DEG_TO_RAD * SINCOS_PRECISION);
    cosLUT[i] = (float) Math.cos(i * DEG_TO_RAD * SINCOS_PRECISION);
  }

  float delta = (float)SINCOS_LENGTH/res;
  float[] cx = new float[res];
  float[] cz = new float[res];

  // Calc unit circle in XZ plane
  for (int i = 0; i < res; i++) {
    cx[i] = -cosLUT[(int) (i*delta) % SINCOS_LENGTH];
    cz[i] = sinLUT[(int) (i*delta) % SINCOS_LENGTH];
  }

  // Computing vertexlist vertexlist starts at south pole
  int vertCount = res * (res-1) + 2;
  int currVert = 0;

  // Re-init arrays to store vertices
  sphereX = new float[vertCount];
  sphereY = new float[vertCount];
  sphereZ = new float[vertCount];
  float angle_step = (SINCOS_LENGTH*0.5f)/res;
  float angle = angle_step;

  // Step along Y axis
  for (int i = 1; i < res; i++) {
    float curradius = sinLUT[(int) angle % SINCOS_LENGTH];
    float currY = -cosLUT[(int) angle % SINCOS_LENGTH];
    for (int j = 0; j < res; j++) {
      sphereX[currVert] = cx[j] * curradius;
      sphereY[currVert] = currY;
      sphereZ[currVert++] = cz[j] * curradius;
    }
    angle += angle_step;
  }
  sDetail = res;
}

// Generic routine to draw textured sphere
void texturedSphere(float r, PImage t) {
  int v1, v11, v2;
  r = (r + 240 ) * 0.33;
  beginShape(TRIANGLE_STRIP);
  texture(t);
  float iu=(float)(t.width-1)/(sDetail);
  float iv=(float)(t.height-1)/(sDetail);
  float u=0, v=iv;
  for (int i = 0; i < sDetail; i++) {
    vertex(0, -r, 0, u, 0);
    vertex(sphereX[i]*r, sphereY[i]*r, sphereZ[i]*r, u, v);
    u+=iu;
  }
  vertex(0, -r, 0, u, 0);
  vertex(sphereX[0]*r, sphereY[0]*r, sphereZ[0]*r, u, v);
  endShape();   

  // Middle rings
  int voff = 0;
  for (int i = 2; i < sDetail; i++) {
    v1=v11=voff;
    voff += sDetail;
    v2=voff;
    u=0;
    beginShape(TRIANGLE_STRIP);
    texture(t);
    for (int j = 0; j < sDetail; j++) {
      vertex(sphereX[v1]*r, sphereY[v1]*r, sphereZ[v1++]*r, u, v);
      vertex(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2++]*r, u, v+iv);
      u+=iu;
    }

    // Close each ring
    v1=v11;
    v2=voff;
    vertex(sphereX[v1]*r, sphereY[v1]*r, sphereZ[v1]*r, u, v);
    vertex(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2]*r, u, v+iv);
    endShape();
    v+=iv;
  }
  u=0;

  // Add the northern cap
  beginShape(TRIANGLE_STRIP);
  texture(t);
  for (int i = 0; i < sDetail; i++) {
    v2 = voff + i;
    vertex(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2]*r, u, v);
    vertex(0, r, 0, u, v+iv);    
    u+=iu;
  }
  vertex(sphereX[voff]*r, sphereY[voff]*r, sphereZ[voff]*r, u, v);
  endShape();
}

void openSpaceNavigator() {
  println(System.getProperty("os.name"));
  controll = ControllIO.getInstance(this);  
  String os = System.getProperty("os.name").toLowerCase();  
  if (os.indexOf( "nix") >=0 || os.indexOf( "nux") >=0)
    device = controll.getDevice("3Dconnexion SpaceNavigator");// magic name for linux    
  else
    device = controll.getDevice("SpaceNavigator");//magic name, for windows
  device.setTolerance(5.00f);
  sliderXpos = device.getSlider(2);
  sliderYpos = device.getSlider(1);
  sliderZpos = device.getSlider(0);
  sliderXrot = device.getSlider(5);
  sliderYrot = device.getSlider(4);
  sliderZrot = device.getSlider(3);
  button1 = device.getButton(0);
  button2 = device.getButton(1);
}