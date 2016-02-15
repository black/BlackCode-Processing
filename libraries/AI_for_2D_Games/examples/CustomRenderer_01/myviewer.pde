
public class JetPlane extends PicturePS {

  public JetPlane(PApplet app) {
    super(app);
  }

  public void draw(BaseEntity owner, 
  float posX, float posY, 
  float velX, float velY, 
  float headX, float headY,
  float etime) 
  {
    // First calculate the angle the entity is facing
    float angle = PApplet.atan2(headY, headX);

    // Can remove the next 4 lines if you never want to show hints
    if (hints != 0) {
      Hints.hintFlags = hints;
      Hints.draw(app, owner, velX, velY, headX, headY);
    }

    // The next line can be included if needed
    MovingEntity entity = (MovingEntity) owner;
    
    pushStyle();
    pushMatrix();
    translate(posX, posY);
    rotate(angle);

    // Drawing code goes here
    fill(140,140,150);
    stroke(0);
    strokeWeight(2);
     // Front wing
    beginShape(TRIANGLES);
    vertex(20,0);
    vertex(-10,-20);
    vertex(-10,20);
    endShape();
    // Rear wing
    beginShape(TRIANGLES);
    vertex(-20,0);
    vertex(-30,-10);
    vertex(-30,10);
    endShape();
     // Fusilage
    ellipseMode(CENTER);
    ellipse(0,0,60,10);
    
    // End of drawing code
    popMatrix(); 
    popStyle();
  }
}

