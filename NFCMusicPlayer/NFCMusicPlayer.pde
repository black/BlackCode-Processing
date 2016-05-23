import processing.serial.*;
import ddf.minim.*;

Minim minim;
AudioPlayer[] player;
boolean play;
int r =200;
float rad = 50;
Serial myPort;  // Create object from Serial class
String val;      // Data received from the serial port
String[] playList = {
  "0x53 0x27 0x26 0xD0", "0xC3 0x76 0x12 0xD0"
};

boolean[] Tags;
void setup() {
  size(displayWidth, displayHeight);
  minim = new Minim(this);
  Tags =  new boolean[playList.length];
  player = new AudioPlayer[playList.length];
  String portName = Serial.list()[0];
  myPort = new Serial(this, portName, 115200);
  player[0] = minim.loadFile("1.mp3");
  player[1] = minim.loadFile("2.mp3");
}
int k = 0;
void draw() {
  fill(0, 50);
  noStroke();
  rect(0, 0, width, height);
  if ( myPort.available() > 0) {     
    val = myPort.readStringUntil('\n');
    //println(val);
    val = trim(val);   
    for (int j=0; j<playList.length; j++) {
      if (val.equals(playList[j])) {

        if (!player[j].isPlaying()) {
          play = true;
          println("Play");
          player[j].pause();
          player[j].rewind();
          player[j].play();
          play = false;
          k = j;
        }
      } else {
        player[j].pause();
        play = false;
      }
    }
  }

  translate(width>>1, height>>1);
  stroke(-1, 50);
  if (player[k].isPlaying()) { 
    int bsize = player[k].bufferSize();
    for (int i = 0; i < bsize - 1; i+=5) {
      float x = (r)*cos(i*2*PI/bsize);
      float y = (r)*sin(i*2*PI/bsize);
      float x2 = (r + player[k].left.get(i)*50)*cos(i*2*PI/bsize);
      float y2 = (r + player[k].left.get(i)*50)*sin(i*2*PI/bsize);
      line(x, y, x2, y2);
    }

    beginShape();
    noFill();
    stroke(-1, 50);
    for (int i = 0; i < bsize; i+=20) {
      float x2 = (r + player[k].right.get(i)*50)*cos(i*2*PI/bsize);
      float y2 = (r + player[k].right.get(i)*50)*sin(i*2*PI/bsize);
      vertex(x2, y2);
      pushStyle();
      stroke(-1);
      strokeWeight(2);
      point(x2, y2);
      popStyle();
    }
    endShape(CLOSE);
  }
}

boolean sketchFullScreen() {
  return true;
}
