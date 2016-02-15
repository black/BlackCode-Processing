import integralhistogram.*;

PImage imgSource;

void setup()
{ 
  int bit = 2;
  
  imgSource       = loadImage("cats.jpg");
  PImage imgTarg  = loadImage("cats2.jpg");
  
  TargetUser targetImg = new MyTargetUser(imgTarg,bit,1,0.022);
  targetImg.setOutputImage(imgSource);
  
  size(imgSource.width*2+10+imgTarg.width,imgSource.height+10);
  image(imgSource, 0, 0);

  IntHistUser test = new MyIntHistUser(imgSource,bit);
  
  maskResult();
  
  test.imageSearch(targetImg);
  
  imgSource.updatePixels();
  
  image(imgTarg, imgSource.width+4, 0);
  image(imgSource, imgSource.width+4 + imgTarg.width+4,0);
}


void maskResult()
{
  for(int i=0;i<imgSource.width*imgSource.height;i++)
    imgSource.pixels[i] = 0|0|(imgSource.pixels[i])&0xFF;
}


