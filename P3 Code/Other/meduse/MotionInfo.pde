class MotionInfo {

  PVector position;
  PVector velocity;
  float orientation;
  float rotation;
  float maxSpeed;
  float maxAccel;
  float maxAngSpeed;
  float maxAngAccel;

  MotionInfo() {
    maxSpeed = 0.f;
    maxAccel = 0.f;
    maxAngSpeed = 0.f;
    maxAngAccel = 0.f;
    position = new PVector(0, 0, 0);
    velocity = new PVector(0, 0, 0);
    orientation = 0.f;
    rotation = 0.f;
  }

  MotionInfo(float ms, float ma, float mas, float maa, PVector p) {
    maxSpeed = ms;
    maxAccel = ma;
    maxAngSpeed = mas;
    maxAngAccel = maa;
    position = p;
    velocity = new PVector(0, 0, 0);
    orientation = 0.f;
    rotation = 0.f;
  }

  PVector getOrientationAsVector() {
    return new PVector(cos(orientation), sin(orientation), 0.f);
  }

  void update(SteeringInfo steering) {

    velocity.add(steering.linear);
    velocity.limit(maxSpeed);
    position.add(velocity);

    float margin = 160.f;

    if(position.x > width + margin)
      position.x = -margin;
    else if(position.x < -margin)
      position.x = width + margin;
    if(position.y  > height + margin)
      position.y = -margin;
    else if(position.y < -margin)
      position.y = height + margin;


    if(rotation < maxAngSpeed)
      rotation += steering.angular;  
    else
      rotation = maxAngSpeed;

    orientation += rotation;
    orientation %= TWO_PI;
  }
};

