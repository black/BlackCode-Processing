import processing.opengl.*;

import net.nexttext.*;
import net.nexttext.behaviour.control.*;
import net.nexttext.behaviour.physics.*;
import net.nexttext.behaviour.standard.*;
import net.nexttext.renderer.*;

/**
 * A NextText physics example.
 * <p>Throw the letters around. For best results, release the mouse while it is in motion.<br/>
 * Toggle the renderers using the 1-4 keys: 1 for glyphs, 2 for bounding boxes, 3 for velocity, and 4 for internals (printed to the console).</p>
 * 
 * <p>by Elie Zananiri | Obx Labs | February 2008<br>
 * Words by <a href="http://www.mitchhedberg.net/">Mitch Hedberg</a></p>
 */

// attributes
Book book;
PFont cheboygan;

// renderers
TextPageRenderer boundingBoxRenderer;
TextPageRenderer velocityRenderer;
TextPageRenderer internalsRenderer;

// rendering flags
boolean renderGlyphs = true;
boolean renderBoundingBoxes = false;
boolean renderVelocity = false;
boolean renderInternals = false;

void setup() {
  // init the applet
  size(640, 360);
  smooth();

  // create the book
  book = new Book(this);
  
  // add a page, which is returned, and save it for later reference
  book.addPage("The Great Page");
  
  // create the extra renderers
  boundingBoxRenderer = new BoundingBoxRenderer(this, #0000CC, true, true);
  velocityRenderer = new VelocityRenderer(this, #00CC00, 10);
  internalsRenderer = new TextInternalsRenderer();
  
  // create the actions
  Move move = new Move(0.01f, 0.01f);
  StayInWindow stayInWindow = new StayInWindow(this);

  MoveTo moveOverMouse = new MoveTo(Book.mouse);
  Repeat followMouse = new Repeat(moveOverMouse);
  MouseInertia mouseInertia = new MouseInertia(this, 0.5f, 0.01f);
  Throw throwAround = new Throw(followMouse, mouseInertia);
  moveOverMouse.setTarget(throwAround.getOnDrag());

  // add the behaviours to the book
  book.addGlyphBehaviour(move.makeBehaviour());
  book.addGlyphBehaviour(stayInWindow.makeBehaviour());
  book.addGlyphBehaviour(throwAround.makeBehaviour());
  
  // init and set the font
  cheboygan = createFont("Cheboygan.ttf", 48, true);
  textFont(cheboygan);
  textAlign(CENTER);
  
  // set the text colour
  fill(250, 5, 5, 200);
  noStroke();

  // add the text
  // doubling all the spaces for it to look better on screen
  book.addText("If  I  had  nine  of  my  fingers  missing  I  wouldn't  type  any  slower.", 
               width/2, height/4, 30, "The Great Page");
}

void draw() {
  background(0);

  // apply the behaviours to the text
  book.step();
  
  // draw the text using the active renderers
  if (renderGlyphs) book.draw();
  if (renderBoundingBoxes) boundingBoxRenderer.renderPage(book.getPage("The Great Page"));
  if (renderVelocity) velocityRenderer.renderPage(book.getPage("The Great Page"));
  if (renderInternals) internalsRenderer.renderPage(book.getPage("The Great Page"));
}

void keyPressed() {
  if (key == '1') renderGlyphs = !renderGlyphs;
  else if (key == '2') renderBoundingBoxes = !renderBoundingBoxes;
  else if (key == '3') renderVelocity = !renderVelocity;
  else if (key == '4') renderInternals = !renderInternals;
}