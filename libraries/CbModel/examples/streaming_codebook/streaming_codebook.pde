/**
  *@author Federico Bartoli
  *
  *This sketch uses the library CbModel for internet video stream,
  *it allows you to view the video test and the results obtained from CbModel on the video test.
  *This application also allows you to set the parameters for the library at run-time
 */

import it.ppm.codebookmodel.*;
import codeanticode.gsvideo.*;
import java.util.*;
import it.lilik.capturemjpeg.*;


CbModel	Model;
CaptureMJPEG myMovie;

PImage next_img = null;
PImage img_play = null;
PImage img_stop = null;

String s;
String path;
String[] servers = { 
                    "217.133.38.243:1024",
                    "webcam03.deg.net",
                    "webcam05.deg.net"
};




PFont font;

boolean mouseX_play;
boolean mouseY_play;
boolean mouseXis_over;
boolean mouseYis_over;
boolean mouseX_path;
boolean mouseY_path;
boolean avoid_writing;
boolean avoid_writing_path;
boolean isPlaying;
boolean first;

int i = 0;
int select = -1;
int frames = 10;
int frameDisplayed;
int index;
int[]inputValue;

float alfa;
float beta;
float e1;
float e2;
float tm;
float input;

char[] inputChar;


void setup(){
  size(850, 600, P2D);
  background(0);

  frameDisplayed = -1;
  alfa = 0.4;
  beta = 1.2;
  e1 = 0.2;
  e2 = 50;
  tm = 0.9;

  first=true;
  path=new String();  
  mouseX_play = false;
  mouseY_play = false;
  mouseXis_over = false;
  mouseYis_over = false;
  mouseX_path = false;
  mouseY_path = false;
  avoid_writing = false;
  avoid_writing_path = false;
  isPlaying = false;

  inputValue = new int[5];
  inputChar = new char[15];

  font = loadFont("ArialMT-32.vlw");
  img_play = loadImage("play.png");
  img_stop = loadImage("stop.png");
  image(img_play, 574, 500);

    
  textFont(font);  
  text("Original", 136, 32);
  text("Processed", 582, 32);
  textFont(font, 12); 
  text("it could be changed at runtime", 40, 590);
  textFont(font, 20);  
  text("Parameters", 30, 375);
  text("Insert new value", 170, 375);
  text("Frames:", 600, 400);
  text("Video:" , 370, 400);
  textFont(font, 15);
  
  text("alpha          " + "      " , 30, 405);
  rect(200, 390, 70, 20);
  text("beta           " + "      " , 30, 435);
  rect(200, 420, 70, 20);
  text("tm             " + "      " , 30, 465);
  rect(200, 450, 70, 20);
  text("epsilon 1      " + "      " , 30, 495);
  rect(200, 480, 70, 20);
  text("frames         " + "      " , 30, 525);
  rect(200, 510, 70, 20);  
  text("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - " , 30, 540);
  text("epsilon 2      " + "      " , 30, 555);
  text("*", 88, 556);
  text("*", 30, 595);
  rect(200, 540, 70, 20);
  rect(330, 349, 2, 400);  
  rect(0, 350, 850, 5);
  rect(430, 382, 100, 20);  
    
  frameRate(25); 
}


void draw(){
  
  if( mouseX >= 574 && mouseX <= 606 )
    mouseX_play = true;
  else 
    mouseX_play = false;
     
  if( mouseY >= 500 && mouseY <= 532 )
    mouseY_play = true;
  else 
    mouseY_play = false;
  
  if( mouseX >= 200 && mouseX <= 270 )
    mouseXis_over = true;
  else 
    mouseXis_over = false;
     
  if( (mouseY >= 390 && mouseY <= 410) || (mouseY >= 420 && mouseY <= 440) || (mouseY >= 450 && mouseY <= 470) 
        || (mouseY >= 480 && mouseY <= 500) || (mouseY >= 510 && mouseY <= 530) || (mouseY >= 540 && mouseY <= 560) ){
    mouseYis_over = true;
   
    if(mouseY<410)
      select = 1;
    else if(mouseY<440)
      select = 2;
    else if(mouseY<470)
      select = 3;
    else if(mouseY<500)
      select = 4;
    else if(mouseY<530)
      select = 5;
    else if(mouseY<560)
      select = 6;
  } 
  else 
    mouseYis_over = false;
  
  if( mouseX >= 430 && mouseX <= 530 )
    mouseX_path = true;
  else 
    mouseX_path = false;
    
  if( mouseY >= 382 && mouseY <= 402 )
    mouseY_path = true;
  else
    mouseY_path = false;
    
   
  stroke(0);  
  textFont(font, 15);
  
 
  if(frameDisplayed >= 0)
  {
    if(frameDisplayed <= frames) 
    {  
      if(next_img!=null) 
      {             
        image(next_img, 10, 50,380,280);

        fill(0);
        rect(395, 12, 70, 30);
        fill(255); 
        text("Learning", 400, 32);

        fill(255, 0, 0);
        stroke(0);
        ellipseMode(CENTER);
        ellipse(380, 27, 20, 20);
        if(first)
        {
          first=false; 
          Model = new CbModel(this, next_img.width,next_img.height,alfa, beta, e1, e2, tm, 0, 0xFFFFFFFF);
        }   
        try{
	  Model.updateModel(next_img);
	}
        catch(LearningStateException Err){
          println("Impossible to pass the state 'test' to the state of 'learning'...");
        }
        catch(WrongSizeException Err){
	  println("Dimension of input's image not equal to the size set in the model...");
        }

        fill(0);
        rect(680, 375, 45, 40);
        fill(255); 
        text(frameDisplayed, 680, 400);

        frameDisplayed ++;
        next_img=null;
      }
    }      
    else{
      Model.setTestState();
      if( Model.isTestState() )
      {
        if(next_img!=null) 
        {             
          image(next_img, 10, 50,380,280);      
          
          fill(0);
          rect(395, 12, 120, 30);
          fill(255); 
          text("Testing", 400, 32);
 
          fill(0, 255, 0);
          stroke(0);
          ellipseMode(CENTER);
          ellipse(380, 27, 20, 20); 
          try{
            image(Model.getDifferenceImage(next_img), 456, 50,380,280);
          }
          catch(WrongSizeException Err){
            println("Dimension of input's image not equal to the size set in the model");
          }
          catch(LearningStateException Err){
            println("Impossible to pass the state 'test' to the state of 'learning'");
          }
          
          fill(0);
          rect(680, 375, 45, 40);
          fill(255); 
          text(frameDisplayed, 680, 400);
          
          frameDisplayed ++;
          next_img=null;
        }
      }
      else{
        fill(0);
        rect(395, 12, 70, 30);
        fill(255);
        text("Building Model...", 400, 32);
      }
    }      
  }
}


void mouseClicked()
{
  if( mouseX_play == true && mouseY_play == true )
  {
    if(isPlaying == true){
      isPlaying = false;
      frameDisplayed = -1;
      first=true;
      rect(430, 382, 100, 20);
      fill(0);  
      rect(574, 500, 40, 40);
      rect(355, 10, 155, 30); 
      image(img_play, 574, 500);
    }
    else if( frameDisplayed < 0 ){
      frameDisplayed = 0;    
      path.toLowerCase();
      if(path.equals("bologna"))
        index=0;                 
      else if(path.equals("citta1"))
        index=1;                 
      else if(path.equals("citta2"))
        index=2;                 
      else
        index=0;                 
      
      myMovie= new CaptureMJPEG(this,new AxisURL(servers[index]).setResolution(width, height).setDesiredFPS(5).getURL());
      myMovie.startCapture();
    
      fill(0);
      rect(405, 500, 40, 40);
      image(img_stop, 574, 500); 
       
      isPlaying = true;

      fill(0);
      rect(95,390,100,20);
      fill(255);
      text("alpha          "+alfa, 30, 405);

      fill(0);
      rect(95,420,100,20);
      fill(255);
      text("beta            "+beta, 30, 435);

      fill(0);
      rect(95,450,100,20);
      fill(255);
      text("tm                "+tm, 30, 465);

      fill(0);
      rect(95,480,100,20);
      fill(255);
      text("epsilon 1    "+e1, 30, 495);

      fill(0);
      rect(95,510,100,20);
      fill(255);
      text("frames        "+frames, 30, 525);

      fill(0);
      rect(95,540,100,20);
      fill(255);
      text("epsilon 2     "+e2, 30, 555);

      isPlaying = true;
      fill(255);

    }
  }
  if( mouseXis_over == true && mouseYis_over == true )
  {
    avoid_writing = true;
    fill(255);
    stroke(0);
    for(int k=1; k<7; k++)
      rect(200, 360+k*30, 70, 20); 
    stroke(255, 0, 0);
    rect(200, 360+select*30, 70, 20); 
  }
  else 
  {
    avoid_writing = false;
    fill(255);
    stroke(0);
    for(int k=1; k<7; k++)
      rect(200, 360+k*30, 70, 20); 
     i = 0;
  }
    
    
  if( mouseX_path == true && mouseY_path == true && !isPlaying)
  {
    avoid_writing_path = true;
    fill(255);
    stroke(255, 0, 0);
    rect(430, 382, 100, 20);  
    
  } 
  else
  {
     avoid_writing_path = false;
     fill(255);
     stroke(0);
     rect(430, 382, 100, 20);  
     i = 0;
   }
} 
  
void keyPressed()
{
  if( avoid_writing == true ){
    fill(0);
       
    if( key >= '0' && key <='9' ){      
      if( i < 5){
        inputValue[i] = key - '0';
        s = str(inputValue[i]);
        text( s, 210+i*10, 377+select*30);
        i++;
      }
    }
    else if( int(key) == 46 ){
      if( i == 1){
        inputValue[i] = (int)key;
        text( key, 210+i*10, 377+select*30);
        i++;
      } 
    }
    else if( int(key) == 10 ){
      
      input=0;
      if(inputValue[1] == 46){
        input=inputValue[0];
        for(int index=2,base=10;index<i;index++,base*=10)
          input+=(float)inputValue[index]/base;
      }  
      else{
        for(int index=i-1,base=1;index>=0;index--,base*=10)
          input+=inputValue[index]*base;
      }
     
      
      fill(0);
      if(select==1){
        rect(95, 390, 45, 20); 
        fill(0);
        rect(95,390,100,20);
        alfa = input;
        fill(255);
        text("alpha          "+alfa, 30, 405);
      }
      else if(select==2){
        rect(95, 420, 45, 20); 
        fill(0);
        rect(95,420,100,20);
        beta = input;
        fill(255);
        text("beta            "+beta, 30, 435);
      }
      else if(select==3){
        rect(95, 450, 45, 20); 
        fill(0);
        rect(95,450,100,20);
        tm = input;
        fill(255);
        text("tm                "+tm, 30, 465);
      }
      else if(select==4){
        rect(95, 480, 45, 20); 
        fill(0);
        rect(95,480,100,20);
        e1 = input;
        fill(255);
        text("epsilon 1    "+e1, 30, 495);
      }
      else if(select==5){
        rect(95, 510, 45, 20); 
        fill(0);
        rect(95,510,100,20);
        frames = int(input);
        fill(255);
        text("frames        "+frames, 30, 525);
      }           
      else if(select==6){
        rect(95, 540, 45, 20); 
        fill(0);
        rect(95,540,100,20);
        fill(255);
        if( isPlaying == true ){
          Model.setEpsilon2(input);
          text("epsilon 2     "+Model.getEpsilon2(), 30, 555);
        }
        else{
          e2 = input;  
          text("epsilon 2     "+e2, 30, 555);
        }  
    }
       
      rect(200, 360+select*30, 70, 20); 
      i = 0;
      select = -1;
    }
    else if( int(key) == 8 ){
      if( i>0 ){
        fill(255);
        stroke(255);
        i--;
        rect(210+i*10, 362+select*30, 10, 15);          
      }
    }     
  }
  if( avoid_writing_path == true){
    fill(0);    
    if( int(key) == 8 )
    {
      if( i>0 )
      {
        fill(255);
        stroke(255);
        i--;
        rect(435+i*10, 385, 10, 15);          
      }
    } 
    else if(int(key)==10){
      path=new String();
      for(int j=0; j<i; j++)
        path = path + str(inputChar[j]);
    }
    else{
      inputChar[i] = key;
      text( inputChar[i], 435+i*10, 397);
      i++;       
    }     
  }
}

void captureMJPEGEvent(PImage img) {
  next_img = img;
}


