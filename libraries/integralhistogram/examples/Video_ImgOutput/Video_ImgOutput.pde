import processing.video.*;
import integralhistogram.*;

Movie myMovie;
PImage imgTarget,imgTarget2,imgTarget3,imgTarget4;

IntHistUser test;
TargetUser targetImg,targetImg2,targetImg3,targetImg4;

int bit = 2;
boolean first = true,stopRecord = false;
float timePos;
MovieMaker mm;

void setup() 
{
  size(420,255);
  background(230);

//  frameRate();
 
  myMovie = new Movie(this,"Test_160x120.mov");
  myMovie.read();
  
  
  mm = new MovieMaker(this, width, height, "Video_sample.mov",8, MovieMaker.RAW, MovieMaker.HIGH);
  

  imgTarget   = loadImage("postpay.jpg");
  imgTarget2  = loadImage("plectrum.jpg");
  imgTarget3  = loadImage("gripper.jpg");
  imgTarget4  = loadImage("colors.jpg");

  targetImg = new MyTargetUser(imgTarget,bit,8,0.1);
  targetImg.setOutputImage(myMovie);
  
  targetImg2 = new MyTargetUser(imgTarget2,bit,20,0.33);
  targetImg2.setOutputImage(myMovie);
  
  targetImg3 = new MyTargetUser(imgTarget3,bit,10,0.1);
  targetImg3.setOutputImage(myMovie);
  
  targetImg4 = new MyTargetUser(imgTarget4,bit,5,0.4);
  targetImg4.setOutputImage(myMovie);

  myMovie.play();
}

void draw()
{
  if(myMovie.available())
  {
    myMovie.pause();

    myMovie.read();

    test = new MyIntHistUser(myMovie,bit);
    
    background(230);
    image(myMovie,10,10);
    
    //maskResult();
    
    if(test.imageSearch(targetImg))
      image(imgTarget,220,150);
     
    if(test.imageSearch(targetImg2))
      image(imgTarget2,280,150);
     
    if(test.imageSearch(targetImg3))
      image(imgTarget3,325,150);
     
    if(test.imageSearch(targetImg4))
      image(imgTarget4,375,150);
   
    myMovie.updatePixels();

    image(myMovie,230,10);
    
    mm.addFrame();
  }
}
 
void maskResult()
{
  for(int i=0;i<myMovie.width*myMovie.height;i++)
    myMovie.pixels[i] = 0|0|(myMovie.pixels[i])&0xFF; 
}       

 void keyPressed()
 {
   if(key == ' ')
   {
     mm.finish();  // Finish the movie if space bar is pressed!
   }
 }






