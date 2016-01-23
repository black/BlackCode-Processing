class Button {
  int x, y, w, h, index;
  color c;
  String str ="ABCDEFGHIJKL MNOPQRSTUVWXYZ ";
  char[] ch = new char[28];
  Button(int x, int y, int w, int h, int index) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.index = index;
    ch = str.toCharArray();
    c = color(-1);
  }
  void show() {
    stroke(0, 50);
    fill(c);
    rect(x, y, w, h, 10);
    fill(#B4BCC6);
    textSize(18);
    textAlign(CENTER);
    text(ch[index], x+w/2, y+2*h/3);
    c=color(-1);
  }
  void update() {
    c = color(#005FD8);
  }
  void pressKey() {
    robot.keyPress( Character.toUpperCase(ch[index]));
    robot.keyRelease( Character.toUpperCase(ch[index]));
  }
}

