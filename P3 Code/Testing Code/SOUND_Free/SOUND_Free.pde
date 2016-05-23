/* OpenProcessing Tweak of *@*http://www.openprocessing.org/sketch/30729*@* */
/* !do not delete the line above, required for linking your tweak if you re-upload */
int Modo=0;

//Call controlP5 gui
import controlP5.*;
ControlP5 controlP5;
public float Low = 261;
public float High = 523;
public float Octave = 4;

//Call Minim
import ddf.minim.*;
import ddf.minim.signals.*;
Minim minim;
AudioOutput out;
SineWave sine;

//Modified version of PixelArray 1.2.1
PImage img;
int direction = 1;
float signal;
float freq; //Link Variable between PixelArray and Minim
float amp; //Link Variable between PixelArray and Minim

//Paint VariableS
int oldX=0, oldY=0, drawHue=0, drawSat=255;
int ColSpacing=46;
int ColOff=8;

void setup() {
  size (800, 600);
  background(255);
  smooth();
  frameRate(123);
  colorMode (HSB, 255);

  //Inicialize controlP5 gui
  controlP5 = new ControlP5(this);

  controlP5.addSlider("Octave", 0, 6, width-33, 10, 10, 570);
  Slider s1 = (Slider)controlP5.controller("Octave");
  s1.setNumberOfTickMarks(7);


  //Inicialize Minim
  minim = new Minim(this);
  // get a line out from Minim, default bufferSize is 1024, default sample rate is 44100, bit depth is 16
  out = minim.getLineOut(Minim.STEREO);
  // create a sine wave Oscillator, set to 440 Hz, at 0.5 amplitude, sample rate from line out
  sine = new SineWave(440, 0.0, out.sampleRate());
  // set the portamento speed on the oscillator to 200 milliseconds
  sine.portamento(10);
  // add the oscillator to the line out
  out.addSignal(sine);
}

void draw() {
  //If modo = 0 => Run Paint ()

  if (Modo==0) {

    int n=0;
    int colors[]= {
      254, 226, 199, 174, 150, 127, 106, 85, 66, 48, 31, 15, 0
    };

    if (mousePressed) {
      //Draw line of seted colour
      strokeWeight(30);
      stroke(drawHue, drawSat, 255);
      line(mouseX, mouseY, oldX, oldY);


      //check if the BLANK button is pressed
      if (inside (10, 10, 40, 40)) {
        background(0, 0, 255);
      }

      //check if LISTEN button is pressed
      if (inside(10, height-40, 70, height-10)) {
        //     save("mypicture.png");
        Modo = 3;
      }   

      //Eraser button
      if (inside(width-150, 560, width-120, 590)) {
        if (mouseButton==LEFT) {
          drawSat = 0;
        }
        else {
          background(0, 0, 255);
        }
      } 


      //color selector
      for (n=0;n<13;n++) {

        if (inside(width-80, ColOff+n*ColSpacing, width-50, 40+n*ColSpacing)) {

          if (mouseButton==LEFT) {
            drawHue = colors[n];
            drawSat=255;
          }
          else {
            background(colors[n], 255, 255);
          }
        }
      }
    }

    oldX=mouseX;
    oldY=mouseY;

    //Erase PAINT TEXT
    fill(0, 0, 255);
    noStroke();
    rect(10, height-40, 190, 30);

    //draw BLANK button
    stroke(0);
    strokeWeight(1);
    noFill();
    rect(10, 10, 30, 30);
    line(10, 10, 40, 40);
    line(40, 10, 10, 40);
    fill(0, 0, 0);
    text("BLANK", 5, 60);

    //draw LISTEN button
    fill(0, 0, 200);
    rect(10, height-40, 60, 30);
    noFill();
    rect(15, height-35, 50, 20);
    fill(0, 0, 0);
    text("LISTEN", 20, height-20);

    //Eraser button
    fill(0, 0, 255);
    rect(width-150, height-40, 30, 30);

    //Back for Hz slider
    noStroke();
    fill(0, 0, 70);
    rect(width-45, 0, 45, height);
    //  stroke(0);
    //  fill(0,0,255);
    //  text("Sound",width-40,288);
    //  text("freq",width-40,300);
    //  text("range",width-40,312);
    //  text("in Hz",width-40,324);

    //Legends
    fill(0, 0, 0);
    text("C2", width-100, 30);
    text("B", width-100, ColOff+20+1*ColSpacing);
    text("A^", width-100, ColOff+20+2*ColSpacing);
    text("A", width-100, ColOff+20+3*ColSpacing);
    text("G^", width-100, ColOff+20+4*ColSpacing);
    text("G", width-100, ColOff+20+5*ColSpacing);
    text("F^", width-100, ColOff+20+6*ColSpacing);
    text("F", width-100, ColOff+20+7*ColSpacing);
    text("E", width-100, ColOff+20+8*ColSpacing);
    text("D^", width-100, ColOff+20+9*ColSpacing);
    text("D", width-100, ColOff+20+10*ColSpacing);
    text("C^", width-100, ColOff+20+11*ColSpacing);
    text("C", width-100, ColOff+20+12*ColSpacing);
    text("Erase ", width-190, height-20);


    //draw color buttons
    for (n=0;n<13;n++)
    {
      fill(colors[n], 255, 255);
      rect(width-80, ColOff+n*ColSpacing, 30, 30);
    }
  } 
  else {
    if (Modo==3) {
      //If modo = 3 => Run SoundColour ()

      //Load image
      //  img = loadImage("mypicture.png");
      img = get();
      //Mode change to 2 makes app to start sounding now that image is loaded
      Modo = 2;
    } 
    else {
      if (Modo==2) {
        //Make signal point at mouse position inside the pixels array
        int mx = constrain(mouseX, 0, img.width-1);
        int my = constrain(mouseY, 0, img.height-1);
        signal = my*img.width + mx;
        int sx = int(signal) % img.width;
        int sy = int(signal) / img.width;
        set(0, 0, img);  // fast way to draw an image


        fill(150, 255, 255);
        rect(10, height-40, 190, 30);
        fill(0, 0, 0);
        text("STRIKE KEY TO PAINT AGAIN", 23, height-20);

        //Draw Pointer on the image for current pixel
        stroke(0, 0, 0);
        point(sx, sy);
        noFill();
        rect(sx - 5, sy - 5, 10, 10);
        color c = img.get(sx, sy);


        //Map color as sound freq for Minim.
        float freq = map(hue(c), 0, 255, Low, High);

        //Set freq for Minim Sinth
        sine.setFreq(freq);

        //Link Amp to Saturation
        float amp = map(saturation(c), 0, 255, 0.0, 0.3);
        sine.setAmp(amp);

        //Print variables to see changes    
        print ("\n Brillo= " + brightness (c) + "   hue= " + hue (c) + "   Freq= " + freq + "   Amp= " + amp);

        //Pressing any key brings back paint mode
        if (keyPressed == true) {
          sine.setAmp(0.0);
          Modo = 0;
        }
      }
    }
  }
}

