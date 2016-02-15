import oscP5.*;
import netP5.*;
import tuioZones.*;

TUIOzoneCollection zones;

void setup(){
  size(screen.width,screen.height);
  zones=new TUIOzoneCollection(this);
  zones.setZone("zone1", width/2,height/2,200,200);
  zones.setZoneParameter("zone1","DRAGGABLE",true);
  zones.setZoneParameter("zone1","THROWABLE",true);
  fill (150);
}

void draw(){
  background(0);
  rect(zones.getZoneX("zone1"),zones.getZoneY("zone1"),zones.getZoneWidth("zone1"),zones.getZoneHeight("zone1"));
}


