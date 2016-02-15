import net.nexttext.behaviour.dform.*;
import net.nexttext.behaviour.*;
import net.nexttext.behaviour.control.*;
import net.nexttext.behaviour.physics.*;
import net.nexttext.renderer.*;
import net.nexttext.*;
import net.nexttext.property.*;
import net.nexttext.behaviour.standard.*;
import net.nexttext.input.*;

/**
 * A simple NextText sketch.
 *
 * <p>by Elie Zananiri | Obx Labs | October 2009</p>
 */

// global attributes
Book book;
PFont font;

void setup() {
  size(700, 240);
  smooth();

  // create the Book
  book = new Book(this);
  
  // load and set the font
  font = createFont("Arial", 48);
  textFont(font);
  textAlign(CENTER);
  fill(255);
  stroke(96);
  strokeWeight(5);
  
  // create the follow mouse Behaviour
  MoveTo followMouse = new MoveTo(Book.mouse, 2);
  
  // add the Behaviour to the Book
  book.addGroupBehaviour(followMouse);
  
  // build the text
  book.addText("NextText", width/2, height/2);
}

void draw() {
  background(0);
  book.stepAndDraw();
}
