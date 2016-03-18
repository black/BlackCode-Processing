import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import rita.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Analysis extends PApplet {



RiLexicon lexicon;
String pos="", word="", sy, ph, ss;
Bubble[] bubbles = new Bubble[10];
StringDict tagsDict;
int[] colors;

public void setup()
{
  

  noStroke();
  textFont(createFont("Georgia", 36));

  // load Lexicon
  lexicon = new RiLexicon();

  colors = colorGradient();

  // initialize bubbles
  for (int i = 0; i < bubbles.length; i++)
    bubbles[i] = new Bubble();

  // start a timer
  RiTa.timer(this, 4.0f);
}


public void draw()
{
  background(255);

  // float gap = width/((float)colors.length+1);
  // for (int i = 0; i < colors.length; i++) {
  //   fill(colors[i]);
  //   ellipse((colors.length-i) * gap, height-2*gap, gap, gap);
  // }

  // the word
  fill(56, 66, 90);
  textAlign(LEFT);
  textSize(36);
  text(word, 80, 100);

  // pos Tag
  fill(100);
  textSize(14);
  text(pos.toUpperCase(), 20, 30);

  for (int i = 0; i < bubbles.length; i++)
    bubbles[i].draw(i);
}


public void onRiTaEvent(RiTaEvent re) { // called every 4 sec by timer

  // random word with <= 12 letters
  do {
    word = lexicon.randomWord();
  }
  while (word.length() > 12);

  // get various features
  sy = RiTa.getSyllables(word);
  ph = RiTa.getPhonemes(word);
  ss = RiTa.getStresses(word);

  // get (WordNet-style) pos-tags
  String[] tags = RiTa.getPosTags(word, true);
  pos = tagName(tags[0]);

  // restart the bubbles
  for (int i = 0; i < bubbles.length; i++) {
    bubbles[i].reset();
  }

  // update the bubbles for the new word
  String[] phs = ph.split("-");
  for (int i = 0; i < phs.length; i++) {
    bubbles[i].update(phs[i], i*50+100);
  }

  addStresses(ss, sy, bubbles);
  addSyllables(sy, bubbles);
}
class Bubble {

  int r; // radius of circle
  String ph; // phoneme
  int c; // color
  int t; // timer
  float ypos, xpos, gravity = 0.5f, speed = 0, sz;

  Bubble() {
    this("", 0);
  }

  Bubble (String phone, float x) {
    xpos = x;
    ypos = 150;
    r = 40;
    ph = phone;
    t = 0;
    sz = 0;
    c = phonemeColor(ph);
  }

  public void reset() {
    ph = "";
    t = 0;
    sz = 0;
    speed = 0;
  }

  public void update(String phoneme, float x) {
    ph = phoneme;
    xpos = x;
    ypos = 150;
    r = 40;
    c = phonemeColor(ph);
  }

  // adjust distance according to syllable
  public void adjustDistance(int dis) {

    xpos += (r == 40) ? dis : 0.7f * dis;
  }

  // adjust the size of the circle
  public void grow() {

    r = 41;
    sz = 0.5f;
  }

  public void draw(int i) {

    if (ph.length() < 1) return;

    // draw the background circle
    fill(c);
    ellipse(xpos, ypos, r+sz, r+sz);

    // display the phoneme
    fill(255);
    textSize(18);
    textAlign(CENTER, CENTER);
    text(ph, xpos, ypos-5);

    if (sz < 10) sz *= 1.1f;

    if (++t > 100 + 2 * i) {
      speed += gravity;
      ypos += speed;
    }
  }
}
public String tagName(String tag) {

  if (tagsDict == null) {
    tagsDict = new StringDict();
    tagsDict.set("n", "Noun");
    tagsDict.set("v", "Verb");
    tagsDict.set("r", "Adverb");
    tagsDict.set("a", "Adjective");
  }

  return tag == null ? null : tagsDict.get(tag);
}

public void addSyllables(String sylls, Bubble[] bubbles)  {

   // split each syllable
   String[] syllables = sylls.split("/");

   // record how many phonemes are in each syllable
   int[] phslength = new int[syllables.length];

   // record the past phonemes number
   int past = 0;

    for (int i = 0; i < syllables.length; i++) {

      String[] phs = syllables[i].split("-");
      for (int j = 1; j < phs.length; j++)
        bubbles[past+j].adjustDistance(-20 * j);

      past += phs.length;
    }
}

public void addStresses(String stresses, String syllables, Bubble[] bubbles) {

   // Split each stress
   String[] stress = stresses.split("/");

   // Split each syllable
   String[] syllable = syllables.split("/");

   // Count phonemes in each syllable
   int[] phslength = new int[syllable.length];

   // Record the previous phoneme count
   int past = 0;

   for (int i = 0; i < stress.length; i++) {

     String[] phs = syllable[i].split("-");

     // if the syllable is stressed, grow its bubbles
     if (Integer.parseInt(stress[i]) == 1) {
       for (int j = 0; j < phs.length; j++)
         bubbles[past+j].grow();
     }

     past += phs.length;
   }
}

public int phonemeColor(String phoneme) {

  int idx = java.util.Arrays.asList(RiTa.ALL_PHONES).indexOf(phoneme);
  return idx > -1 ? colors[idx] : 0;
}

public int[] colorGradient() {

  colorMode(HSB, 1,1,1,1);
  int[] tmp = new int[RiTa.ALL_PHONES.length];
  for (int i = 0; i < tmp.length; i++) {
    float h = map(i, 0, tmp.length, .2f, .8f);
    tmp[i] = color(h,.9f,.9f,.6f);
  }
  colorMode(RGB,255,255,255,255);
  return tmp;
}
  public void settings() {  size(600, 300); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Analysis" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
