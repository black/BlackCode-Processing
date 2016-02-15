/*
 * This software is part of Blobscanner Processing library examples.
 * Hand detection and gesture recognition 
 * (c) Antonio Molinaro 2011 http://code.google.com/p/blobscanner/.
 *
 * Based on Background subtraction Class for processing 
 * by Andrew Senior   www.andrewsenior.com.
 *  
 */

class PBGS
{ 
  //cambiato to int 17/03/2011
  int  []  Background;   
  PImage     Difference;
 
  int  iWidth;
  int  iHeight;
  
  // Create the background model to look like the start frame
  public PBGS(PImage I)
    {
      iWidth=I.width;
      iHeight=I.height;
      Background=new int[I.width* I.height*3];
       Difference=createImage(  I.width, I.height, RGB);
      arrayCopy(I.pixels, Difference.pixels);
      Set(I);
    }
    
  // Set the background image to the given image
  void Set(PImage I)
  {
    for(int i=0; i<I.width*I.height; i++)
      {
        color iP=I.pixels[i];
        Background[i*3]=(int)red(iP);
         Background[i*3+1]=(int)green(iP);
         Background[i*3+2]=(int)blue(iP);
      }
       
  }
  
  void PutDifference()
  {
    PImOps.Copy(imgDiff,  Difference);
  }
 
  
  
  // update the model and compute the difference
    void Update(PImage I)
    {
      Difference=createImage(  I.width, I.height, RGB);
      arrayCopy(I.pixels, Difference.pixels);
     for(int i=0; i<I.width*I.height; i++)
      {
        color iP=I.pixels[i];
 
       Difference.pixels[i]=color(abs( Background[i*3]-red(iP)), abs( Background[i*3+1]-green(iP)), abs( Background[i*3+2]-blue(iP))); 
      }
      PImOps.Threshold( Difference, 30 );
      PImage T=new PImage( Difference.width,  Difference.height);
      PImOps.Copy(T,  Difference);
      PImOps.MorphClose(T,  Difference);
      PImOps.MorphOpen(T, Difference);
      pconcomps  pcc = new pconcomps();
      pcc.Compute( Difference);
      pcc.RemoveSmall(18);
      pcc.SetImage( Difference);
    } 
}

