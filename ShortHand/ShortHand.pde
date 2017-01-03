int x, y, z;
int a, b, c;
boolean yes, no;
void setup() {
  x =30;
}
void draw() {
  //Longhand
  if (x==2) {
    a = x;
  }
  //shorthand
  b = x < 0  ;
  println(a +  " "  + b);
}

