
class Bands{
  PVector[] map3D;
  PImage rgbImg;
  boolean[] consImg; 
  PVector[] vel;
  int xSize;
  int ySize;
  int nPixels;
    
  Bands(PVector[] tempMap3D, PImage tempRGBimg, boolean[] tempConsImg, int tempXsize, int tempYsize){
    map3D = tempMap3D;
    rgbImg = tempRGBimg;
    consImg = tempConsImg;
    xSize = tempXsize;
    ySize = tempYsize;
    nPixels = consImg.length;
    vel = new PVector[nPixels];
    for(int i = 0; i < nPixels; i++){
      vel[i] = new PVector(0,0,0);
    }
  }
  
  void paint(color col, int gaps){
    noStroke();
    fill(col);
    PVector p, lp;
    int index, indexl;
    boolean started = false;
    PVector prevPoint = new PVector(0,0,0);
    float maxSep = 50;
    
    for(int y = 0; y < ySize-1; y+=(1+gaps)){
      for(int x = 0; x < xSize; x++){
        index = x + y*xSize;
        if(consImg[index]){
          p = map3D[index];
          if(!started){
            beginShape(TRIANGLE_STRIP);
            vertex(p.x,p.y,p.z);
            prevPoint.set(p);
            started = true;
          }
          else if(prevPoint.dist(p) < maxSep){  
            vertex(p.x,p.y,p.z);         
            prevPoint.set(p);
          }
          else{
            endShape();
            started = false;
            x--;
            continue;
          }           
          indexl = x + (y+1)*xSize;
          lp = map3D[indexl];
          if(consImg[indexl] && (p.dist(lp) < maxSep)){
            vertex(lp.x,lp.y,lp.z);
            if(x == xSize-1){
              endShape();
              started = false;
            }
          } 
          else{
            vertex(p.x,p.y,p.z);
            endShape();
            started = false;
          }
        }
        else if(started){
          endShape();
          started = false;
        }
      }
    }
  }
  
  void paint(int gaps){
    noStroke();
    PVector p, lp;
    int index, indexl;
    boolean started = false;
    PVector prevPoint = new PVector(0,0,0);
    float maxSep = 50;

    for(int y = 0; y < ySize-1; y+=(1+gaps)){
      for(int x = 0; x < xSize; x++){
        index = x + y*xSize;
        if(consImg[index]){
          p = map3D[index];
          fill(rgbImg.pixels[index]);
          if(!started){
            beginShape(TRIANGLE_STRIP);
            vertex(p.x,p.y,p.z);
            prevPoint.set(p);
            started = true;
          }
          else if(prevPoint.dist(p) < maxSep){  
            vertex(p.x,p.y,p.z);         
            prevPoint.set(p);
          }
          else{
            endShape();
            started = false;
            x--;
            continue;
          }           
          indexl = x + (y+1)*xSize;
          lp = map3D[indexl];
          if(consImg[indexl] && (p.dist(lp) < maxSep)){
            vertex(lp.x,lp.y,lp.z);
            if(x == xSize-1){
              endShape();
              started = false;
            }
          } 
          else{
            vertex(p.x,p.y,p.z);
            endShape();
            started = false;
          }
        }
        else if(started){
          endShape();
          started = false;
        }
      }
    }
  }
  
  void disolve(float yMin){
    PVector g = new PVector(0,-15,0);

    for(int i = 0; i < nPixels; i++){
      if(consImg[i] && ((map3D[i].y + vel[i].y) > yMin)){
        map3D[i].add(vel[i]);
        vel[i].add(g);
        //map3D[i].add(vel[i]);
        //vel[i].set(new PVector(0,map(p.y,yMin,900,-40,-4),0));
      }
    }
  }
  
  void explode(){
    PVector explodeAcc = new PVector(0,0,-10);

    for(int i = 0; i < nPixels; i++){
      if(consImg[i]){
        map3D[i].add(vel[i]);
        vel[i].add(explodeAcc);
      }
    }
  }
  
}

