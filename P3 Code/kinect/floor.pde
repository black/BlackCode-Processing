
void drawFloor(color col, float xMin, float xMax, float yMin, float yMax, float zMin, float zMax){
  float deltaX = (xMax-xMin);
  float deltaZ = (zMax-zMin);

  noStroke();
  fill(col); 
  beginShape();
    vertex(xMin-0.1*deltaX,yMin,zMin-0.1*deltaZ);
    vertex(xMin-0.1*deltaX,yMin,zMax+0.1*deltaZ);
    vertex(xMax+0.1*deltaX,yMin,zMax+0.1*deltaZ);
    vertex(xMax+0.1*deltaX,yMin,zMin-0.1*deltaZ);
  endShape(CLOSE);
}

void drawGrid(color col, float xMin, float xMax, float yMin, float yMax, float zMin, float zMax){
  int steps = 10;
  float deltaX = (xMax-xMin)/steps;
  float deltaZ = (zMax-zMin)/steps;

  smooth();
  strokeWeight(1);
  stroke(col);
  for(int i = 0; i <= steps; i++){
    line(xMin+i*deltaX,yMin+10,zMin,xMin+i*deltaX,yMin+10,zMax);
    line(xMin,yMin+10,zMin+i*deltaZ,xMax,yMin+10,zMin+i*deltaZ);
  }
  noStroke();
  noSmooth();
}

