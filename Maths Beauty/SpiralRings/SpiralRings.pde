// Aniomation variable and constants
float t = 0;
int lastTime;
final int ANIM_REST = 1000;
final int ANIM_ROTATE = 800;
final int ANIM_DURATION = ANIM_REST + ANIM_ROTATE;

void setup() {
  size(400, 400);
  stroke(255);
  strokeWeight(2);
}

void draw() {
  background(0);
  translate(width / 2, height / 2);
  drawRing(t, 8, 10, 30, -1);
  drawRing(t, 16, 30, 50, 0);
  drawRing(t, 32, 50, 70, 1);
  drawRing(t, 32, 70, 90, 0);
  drawRing(t, 64, 90, 110, -1);
  drawRing(t, 64, 110, 130, 0);
  drawRing(t, 64, 130, 150, 1);
  drawRing(t, 64, 150, 170, 0);
  drawRing(t, 64, 170, 190, -1);
  drawRing(t, 64, 190, 210, 0);
  drawRing(t, 128, 210, 230, 1);
  drawRing(t, 128, 230, 250, 0);
  drawRing(t, 128, 250, 270, -1);
  drawRing(t, 128, 270, 290, 0);

  t =getT();
}

void drawRing(float t, float n, float innerRad, float outerRad, int dir) {
  float deltaAng = TWO_PI / n;
  float tAngle = t * deltaAng * dir;
  for (int i = 0; i < n; i++) {
    float a = i * deltaAng + tAngle;
    float sina = sin(a), cosa = cos(a);
    line(innerRad * cosa, innerRad * sina, outerRad * cosa, outerRad * sina);
  }
}

float getT() {
  int currTime = millis();
  int deltaTime = currTime - lastTime;
  if (deltaTime >= ANIM_DURATION) {
    lastTime += ANIM_DURATION;
    deltaTime -= ANIM_DURATION;
  }
  if (deltaTime < ANIM_REST)
    return 0; // rest period
  // Rotation cycle
  deltaTime -= ANIM_REST;
  return (1.0 * deltaTime) / ANIM_ROTATE;
}

