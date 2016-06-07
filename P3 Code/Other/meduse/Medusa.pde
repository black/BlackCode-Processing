class Medusa extends MotionInfo {

  float radX, radY;
  float orientation;
  color headClr;
  SteeringInfo steering;

  ArrayList<Tentacle> tentacles;
  int nbTentacles;
  int tentaclesLength;

  int moveTime;
  boolean stopped;

  Medusa(PVector pos, float rx, float ry, int nb, int l, float ts, float td) {

    super(
    9.f,
    3.f,
    radians(40),
    radians(10),
    pos
      );
    radX = rx;
    radY = ry;
    orientation = 0;
    steering = new SteeringInfo();

    nbTentacles = nb;
    tentaclesLength = l;
    tentacles = new ArrayList<Tentacle>();

    headClr = color(random(50,200), random(50,200), random(50,200));

    stopped = false;
    moveTime = 0;


    for (int i = 0; i < nbTentacles; i++) {
      float tx = position.x + (cos(i * TWO_PI / nbTentacles) * radX/2);
      float ty = position.y + (sin(i * TWO_PI / nbTentacles) * radY/2);
      float tr = atan2(ty - position.y, tx - position.x);
      Tentacle tentacle = new Tentacle(new PVector(tx, ty), tentaclesLength, ts, ts, tr, td);
      tentacles.add(tentacle);
    }
  }

  void update() {

    if(moveTime % 100 == 0) {
      stopped = true;
      moveTime = 1;
    }

    if(stopped) {
      maxAccel = 0.15;
      velocity.mult(0.5);
      orientation += random(-1,1) * radians(.1);

      if(moveTime % 50 == 0)
        stopped = false;
    }
    else {
      maxAccel = 3.f;
      orientation += random(-1,1) * radians(100);
    }

    //      steering.linear.div(moveTime/50 > 0 ? moveTime/50 : 1);
    steering = wander(this, 100.f, 100.f, radians(5));
    super.update(steering);

    moveTime++;

    for (int i = 0; i < nbTentacles; i++) {
      Tentacle t = tentacles.get(i);
      t.position.x = position.x + (cos((i * TWO_PI / nbTentacles) + orientation) * radX/2);
      t.position.y = position.y + (sin((i * TWO_PI / nbTentacles) + orientation) * radY/2);
      t.orientation = atan2((t.position.y - position.y), (t.position.x - position.x));
      t.update();
    }
  }

  void draw() {
    for(int i = 0; i < nbTentacles; i++)
      tentacles.get(i).draw();
  }
};
