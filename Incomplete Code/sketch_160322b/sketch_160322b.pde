import java.awt.Frame; 

int R = 200;
PFrame[] f; 
void setup() {
  size(200, 200); 
  f = new PFrame[R];
}

void draw() {
  background(255, 0, 0);
}

public class PFrame extends Frame {
  public PFrame(int x, int y, int w, int h) { 
    setBounds(x, y, w, h);
    show();
  }
}

public class secondApplet extends PApplet {
  public void setup() {
    size( 50, 150);
    noLoop();
  }

  public void draw() {
  }
}

