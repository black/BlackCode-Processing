import oscP5.*;
import netP5.*;
import tuioZones.*;

TUIOzoneCollection zones;

void setup(){
  size(screen.width,screen.height);
  zones=new TUIOzoneCollection(this);
  zones.setZone("zone1", width/10,height/4,width/3,height/2);
  zones.setZoneParameter("zone1","DRAGGABLE",true);
  zones.setZoneParameter("zone1","SCALABLE",true);
  zones.assignZoneToGroup("zone1","group1");
  zones.setZone("zone2", width*6/10,height/4,width/3,height/2);
  zones.setZoneParameter("zone2","DRAGGABLE",true);
  zones.setZoneParameter("zone2","SCALABLE",true);
  zones.assignZoneToGroup("zone2","group1");
}

void draw(){
  background(0); 
  rect(zones.getZoneX("zone1"),zones.getZoneY("zone1"),zones.getZoneWidth("zone1"),zones.getZoneHeight("zone1"));
  fill (150); 
  rect(zones.getZoneX("zone2"),zones.getZoneY("zone2"),zones.getZoneWidth("zone2"),zones.getZoneHeight("zone2"));
}


