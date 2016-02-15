import integralhistogram.*;

void setup()
{ 
  int bit = 2;
  
  PImage imgSource = loadImage("cats.jpg");
  PImage imgTarg   = loadImage("cats2.jpg");
  
  TargetUser targetImg = new MyTargetUser(imgTarg,bit,1,0.022);
  targetImg.setOutputFile("Test_FILE");
  targetImg.startOutputFile();
  
  IntHistUser test = new MyIntHistUser(imgSource,bit);
  
  test.imageSearch(targetImg);
  
  targetImg.endOutputFile();
  
  println("Output File Saved to your root directory!");
}

