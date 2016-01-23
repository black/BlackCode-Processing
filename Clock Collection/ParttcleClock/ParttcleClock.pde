ArrayList<Particle> particleH = new ArrayList();
ArrayList<Particle> particleM = new ArrayList();
ArrayList<Particle> particleS = new ArrayList();

PGraphics pgH, pgM, pgS;
int h, m, s;
PFont f;

void setup() {
  size(500, 200, P3D);
  pgH = createGraphics(width/3, height);
  pgM = createGraphics(width/3, height);
  pgS = createGraphics(width/3, height);
  f = loadFont("ArialMT-60.vlw");
}

void draw() {
  background(0);
  h = hour();
  m = minute();
  s = second();

  getPixlesHour();
  getPixlesMinute();
  getPixlesSecond();

  creatGraphicsH();
  creatGraphicsM();
  creatGraphicsS();

  //  image(pgH, 0, 0);
  //  image(pgM, width/3, 0);
  //  image(pgS, 2*width/3, 0);

  showPixlesHour();
  showPixlesMinute();
  showPixlesSecond();
}

