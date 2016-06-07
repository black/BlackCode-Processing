// boid test

Predator currentSnake =null;
Predator[] g_Snakes = null;
Prey[] g_littleSnakes=new Prey[0];

float g_prevT =0.;
void setup()
{
  size(640,480);
  g_Snakes =new Predator[1];
  g_Snakes[0] = new Predator(new PVector(width/2, height/2,0.0), 8);
  frameRate(60);
  smooth();
  strokeCap(ROUND);
  strokeJoin(ROUND);
 }
PVector handleBoundary( Boid b)
{
   PVector c= new PVector(width/2, height/2,0.0);
   float d = b.position.dist(c);
   if ( d > (float)width*0.5) 
   {
     return b.Seek(c);
   }
   return new PVector(0.,0.,0.);
}
void spawnBaby(PVector p )
{
  Prey newbaby = new Prey(p, random(1,4));
  g_littleSnakes = (Prey[])append(g_littleSnakes, newbaby);
}
void mouseDragged()
{
  PVector mpos = new PVector(mouseX, mouseY,0.);
  if (currentSnake==null) // create new predator
  {
    g_Snakes = (Predator[])append( g_Snakes, 
            new Predator( mpos, (int)random(6,8)));
    currentSnake = g_Snakes[g_Snakes.length-1];
  }  
  else
  {
     currentSnake.m_tail.AddPoint( mpos, currentSnake.m_boid.position);
  }
}
void mouseReleased()
{
  if ( currentSnake ==null)
    spawnBaby(new PVector(mouseX, mouseY,0.));
  currentSnake=null;
}
void drawBackground(float t)
{
  int numPlankton = 128;
  strokeWeight(4);
   
  for (int i = 0; i<numPlankton; i++)
  {
    float v = (float)i*0.2;
    float x = noise(v*54.7 + t*0.02f);
    float y = noise(17*v + t*0.02f + 127. );
    y=pow(y,1.6);
    x=pow(x,1.6);

    float b = 1.-min(x+y,1);
    stroke( 20+220*b,80 + 180*b,40 + 210*b,30);//random(0,14), random(80,160), random(60,100));
    point( (1.-x) *width , y* height);
  }
}
void draw()
{
  float t = (float)millis()/1000.0f;
  float dt = t - g_prevT;
  g_prevT = t;
  dt *=15.;
 
 background(32,15,45); 
 drawBackground(t);

 if ( random(1.)<0.01)
    spawnBaby(new PVector(random(0,(float)width), random(0,(float)height),0.));

  if ( mousePressed)
 {
   PVector mpos =new PVector(mouseX, mouseY,0.);
   for ( Predator pred : g_Snakes )
     if ( pred.SeekMagicZone(mpos) && pred != currentSnake)
     {
       mouseReleased();
     } 
 }
// build obstacle list

  for ( Predator pred : g_Snakes )
    if ( pred != currentSnake)
      g_littleSnakes = pred.Update(g_littleSnakes, g_Snakes, dt);
 
  for ( Predator pred : g_Snakes ) 
    pred.Draw();

  PVector foePosition = g_Snakes[0].m_boid.position; 
  for ( Prey baby : g_littleSnakes )
  {
     baby.Update( foePosition, dt); 
      baby.Draw();
  }  
//   fill(~0);
//   text( "fps : "+ (int)frameRate + "babys " + g_littleSnakes.length ,10,20);
}
