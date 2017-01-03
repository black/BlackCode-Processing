import ddf.minim.*;
import java.net.*;
import neurosky.*;
import org.json.*;

ThinkGearSocket neuroSocket;

ArrayList<Particle> poop = new ArrayList();
ArrayList<AudioPlayer> player = new ArrayList();
ArrayList<Blast> blastList = new ArrayList();

Minim minim; 
color c;
float R=50;
int attention=10;

void setup() {
  size(displayWidth, displayHeight);
  ThinkGearSocket neuroSocket = new ThinkGearSocket(this);
  try {
    neuroSocket.start();
  } 
  catch (ConnectException e) {
    println("Is ThinkGear running??");
  }
  minim = new Minim(this);
  for (int i=0; i<150; i++) {
    poop.add(new Particle(random(width), random(height), 0.05, 3));
    //player.add(minim.loadFile("1.mp3", 2048));
  }
  c = (color)random(#000000);
}

void draw() {
  background(c);
  if (millis()/1000>6) {
    float mx = map(attention, 0, 100, -2, 1);
    Particle M = new Particle(width/2, height/2, -mx, R);
    fill(-1, 255-poop.size()+10);
    M.show();
    M.update();
    for (int i=0; i<poop.size (); i++) {
      Particle P = poop.get(i); 
      P.applyForce(M);
      //P.applyForce(P);
      P.update(); 
      P.bounce();
      if (M.loc.dist(P.loc)<R) {
//        AudioPlayer play = player.get(i);   
//        play.play(); 
        for (int k=0; k<30; k++) {
          Blast KP = new Blast(P.loc.x+random(-100, 100), P.loc.y+random(-100, 100));
          blastList.add(KP);
        }

        poop.remove(i);
      }
    }


    if (blastList.size ()>0)
      for (int i=0; i<blastList.size (); i++) {
        Blast P = blastList.get(i);
        P.display();
        P.move();
        if (P.r<0)blastList.remove(i);
      }

    for (int i=0; i<poop.size (); i++) {
      Particle P = poop.get(i);
      fill(-1);
      P.show();
      if (M.loc.dist(P.loc)<50) {
        stroke(-1, 50);
        line(P.loc.x, P.loc.y, M.loc.x, M.loc.y);
      } 
      for (int j=0; j<poop.size (); j++) {
        Particle K = poop.get(j);
        if (P.loc.dist(K.loc)<70) {
          stroke(-1, 50);
          line(P.loc.x, P.loc.y, K.loc.x, K.loc.y);
        }
      }
    }
    textAlign(CENTER);
    fill(-1);
    textSize(20);
    text(attention, width/2, height/2);
    text("Life " +poop.size(), width/2, 40);
  }
}
void stop() {
  for (int i=0; i<player.size (); i++) {
    AudioPlayer play = player.get(i);
    play.close();
  }
  minim.stop();
  neuroSocket.stop();
  super.stop();
} 

void keyPressed() {
  //  if (key==CODED) {
  //    if (keyCode==UP)mx+=0.05f;
  //    if (keyCode==DOWN)mx-=0.05f;
  //  }
  if (key==' ') {
    poop.clear();
    player.clear();
    setup();
  }
}

boolean sketchFullScreen() {
  return true;
}


void poorSignalEvent(int sig) {
  println("SignalEvent "+sig);
}

//public void attentionEvent(int attentionLevel) {
//  println("Attention Level: " + attentionLevel);
//  attention = attentionLevel;
//}

void meditationEvent(int meditationLevel) {
  println("Meditation Level: " + meditationLevel);
  attention = meditationLevel;
}
