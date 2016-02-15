/*

This example shows a whole lot of different
tracking methods being rendered at one time.
Don't be surprised if this one runs really slowly.

 last tested to work in Processing 0090
 
 JTNIMOY
 
*/

import JMyron.*;

JMyron m;
 
void setup(){
  int w = 320;
  int h = 240;
  
  size(w,h);
  m = new JMyron();
  m.start(320,240);
  m.findGlobs(1);
  println("Myron " + m.version());
}

void mousePressed(){
  m.settings();
}

void draw(){
  m.trackColor(255,255,255,255);

  m.update();
  int[] img = m.image();
  
  //first draw the camera view onto the screen
  loadPixels();
  for(int i=0;i<width*height;i++){
      pixels[i] = img[i];
  }
  updatePixels();
  
  
  //draw an averaged color block where the mouse is.
  noStroke();
  int c = m.average(mouseX-20,mouseY-20,mouseX+20,mouseY+20);
  fill(red(c),green(c),blue(c));
  rect(mouseX-20,mouseY-20,40,40);

  noFill();
  int[][] a;


  //draw center points of globs
  a = m.globCenters();
  stroke(255,255,0);
  for(int i=0;i<a.length;i++){
    int[] p = a[i];
    point(p[0],p[1]);
  }
 

  //draw bounding boxes of globs
  a = m.globBoxes();
  stroke(255,0,0);
  for(int i=0;i<a.length;i++){
    int[] b = a[i];
    rect(b[0], b[1], b[2], b[3]);
  }


  //draw edge pixels of globs (this and the next chunks of code are chokers)
  int list[][][] = m.globPixels();
  stroke(110,110,110);
  
  for(int i=0;i<list.length;i++){
    int[][] pixellist = list[i];
    if(pixellist!=null){
      beginShape(POINTS);
      for(int j=0;j<pixellist.length;j++){    
        vertex( pixellist[j][0]  ,  pixellist[j][1] );
       // print( pixellist[j][0]  +" " +  pixellist[j][1] );
      }
      endShape();
     }
  }

  //draw edge points (same as last, but vector based)
  list = m.globEdgePoints(20);
  stroke(0,128,0);
  for(int i=0;i<list.length;i++){
    int[][] contour = list[i];
    if(contour!=null){
      beginShape(LINE_LOOP);
      for(int j=0;j<contour.length;j++){    
        vertex( contour[j][0]  ,  contour[j][1] );
      }
      endShape();
     }
  }

  //draw quads - like bounding box, but a 4-pointed polygon.
  a = m.globQuads(50,51);
  stroke(0,0,100);
  for(int i=0;i<a.length;i++){
    int[] b = a[i];
    quad(b[0], b[1],
         b[2], b[3],
         b[4], b[5],
         b[6], b[7]);
  }



}

public void stop(){
  m.stop();
  super.stop();
}

