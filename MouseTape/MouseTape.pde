int i=0;
float factor, dist;
void setup() {
  size(600, 200); 
  dist = 8.5; // 2*Pi*r of your mousewheel
  factor = dist/24;
}

void draw() {
  background(-1);
  fill(0);
  textSize(72);
  textAlign(CENTER);
  text(i*factor*-1, width/2, height/2);
  interaction();
}

void keyPressed() {
  if (key==' ')i=0;
}

void mouseWheel(MouseEvent event) {
  float e = event.getCount();
  if (e>0)i++;
  if (e<0)i--;
  if (e==0)print("stop");
}

void interaction(){
    textSize(12);
  text("Press  space key to reset..",width/2,height-10);
}
