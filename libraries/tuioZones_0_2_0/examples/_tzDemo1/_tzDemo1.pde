import oscP5.*;
import netP5.*;
import tuioZones.*;

TUIOzoneCollection zones;
PImage b;

void setup(){
  size(screen.width,screen.height);
  // create a draggable, throwable, and scalable zone for a photo
  zones=new TUIOzoneCollection(this);
  zones.setZone("zone1", width/2,height/2,400,300);
  zones.setZoneParameter("zone1","DRAGGABLE",true);
  zones.setZoneParameter("zone1","THROWABLE",true);
  zones.setZoneParameter("zone1","SCALABLE",true);
  b = loadImage("photo.jpg");
  noFill();
  smooth();
}

void draw(){
  background(0);
  // use the zone coordinates and size to display the image
  image(b, zones.getZoneX("zone1"),zones.getZoneY("zone1"),zones.getZoneWidth("zone1"),zones.getZoneHeight("zone1"));
  // outline photo when pressed
  if (zones.isZonePressed("zone1")) {
    stroke(200,200,0);
    strokeWeight(4);
    rect(zones.getZoneX("zone1"),zones.getZoneY("zone1"),zones.getZoneWidth("zone1"),zones.getZoneHeight("zone1"));
  }
  // draw the touch trails for testing hardware calibration
  /*int[][] coord=zones.getPoints();
  stroke(100,100,100);
  strokeWeight(1);
  if (coord.length>0){
    for (int i=0;i<coord.length;i++){
      ellipse(coord[i][0],coord[i][1],20,20);
      int [][] trail=zones.getTrail(coord[i][2]);
      if (trail.length>1){
        for (int j=1;j<trail.length;j++){
          line(trail[j][0],trail[j][1],trail[j-1][0],trail[j-1][1]);
          ellipse(trail[j][0],trail[j][1],5,5);
          println(trail[j][2] + "," + trail[j][3]);
        }
      }
    }
  }
  */

}




