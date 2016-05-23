/////////////////////////////////////////////////////////////////////////////////////////////////////
// PointDrawer keeps track of the handpoints

class PointDrawer extends XnVPushDetector
{
  HashMap    _pointLists;
  int        _maxPoints;
  color[]    _colorList = { 
    color(255, 0, 0), color(0, 255, 0), color(0, 0, 255), color(255, 255, 0)
  };

  int pushed = 0;

  public PointDrawer()
  {
    _maxPoints = 30;
    _pointLists = new HashMap();

    RegisterPush(this);
  }




  public void OnPointCreate(XnVHandPointContext cxt)
  {
    // create a new list
    addPoint(cxt.getNID(), new PVector(cxt.getPtPosition().getX(), cxt.getPtPosition().getY(), cxt.getPtPosition().getZ()));

    println("onPointCreate " + cxt.getFTime() +":"+cxt.getNUserID()+"("+cxt.getNID()  +") "+ 
      cxt.getPtPosition().getX() + " " +
      cxt.getPtPosition().getY()+" " +
      cxt.getPtPosition().getZ());
  }

  public void OnPointUpdate(XnVHandPointContext cxt)
  {
    /*
      println("onPointUpdate "  + cxt.getFTime() +":"+cxt.getNUserID()+"("+cxt.getNID()  +") "+ 
     cxt.getPtPosition().getX() + " " +
     cxt.getPtPosition().getY()+" " +
     cxt.getPtPosition().getZ());
     */
    //println("OnPointUpdate " + cxt.getPtPosition());   
    addPoint(cxt.getNID(), new PVector(cxt.getPtPosition().getX(), cxt.getPtPosition().getY(), cxt.getPtPosition().getZ()));
  }

  public void OnPointDestroy(long nID)
  {
    println("OnPointDestroy, handId: " + nID);

    // remove list
    if (_pointLists.containsKey(nID))
      _pointLists.remove(nID);
  }

  void onPush(float vel, float angle) {
    println(">>>>>>>>> PUSH v:" + vel + "a: " + angle);
    pushed = 10;
    //   println("ID = " +  GetPrimaryID()+" immduration: " + GetPushImmediateDuration()+" immoffset: "+GetPushImmediateOffset()  );
  }

  public ArrayList getPointList(long handId)
  {
    ArrayList curList;
    if (_pointLists.containsKey(handId))
      curList = (ArrayList)_pointLists.get(handId);
    else
    {
      curList = new ArrayList(_maxPoints);
      _pointLists.put(handId, curList);
    }
    return curList;
  }

  public void addPoint(long handId, PVector handPoint)
  {
    ArrayList curList = getPointList(handId);

    curList.add(0, handPoint);      
    if (curList.size() > _maxPoints)
      curList.remove(curList.size() - 1);
  }

  public void draw()
  {
   
    if (_pointLists.size() <= 0)
      return;

    pushStyle();
    noFill();

    PVector vec;
    PVector firstVec;
    PVector screenPos = new PVector();
    int colorIndex=0;

    // draw the hand lists
    Iterator<Map.Entry> itrList = _pointLists.entrySet().iterator();
    while (itrList.hasNext ()) 
    {
      strokeWeight(2);
      stroke(_colorList[colorIndex % (_colorList.length - 1)]);

      ArrayList curList = (ArrayList)itrList.next().getValue();     

      // draw line
      firstVec = null;
      Iterator<PVector> itr = curList.iterator();
      beginShape();
      while (itr.hasNext ()) 
      {
        vec = itr.next();
        if (firstVec == null)
          firstVec = vec;
        // calc the screen pos
        kinect.convertRealWorldToProjective(vec, screenPos);
        vertex(screenPos.x, screenPos.y);
      } 
      endShape();   

      // draw current pos of the hand
      if (firstVec != null)
      {
        strokeWeight(8);
        kinect.convertRealWorldToProjective(firstVec, screenPos);
        point(screenPos.x, screenPos.y);


       

        if (pushed > 0) {
          rect(screenPos.x, screenPos.y, 10, 10);
          pushed --;
        }
      }
      colorIndex++;
    }

    popStyle();
  }
}

