import oscP5.*;
import netP5.*;
import tuioZones.*;

TUIOzoneCollection zones;
color bk1, bk2;
int count;

void setup(){
  size(screen.width,screen.height);
  zones=new TUIOzoneCollection(this);
  zones.setZone("zone1", width/2,20,10,height-20);
  zones.setZoneParameter("zone1", "HSWIPEABLE", true);
  bk1=color(100,0,0);
  bk2=color(0,0,100);
  count=0;
  fill(255);
}

void draw(){
  if (count%2==0) background(bk1);  // alternate background color based on number of swipes
  else background(bk2);
  rect(zones.getZoneX("zone1"),zones.getZoneY("zone1"),zones.getZoneWidth("zone1"),zones.getZoneHeight("zone1"));
}

void hSwipeEvent(String zName){
  if (zName.equals("zone1")){  //don't really need the conditional here with 1 zone in use
    count++;
  }
}

