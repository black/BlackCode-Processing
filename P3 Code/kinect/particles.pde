
class Particles{
  PVector[] map3D;
  PImage rgbImg;
  boolean[] consImg;
  PVector[] vel;
  int nPar;

  Particles(PVector[] tempMap3D, PImage tempRGBimg, boolean[] tempConsImg){
    map3D = tempMap3D;
    rgbImg = tempRGBimg;
    consImg = tempConsImg;
    nPar = map3D.length;
    vel = new PVector[nPar];
    for(int i = 0; i < nPar; i++){
      vel[i] = new PVector(0,0,0);
    }
  }
  
  void paint(int pSize){
    strokeWeight(pSize);

    for(int i = 0; i < nPar; i++){
      if(consImg[i]){
        PVector p = map3D[i];
        stroke(rgbImg.pixels[i]);
        point(p.x,p.y,p.z);
      }  
    }
  }
  
  void paint(int pSize, color col){
    strokeWeight(pSize);
    stroke(col);
    
    for(int i = 0; i < nPar; i++){
      if(consImg[i]){ 
        PVector p = map3D[i];
        point(p.x,p.y,p.z);
      }  
    }
  }
  
  void update(float yMin){
    PVector g = new PVector(0,-5,0);
    
    for(int i = 0; i < nPar; i++){
      if(consImg[i]){
        if((map3D[i].y + vel[i].y) > yMin){
          map3D[i].add(vel[i]);
          vel[i].add(g);
        }
        else{
          map3D[i].y = yMin;
        } 
      }
    }
  }
  
}

