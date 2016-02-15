// Eye for an Eye by Aaron Steed 2006.

// A closed automata ecology of necrophages.

//import processing.pdf.*;

ParticleSystem ether;
PVector plankton, amoeba;
Soup soup;
float backgroundRed, backgroundGreen, backgroundBlue, backgroundRedTemp, backgroundGreenTemp, backgroundBlueTemp;
int [] backgroundTable = {#B788F8, #F888E4, #F88890, #F8C788, #E7F888, #A2F888, #88F8C5, #88DFF8, #88A5F8};
int backgroundChoice;
int wide, high;

void setup(){
  size(400, 350);
  frameRate(24);
  wide = width;
  high = height;
  ether = new ParticleSystem(0.0, 0.01);
  amoeba = new PVector();
  soup = new Soup();
  plankton = new PVector();
  backgroundChoice = (int)random(backgroundTable.length);
  backgroundRed = backgroundRedTemp = red(backgroundTable[backgroundChoice]);
  backgroundGreen = backgroundGreenTemp = green(backgroundTable[backgroundChoice]);
  backgroundBlue = backgroundBlueTemp = blue(backgroundTable[backgroundChoice]);
  for(int i = 0; i < 5; i++){
    amoeba.add(new Amoeba(random(-wide * 0.5, wide * 0.5), random(-high * 0.5, high * 0.5), soup.makeChromosome()));
  }
  for(int i = 0; i < 25; i++){
    plankton.add(new Plankton());
  }
  ellipseMode(CENTER);
  smooth();
}

void draw(){
  scale(1.0);
  drawRoutines();
  ether.tick();
}

void mousePressed(){
  //beginRecord(PDF, "amoeba.pdf");
  drawRoutines();
  //endRecord();
}

void drawRoutines(){
  fadeBackground();
  pushMatrix();
  translate(wide * 0.5, high * 0.5);
  for(int i = 0; i < amoeba.size(); i++){
    Amoeba temp = (Amoeba)amoeba.get(i);
    temp.draw();
  }
  for(int i = 0; i < plankton.size(); i++){
    Plankton temp = (Plankton)plankton.get(i);
    temp.draw();
  }
  popMatrix();
}

void setBackground(){
  backgroundChoice = (int)random(backgroundTable.length);
  backgroundRed = red(backgroundTable[backgroundChoice]);
  backgroundGreen = green(backgroundTable[backgroundChoice]);
  backgroundBlue = blue(backgroundTable[backgroundChoice]);
}

void fadeBackground(){
  float redDelta = abs(backgroundRed - backgroundRedTemp);
  float greenDelta = abs(backgroundGreen - backgroundGreenTemp);
  float blueDelta = abs(backgroundBlue - backgroundBlueTemp);
  if(redDelta + greenDelta + blueDelta < 0.05){
    setBackground();
  }
  if(redDelta > 0.01){
    backgroundRedTemp += (backgroundRed - backgroundRedTemp) * 0.005;
  }
  if(greenDelta > 0.01){
    backgroundGreenTemp += (backgroundGreen - backgroundGreenTemp) * 0.005;
  }
  if(blueDelta > 0.01){
    backgroundBlueTemp += (backgroundBlue - backgroundBlueTemp) * 0.005;
  }
  background(backgroundRedTemp, backgroundGreenTemp, backgroundBlueTemp);
}
