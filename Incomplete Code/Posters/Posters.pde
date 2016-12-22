ArrayList<DotPoster> dotPoster = new ArrayList();
ArrayList<LineSpace> lineSpace = new ArrayList();
int t = 10;
void setup() {
  size(350, 550); 
  for (int i=0; i<width; i+=t) {
    for (int j=0; j<height; j+=t) {
      int k = (int)random(0, 360);
      dotPoster.add(new DotPoster(i, j, k));
    }
  }
}

int f=0;
void draw() {
  background(#150C27);
  for (int i=0; i<dotPoster.size (); i++) {
    DotPoster D = dotPoster.get(i);
    D.update();
    D.show();
   // if(dist(mouseX,mouseY,D.x,D.y)<20) D.k =(int)random(360);
  }
}

