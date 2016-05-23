class Line{
  public float x1,y1, x2,y2,m;
  public boolean vertical;
  
  Line(float x1, float y1, float x2, float y2){
    //Begin point
    this.x1=x1;
    this.y1=y1;
    //End point
    this.x2=x2;
    this.y2=y2;
    
    //Dunno if I need this yet, we shall see
    vertical=false;
    
    //slope
    //If not vertical
    if(x2!=x1){
      m=(y2-y1)/(x2-x1);
    }
    else{
      vertical=true;
     
      //m=0;
    }
  }
  
  /**
  Based on the intersection of line segments algorithm 
  by Mukesh Prasad in Graphic Gems 2
  
  @returns true if intersects, false otherwise
  */
  boolean intersect(float a1,float b1,float a2,float b2){
    boolean intersection =false;
    
    //If i) a1,b1 in the line's formula !=0 (r1)
    //ii) a2,b2 in the line's formula !=0 (r2)
    //iii) the signs of r1 and r2 are the same
    //Then lines do not intersect
    float r1 = linesFormula(a1,b1);
    float r2 = linesFormula(a2,b2);
    if(r1!=0 && r2!=0 && sameSign(r1,r2)){
      intersection=false;
    }
    else{
      //If the above failed, we have to check the second part of the algorithm
      Line otherLine = new Line(a1, b1, a2, b2);
      
      //If i)x1,y1, in the other line's formula !=0 (r3)
      //ii) x2,y2 in the other line's formula !=0 (r4)
      //iii) the signs of r3 and r4 are the same
      // Then the lines do not intersect
      float r3 = otherLine.linesFormula(x1,y1);
      float r4 = otherLine.linesFormula(x2,y2);
      if(r3!=0 && r4!=0 && sameSign(r3,r4)){
        intersection=false;
      }
      else{
        intersection=true;
      }
    }
    
    if(a1==x1 && b1==y1){
      intersection=false;
    }
    
    
    return intersection;
  }
  
  //Plugs two points into this line's formula
  float linesFormula(float a, float b){
    float answer=0.0f;
    //if(!vertical){
      answer = (m*(x1-a))-(y1-b);
    //}
    //else{
    //  answer = y1-b;
    //}
    return answer;
  }
  
  //Returns true if same sign (both positive or negative) false otherwise
  boolean sameSign(float a, float b){
    boolean sameSign = false;
    if(a>0){
      if(b>0){
        sameSign=true;
      }
    }
    else{
      if(b<0){
        sameSign=true;
      }
    }
    return sameSign;
  }
  
  /** 
  Determines if point occurs between line's
  start and end points
  */
  boolean collinear(float p, float q){
   PVector a = new PVector(x1,y1);
   PVector c = new PVector (p,q);
   PVector b = new PVector (x2,y2);
   
   
   float crossProduct = (c.y - a.y) * (b.x - a.x) - (c.x - a.x) * (b.y - a.y);

   if(crossProduct!=0){
     return false;
   }
   float dotProduct= (c.x - a.x) * (b.x - a.x) + (c.y - a.y)*(b.y - a.y);
   if(dotProduct<=0){
     return false;
   }
   float squareLength = (b.x - a.x)*(b.x - a.x) + (b.y - a.y)*(b.y - a.y);
   if(dotProduct>squareLength){
     return false;
   }
   
   return true;
  }
  
  String toString(){
    String result = ("Line from point ("+x1+","+y1+") to point ("+x2+","+y2+")");
    return result;
  }
}
