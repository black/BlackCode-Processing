class PointDrawer extends XnVPointControl
{
  HashMap    _pointLists;
  int        _maxPoints;
  color[]    _colorList = { 
    color(255, 0, 0), color(0, 255, 0), color(0, 0, 255), color(255, 255, 0)
  };

  public PointDrawer()
  {
    _maxPoints = 30;
    _pointLists = new HashMap();
  }

  public void OnPointCreate(XnVHandPointContext cxt)
  {
    // create a new list
    addPoint(cxt.getNID(), new PVector(cxt.getPtPosition().getX(), cxt.getPtPosition().getY(), cxt.getPtPosition().getZ()));
    println("OnPointCreate, handId: " + cxt.getNID());
  }

  public void OnPointUpdate(XnVHandPointContext cxt)
  {
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
    if (_pointLists.size() <= 0) return;
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
        context.convertRealWorldToProjective(vec, screenPos);
        vertex(screenPos.x, screenPos.y);
      } 
      endShape();   

      // draw current pos of the hand
      if (firstVec != null)
      {
        strokeWeight(8);
        context.convertRealWorldToProjective(firstVec, screenPos);
        point(screenPos.x, screenPos.y);
      }
      colorIndex++;
    }

    popStyle();
  }
}

