
import processing.serial.*;
import pt.citar.diablu.processing.mindset.*;

MindSet mindSet;


SampleWidget attentionWidget;
SampleWidget meditationWidget;
SampleWidget signalWidget;

SampleWidget deltaWidget;
SampleWidget thetaWidget;
SampleWidget lowAlphaWidget;
SampleWidget highAlphaWidget;
SampleWidget lowBetaWidget;
SampleWidget highBetaWidget;
SampleWidget lowGammaWidget;
SampleWidget midGammaWidget;

void setup() {
  size(800, 600);

  mindSet = new MindSet(this, "/dev/cu.MindWaveMobile-DevA");

  attentionWidget = new SampleWidget(100, false, 100);
  meditationWidget = new SampleWidget(100, false, 100);
  signalWidget = new SampleWidget(50, false, 200);


  deltaWidget = new SampleWidget(200, true, 0);
  thetaWidget = new SampleWidget(200, true, 0);
  lowAlphaWidget = new SampleWidget(200, true, 0);
  highAlphaWidget = new SampleWidget(200, true, 0);
  lowBetaWidget = new SampleWidget(200, true, 0);
  highBetaWidget = new SampleWidget(200, true, 0);
  lowGammaWidget = new SampleWidget(200, true, 0);
  midGammaWidget = new SampleWidget(200, true, 0);
}


void draw() {
  background(0);

  //simulate();

  text("Attention Level", 10, 10);
  attentionWidget.draw(10, 10+20, 200, 150);

  text("Meditation Level", 10, 200);
  meditationWidget.draw(10, 200+20, 200, 150);

  text("Signal quality", 10, height-10-20-100);
  signalWidget.draw(10, height-100-10, 200, 100);


  int h = height/10;
  text("Delta", width/2-80, 10+h/2);
  deltaWidget.draw(width/2, 10, width/2, h);

  text("Theta", width/2-80, 10+(h+10) + h/2);
  thetaWidget.draw(width/2, 10+(h+10), width/2, height/10);

  text("Low alpha", width/2-80, 10+(h+10)*2 + h/2);
  lowAlphaWidget.draw(width/2, 10+(h+10)*2, width/2, height/10);

  text("High alpha", width/2-80, 10+(h+10)*3 + h/2);
  highAlphaWidget.draw(width/2, 10+(h+10)*3, width/2, height/10);

  text("Low beta", width/2-80, 10+(h+10)*4 + h/2);
  lowBetaWidget.draw(width/2, 10+(h+10)*4, width/2, height/10);

  text("High beta", width/2-80, 10+(h+10)*5 + h/2);
  highBetaWidget.draw(width/2, 10+(h+10)*5, width/2, height/10);  

  text("Low gamma", width/2-80, 10+(h+10)*6 + h/2);
  lowGammaWidget.draw(width/2, 10+(h+10)*6, width/2, height/10);    

  text("Mid gamma", width/2-80, 10+(h+10)*7 + h/2);
  midGammaWidget.draw(width/2, 10+(h+10)*7, width/2, height/10);    

  //  
  //  fill(255, 0, 0, 100);
  //  stroke(255, 100);
  //  rect(width/2-50, height-attention*height/100.0, 100, attention*height/100.0);
  //
  //  //signal strength
  //  stroke(255);
  //  line(0, 50, 50, 50); 
  //  fill(0, 255, 0);
  //  rect(12, 50, 25, -(50-strength*50/200.0));
}

void simulate() {
  poorSignalEvent(int(random(200)));
  attentionEvent(int(random(100)));
  meditationEvent(int(random(100)));
  eegEvent(int(random(20000)), int(random(20000)), int(random(20000)), 
  int(random(20000)), int(random(20000)), int(random(20000)), 
  int(random(20000)), int(random(20000)) );
}


void exit() {
  println("Exiting");
  mindSet.quit();
  super.exit();
}


public void poorSignalEvent(int sig) {
  println(sig);
  signalWidget.add(200-sig);
}

public void attentionEvent(int attentionLevel) {
  attentionWidget.add(attentionLevel);
}


public void meditationEvent(int meditationLevel) {
  meditationWidget.add(meditationLevel);
}

public void eegEvent(int delta, int theta, int low_alpha, 
int high_alpha, int low_beta, int high_beta, int low_gamma, int mid_gamma) {
  deltaWidget.add(delta);
  thetaWidget.add(theta);
  lowAlphaWidget.add(low_alpha);
  highAlphaWidget.add(high_alpha);
  lowBetaWidget.add(low_beta);
  highBetaWidget.add(high_beta);
  lowGammaWidget.add(low_gamma);
  midGammaWidget.add(mid_gamma);
} 

