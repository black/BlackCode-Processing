/**
* Add a sound effect to the game.
* Uses the Minim library by Damien Di Fede.
* A bounce sound is loaded as an AudioSample then
* played when a collision is detected with a wall.
*
* SoundCipher library example by Andrew R. Brown
*/
import arb.soundcipher.*;

SoundCipher sc = new SoundCipher(this);

AudioClip bounce;
boolean hit = false;

float x = random(80) + 10;
float y = random(80) + 10;
float w = 10;
float xv = 1.1;
float yv = 1.2;

void setup() {
  bounce = sc.loadAudioClip("bounce.wav");
}
  
void draw() {
  x += xv;
  y += yv;
  if(x<w/2 || x>100-w/2) {
    xv *= -1;
    sc.playAudioClip(bounce);
    hit = true;
  }
  if(y<w/2 || y>100-w/2) {
    yv *= -1;
    if (!hit) sc.playAudioClip(bounce);
  }
  background(255);
  ellipse(x, y, w, w);
  hit = false;
}
