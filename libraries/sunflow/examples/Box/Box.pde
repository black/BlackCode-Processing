import org.sunflow.math.Point3;
import org.sunflow.math.Vector3;

import processing.core.*;
import sunflowapiapi.*;

private boolean render = false;
private P5SunflowAPIAPI sunflow ;

void setup() {
  size(500, 500, "sunflowapiapi.P5SunflowAPIAPI");
  sunflow = (P5SunflowAPIAPI) g;
}

void draw() {
  if (render) {
    sunflow.setSunSkyLight("mySunskyLight");
    sunflow.setDirectionalLight("myDirectionalLight1", new Point3(0, 1, 0), new Vector3(1, 1, 0), 100, new Color(125, 125, 125));
    // sunflow.setAmbientOcclusionShader(new Color(255, 125, 125), new Color(0, 0, 0), 128, 16);
  }

  fill(255, 125, 125);
  pushMatrix();
  rotateY(0.5f);
  rotateX(0.5f);
  box(7);
  popMatrix();
  
  if (render) {
    sunflow.setPathTracingGIEngine(16);
    sunflow.render("box.png");            // this will be rendered into the applications folder.
    // TODO: have to render it into the pde's folder 
    render = false;
  }
}

public void keyPressed() {
  switch (key) {
  case 'r':
    render = true;
    break;
  }
}

