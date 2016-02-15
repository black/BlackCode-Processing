import sures.remotevlc.*;
VlcConnection vlc;

void setup() {
  size(450,400);

  vlc = new VlcConnection(this, "127.0.0.1", 1234);
  
  PFont font = createFont("",15);
  textFont(font);
}


void keyPressed() 
{
  if (key == 'g') vlc.gotosec(56);
  if (key == ' ') vlc.pause();
  if (key == '0') vlc.play();
  if (key == 's') vlc.vlcstop();
  if (key == 'n') vlc.next();
  if (key == 'p') vlc.previous();
  if (key == 'r') vlc.repeat();
  if (key == 'l') vlc.vlcloop();
  if (key == 'd') vlc.vlcrandom();
  if (key == '.') vlc.fastforward();
  if (key == ',') vlc.rewind();
  if (key == 'f') vlc.fullscreen();
}

void draw() {
  background(0);
  fill(255);
   
  // ---------- printing shortcuts in the sketch starts here --------------
  
  text("Remote VLC Library Example:", 10, 30);
  stroke(255);
  line(10,35, 203,35);
  
  text("Shortcuts:", 10, 50);
  stroke(255);
  line(10,55, 73,55);
  
  text("Space Bar", 10, 80);
  text("=", 100, 80);
  text("Pause / Resume", 120, 80);
  
  text("0", 10, 100);
  text("=", 100, 100);
  text("Play", 120, 100);
  
  text("s", 10, 120);
  text("=", 100, 120);
  text("Stop", 120, 120);
  
  text("n", 10, 140);
  text("=", 100, 140);
  text("Next Track", 120, 140);
  
  text("p", 10, 160);
  text("=", 100, 160);
  text("Previous Track", 120, 160);
  
  text("r", 10, 180);
  text("=", 100, 180);
  text("Repeat [on / off]", 120, 180);
  
  text("l", 10, 200);
  text("=", 100, 200);
  text("Loop [on / off]", 120, 200);
  
  text("d", 10, 220);
  text("=", 100, 220);
  text("Random / Shuffle [on / off]", 120, 220);
  
  text(".", 10, 240);
  text("=", 100, 240);
  text("Fast forward", 120, 240);
  
  text(",", 10, 260);
  text("=", 100, 260);
  text("Rewind", 120, 260);
  
  text("f", 10, 280);
  text("=", 100, 280);
  text("Fullscreen Toggle", 120, 280);
  
  text("g", 10, 300);
  text("=", 100, 300);
  text("Goto X th sec", 120, 300);
 
 // ---------- printing shortcuts in the sketch ends here --------------
 

}
