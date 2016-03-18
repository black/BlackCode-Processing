import java.awt.Frame;

int R = 200, N=90;
PFrame[] f = new PFrame[N]; 
void setup() {
  size(300, 300);
  for (int i=0; i<N; i++) {
    f[i] = new PFrame(i);
  }
}

void draw() {
  background(255, 0, 0);
  int k = (int)map(mouseX, 0, width, 0, 360);
  for (int i=0; i<N; i++) {
    int x = (int)(displayWidth/2+R*cos(radians(i*4+k)));
    int y = (int)(displayHeight/2+R*sin(radians(i*4+k)));
    f[i].setLocation(x, y);
  }
}

public class PFrame extends Frame {
  public PFrame(int i) {
    int x = (int)(displayWidth/2+R*cos(radians(i*2)));
    int y = (int)(displayHeight/2+R*sin(radians(i*2)));
    setBounds(x, y, 150, 150);
    show();
  }
}

public class secondApplet extends PApplet {
  public void setup() {
    size(150, 150);
    noLoop();
  }

  public void draw() {
  }
}

