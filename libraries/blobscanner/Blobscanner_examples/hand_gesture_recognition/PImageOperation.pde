/*
 * This software is part of Blobscanner Processing library.
 * Hand detection and gesture recognition 
 * (c) Antonio Molinaro 2011 http://code.google.com/p/blobscanner/.
 *
 * PImageOperations Class for processing 
 * (c) 2006 Andrew Senior 
 * Preserve this header when redistributing. 
 *
 * I removed some methods not used by the main program.
 * The original code is available at http://www.andrewsenior.com.
 */ 
 
class PImageOperations
{
  
  // Copy Image I2 to I1. 
  void Copy(PImage I1, PImage I2)
    {
      for(int i=0; i<I1.width*I1.height; i++)
      {
	I1.pixels[i]=I2.pixels[i];
      }
   }
 
   
 //    Threshold an image - threshold is in [1,255]
    void   Threshold(PImage I1, int iThresh)
    {
      for(int i=0; i<I1.width*I1.height; i++)
      {
	color iP=I1.pixels[i];
        if (red(iP)+green(iP)+blue(iP)>iThresh*3)
	  I1.pixels[i]=color(255,255,255);
        else
	  I1.pixels[i]=color(0,0,0);
      }
   }
   // Binary morphology Open (black holes up) operator
    void   MorphOpen(PImage I1, PImage I2)
    {
      int iThresh=128;
      int k=0;
      for(int j=0; j<I1.height; j++)
      for(int i=0; i<I1.width; i++, k++)
      {
        if (                                red(I1.pixels[k])>iThresh && 
        (i==0 ||                            red(I1.pixels[k-1])>iThresh) && 
        (i==I1.width-1 ||                   red(I1.pixels[k+1])>iThresh) && 
        (j==0 ||                            red(I1.pixels[k-I1.width])>iThresh) && 
        (j==I1.height-1 ||                  red(I1.pixels[k+I1.width])>iThresh) && 
        (i==0 || j==0 ||                    red(I1.pixels[k-1-I1.width])>iThresh) && 
        (i==0 || j==I1.height-1 ||          red(I1.pixels[k-1+I1.width])>iThresh) && 
        (i==I1.width-1 || j==0 ||           red(I1.pixels[k+1-I1.width])>iThresh) && 
        (i==I1.width-1 || j==I1.height-1 || red(I1.pixels[k+1+I1.width])>iThresh) 
          )
	  I2.pixels[k]=color(255,255,255);
        else
	  I2.pixels[k]=color(0,0,0);
      }
   }
   
   // Binary morphology Close (black holes- enlarge white areas) 
    void   MorphClose(PImage I1, PImage I2)
    {
      int iThresh=128;
      int k=0;
      for(int j=0; j<I1.height; j++)
      for(int i=0; i<I1.width; i++, k++)
      {
        if (                                red(I1.pixels[k])>iThresh || 
        (i!=0 &&                            red(I1.pixels[k-1])>iThresh) || 
        (i!=I1.width-1 &&                   red(I1.pixels[k+1])>iThresh) || 
        (j!=0 &&                            red(I1.pixels[k-I1.width])>iThresh) || 
        (j!=I1.height-1 &&                  red(I1.pixels[k+I1.width])>iThresh) || 
        (i!=0 && j!=0 &&                    red(I1.pixels[k-1-I1.width])>iThresh) || 
        (i!=0 && j!=I1.height-1 &&          red(I1.pixels[k-1+I1.width])>iThresh) || 
        (i!=I1.width-1 && j!=0 &&           red(I1.pixels[k+1-I1.width])>iThresh) || 
        (i!=I1.width-1 && j!=I1.height-1 && red(I1.pixels[k+1+I1.width])>iThresh) 
          )
	  I2.pixels[k]=color(255,255,255);
        else
	  I2.pixels[k]=color(0,0,0);
      }
   }
 }
