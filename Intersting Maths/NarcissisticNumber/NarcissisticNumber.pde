// Done 
int nn = 0;
int sum = 0;
void setup() {
  size(100, 100, P3D);
}
void draw() {
  String s = nn+"";
  int len = s.length ();
  for (int i=0; i<len; i++) {
    String str = s.charAt(i)+"";
    int num = Integer.parseInt(str); 
    int finalNumber = (int)pow(num, len);
    sum = sum + finalNumber;
  }

  if (nn==sum) {
    println("narcs = "+ nn+"  "+millis()/1000);
  }
  nn++;
  sum = 0;
}

