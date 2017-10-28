float k=0, l=20;
void setup() {
  size(600, 200);
}

void draw() {
  background(-1);
  float x = l*sin(radians(k));
  translate(x, height/2);
  line(0, 0, (90<k)?x:-x, 0);
  if (k<180)k++;
  else k =0;
}

