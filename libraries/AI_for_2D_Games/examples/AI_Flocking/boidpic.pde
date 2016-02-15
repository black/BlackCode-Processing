/**
 Demonstrates how to create a your own entity renderer for
 your own applications. <br>
 
 The class must extend the Picture class and the constructor
 must have the PApplet as a parameter so the first statement 
 can be a call to the super constructor, passing the PApplet 
 object.
 */
class BoidPic extends PicturePS {
  float arrowLength = 20;
  float[] x = new float[] {
    0.5f * arrowLength, -0.4f * arrowLength, -0.4f * arrowLength
  };
  float[] y = new float[] {
    0, 0.3f * arrowLength, -0.3f * arrowLength
  };

  int fillCol;

  BoidPic(PApplet app) {
    super(app);
  }

  BoidPic(PApplet app, float length, int fill) {
    super(app);
    fillCol = fill;
    arrowLength = length;
    x = new float[] {
      0.5f * arrowLength, -0.4f * arrowLength, -0.4f * arrowLength
    };
    y = new float[] {
      0, 0.3f * arrowLength, -0.3f * arrowLength
    };
  }

  public void draw( BaseEntity owner, 
  float posX, float posY, 
  float velX, float velY, 
  float headX, float headY,
  float etime) {
    // First calculate the angle the entity is facing
    float angle = PApplet.atan2(headY, headX);

    // Can remove the next 4 lines if you never want to show hints
    if (hints != 0) {
      Hints.hintFlags = hints;
      Hints.draw(app, owner, velX, velY, headX, headY);
    }

    pushMatrix();
    translate(posX, posY);
    rotate(angle);

    // Draw arrow head
    fill(fillCol);
    beginShape(PApplet.TRIANGLES);
    vertex(x[0], y[0]);
    vertex(x[1], y[1]);
    vertex(x[2], y[2]);
    endShape(PApplet.CLOSE);
    popMatrix();
  }

  void setFill(int col) {
    fillCol = col;
  }
}