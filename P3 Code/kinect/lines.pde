
class ConnectedLines{
  PVector[] map3D;
  boolean[] consImg; 
  int xSize;
  int ySize;
    
  ConnectedLines(PVector[] tempMap3D, boolean[] tempConsImg, int tempXsize, int tempYsize){
    map3D = tempMap3D;
    consImg = tempConsImg;
    xSize = tempXsize;
    ySize = tempYsize;
  }
  
  void paint(color col){
    stroke(col);
    float maxSep = 50;
    
    for(int y = 0; y < ySize-1; y++){
      for(int x = 0; x < xSize-1; x++){
        if(consImg[x + y*xSize]){
          PVector p1 = map3D[x + y*xSize];
          if(consImg[x+1 + y*xSize]){
            PVector p2 = map3D[x+1 + y*xSize];
            if(p2.dist(p1) < maxSep){
              line(p1.x,p1.y,p1.z,p2.x,p2.y,p2.z);
            }
          }
          if(consImg[x + (y+1)*xSize]){
            PVector p3 = map3D[x + (y+1)*xSize];
            if(p3.dist(p1) < maxSep){
              line(p1.x,p1.y,p1.z,p3.x,p3.y,p3.z);
            }
          }
        }
      }
    }  
  }
  
}

