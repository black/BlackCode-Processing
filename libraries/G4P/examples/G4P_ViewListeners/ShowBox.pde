/** 
 * View listener for the 3D view of the rotating box
 */
public class ShowBox extends GViewListener {

  float a = 0, delta = 0.02f;
  boolean is_rotating = true;

  public void mouseClicked() {
    if (is_rotating) {
      validate(); // will stop update()
    } else {
      invalidate(); // will enable update() in every frame until validate() is executed
    }
    is_rotating = !is_rotating;
  }

  public void reverseRotation() {
    delta *= -1;
    //invalidate();
  }

  public void update() {
    PGraphics3D v = (PGraphics3D) getGraphics();
    v.beginDraw();
    v.ambientLight(100, 100, 10);
    v.directionalLight(255, 255, 0, 0.5f, 1, -2f);
    v.background(0);
    v.translate(v.width/2, v.height/2);
    v.rotateX(0.19f * a);
    v.rotateY(1.101f * a);
    v.rotateZ(1.357f * a);
    v.fill(255, 200, 128);
    v.stroke(255, 0, 0);
    v.strokeWeight(4);
    v.box(80);
    a += delta;
    v.endDraw();
  }
}
