class BoundingBox{
  //The numbers denote the points the line go between
  Line line12, line23, line34, line41;
  Line[] lines;
  
  /**
  Constructor for the BoundingBox, takes four points, creates
  four lines to make up the bounding box from these points
  
  */
  BoundingBox(PVector one, PVector two, PVector three, PVector four){
    line12= new Line(one.x,one.y,two.x,two.y);
    line23 = new Line(two.x,two.y,three.x,three.y);
    line34=new Line(three.x,three.y,four.x,four.y);
    line41=new Line(four.x,four.y,one.x,one.y);
    
    //Sets up array
    lines = new Line[4];
    lines[0]=line12;
    lines[1]=line23;
    lines[2]=line34;
    lines[3]=line41;
  }
  
  //Resets all values for the BoundingBox
  public void reset(PVector one, PVector two, PVector three, PVector four){
    line12= new Line(one.x,one.y,two.x,two.y);
    line23 = new Line(two.x,two.y,three.x,three.y);
    line34=new Line(three.x,three.y,four.x,four.y);
    line41=new Line(four.x,four.y,one.x,one.y);
    
    //println("line12: "+line12.toString());  
    //println("line23: "+line23.toString()); 
    //println("line34: "+line34.toString());   
    //println("line41: "+line41.toString());
    
    //Sets up array
    //lines = new Line[4];
    lines[0]=line12;
    lines[1]=line23;
    lines[2]=line34;
    lines[3]=line41;
  }
  
  
  void drawBox(){
    stroke(255,255,255);
    strokeWeight(10);
    for(int i =0; i<4; i++){
      line(lines[i].x1, lines[i].y1, lines[i].x2,lines[i].y2);
    }
  }
  
  //Checks if point is within, based on how many times it intersects
  //a line of the bounding box. Even=not within, odd=within
  boolean contains(float a1, float b1, float a2, float b2){
     boolean within=false;
     int intersections=0;
     for(int i = 0; i<4; i++){
       if(lines[i].intersect(a1, b1, a2, b2)){
         intersections++;
       }
     }
     
     
     if(intersections!=0){
       if(intersections%2!=0){
         within=true;
       }
     }
     return within;
  }
  
  
  
  float getMinX(){
    float mini = line12.x1;
    
    for(int i =0; i<4; i++){
      if(lines[i].x1<mini){
        mini=lines[i].x1;
      }
      if(lines[i].x2<mini){
        mini=lines[i].x2;
      }
    }
    
    return mini;
  }
  
  float getMinY(){
    float mini = line12.y1;
    
    for(int i =0; i<4; i++){
      if(lines[i].y1<mini){
        mini=lines[i].y1;
      }
      if(lines[i].y2<mini){
        mini=lines[i].y2;
      }
    }
    
    return mini;
  }
  
  float getMaxX(){
    float maxi = line12.x1;
    
    for(int i =0; i<4; i++){
      if(lines[i].x1>maxi){
        maxi=lines[i].x1;
      }
      if(lines[i].x2>maxi){
        maxi=lines[i].x2;
      }
    }
    
    return maxi;
  }
  
  float getMaxY(){
    float maxi = line12.y1;
    
    for(int i =0; i<4; i++){
      if(lines[i].y1>maxi){
        maxi=lines[i].y1;
      }
      if(lines[i].y2>maxi){
        maxi=lines[i].y2;
      }
    }
    
    return maxi;
  }
  
  public void printBox(){
    for(int i =0; i<4; i++){
      println("Line"+i+": "+lines[i].toString());
    }
  }
}
