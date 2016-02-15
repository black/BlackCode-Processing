public class Mouse extends Vehicle {

  boolean alive = true;

  public Mouse(Vector2D position, double radius, Vector2D velocity, 
  double max_speed, Vector2D heading, double mass, 
  double max_turn_rate, double max_force) {
    super(position, radius, velocity, max_speed, heading, mass, max_turn_rate, 
    max_force);
    addFSM();
  }
} // End of Mouse class



public class MousePic extends PicturePS {

  int head, eye, whiskers;
  float size;

  public MousePic(PApplet app, float size, int body, int eye, int whiskers) {
    super(app);
    this.size = size;
    this.head = body;
    this.eye = eye;
    this.whiskers = whiskers;
  }

  public MousePic(PApplet app, float size) {
    this(app, size, color(160), color(255, 200, 200), color(0));
  }


  public void draw(BaseEntity user, float posX, float posY, float velX, 
  float velY, float headX, float headY, float etime) {

    // Draw and hints that are specified and relevant
    if (hints != 0) {
      Hints.hintFlags = hints;
      Hints.draw(app, user, velX, velY, headX, headY);
    }
    // Determine the angle the tank is heading
    float angle = PApplet.atan2(headY, headX);

    // Prepare to draw the entity    
    pushStyle();
    ellipseMode(PApplet.CENTER);
    pushMatrix();
    translate(posX, posY);
    rotate(angle);

    // Draw the entity  
    stroke(whiskers);
    strokeWeight(1);
    line(0.4f*size, -0.5f*size, 0.6f*size, 0.5f*size);
    line(0.6f*size, -0.5f*size, 0.4f*size, 0.5f*size);

    strokeWeight(0.5f);
    fill(head);
    arc(0.15f*size, 0, 0.3f*size, 1.2f*size, PApplet.HALF_PI - 0.4f, 3* PApplet.HALF_PI + 0.4f, PApplet.CHORD);
    arc(0, 0, 0.7f*size, 0.7f*size, PApplet.HALF_PI, 3* PApplet.HALF_PI);
    arc(0, 0, 1.1f*size, 0.7f*size, 3* PApplet.HALF_PI, PApplet.TWO_PI);
    arc(0, 0, 1.1f*size, 0.7f*size, 0, PApplet.HALF_PI);

    fill(whiskers);
    ellipse(0.55f*size, 0, 0.2f*size, 0.2f*size);

    fill(eye);
    ellipse(0.12f*size, 0.15f*size, 0.2f*size, 0.22f*size);
    ellipse(0.12f*size, -0.15f*size, 0.2f*size, 0.22f*size);


    // Finished drawing
    popMatrix();
    popStyle();
  }
} // End of MousePic class

