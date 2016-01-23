import com.sun.awt.AWTUtilities;
import java.awt.*;
import javax.swing.JFrame;


class DashedCircle {
  float r;
  int dashWidth, dashSpacing;

  DashedCircle(float radius, int dWidth, int dSpacing) {
    r = radius;
    dashWidth = dWidth ;
    dashSpacing = dSpacing;
  }

  void display() {
    int steps = 300;
    int dashPeriod = dashWidth + dashSpacing;
    boolean lastDashed = false;
    for (int i = 0; i < steps; i++) {
      boolean curDashed = (i % dashPeriod) < dashWidth;
      if (curDashed && !lastDashed) {
        pg3D.beginShape();
      }
      if (!curDashed && lastDashed) {
        pg3D.endShape();
      }
      if (curDashed) {
        float theta = map(i, 0, steps, 0, TWO_PI);
        pg3D.
        vertex(cos(theta) * r, sin(theta) * r);
      }
      lastDashed = curDashed;
    }
    if (lastDashed) {
      pg3D.endShape();
    }
  }
}

