String[] s = {
  "A", "B", "C", "D", "E", "F", "G"
};  
void setup() {
  size(300, 300);
}
int k=0;
void draw() {
  background(-1);
  for (int i=0; i<7; i++) {
    float R = map(i, 2, 7, 0, 50);
    float ang = i*360/7;
    float x = width/2+R*cos(radians(ang)+radians(k));
    float y = height/2+R*sin(radians(ang)+radians(k));
    // println(i);
    //    if (i<4) {
    fill(0);
    text(s[i], x, y);
    //    } else {
    //      fill(0);
    //      ellipse(x, y, 5, 5);
    //    }
  }
  k++;
}

