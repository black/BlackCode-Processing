
void calculateLimits(int[] depthImg, PVector[] map3D){
  float xMin, xMax, yMin, yMax, zMin, zMax;
  xMin = yMin = zMin = 10000;
  xMax = yMax = zMax = -10000;
  
  for(int i = 0; i < depthImg.length; i++){
    if(depthImg[i] > 0){
      PVector p = map3D[i];
      if(p.x < xMin){
        xMin = p.x;
      }
      if(p.x > xMax){
        xMax = p.x;
      }
      if(p.y < yMin){
        yMin = p.y;
      }
      if(p.y > yMax){
        yMax = p.y;
      }
      if(p.z < zMin){
        zMin = p.z;
      }
      if(p.z > zMax){
        zMax = p.z;
      }
    } 
  }
  xmin = xMin - 0.1*(xMax - xMin);
  xmax = xMax + 0.1*(xMax - xMin);
  ymin = yMin - 0.1*(yMax - yMin);
  ymax = yMax + 0.1*(yMax - yMin);
  zmin = zMin - 0.5*(zMax - zMin);
  zmax = zMax + 0.1*(zMax - zMin);
}  

int[] resizeDepth(int[] depthImg, int n){
  int xSizeOrig = context.depthWidth();
  int ySizeOrig = context.depthHeight();
  int xSize = xSizeOrig/n;
  int ySize = ySizeOrig/n;
  int[] resDepthImg = new int[xSize*ySize];

  for(int y = 0; y < ySize; y++){
    for(int x = 0; x < xSize; x++){
      resDepthImg[x + y*xSize] = depthImg[x*n + y*n*xSizeOrig];
    }
  }
  return resDepthImg;
}

PVector[] resizeMap3D(PVector[] map3D, int n){
  int xSizeOrig = context.depthWidth();
  int ySizeOrig = context.depthHeight();
  int xSize = xSizeOrig/n;
  int ySize = ySizeOrig/n;
  PVector[] resMap3D = new PVector[xSize*ySize];

  for(int y = 0; y < ySize; y++){
    for(int x = 0; x < xSize; x++){
      resMap3D[x + y*xSize] = map3D[x*n + y*n*xSizeOrig].get();
    }
  }
  return resMap3D;
}

PImage resizeRGB(PImage rgbImg, int n){
  int xSizeOrig = context.depthWidth();
  int ySizeOrig = context.depthHeight();
  int xSize = xSizeOrig/n;
  int ySize = ySizeOrig/n;
  PImage resRGB = createImage(xSize,ySize,RGB);
 
  for(int y = 0; y < ySize; y++){
    for(int x = 0; x < xSize; x++){
      resRGB.pixels[x + y*xSize] = rgbImg.pixels[x*n + y*n*xSizeOrig];
    }
  }
  return resRGB; 
}

boolean[] constrainImg(int[] depthImg, PVector[] map3D, float xMin, float xMax, float yMin, float yMax, float zMin, float zMax){
  boolean[] consImg = new boolean[depthImg.length];

  for(int i = 0; i < consImg.length; i++){
    PVector p = map3D[i];
    consImg[i] = (depthImg[i] > 0) && (p.x > xMin) && (p.x < xMax) && (p.y > yMin) && (p.y < yMax) && (p.z > zMin) && (p.z < zMax);
  } 
  return consImg; 
}  

