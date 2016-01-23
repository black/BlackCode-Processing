import ddf.minim.*;
import neurosky.*;
import org.json.*;
/*-------------------------------*/
Minim minim;
ThinkGearSocket neuroSocket;
int blinkSt = 0;
int blink = 0;
AudioPlayer song;
PImage bg;
/*-------------------------------*/
int i=0,k,j=0,m,l=0,t=0,g=0, a=0;
void setup()
{
  size (1200,550);
    ThinkGearSocket neuroSocket = new ThinkGearSocket(this);
  try 
  {
    neuroSocket.start();
  } 
  catch (ConnectException e) {
    e.printStackTrace();
  }
  bg=loadImage("data1.jpg");
  minim = new Minim(this);
  
}

void draw()
{ 
  background(130);
  image(bg,0,0);
  text("Blink: " + blinkSt, 1000, 25);
  /*--------------------------------------------------------*/
  if(t==0){
            if(i<3){
                    fill(255,3,7,80);
                    stroke(255,3,79);
                    strokeWeight(2);
                    rect(i*400,50,100,500);
                    /*-------------------*/
                    if(/*mousePressed==true*/blink>0 )
                    {
                       k=i;
                       i=0;
                       t=t+1;
                       //mousePressed=false;
                       blink=0;
                    }
                   }
               else{
                   i=0;
                   }
         }
 // println(" the value of K = " + k + " the value of T = " + t );
          /*-------------------*/   
  if(t==1){ /*-------t ==1 -----------*/
          if(l<5 ){  /*-------l<4 -----------*/
          println(" TATTI " + " L = " + l);
                    if(k==0){
                      stroke(255,153,0);
                      strokeWeight(5);
                      fill(3,38,255,80);
                      rect(0,l*100+50,100,100);              
                    }
                     /*----------------*/
                    if(k==1){  
                      stroke(255,153,0);
                      strokeWeight(5);
                      fill(3,38,255,80);
                      rect(400,l*100+50,100,100);              
                     }
                      /*-------------------*/    
                    if(k==2){  
                      stroke(255,153,0);
                      strokeWeight(5);
                      fill(3,38,255,80);
                      rect(800,l*100+50,100,100);              
                    }
                   if(/*mousePressed==true*/ blink>0 )
                    {
                      m=l;
                      t=0;
                      l=0;
                          if(k==0){ 
                               println("value of m =  " + m +" k = " + k);
                               String filename=(m+"."+"MP3");
                               song = minim.loadFile(filename);
                               song.play();
                            }
                                    
                            if(k==1){  
                               println("value of m =  " + m +" k = " + k);
                               String filename=(m+"a"+"."+"MP3");
                               song = minim.loadFile(filename);
                               song.play();
                                       
                             }                  
                            if(k==2){  
                               println("value of m =  " + m +" k = " + k);
                               String filename=(m+"b"+"."+"MP3");
                               song = minim.loadFile(filename);
                               song.play();
                                          
                            }          
                      blink=0;
                    }
                    
                    
                  } /*-------l<4 -----------*/
          else{
                l=0;
              }

          }/*-------t ==1 -----------*/
     
        if(j==50){
                 i=i+1;
                 j=0;
                }
                if(t==0){
               //  println("I= " + i + " J= "+ j);
                }
                j++;
      /*-------------------*/
     if(g==50)
            {
              l=l+1;
              g=0;
            }
             if(t==1)
             {
         //      println("L= " + l + " G= "+ g + " T= " + t + " K= " +k);
             }
              g++;  
      
}

void blinkEvent(int blinkStrength) 
{
  blinkSt = blinkStrength;
  blink = 1;
}
 
void stop() {
  neuroSocket.stop();
  super.stop();
}



