ArrayList<Brick> poop = new ArrayList();
int xm, ym, wm, hm;
void setup() {
  size(400, 400);
  wm=hm=100;
  xm=width/2-wm/2; 
  ym=height/2;
  poop.add(new Brick());
}
void draw() {
  //  background(-1); 
  fill(-1, 100);
  rect(0, 0, width, height);
  grid(); 
  fill(0, 10);
  rect(xm, ym-hm/2, wm, hm);
  Brick B = poop.get(poop.size()-1);

  if (stop) {
    B.check(stop, xm);
    boolean ty = B.peice(); 

    //  println(ty + " " + B.xp + " " + B.yp + " " + B.wp + " " + B.hp);
    if (ty) {
      poop.clear();
      poop.add(new Brick());
      stop= false;
    }
  } else {
    B.show();
    B.update();
  }
}
boolean stop;
void keyPressed() {
  stop = true;
}

