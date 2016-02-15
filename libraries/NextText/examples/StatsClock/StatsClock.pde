import processing.opengl.*;

import net.nexttext.*;
import net.nexttext.behaviour.*;
import net.nexttext.behaviour.control.*;
import net.nexttext.behaviour.physics.*;
import net.nexttext.behaviour.standard.*;
import net.nexttext.behaviour.dform.*;

/**
 * A NextText clock displaying interesting facts that occur every second, minute, and hour.
 *
 * by Elie Zananiri | Obx Labs | July 2007
 */

// attributes
Book book;
PFont steelfish;

int SECOND = 0;
int MINUTE = 1;
int HOUR = 2;

float FRAMERATES[] = {
  30.0,
  30.0*60,
  30.0*60*60
};
String PAGES[] = {
  "Second",
  "Minute",
  "Hour"
};
int BAR_SIZE = 80;

String STATS[][] = {
  {
    // Every second,
    "3.6 cans of Spam are consumed.",
    "6 tubs of Cool Whip are sold in the USA.",
    "100 pounds of chocolate is eaten in the USA.",
    "350 slices of pizza are eaten in the USA.",
    "2 Barbie dolls are sold.",
    "3 jars of peanut butter are sold.",
    "418 Kit Kat fingers are eaten.",
    "8000 Coca-Cola products are consumed.",
    "2 000 000 red blood cells die."
  }, {
    // Every minute,
    "40 people are sent to the hospital for dog bites.",
    "The average computer user blinks 7 times.",
    "50 Bibles are sold.",
    "A resting body takes in about 10 litres of air.",
    "Approximately 1-2 calories are burned while watching T.V."
  }, {
    // Every hour,
    "1 000 000 000 cells in the body must be replaced.",
    "The sun shrinks by 5 feet.",
    "1 250 000 Jelly Belly beans are produced."
  }
};

Behaviour behaviourTree;
IsInside isInside;
MoveBy moveBy;
Kill kill;

void setup() {
  // init the applet
  size(600, 400);
  smooth();
  frameRate(FRAMERATES[SECOND]);
  rectMode(CORNER);
  strokeWeight(2);
  
  // create the book
  book = new Book(this);
  
  // create the pages
  for (int i=0; i < PAGES.length; i++) {
    book.addPage(PAGES[i]); 
  }

  // init and set the font
  steelfish = createFont("Steelfish.ttf", 48, true);
  textFont(steelfish);
  
  // add the starting stats
  addStat(SECOND);
  addStat(MINUTE);
  addStat(HOUR);
}

void addStat(int type) {
  fill(0);
  noStroke();
  
  // create the Behaviour tree
  kill = new Kill();
  moveBy = new MoveBy(0, height/FRAMERATES[type]);
  isInside = new IsInside(this.getBounds(), moveBy, kill);
  behaviourTree = isInside.makeBehaviour();
    
  book.addGroupBehaviour(behaviourTree);
  
  // add the text to the appropriate page
  book.addText(STATS[type][(int)random(0, STATS[type].length)], 2, 40, PAGES[type]);
  
  // clean up
  book.removeGroupBehaviour(behaviourTree);
}

void drawBar(int type) {
  // draw a line from the bottom-right of the applet to the top-left of the bar
  stroke(255, 100, 0);
  line((width/FRAMERATES[type])*(frameCount%FRAMERATES[type]), 0, 0, height);
  // draw a bar representing the appropriate time frame
  noStroke();
  fill(255, 243-26*type, 0);
  rect((width/FRAMERATES[type])*(frameCount%FRAMERATES[type]), 0, BAR_SIZE, height);
}

void draw() {
  fill(255, 183, 0, 200);
  rect(0, 0, width, height);
  
  // apply the behaviours to the text
  book.step();
  
  for (int i=PAGES.length-1; i >= 0; i--) {
    // draw the layers in order
    drawBar(i);
    book.drawPage(PAGES[i]); 
    
    // add new stats if necessary
    if (frameCount%FRAMERATES[i] == 0) {
      addStat(i);
    }
  }
}
