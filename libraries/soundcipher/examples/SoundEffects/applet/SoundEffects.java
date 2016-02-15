import processing.core.*; 
import processing.xml.*; 

import arb.soundcipher.*; 

import java.applet.*; 
import java.awt.*; 
import java.awt.image.*; 
import java.awt.event.*; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class SoundEffects extends PApplet {

/**
* Add a sound effect to the game.
* Uses the Minim library by Damien Di Fede.
* A bounce sound is loaded as an AudioSample then
* played when a collision is detected with a wall.
*
* SoundCipher library example by Andrew R. Brown
*/
//import ddf.minim.*;


//Minim minim = new Minim(this);
SoundCipher sc = new SoundCipher(this);
//AudioSample bounce;
AudioClip bounce;
boolean hit = false;

float x = random(80) + 10;
float y = random(80) + 10;
float w = 10;
float xv = 1.1f;
float yv = 1.2f;

public void setup() {
  //bounce = minim.loadSample("bounce.wav");
  bounce = sc.loadAudioClip("bounce.wav");
}
  
public void draw() {
  x += xv;
  y += yv;
  if(x<w/2 || x>100-w/2) {
    xv *= -1;
    //bounce.trigger();
    sc.playAudioClip(bounce);
    hit = true;
  }
  if(y<w/2 || y>100-w/2) {
    yv *= -1;
    //if (!hit) bounce.trigger();
    if (!hit) sc.playAudioClip(bounce);
  }
  background(255);
  ellipse(x, y, w, w);
  hit = false;
}

public void stop()
{
  // always close Minim audio classes when you are done with them
  //bounce.close();
  //minim.stop();
  super.stop();
}

  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#ffffff", "SoundEffects" });
  }
}
