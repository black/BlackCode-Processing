/**
 * View listener for the 2D view of line drawer.
 */
public class DrawLine extends GViewListener {
  int[] back_col = { color(200, 255, 200), color(255, 255, 200) };
  int back_col_idx = 0;

  int sx, sy, ex, ey;
  String mousePos;

  DrawLine() {
  }

  public void mouseEntered() {
    back_col_idx = 1;
    invalidate();
  }

  public void mouseExited() {
    back_col_idx = 0;
    invalidate();
  }

  public void mousePressed() {
    sx = ex = mouseX();
    sy = ey = mouseY();
    invalidate();
  }

  public void mouseDragged() {
    ex = mouseX();
    ey = mouseY();
    invalidate();
  }

  public void mouseMoved() {
    invalidate();
  }

  public String getMousePositionText() {
    return "Mouse @ [" + mouseX() + ", " + mouseY() + "]";
  }

  public void randomLine() {
    sx = round(random(width()));
    sy = round(random(height()));
    ex = round(random(width()));
    ey = round(random(height()));
    invalidate();
  }

  public void update() {
    PGraphics v = getGraphics();
    v.beginDraw();
    v.background(20, 20, 120);
    v.noStroke();
    v.fill(back_col[back_col_idx]);
    v.rect(5, 5, v.width - 11, v.height - 11);
    if (sx != ex || sy != ey) {
      v.stroke(color(240, 0, 240));
      v.strokeWeight(10);
      v.strokeCap(ROUND);
      v.line(sx, sy, ex, ey);
    }
    v.textSize(14);
    v.fill(color(0));
    v.text(getMousePositionText(), 10, v.height - 14);
    v.endDraw();
    validate(); // view is current no need to update
  }
}
