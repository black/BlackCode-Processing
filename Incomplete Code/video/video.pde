import processing.video.*;
 
Movie mov;
int newFrame = 0;
 
void setup() {
  size(640, 360);
  background(0);
  // Load and set the video to play. Setting the video 
  // in play mode is needed so at least one frame is read
  // and we can get duration, size and other information from
  // the video stream. 
  mov = new Movie(this, "3.mp4");  
 
  // Pausing the video at the first frame. 
  mov.play();
  mov.jump(0);
  mov.pause();
}
 
void movieEvent(Movie m) {
  m.read();
}
 
void draw() {
  background(0);
  image(mov, 0, 0, width, height);
  fill(255);
  text(getFrame() + " / " + (getLength() - 1), 10, 30);
  int f=ceil (map(mouseX,0,width,1,getLength()/2));
  setFrame(f);
}
 
//void keyPressed() {
//  if (key == CODED) {
//    if (keyCode == LEFT) {
//      if (0 < newFrame) newFrame--; 
//    } else if (keyCode == RIGHT) {
//      if (newFrame < getLength() - 1) newFrame++;
//    }
//  } 
//  setFrame(newFrame);  
//}
 
int getFrame() {    
  return ceil(mov.time() * 30) - 1;
}
 
void setFrame(int n) {
  mov.play();
 
  // The duration of a single frame:
  float frameDuration = 1.0 / mov.frameRate;
 
  // We move to the middle of the frame by adding 0.5:
  float where = (n + 0.5) * frameDuration; 
 
  // Taking into account border effects:
  float diff = mov.duration() - where;
  if (diff < 0) {
    where += diff - 0.25 * frameDuration;
  }
 
  mov.jump(where);
  mov.pause();  
}  
 
int getLength() {
  return int(mov.duration() * mov.frameRate);
}
