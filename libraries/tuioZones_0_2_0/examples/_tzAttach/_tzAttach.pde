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
  zones.setZone("subZone1", width*6/10,height/4,width/3,height/2);
  zones.setZoneParameter("subZone1","DRAGGABLE",true);
  zones.setZoneParameter("subZone1","SCALABLE",true);
  zones.attachZoneTo("subZone1","zone1");
}

void draw(){
  background(0); 
  rect(zones.getZoneX("zone1"),zones.getZoneY("zone1"),zones.getZoneWidth("zone1"),zones.getZoneHeight("zone1"));
  fill (150); 
  rect(zones.getZoneX("subZone1"),zones.getZoneY("subZone1"),zones.getZoneWidth("subZone1"),zones.getZoneHeight("subZone1"));
}


