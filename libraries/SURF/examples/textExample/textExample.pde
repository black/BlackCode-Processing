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
  makeResultsFile("SURF_ResultsWithNewDescriptor");
  
}

public void makeResultsFile(String outputFilename){
    
    PrintWriter output = createWriter(outputFilename+".txt");
    output.println("64");
    for(int i = 0; i < interest_points.size(); i++){
      
      Interest_Point IP = (Interest_Point) interest_points.get(i);
      output.println(IP.getInterestPointInformationAsAString());
      output.flush();
      
    }
    output.println("#END#");
    output.println("Time elapsed: " + millis() + " ms");
    output.close();
    
}
