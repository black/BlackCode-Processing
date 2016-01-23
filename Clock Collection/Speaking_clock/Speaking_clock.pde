import guru.ttslib.*;
import ddf.minim.*;

Minim notification;
AudioPlayer song;
TTS tts;

void setup() {
  size(400, 300);
  tts = new TTS();
  notification =new Minim(this);
}
void draw() {
  int a=hour();
  int b=minute(); 
  int c=second(); 
  String k = "A M"; 
  if (a>12)
  { 
    a = a-12;
    k ="P M";
  }
  println("h:"+a +" " +"m:"+b);
  String i=String.valueOf(a);
  String j=String.valueOf(b);
  String m=String.valueOf(c);
  if (mousePressed) {
    song = notification.loadFile("1.mp3");
    song.play();
    tts.speak("Hello Sir the time is" + i+ " hour " );
    delay(500);
    tts.speak(  j+ " minutes " );
    delay(500);
    tts.speak(" and" + m + " seconds " );
  }
}

