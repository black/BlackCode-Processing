// This class shows an alternative way of using an InteractiveFrame by deriving from it.

public class Patch extends GenericFrame {
  private int number; // patch number
  private float size; // patch size in world
  private float padding = 0f; // space arround the patch
  private Board board; // reference to the board containing the patch
  private PImage img; // image for the patch (if null, the patch number will be rendered)

  public Patch(int number, float size, PImage img, Scene scene, Board board) {
    super(scene);
    this.number = number;
    this.size = size;
    this.img = img;
    this.board = board;
  }

  public void draw(float x, float y) {
    pushMatrix();
    pushStyle();
    setPosition(x, y, 0);
    setRotation(0, 0, 0, 0);
    applyTransformation();

    // set the appropiate fill and stroke weight based on the result of grabsMouse()
    if (scene.motionAgent().isInputGrabber(this )) {
      fill(250, 250, 60);
      stroke(200, 200, 100);
      strokeWeight(3);
    } 
    else {
      if (number % 2 == 0) {
        fill(250, 250, 255);
      } 
      else {
        fill(100, 100, 255);
      }
      stroke(150, 150, 255);
      strokeWeight(2);
    }

    // draw patch faces
    float thickness = 6;
    beginShape();
    vertex(-getSize() / 2, -getSize() / 2, -thickness);
    vertex(getSize() / 2, -getSize() / 2, -thickness);
    vertex(getSize() / 2, getSize() / 2, -thickness);
    vertex(-getSize() / 2, getSize() / 2, -thickness);
    endShape(CLOSE);

    beginShape();
    vertex(-getSize() / 2, -getSize() / 2, 0);
    vertex(-getSize() / 2, getSize() / 2, 0);
    vertex(-getSize() / 2, getSize() / 2, -thickness);
    vertex(-getSize() / 2, -getSize() / 2, -thickness);
    endShape(CLOSE);

    beginShape();
    vertex(getSize() / 2, -getSize() / 2, 0);
    vertex(getSize() / 2, getSize() / 2, 0);
    vertex(getSize() / 2, getSize() / 2, -thickness);
    vertex(getSize() / 2, -getSize() / 2, -thickness);
    endShape(CLOSE);

    beginShape();
    vertex(-getSize() / 2, -getSize() / 2, 0);
    vertex(getSize() / 2, -getSize() / 2, 0);
    vertex(getSize() / 2, -getSize() / 2, -thickness);
    vertex(-getSize() / 2, -getSize() / 2, -thickness);
    endShape(CLOSE);

    beginShape();
    vertex(-getSize() / 2, getSize() / 2, 0);
    vertex(getSize() / 2, getSize() / 2, 0);
    vertex(getSize() / 2, getSize() / 2, -thickness);
    vertex(-getSize() / 2, getSize() / 2, -thickness);
    endShape(CLOSE);

    // draw front face
    textureMode(NORMAL);
    beginShape();
    if (img != null) { // we've got an image, set it as a texture
      if( scene.motionAgent().isInputGrabber( this) ) {
        fill(250, 250, 0);
      } 
      else {
        fill(10);
      }
      texture(img);
      vertex(-getSize() / 2, -getSize() / 2, 0, 0, 0);
      vertex(-getSize() / 2, getSize() / 2, 0, 0, 1);
      vertex(getSize() / 2, getSize() / 2, 0, 1, 1);
      vertex(getSize() / 2, -getSize() / 2, 0, 1, 0);
    } 
    else { // no image, just another face without a texture
      vertex(-getSize() / 2, -getSize() / 2, 0);
      vertex(-getSize() / 2, getSize() / 2, 0);
      vertex(getSize() / 2, getSize() / 2, 0);
      vertex(getSize() / 2, -getSize() / 2, 0);
    }
    endShape(CLOSE);

    if (img == null) { // no image, render the patch number    
      textFont(font2, 100 / board.getSize());
      strokeWeight(1);
      fill(0, 0, 20);
      text("" + number, -10, 10, 0.1);
    }

    popStyle();
    popMatrix();
  }
  
  @Override
  public void performInteraction(ClickEvent event) {
    board.movePatch(this);
  }

  @Override
  public boolean checkIfGrabsInput(BogusEvent event) {
    return pointInsideQuad(scene);
  }

  private boolean pointInsideQuad(Scene scene) {
    Vec v1 = scene.projectedCoordinatesOf(new Vec((-getSize() / 2) + position().x(), (-getSize() / 2) + position().y()));
    Vec v2 = scene.projectedCoordinatesOf(new Vec((-getSize() / 2) + position().x(), (getSize() / 2) + position().y()));
    Vec v3 = scene.projectedCoordinatesOf(new Vec((getSize() / 2) + position().x(), (getSize() / 2) + position().y()));
    Vec v4 = scene.projectedCoordinatesOf(new Vec((getSize() / 2) + position().x(), (-getSize() / 2) + position().y()));

    return
      computePointPosition(mouseX, mouseY, v1.x(), v1.y(), v2.x(), v2.y()) < 0 &&
      computePointPosition(mouseX, mouseY, v2.x(), v2.y(), v3.x(), v3.y()) < 0 &&
      computePointPosition(mouseX, mouseY, v3.x(), v3.y(), v4.x(), v4.y()) < 0 &&
      computePointPosition(mouseX, mouseY, v4.x(), v4.y(), v1.x(), v1.y()) < 0;
  }

  private float computePointPosition(float x, float y, float x0, float y0, float x1, float y1) {
    // see "solution 3" at http://local.wasp.uwa.edu.au/~pbourke/geometry/insidepoly/
    return (y - y0) * (x1 - x0) - (x - x0) * (y1 - y0);
  }

  public float getSize() {
    return size;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }
}