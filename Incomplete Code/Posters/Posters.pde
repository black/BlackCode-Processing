ArrayList<DotPoster> dotPoster = new ArrayList();
ArrayList<Bubble> bubblelist = new ArrayList();
int t = 10;
void setup() {
  size(350, 550); 
  for (int i=0; i<width; i+=t) {
    for (int j=0; j<height; j+=t) {
      int k = (int)random(0, 360);
      dotPoster.add(new DotPoster(i, j, k));
    }
  }
  int N = 15;
  for (int i =0; i<N; i++) {
    int x = (int)random(20, width-20);
    float m = 550/N;
    int y = (int)random(m*i+10, m*i+40);
    bubblelist.add(new Bubble(x, y));
  }
}

int f=0;
void draw() {
  background(255, 224, 64);
  poster2();
}

void poster1() {
  for (int i=0; i<dotPoster.size (); i++) {
    DotPoster D = dotPoster.get(i);
    D.update();
    D.show();
    // if(dist(mouseX,mouseY,D.x,D.y)<20) D.k =(int)random(360);
  }
}

void poster2() { 
  for (int i=0; i<bubblelist.size (); i++) {
    Bubble B = bubblelist.get(i);
    B.blur();
  } 
  filter(BLUR, 10); 
  for (int i=0; i<bubblelist.size (); i++) {
    Bubble B = bubblelist.get(i);
    B.show();
  }
  fill(0, 8);
  rect(0, 0, width, height);
  filter(BLUR, 0.3);
}

