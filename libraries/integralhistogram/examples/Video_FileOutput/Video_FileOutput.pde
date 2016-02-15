  
  import processing.video.*;
  import integralhistogram.*;
  
  Movie myMovie;
  PImage imgTarget,imgTarget2,imgTarget3,imgTarget4;
  int bit = 2;
  boolean first = true;
  boolean stopRecord = false;
  float timePos;
  IntHistUser test;
  TargetUser targetImg,targetImg2,targetImg3,targetImg4;
  //MovieMaker mm;
  
  void setup() 
  {
    size(430,260);
    background(230);
  
    frameRate(25);
  
    myMovie = new Movie(this,"Test.mov");
    myMovie.read();
    
    /*
    mm = new MovieMaker(this, width, height, "Video_sample.mov",8, MovieMaker.RAW, MovieMaker.HIGH);
    */
  
    imgTarget   = loadImage("postpay.jpg");
    imgTarget2  = loadImage("plectrum.jpg");
    imgTarget3  = loadImage("gripper.jpg");
    imgTarget4  = loadImage("colors.jpg");
    
    targetImg = new MyTargetUser(imgTarget,bit,8,0.1);
    targetImg.setOutputFile("FILE_1");
    targetImg.startOutputFile();
    
    targetImg2 = new MyTargetUser(imgTarget2,bit,20,0.33);
    targetImg2.setOutputFile("FILE_2");
    targetImg2.startOutputFile();
    
    targetImg3 = new MyTargetUser(imgTarget3,bit,10,0.1);
    targetImg3.setOutputFile("FILE_3");
    targetImg3.startOutputFile();

    targetImg4 = new MyTargetUser(imgTarget4,bit,5,0.4);
    targetImg4.setOutputFile("FILE_4");
    targetImg4.startOutputFile();
    
    println("Files writing started! Press 's' to finalize the process");
    
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
      if(stopRecord == false)
      {
        timePos = myMovie.time();
        
        targetImg.updateOutputFile(timePos);
        if(test.imageSearch(targetImg))
          image(imgTarget,220,150);
         
        targetImg2.updateOutputFile(timePos);
        if(test.imageSearch(targetImg2))
          image(imgTarget2,280,150);
         
        targetImg3.updateOutputFile(timePos);
        if(test.imageSearch(targetImg3))
          image(imgTarget3,325,150);
         
        targetImg4.updateOutputFile(timePos);
        if(test.imageSearch(targetImg4))
          image(imgTarget4,375,150);
       
        myMovie.updatePixels();
      }
      else
      {
        if(first)
        {
          closeFiles();
          first = false;
        }
      }
      image(myMovie,230,10);
      
      //mm.addFrame();
    }
  }
  
  void closeFiles()
  {
    targetImg.endOutputFile();
    targetImg2.endOutputFile();
    targetImg3.endOutputFile();
    targetImg4.endOutputFile();
    
    println("Output files finalized! You can find them in your root directory.");
  }
  
  void keyPressed()
  {
     if(key == 's')
     {
       stopRecord = true; // close the output files
     }
  }
   
  void maskResult()
  {
    for(int i=0;i<myMovie.width*myMovie.height;i++)
      myMovie.pixels[i] = 0|0|(myMovie.pixels[i])&0xFF; 
  }       
  /*
   void keyPressed()
   {
     if(key == ' ')
     {
       mm.finish();  // Finish the movie if space bar is pressed!
     }
   }
   */
  
  
  
  
  

