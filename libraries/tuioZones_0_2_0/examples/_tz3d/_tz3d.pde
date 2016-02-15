import tuioZones.*;
TUIOzoneCollection zones;
import oscP5.*;
import netP5.*;

void setup() 
{
  size(screen.width, screen.height, P3D);
  zones=new TUIOzoneCollection(this);
  zones.setZoneParameter("canvas","WINDOW3D", true);//'canvas' is the default zone filling the screen
}

void draw() 
{
  background(255);
  fill(150);
  pushMatrix();
  zones.applyZone3dMatrix("canvas");
  box(300*zones.getZoneScale("canvas"));//scale gesture results are stored even though the zone is not set to scalable
  popMatrix();
}
