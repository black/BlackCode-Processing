PImage Logo;
PImage Brush, Music, Splash, Grid, BrushB, MusicB, SplashB, GridB;
PGraphics GUILAYER;
boolean drawmode = false, splashmode = false, musicmode = false, gridonoff = false ;
PShape Kids;
AudioOutput out;
SineWave sine;
void Loadimages() {
  GUILAYER = createGraphics(displayWidth,displayHeight);
  //---------NEUTRAL------------------------
  BrushB = loadImage("Brushb.jpg");
  MusicB = loadImage("Musicb.jpg");
  SplashB = loadImage("splashb.jpg");
  GridB = loadImage("GridB.jpg");
  //---------ACTIVE-------------------------
  Brush = loadImage("Brush.jpg");
  Music = loadImage("Music.jpg");
  Splash = loadImage("splash.jpg");
  Grid = loadImage("Grid.jpg");
  Logo = loadImage("logo.png");
  Kids = loadShape("kids.svg");
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
  GUILAYER.line(0+10, height-BrushB.height-20, width-10, height-BrushB.height-20);
  GUILAYER.noStroke();
  //----------------- Music -----------------------------------------------
  Music.resize(int(width*0.04),int(width*0.04));
  MusicB.resize(int(width*0.04),int(width*0.04));
  if (!musicmode) GUILAYER.image(MusicB, width/2-MusicB.width-50, height-MusicB.height-10);
  if (musicmode) GUILAYER.image(Music, width/2-Music.width-50, height-Music.height-10);
  //----------------- Brush -----------------------------------------------
  Brush.resize(int(width*0.04),int(width*0.04));
  BrushB.resize(int(width*0.04),int(width*0.04));
  if (!drawmode) GUILAYER.image(BrushB, width/2-BrushB.width/2, height-BrushB.height-10);
  if (drawmode) GUILAYER.image(Brush, width/2-Brush.width/2, height-Brush.height-10);
  //----------------- Splash -----------------------------------------------
  Splash.resize(int(width*0.04),int(width*0.04));
  SplashB.resize(int(width*0.04),int(width*0.04));
  if (!splashmode) GUILAYER.image(SplashB, width/2+50, height-SplashB.height-10);
  if (splashmode) GUILAYER.image(Splash, width/2+50, height-Splash.height-10);
  //----------------- GRID -----------------------------------------------
  Grid.resize(int(width*0.04),int(width*0.04));
  GridB.resize(int(width*0.04),int(width*0.04));
  if (!gridonoff) GUILAYER.image(GridB, -GridB.width/2, height/2);
  if (gridonoff) GUILAYER.image(Grid, -GridB.width/2, height/2);
  //------------------ LOGO -----------------------------------------------
  Logo.resize(int(width*0.04),int(width*0.04));
  GUILAYER.image(Logo, width/2-Logo.width/2, Logo.height/2);
  /// Kids.disableStyle();
  // GUILAYER.fill(255);    // Set the SVG fill to blue         // Set the SVG fill to white
  // GUILAYER.shape(Kids, 0, 0, Kids.width/2, Kids.height/2);
  GUILAYER.endDraw();
  //--------------------------END MAIN GUI ---------------------------------
  //image(GUILAYER, 0, 0);
}

