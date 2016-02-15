import controlP5.*;
import oscP5.*;
import netP5.*;

OscP5 oscP5;
NetAddress myRemoteLocation;

int fontSize = 18;
ControlP5 cp5;
int myColor = color(0, 0, 0);

ColorPicker cpTop, cpBottom;
Textlabel myTextlabelCP;
Button b;

void setup() {
  size(700, 400);
  noStroke();

  /* start oscP5, listening for incoming messages at port 5432 */
  oscP5 = new OscP5(this, 5432);

  /* myRemoteLocation is a NetAddress. a NetAddress takes 2 parameters,
   * an ip address and a port number. myRemoteLocation is used as parameter in
   * oscP5.send() when sending osc packets to another computer, device, 
   * application. usage see below. for testing purposes the listening port
   * and the port of the remote location address are the same, hence you will
   * send messages back to this sketch.
   */
  myRemoteLocation = new NetAddress("127.0.0.1", 7000);

  cp5 = new ControlP5(this);

  PFont pfont = createFont("Arial", 20, true); // use true/false for smooth/no-smooth
  ControlFont font = new ControlFont(pfont, 241);

  // Add Gradient Button
  b = cp5.addButton("AddGradient")
    .setValue(0)
      .setPosition(10, 10)
        .setSize(255, 30)
          ;

  b.setCaptionLabel("Add Gradient")
    .getCaptionLabel()
      .setFont(font)
        .toUpperCase(false)
          .setSize(fontSize)
            ;

  // Add RGB Cube Button
  b = cp5.addButton("AddRGBCube")
    .setValue(0)
      .setPosition(10, 41)
        .setSize(255, 30)
          ;

  b.setCaptionLabel("Add RGB Cube")
    .getCaptionLabel()
      .setFont(font)
        .toUpperCase(false)
          .setSize(fontSize)
            ;

  // Add BBC Effect Button
  b = cp5.addButton("AddBBCEffect")
    .setValue(0)
      .setPosition(10, 72)
        .setSize(255, 30)
          ;

  b.setCaptionLabel("Add Barrel Blur Chroma Effect")
    .getCaptionLabel()
      .setFont(font)
        .toUpperCase(false)
          .setSize(fontSize)
            ;

  // Gradient Top Color controls
  myTextlabelCP = cp5.addTextlabel("topColorLabel")
    .setText("Gradient top color")
      .setPosition(10, 100)
        .setFont(pfont)
          ;

  cpTop = cp5.addColorPicker("topPicker")
    .setPosition(10, 131)
      .setColorValue(color(0, 0, 255, 255))
        ;

  // Gradient Bottom Color controls
  myTextlabelCP = cp5.addTextlabel("botColorLabel")
    .setText("Gradient bottom color")
      .setPosition(10, 194)
        .setFont(pfont)
          ;

  cpBottom = cp5.addColorPicker("botPicker")
    .setPosition(10, 225)
      .setColorValue(color(0, 0, 0, 255))
        ;

  // A slider for the Alpha value of the RGB Cube
  controlP5.Slider sl = cp5.addSlider("RGBCubeAlpha")
    .setCaptionLabel("RGB Cube Alpha")
      .setPosition(10, 367)
        .setSize(255, 20)
          .setRange(0, 255)
            .setValue(255)
              ;

  sl.getCaptionLabel()
    .setFont(font)
      .toUpperCase(false)
        .setSize(fontSize)
          ;

  sl.getValueLabel()
    .setFont(font)
      .toUpperCase(false)
        .setSize(fontSize)
          ;

  // reposition the Label for controller 'slider'
  cp5.getController("RGBCubeAlpha").
    getValueLabel().
    align(ControlP5.RIGHT, ControlP5.CENTER).
    setPaddingX(0);

  cp5.getController("RGBCubeAlpha").
    getCaptionLabel().
    align(ControlP5.LEFT, ControlP5.TOP_OUTSIDE).
    setPaddingX(0);

  // A slider for the Scale value of the RGBCube
  sl = cp5.addSlider("RGBCubeScale")
    .setCaptionLabel("RGB Cube Scale")
      .setPosition(10, 320)
        .setSize(255, 20)
          .setRange(0, 255)
            .setValue(64)
              ;

  sl.getCaptionLabel()
    .setFont(font)
      .toUpperCase(false)
        .setSize(fontSize)
          ;

  sl.getValueLabel()
    .setFont(font)
      .toUpperCase(false)
        .setSize(fontSize)
          ;

  // reposition the Label for controller 'slider'
  cp5.getController("RGBCubeScale").
    getValueLabel().
    align(ControlP5.RIGHT, ControlP5.CENTER).
    setPaddingX(0);

  cp5.getController("RGBCubeScale").
    getCaptionLabel().
    align(ControlP5.LEFT, ControlP5.TOP_OUTSIDE).
    setPaddingX(0);
    
    
    
  // A slider for the NumIter value of the BBC Effect
  controlP5.Slider slbb = cp5.addSlider("BBCEffectNumIter")
    .setCaptionLabel("BBC Effect Iterations")
      .setPosition(276, 367)
        .setSize(255, 20)
          .setRange(0, 255)
            .setValue(12)
              ;

  slbb.getCaptionLabel()
    .setFont(font)
      .toUpperCase(false)
        .setSize(fontSize)
          ;

  slbb.getValueLabel()
    .setFont(font)
      .toUpperCase(false)
        .setSize(fontSize)
          ;

  // reposition the Label for controller 'slider'
  cp5.getController("BBCEffectNumIter").
    getValueLabel().
    align(ControlP5.RIGHT, ControlP5.CENTER).
    setPaddingX(0);

  cp5.getController("BBCEffectNumIter").
    getCaptionLabel().
    align(ControlP5.LEFT, ControlP5.TOP_OUTSIDE).
    setPaddingX(0);
    
    
    
// A slider for the Scale value of the BBCEffectNumIter
  sl = cp5.addSlider("BBCEffectMaxDistort")
    .setCaptionLabel("BBC Effect Max Distortion")
      .setPosition(276, 320)
        .setSize(255, 20)
          .setRange(-30, 30)
            .setValue(-10)
              ;

  sl.getCaptionLabel()
    .setFont(font)
      .toUpperCase(false)
        .setSize(fontSize)
          ;

  sl.getValueLabel()
    .setFont(font)
      .toUpperCase(false)
        .setSize(fontSize)
          ;

  // reposition the Label for controller 'slider'
  cp5.getController("BBCEffectMaxDistort").
    getValueLabel().
    align(ControlP5.RIGHT, ControlP5.CENTER).
    setPaddingX(0);

  cp5.getController("BBCEffectMaxDistort").
    getCaptionLabel().
    align(ControlP5.LEFT, ControlP5.TOP_OUTSIDE).
    setPaddingX(0);    
    
    

  // Remove Gradient Button
  b = cp5.addButton("RemoveGradient")
    .setValue(0)
      .setPosition(276, 10)
        .setSize(255, 30)
          ;

  b.setCaptionLabel("Remove Gradient")
    .getCaptionLabel()
      .setFont(font)
        .toUpperCase(false)
          .setSize(fontSize)
            ;

  // Remove RBGB CubeButton
  b = cp5.addButton("RemoveRGBCube")
    .setValue(0)
      .setPosition(276, 41)
        .setSize(255, 30)
          ;

  b.setCaptionLabel("Remove RGB Cube")
    .getCaptionLabel()
      .setFont(font)
        .toUpperCase(false)
          .setSize(fontSize)
            ;
            
            
            

// Remove BBC Effect Button
  b = cp5.addButton("RemoveBBCEffect")
    .setValue(0)
      .setPosition(276, 72)
        .setSize(255, 30)
          ;

  b.setCaptionLabel("Remove BBC Effect")
    .getCaptionLabel()
      .setFont(font)
        .toUpperCase(false)
          .setSize(fontSize)
            ;

            
            
            
  // Move RGB Cube To Top
  b = cp5.addButton("MoveRGBCubeToTop")
    .setValue(0)
      .setPosition(276, 103)
        .setSize(255, 30)
          ;

  b.setCaptionLabel("Move RGB Cube to Top")
    .getCaptionLabel()
      .setFont(font)
        .toUpperCase(false)
          .setSize(fontSize)
            ;
            
  // Move RGB Cube To Bottom
  b = cp5.addButton("MoveRGBCubeToBottom")
    .setValue(0)
      .setPosition(276, 134)
        .setSize(255, 30)
          ;

  b.setCaptionLabel("Move RGB Cube to Bottom")
    .getCaptionLabel()
      .setFont(font)
        .toUpperCase(false)
          .setSize(fontSize)
            ;
            
  // Query Synth Names
  b = cp5.addButton("QuerySynthNames")
    .setValue(0)
      .setPosition(276, 165)
        .setSize(255, 30)
          ;

  b.setCaptionLabel("Query Synth Names")
    .getCaptionLabel()
      .setFont(font)
        .toUpperCase(false)
          .setSize(fontSize)
            ;
  
  // Query Grad Synth supported Messages
  b = cp5.addButton("QuerySupportedMessages")
    .setValue(0)
      .setPosition(276, 196)
        .setSize(255, 30)
          ;

  b.setCaptionLabel("Query Grad Synth Messages")
    .getCaptionLabel()
      .setFont(font)
        .toUpperCase(false)
          .setSize(fontSize)
            ;
            
            
 // Trigger OSC-Namespace writing
  b = cp5.addButton("WriteOSCNamespacefiles")
    .setValue(0)
      .setPosition(276, 227)
        .setSize(255, 30)
          ;

  b.setCaptionLabel("Write OSC-Namespace files")
    .getCaptionLabel()
      .setFont(font)
        .toUpperCase(false)
          .setSize(fontSize)
            ;
}

void draw() {
  background(0);

  fill(myColor);
  //rect(0,280,width,70);
}

public void controlEvent(ControlEvent theEvent) {
  // when a value change from a ColorPicker is received, extract the ARGB values
  // from the controller's array value
  if (theEvent.isFrom(cpTop)) {
    float r = int(theEvent.getArrayValue(0));
    float g = int(theEvent.getArrayValue(1));
    float b = int(theEvent.getArrayValue(2));

    OscMessage myMessage;

    myMessage = new OscMessage("/Mother/Grad_01/TopRed"); 
    myMessage.add(r/255f);
    oscP5.send(myMessage, myRemoteLocation);

    myMessage = new OscMessage("/Mother/Grad_01/TopGreen");
    myMessage.add(g/255f);
    oscP5.send(myMessage, myRemoteLocation);

    myMessage = new OscMessage("/Mother/Grad_01/TopBlue");
    myMessage.add(b/255f);
    oscP5.send(myMessage, myRemoteLocation);
  }
  else if (theEvent.isFrom(cpBottom)) {
    float r = int(theEvent.getArrayValue(0));
    float g = int(theEvent.getArrayValue(1));
    float b = int(theEvent.getArrayValue(2));

    OscMessage myMessage;

    myMessage = new OscMessage("/Mother/Grad_01/BotRed"); 
    myMessage.add(r/255f);
    oscP5.send(myMessage, myRemoteLocation);

    myMessage = new OscMessage("/Mother/Grad_01/BotGreen");
    myMessage.add(g/255f);
    oscP5.send(myMessage, myRemoteLocation);

    myMessage = new OscMessage("/Mother/Grad_01/BotBlue");
    myMessage.add(b/255f);
    oscP5.send(myMessage, myRemoteLocation);
  }
}

void RGBCubeAlpha(float alpha) {
  OscMessage myMessage;
  myMessage = new OscMessage("/Mother/RGBCube_01/Set_Alpha"); 
  myMessage.add((float)alpha/255.0);
  oscP5.send(myMessage, myRemoteLocation);
}

void RGBCubeScale(float scale) {
  OscMessage myMessage;
  myMessage = new OscMessage("/Mother/RGBCube_01/Scale"); 
  myMessage.add((float)scale/64f);
  oscP5.send(myMessage, myRemoteLocation);
}

void BBCEffectNumIter(float iter) {
  OscMessage myMessage;
  myMessage = new OscMessage("/Mother/BBCE_01/NumIter"); 
  myMessage.add((int)iter);
  oscP5.send(myMessage, myRemoteLocation);
}

void BBCEffectMaxDistort(float dist) {
  OscMessage myMessage;
  myMessage = new OscMessage("/Mother/BBCE_01/MaxDistort"); 
  myMessage.add((float)dist);
  oscP5.send(myMessage, myRemoteLocation);
}

void MoveRGBCubeToTop(int theValue) {
  OscMessage myMessage;
  myMessage = new OscMessage("/Mother/Move_synth"); 
  myMessage.add("RGBCube_01");
  myMessage.add(1);
  oscP5.send(myMessage, myRemoteLocation);
}

void MoveRGBCubeToBottom(int theValue) {
  OscMessage myMessage;
  myMessage = new OscMessage("/Mother/Move_synth"); 
  myMessage.add("RGBCube_01");
  myMessage.add(0);
  oscP5.send(myMessage, myRemoteLocation);
}

void QuerySynthNames(int theValue) {
  OscMessage myMessage;
  myMessage = new OscMessage("/Mother/Get_synth_names"); 
  oscP5.send(myMessage, myRemoteLocation);
}

void QuerySupportedMessages(int theValue) {
  OscMessage myMessage;
  myMessage = new OscMessage("/Mother/Grad_01/Get_Supported_Messages"); 
  oscP5.send(myMessage, myRemoteLocation);
}

void WriteOSCNamespacefiles(int theValue) {
  OscMessage myMessage;
  myMessage = new OscMessage("/Mother/WriteSynths_OSC_Namespace"); 
  oscP5.send(myMessage, myRemoteLocation);
}


// function AddGradient will receive changes from 
// controller with name AddGradient
public void AddGradient(int theValue) {
  OscMessage myMessage = new OscMessage("/Mother/Add_synth");
  myMessage.add("Gradient");
  myMessage.add("Grad_01");
  oscP5.send(myMessage, myRemoteLocation);
}

// function AddRGBCube will receive changes from 
// controller with name AddRGBCube
public void AddRGBCube(int theValue) {
  OscMessage myMessage = new OscMessage("/Mother/Add_synth");
  myMessage.add("RGBCubeExample");
  myMessage.add("RGBCube_01");
  oscP5.send(myMessage, myRemoteLocation);
}

public void AddBBCEffect(int theValue) {
  OscMessage myMessage = new OscMessage("/Mother/Add_synth");
  myMessage.add("BarrelBlurChromaEffect");
  myMessage.add("BBCE_01");
  oscP5.send(myMessage, myRemoteLocation);
}


// function RemoveGradient will receive changes from 
// controller with name RemoveGradient
public void RemoveGradient(int theValue) {
  OscMessage myMessage = new OscMessage("/Mother/Remove_synth");
  myMessage.add("Grad_01");
  oscP5.send(myMessage, myRemoteLocation);
}

// function RemoveRGBCube will receive changes from 
// controller with name RemoveRGBCube
public void RemoveRGBCube(int theValue) {
  OscMessage myMessage = new OscMessage("/Mother/Remove_synth");
  myMessage.add("RGBCube_01");
  oscP5.send(myMessage, myRemoteLocation);
}


// function RemoveRGBCube will receive changes from 
// controller with name RemoveBBCEffect
public void RemoveBBCEffect(int theValue) {
  OscMessage myMessage = new OscMessage("/Mother/Remove_synth");
  myMessage.add("BBCE_01");
  oscP5.send(myMessage, myRemoteLocation);
}

/* incoming osc message are forwarded to the oscEvent method. */
void oscEvent(OscMessage theOscMessage) {
  /* print the address pattern and the typetag of the received OscMessage */
  print("### received an osc message.");
  print(" addrpattern: "+theOscMessage.addrPattern());
  println(" typetag: "+theOscMessage.typetag());
}

