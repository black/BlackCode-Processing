import arb.soundcipher.*;

SoundCipher sc = new SoundCipher(this);
SCscore score;
float beatCount = 0;
float pitch = 60;
float duration = 0.5;

void setup() {
  noLoop();
  frameRate(2);
  score = new SCScore();
  score.addCallbackListener(this);
  score.addCallback(duration, 1);
  score.play();
  redraw();
}

void draw() {
  if (beatCount * 10 > width) {
    background(204);
    beatCount = 0;
  }
  line(beatCount * 10 , height-pitch, beatCount * 10 + duration * 10, height-pitch);
  sc.playNote(pitch, 100, duration);
}

void handleCallbacks(int callbackID) {
  redraw();
  beatCount += duration;
  pitch += random(10)-5;
  duration = random(1)*0.5 + 0.5;
  score.addCallback(duration, 1);
  score.play();
}
