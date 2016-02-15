import sunflowapiapi.P5SunflowAPIAPI;

P5SunflowAPIAPI sunflow ;
int sceneWidth = 640;
int sceneHeight = 480;

void setup() {
  size(sceneWidth, sceneHeight, "sunflowapiapi.P5SunflowAPIAPI");
  sunflow = (P5SunflowAPIAPI) g;
}

void draw() {
  background(255);

  for(int i=0;i<10;i++) {
    fill(random(255), random(255), random(255));
    pushMatrix();
    translate(random(-10, 10), random(-10, 10), random(-10, 10));
    pushMatrix();
    rotateY(0.5f + i*.001f);
    rotateX(0.5f);
    box(5);
    popMatrix();
    popMatrix();
  }
}

void mouseReleased() {
  sunflow.setPathTracingGIEngine(32);
  sunflow.render();
}

