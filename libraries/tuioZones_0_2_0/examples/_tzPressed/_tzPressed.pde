import oscP5.*;
import netP5.*;
import tuioZones.*;

TUIOzoneCollection zones;

void setup(){
  size(screen.width,screen.height);
  zones=new TUIOzoneCollection(this);
  zones.setZone("zone1", width/2,height/2,200,200);
}

void draw(){
  background(0);
  if (zones.isZonePressed("zone1")) fill (255); //change brightness when zone is pressed
  else fill (100);
  rect(zones.getZoneX("zone1"),zones.getZoneY("zone1"),zones.getZoneWidth("zone1"),zones.getZoneHeight("zone1"));
}


