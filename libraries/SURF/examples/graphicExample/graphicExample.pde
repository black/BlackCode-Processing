import surf.*;

ArrayList interest_points;
float threshold = 640;
float balanceValue = 0.9;
int octaves = 4;

void setup(){
  
  PImage img = loadImage("graffiti.png");
  size(img.width,img.height);
  image(img, 0, 0);
  loadPixels();
    
  SURF_Factory mySURF = SURF.createInstance(img, balanceValue, threshold, octaves, this);
  Detector detector = mySURF.createDetector();
  interest_points = detector.generateInterestPoints();
  Descriptor descriptor = mySURF.createDescriptor(interest_points);
  descriptor.generateAllDescriptors();
  drawInterestPoints();
  //drawDescriptors();
  
}

void drawInterestPoints(){
    
    println("Drawing Interest Points...");
    
    for(int i = 0; i < interest_points.size(); i++){
      
      Interest_Point IP = (Interest_Point) interest_points.get(i);
      IP.drawPosition();
      
    }
    
}
  
void drawDescriptors(){
   
    println("Drawing Descriptors...");
    
    for(int i = 0; i < interest_points.size(); i++){
      
      Interest_Point IP = (Interest_Point) interest_points.get(i);
      IP.drawDescriptor();
      
     }
  
}
