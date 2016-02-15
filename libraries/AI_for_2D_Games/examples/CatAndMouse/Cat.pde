public class Cat extends Vehicle {

  public Mouse chasing = null;
  public Vector2D lastKnownPos = new Vector2D();
  public int miceKilled = 0;

  public Cat(Vector2D position, double radius, Vector2D velocity, 
  double max_speed, Vector2D heading, double mass, 
  double max_turn_rate, double max_force) {
    super(position, radius, velocity, max_speed, heading, mass, max_turn_rate, 
    max_force);
    addFSM();
  }

  public void lookForMouse() {
    chasing = null;
    for (int i = 0; i < mice.length; i++) {
      if (mice[i].alive &&  canSee(world, mice[i].pos())) {
        chasing = mice[i];
        lastKnownPos.set(chasing.pos());
      }
    }
  }

  public void adjustLastKnownPosition() {
    // Adjust x position
    if (lastKnownPos.x < wd.lowX)
      lastKnownPos.x = wd.lowX;
    else if (lastKnownPos.x > wd.highX)
      lastKnownPos.x = wd.highX;
    // Adjust y position
    if (lastKnownPos.y < wd.lowY)
      lastKnownPos.y = wd.lowY;
    else if (lastKnownPos.y > wd.highY)
      lastKnownPos.y = wd.highY;
  }
} // End of Cat class



public class CatPic extends PicturePS {

  int head, eye, whiskers;
  float size;

  public CatPic(PApplet app, float size, int body, int eye, int whiskers) {
    super(app);
    this.size = size;
    this.head = body;
    this.eye = eye;
    this.whiskers = whiskers;
  }

  public CatPic(PApplet app, float size) {
    this(app, size, color(255, 169, 19), color(100, 100, 200), color(0));
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
    pushMatrix();
    translate(posX, posY);
    rotate(angle);

    // Draw the entity  
    ellipseMode(PApplet.CENTER);
    stroke(whiskers);
    strokeWeight(1);
    line(0.3f*size, -0.35f*size, 0.4f*size, 0.35f*size);
    line(0.4f*size, -0.35f*size, 0.3f*size, 0.35f*size);

    stroke(whiskers);
    strokeWeight(0.5f);
    fill(head);

    arc(0.05f*size, 0, 0.5f*size, 1.2f*size, PApplet.HALF_PI - 0.1f, 3* PApplet.HALF_PI + 0.1f, PApplet.CHORD);
    ellipse(0, 0, 0.7f*size, 0.7f*size);

    fill(whiskers);
    ellipse(0.35f*size, 0, 0.14f*size, 0.2f*size);

    fill(eye);
    ellipse(0.12f*size, 0.18f*size, 0.12f*size, 0.22f*size);
    ellipse(0.12f*size, -0.18f*size, 0.12f*size, 0.22f*size);

    // Finished drawing
    popMatrix();
    popStyle();
  }
} // End of CatPic class

