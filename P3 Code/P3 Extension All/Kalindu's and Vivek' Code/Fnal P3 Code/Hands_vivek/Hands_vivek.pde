import SimpleOpenNI.*;
import java.awt.Toolkit;
import java.util.*;

SimpleOpenNI      context;
//color c = color(255);
ArrayList<Integer> c;
// NITE
XnVSessionManager sessionManager;
XnVFlowRouter     flowRouter;

PointDrawer       pointDrawer;

void setup()
{
  context = new SimpleOpenNI(this);
   
  // mirror is by default enabled
  context.setMirror(true);
  
  // enable depthMap generation 
  if(context.enableDepth() == false)
  {
     println("Can't open the depthMap, maybe the camera is not connected!"); 
     exit();
     return;
  }
  
  // enable the hands + gesture
  context.enableGesture();
  context.enableHands();
  context.enableRGB();
  // setup NITE 
  sessionManager = context.createSessionManager("Click,Wave", "RaiseHand");

  pointDrawer = new PointDrawer();
  flowRouter = new XnVFlowRouter();
  flowRouter.SetActive(pointDrawer);
  
  sessionManager.AddListener(flowRouter);
           
  size(context.rgbWidth(), context.rgbHeight()); 
  smooth();
  c=new ArrayList<Integer>();
}

void draw()
{
  background(200,0,0);
  // update the cam
  context.update();
  
  // update nite
  context.update(sessionManager);
  
  // draw depthImageMap
  image(context.rgbImage(),0,0);
  
  // draw the list
  pointDrawer.draw();
}

void keyPressed()
{
  switch(key)
  {
  case 'e':
    // end sessions
    sessionManager.EndSession();
    println("end session");
    break;
  }
}

/////////////////////////////////////////////////////////////////////////////////////////////////////
// session callbacks

void onStartSession(PVector pos)
{
  println("onStartSession: " + pos);
}

void onEndSession()
{
  println("onEndSession: ");
}

void onFocusSession(String strFocus,PVector pos,float progress)
{
  println("onFocusSession: focus=" + strFocus + ",pos=" + pos + ",progress=" + progress);
}


/////////////////////////////////////////////////////////////////////////////////////////////////////
// PointDrawer keeps track of the handpoints

class PointDrawer extends XnVPointControl
{
  HashMap    _pointLists;
  int        _maxPoints;
  color[]    _colorList = { color(255,0,0),color(0,255,0),color(0,0,255),color(255,255,0)};
  
  public PointDrawer()
  {
    _maxPoints = 30;
    _pointLists = new HashMap();
  }
	
  public void OnPointCreate(XnVHandPointContext cxt)
  {
    // create a new list
    addPoint(cxt.getNID(),new PVector(cxt.getPtPosition().getX(),cxt.getPtPosition().getY(),cxt.getPtPosition().getZ()));
    println("OnPointCreate, handId: " + cxt.getNID());
  }
  
  public void OnPointUpdate(XnVHandPointContext cxt)
  {
    //println("OnPointUpdate " + cxt.getPtPosition());   
    addPoint(cxt.getNID(),new PVector(cxt.getPtPosition().getX(),cxt.getPtPosition().getY(),cxt.getPtPosition().getZ()));
  }
  
  public void OnPointDestroy(long nID)
  {
    println("OnPointDestroy, handId: " + nID);
    // remove list
    if(_pointLists.containsKey(nID))
       _pointLists.remove(nID);
  }
  
  public ArrayList getPointList(long handId)
  {
    ArrayList curList;
    if(_pointLists.containsKey(handId))
      curList = (ArrayList)_pointLists.get(handId);
    else
    {
      curList = new ArrayList(_maxPoints);
      _pointLists.put(handId,curList);
    }
    return curList;  
  }
  
  public void addPoint(long handId,PVector handPoint)
  {
    ArrayList curList = getPointList(handId);
    
    curList.add(0,handPoint);      
    if(curList.size() > _maxPoints)
      curList.remove(curList.size() - 1);
  }
  
  public void draw()
  {
    if(_pointLists.size() <= 0)
      return;
      
    pushStyle();
      noFill();
      
      PVector vec;
      PVector firstVec;
      PVector screenPos = new PVector();
      int colorIndex=0;
      
      // draw the hand lists
      Iterator<Map.Entry> itrList = _pointLists.entrySet().iterator();
      while(itrList.hasNext()) 
      {
        strokeWeight(2);
        stroke(_colorList[colorIndex % (_colorList.length - 1)]);
        ArrayList curList = (ArrayList)itrList.next().getValue();     
        
        // draw line
        firstVec = null;
        Iterator<PVector> itr = curList.iterator();
        //beginShape();
          while (itr.hasNext()) 
          {
            vec = itr.next();
            if(firstVec == null)
              firstVec = vec;
            // calc the screen pos
            //context.convertRealWorldToProjective(vec,screenPos);
            //vertex(screenPos.x,screenPos.y);    
          } 
        //endShape();   
  
        // draw current pos of the hand
        if(firstVec != null)
        {
          strokeWeight(8);
          context.convertRealWorldToProjective(firstVec,screenPos);
          point(screenPos.x,screenPos.y);
          /*---------------------------------Initialise---------------------------------*/
          int[] depthValues = context.depthMap();
          int val=50;int tol=100;float mm;int pos=0;     //change parameter
          PImage rgbValues = context.rgbImage();
          rgbValues.loadPixels();
          /*---------------------------------One Point-----------------------------------*/
          /*pos=(int)screenPos.x+val+(int)(screenPos.y+val)*context.rgbWidth();
          mm=depthValues[pos];
          println(screenPos.z+"//"+mm);
          strokeWeight(8);
          stroke(255,255,0);
          point(screenPos.x+val,screenPos.y+val);
          if(abs(screenPos.z-mm)<tol){
            c=rgbValues.pixels[pos];
            println("Color Picked!");
            Toolkit.getDefaultToolkit().beep();
            delay(100);
          }*/
          /*---------------------------------Four Points-----------------------------------*/
          int count=0,i,j;
          for (i=0;i<2;i++){
            for (j=0;j<2;j++){
              mm=depthValues[(int)screenPos.x+(int)pow(-1,i)*val+((int)screenPos.y+(int)pow(-1,j)*val)*context.rgbWidth()];
              if(abs(firstVec.z-mm)<tol){
                count=count+1;
                pos=(int)screenPos.x+(int)pow(-1,i)*val+((int)screenPos.y+(int)pow(-1,j)*val)*context.rgbWidth();
              }
                strokeWeight(8);
                stroke(255,255,0);
                point((int)screenPos.x+(int)pow(-1,i)*val,(int)screenPos.y+(int)pow(-1,j)*val);
                print(mm+"  ");
            }
          }
          println(firstVec.z+"//"+screenPos.z+"  "+count);
          if(count>2){
            c.set(colorIndex,rgbValues.pixels[pos]);
            println("Color Picked!");
            Toolkit.getDefaultToolkit().beep();
            delay(100);
          }
          /*----------------------------------Draw-----------------------------------------*/
          strokeWeight(2);
          stroke(255);
          if(c.size()<colorIndex+1)c.add(colorIndex,#FFFFFF);
          fill(c.get(colorIndex));
          rect((colorIndex*150)%context.rgbWidth(), 0, 100, 100);
        }
        colorIndex++;
      }
    popStyle();
  }
}
