import java.awt.Color;
import sunflowapiapi.P5SunflowAPIAPI;
import org.sunflow.math.Point3;
import org.sunflow.math.Vector3;

int sceneWidth = 640;
int sceneHeight = 480;
P5SunflowAPIAPI sunflow;

void setup() {
  size(sceneWidth, sceneHeight, "sunflowapiapi.P5SunflowAPIAPI");
  sunflow = (P5SunflowAPIAPI) g;
}

void draw() {
  background(126, 130, 86);
  noStroke();
  
  // set basic light
  sunflow.setSunSkyLight("mySunskyLight");
  sunflow.setPointLight("myPointLight", new Point3(0,5,5), new Color(255,255,255));
  sunflow.setDirectionalLight("myDirectionalLight", new Point3(-2,3,0), new Vector3(0,0,0), 3, new Color(1f,0f,0f));
  sunflow.setSphereLight("mySphereLight", new Point3(0,20,-5), new Color(0,0,255), 32, 10);

  // set some color that is used from now on
  fill(255, 0, 0);

  // set default ambient shader setting, you can send your own finetuned settings as well
  sunflow.setAmbientOcclusionShader();
  // sunflow.setAmbientOcclusionShader(new Color(255,0,0), new Color(0,0,0), 16, 1);
  
  // draw an object
  pushMatrix();
    translate(-4, 0, 0);
    sphere(1);
  popMatrix();
  // sunflow version of a sphere
  sunflow.drawSphere("sphere00", -4, 2, 0, 1);

  // set shader
  sunflow.setDiffuseShader();

  // draw an object
  pushMatrix();
    translate(-2, 0, 0);
    sphere(1);
  popMatrix();
  // sunflow version of a sphere
  sunflow.drawSphere("sphere01", -2, 2, 0, 1);

  // set default shader setting
  sunflow.setGlassShader();
  // sunflow.setGlassShader(new Color(1f,1f,1f), 2.5f, 3f, new Color(1f,1f,1f));
  // draw an object
  pushMatrix();
    translate(0, 0, 0);
    sphere(1);
  popMatrix();
  // sunflow version of a sphere
  sunflow.drawSphere("sphere02", 0, 2, 0, 1);

  // set shader with sininess of 2
  sunflow.setShinyDiffuseShader(2);
  // draw an object
  pushMatrix();
    translate(2, 0, 0);
    sphere(1);
  popMatrix();
  // sunflow version of a sphere
  sunflow.drawSphere("sphere03", 2, 2, 0, 1);

  // set advanced phong shader. to see the right result, one has to set the fill color corresponding the shader here as well
  // i will find a more elegant solution 
  sunflow.setPhongShader("myPhongShader", new Color(1.0, 1.0, 1.0), new Color(.5,.5,.9), 10, 16);
  fill(255);
  // draw an object
  pushMatrix();
    translate(4, 0, 0);
    sphere(1);
  popMatrix();
  // sunflow version of a sphere
  sunflow.drawSphere("sphere04", 4, 2, 0, 1);
}

void mouseReleased() {
  sunflow.setAmbientOcclusionEngine(new Color(255), new Color(0), 16, 10);
  sunflow.render();
}

void keyPressed() {
  if(key == ' ') {
    sunflow.setInstantGIEngine(16, 1, 1.0, 1);
    sunflow.render("testrender.png");            // this will be rendered into the applications folder.
    // TODO: have to render it into the pde's folder 
  }
}






