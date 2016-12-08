import peasy.*;
PeasyCam cam;
float noiseScale= .12;
int meshSize = 10;
int resX = 50;
int resY = 50;
float[][] val = new float[resX][resY]; 
void setup() {  
  size(900, 600, P3D);  
  smooth();  
  background(255);  
  cam = new PeasyCam(this, 0, 0, 0, 600);
}
void draw() {  
  translate(-resX/2*meshSize, -resY/2*meshSize);  
  noiseScale= mouseX*.0002;  
  float xoff = 0.0;  
  for (int x =0; x<resX; x++) {    
    xoff +=noiseScale;    
    float yoff = 0.0;    
    for (int y =0; y<resY; y++) {      
      yoff +=noiseScale;      
      val[x][y] = noise(xoff, yoff)*255;
    }
  }  
  background(0);  
  for (int x =0; x<resX-1; x++) {    
    for (int y =0; y<resY-1; y++) {      
      beginShape();      
      colorMode(HSB, 255);      
      fill( val[x][y], 255, 255);      
      vertex(x*meshSize, y*meshSize, val[x][y] );      
      vertex((x+1)*meshSize, y*meshSize, val[x+1][y] );      
      vertex((x+1)*meshSize, (y+1)*meshSize, val[x+1][y+1] );      
      vertex(x*meshSize, (y+1)*meshSize, val[x][y+1] );      
      endShape(CLOSE);
    }
  }
}

