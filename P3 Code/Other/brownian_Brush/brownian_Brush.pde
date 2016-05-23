// --- BgLines ------------------------------------------------------------------

void drawBgLines()
{
  int m = width + height + 20;
  for (int i = 0; i < m; i += 10)
  {
    int n = (int) (180 + 40 * abs(sin(i/100)));
    stroke(n);
    line(0, i, i, 0);
  }
}

// ------------------------------------------------------------------------------

// --- Drawable -----------------------------------------------------------------

/*
 * initDrawables()
 * drawDrawables(float elapsed)
 *
 * addDrawable(Drawable d)
 *
 * getDrawableByID(int id)
 *
 * requestDestroyDrawable(int id)
 * destroyDrawable(int id)
 * destroyAllDrawables(int id)
 *
 */
 
class Drawable
{
  Boolean persistent;
  Boolean toDestroy;
  int id;
  int type;
  
  Drawable() { toDestroy = false; }
  
  void draw(float elapsed) { }
  void destroy() { }
};

ArrayList gDrawables;

void initDrawables()
{
  gDrawables = new ArrayList();
}

void drawDrawables(float elapsed)
{
  // draw each
  for (int i = 0; i < gDrawables.size(); ++i)
  {
    Drawable d = (Drawable) gDrawables.get(i);
    if (d != null)
    {
      d.draw(elapsed);
      
      if (d.toDestroy)
      {
        gDrawables.set(i, null);
        d.destroy();
      }
    }
  }
}

int addDrawable(Drawable d)
{
  // find empty id and put it there
  int i;
  for (i = 0; i < gDrawables.size() && gDrawables.get(i) != null; ++i);
  
  if (i < gDrawables.size())
    gDrawables.set(i, d);
  else
    gDrawables.add(i, d);
  
  d.id = i;
  return i;
}

Drawable getDrawableByID(int id)
{
  return (Drawable) gDrawables.get(id);
}

void destroyDrawable(int id)
{
  Drawable d = getDrawableByID(id);
  if (d != null)
    d.toDestroy = true;
}

void destroyAllDrawables()
{
  ArrayList persistents = new ArrayList();
  for (int i = 0; i < gDrawables.size(); ++i)
  {
    Drawable d = (Drawable) gDrawables.get(i);
    if (d != null)
     if (d.persistent)
       persistents.add(d);
     else
       d.destroy();
  }
  gDrawables = persistents;
}

// ------------------------------------------------------------------------------

// --- Walker -------------------------------------------------------------------

int createWalker(PVector p, color c, float maxTime, float alphaMult)
{
  Walker w = new Walker(p, c, maxTime, alphaMult);
  return addDrawable(w);
}

class Walker extends Drawable
{
  PVector mInitPos;
  PVector mPos;
  PVector mPrevPos;
  
  float mTime;
  float mTotalTime;
  
  color mColor;
  
  float mAlphaMult;
  
  Walker(PVector p, color c, float maxTime, float alphaMult)
  {
    persistent = false;
    type = WALKER;
    
    mInitPos = p;
    mPos = p.get();
    mPrevPos = p.get();
    
    mColor = c;
    
    if (maxTime > 5)
      mTime = mTotalTime = random(5, maxTime);
    else
      mTotalTime = 0;
    
    mAlphaMult = alphaMult;
  }
  
  void draw(float elapsed)
  {
    // time flies!
    if (mTotalTime > 0)
    {
      mTime -= elapsed;
      if (mTime < 0)
      {
        destroyDrawable(id);
        mTime = 0;
      }
    }
    
    // walked out?
    checkBounds();
    
    // random vel
    float amp = 10 * elapsed;
    mPos.x += random(-amp, amp);
    mPos.y += random(-amp, amp);
    
    // push away (truly random doesn't venture very far)
    PVector p = mPos.get();
    p.sub(mInitPos);
    p.normalize();
    p.mult(elapsed);
    mPos.add(p);
    
    // draw line, alpha-fade based on time
    if (mTotalTime > 0)
      stroke(mColor, mTime/mTotalTime * 120 * mAlphaMult);
    else
      stroke(mColor, 120 * mAlphaMult);
    line(mPrevPos.x, mPrevPos.y, mPos.x, mPos.y);
    
    // prev pos
    mPrevPos = mPos.get();
  }
  
  void checkBounds()
  {
    int b = 50; // can go this far outside
    int xL = -b;
    int xU = width + b;
    int yL = -b; 
    int yU = height + b;
      
    if (mPos.x < xL || mPos.x > xU 
      || mPos.y < yL || mPos.y > yU)
      destroyDrawable(id);
  }
};

// ------------------------------------------------------------------------------

// --- App ----------------------------------------------------------------------

PVector gGravity;
int gPrevTime;

int PARTICLE = 0;
int WALKER = 1;

PVector gPrevMouse;

void setup()
{
  size(640, 480);
  smooth();
  
  gGravity = new PVector(0, 0);
  gPrevTime = millis();
  gPrevMouse = new PVector(mouseX, mouseY);
  
  initDrawables();
  
  firstDraw();
}

void firstDraw()
{
  clear();
  
  // initial corner Walkers
  for (int i = 0; i < 400; ++i)
  {
    color c = color(random(0,100), 
      random(0, 100), 
      random(0, 100));
    createWalker(new PVector(0, height), c, 200, 0.2);
    createWalker(new PVector(width, 0), c, 400, 0.2);
  }
}

void clear()
{
  background(255);
  drawBgLines();
}

void draw()
{
  // calculate elapsed time
  int currTime = millis();
  float elapsed = (currTime - gPrevTime) / 100.0;
  
  // add walkers, doing 10 iterations, each time adding
  // 0.1th of the mouse displacement to get a continuous
  // line
  if (mousePressed)
  {
    PVector mousePos = new PVector(mouseX, mouseY);
    
    PVector mouseDisp = mousePos.get();
    mouseDisp.sub(gPrevMouse);
    
    float mouseSpeed = mouseDisp.mag();
    
    mouseDisp.mult(0.1);
    for (int i = 0; i < 10; ++i)
    {
      gPrevMouse.add(mouseDisp);
      color c = color(random(20,100), 0, 0);
      createWalker(gPrevMouse, c, 30 - 20*min(1, mouseSpeed), 1);
    }
  }
      
  // draw all
  drawDrawables(elapsed);
  
  // set previous
  gPrevTime = currTime;
  gPrevMouse.x = mouseX; gPrevMouse.y = mouseY; 
}

void keyPressed()
{
  if (key == 'c')
  {
    destroyAllDrawables();
    clear();
  }
}

// ------------------------------------------------------------------------------


