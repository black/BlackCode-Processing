int rectW, rectH, rindex;
int k = 0;
boolean move;
PVector U, V;
int df = 2;
float t = 0.0;
int layerId = 0;
int contentId = 0;
int home, setting, back, close;

void setup() {
  size(900, 300);
  rectW = rectH = 50;
  rindex = -1;
  U = new PVector(100, 100);
  V = new PVector(0, 0);
  move = false;
}

void draw() {
  background(-1);

  if (k == 0) {
    background(0, 30);
    lineScanner();
    t = 0;
  }
  if (k == 1) {
    background(0);
    moveCircle();
    if (t < 1.0) {
      t += 0.0005;
    }
  }

  if (k > 1) {
    k = 0; 
    checkTiles(U.x, U.y);
  }
  drawTiles(0, 0);
  stroke(255);
  ellipse(U.x, U.y, 10, 10);
  stroke(255);
  line(U.x, U.y, V.x, V.y);


  menu1();
  menu2();
}

void lineScanner() {
  if (V.y == 0 && V.x < 200) {
    V.x += df;
  } else if (V.x == 200 && V.y < 200) {
    V.y += df;
  } else if (V.y == 200 && 0 < V.x) {
    V.x -= df;
  } else if (V.x == 0 && 0 < V.y) {
    V.y -= df;
  }
}

void moveCircle() {
  U.x = U.x + t * (V.x - U.x);
  U.y = U.y + t * (V.y - U.y);
}

void drawTiles(int x, int y) {
  noFill();
  stroke(255);
  for (int i = 0; i < 4; i++) {
    for (int j = 0; j < 4; j++) {
      int index = i + j * 4;
      int xt = x+i*rectW;
      int yt = y+j*rectH;
      rect(xt, yt, rectW, rectH);
      if (index == 0) {
        //        image(home, i * rectW + home.width / 4, j * rectH + home.height / 4, home.width / 2, home.height / 2);
      } else if (index == 1) {
        //    image(setting, i * rectW + setting.width / 4, j * rectH + setting.height / 4, +setting.width / 2, setting.height / 2);
      } else if (index == 2) {
        //    image(back, i * rectW + back.width / 4, j * rectH + back.height / 4, back.width / 2, back.height / 2);
      } else if (index == 3) {
        //    image(close, i * rectW + close.width / 4, j * rectH + close.height / 4, close.width / 2, close.height / 2);
      }
    }
  }
}

void mousePressed() {
  k++;
}


void checkTiles(float cx, float cy) {
  for (int i = 0; i < 4; i++) {
    for (int j = 0; j < 4; j++) {
      int index = i + j * 4;
      if (dist(cx, cy, i * rectW + rectW / 2, j * rectH + rectH / 2) < 30) {
        eyeClick(index);
      }
    }
  }
}

void eyeClick(int getIndex) {
  for (int i = 0; i < 16; i++) {
    if (getIndex == i) {
      if (getIndex == 0) {
      } else if (getIndex == 1) {
      } else if (getIndex == 2) {
      } else if (getIndex == 3) {
      } else {
        launchDiv(i);
      }
    }
  }
}


void menu1() {
  drawTiles(300, 0);
}

void menu2() {
  drawTiles(600, 0);
}

