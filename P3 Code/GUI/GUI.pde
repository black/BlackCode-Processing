PImage Logo;
PImage Brush, Music, Splash, BrushB, MusicB, SplashB;
PGraphics GUILAYER;
boolean drawmode = false, splashmode = false, musicmode = false;
void setup()
{
  size(displayWidth, displayHeight-100, P3D);
  //smooth();
  GUILAYER = createGraphics(width, height);
  //---------NEUTRAL------------------------
  BrushB = loadImage("Brushb.jpg");
  MusicB = loadImage("Musicb.jpg");
  SplashB = loadImage("splashb.jpg");
  //---------ACTIVE-------------------------
  Brush = loadImage("Brush.jpg");
  Music = loadImage("Music.jpg");
  Splash = loadImage("splash.jpg");
  Logo = loadImage("logo.png");

  //--------------------------------------------
  // fs = new FullScreen(this); 
  //  fs.enter();
}

void draw() {
  background(-1);
  GUI();
}

void GUI() {
  //--------------------------START MAIN GUI ---------------------------------
  GUILAYER.beginDraw();
  GUILAYER.smooth();
  GUILAYER.beginShape();
  GUILAYER.noStroke();
  GUILAYER.fill(#FF005E);
  GUILAYER.vertex(0, 0);//1
  GUILAYER.vertex(width, 0);//2
  GUILAYER.vertex(width, 30);//3
  GUILAYER.vertex(7*width/10+25, 30);//4
  GUILAYER.vertex(7*width/10, 45);//5
  GUILAYER.vertex(3*width/10, 45);//6
  GUILAYER.vertex(3*width/10-25, 30);//7
  GUILAYER.vertex(0, 30);//8
  GUILAYER.endShape();
  GUILAYER.stroke(0);
  GUILAYER.line(0+10, height-100, width-10, height-100);
  GUILAYER.noStroke();
  //----------------- Music -----------------------------------------------
  Music.resize(80, 80);
  MusicB.resize(80, 80);
  if (!musicmode) GUILAYER.image(MusicB, width/2-MusicB.width-100, height-MusicB.height-10);
  if (musicmode) GUILAYER.image(Music, width/2-Music.width-100, height-Music.height-10);
  //----------------- Brush -----------------------------------------------
  Brush.resize(80, 80);
  BrushB.resize(80, 80);
  if (!drawmode) GUILAYER.image(BrushB, width/2-BrushB.width/2, height-BrushB.height-10);
  if (drawmode) GUILAYER.image(Brush, width/2-Brush.width/2, height-Brush.height-10);
  //----------------- Splash -----------------------------------------------
  Splash.resize(80, 80);
  SplashB.resize(80, 80);
  if (!splashmode) GUILAYER.image(SplashB, width/2+100, height-SplashB.height-10);
  if (splashmode) GUILAYER.image(Splash, width/2+100, height-Splash.height-10);
  //------------------ LOGO -----------------------------------------------
  Logo.resize(80, 80);
  GUILAYER.image(Logo, 25, height-Logo.height);
  GUILAYER.stroke(0);
  //GUILAYER.line(width/2, 0, width/2, height);
  GUILAYER.endDraw();
  //--------------------------END MAIN GUI ---------------------------------
  image(GUILAYER, 0, 0);
}


void keyPressed() {
  if (key=='a') drawmode = !drawmode;
  if (key=='b') musicmode = !musicmode;
  if (key=='c') splashmode = !splashmode;
}

