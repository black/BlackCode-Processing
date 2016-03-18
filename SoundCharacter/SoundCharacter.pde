import ddf.minim.*;
import ddf.minim.ugens.*;
Persona P;
Minim       minim;
AudioOutput out;
Oscil       wave;

void setup() {
  size(300, 300);
  minim = new Minim(this);
  out = minim.getLineOut();
  wave = new Oscil( 440, 0.5f, Waves.SINE );
  float amp = 0.8;
  wave.setWaveform( Waves.SQUARE );
  wave.setAmplitude( amp );
  P = new Persona();
}

void draw() {
  background(-1);
  translate(width>>1, height>>1);
  P.show(mousePressed);
  if (mousePressed)P.react();
}
float freq;
void mouseDragged() { 
  freq = map( mouseY, 0, height, 0, 1500 );
  wave.setFrequency( freq );
  out.unmute(); 
  wave.patch( out );
}
void mouseReleased() {
  out.mute();
}

class Persona {
  float  a, b, w, h, k;
  Persona() {
    k = 1;
    w = h= 100;
    a=b= 20;
  }
  void show(boolean pressed) {
    if (pressed) b=20;
    else b = a*sin(radians(k));
    rectMode(CENTER);
    fill(#FF5F15);
    noStroke();
    rect(0, 0, w, h, 5, 5, 5, 5);
    fill(-1);
    ellipse(-30, -10, a, b);
    ellipse(30, -10, a, b);
    k+=noise(k)*30;
  }
  void react( ) {
    if (mousePressed) {
      fill(-1);
      arc(0, 10, 2*w/3, 50, 0, PI);
    } else { 
      stroke(0);
      strokeWeight(2);
      line(-20, 0, 20, 0);
    }
  }
}

