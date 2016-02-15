/**
 * RGB Cube.
 * 
 * The three primary colors of the additive color model are red, green, and blue.
 * This RGB color cube displays smooth transitions between these colors. 
 */
 
import oscP5.*;
import foetus.*;
import processing.core.*;
import java.util.*;
import processing.opengl.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;

float xmag, ymag = 0;
float newXmag, newYmag = 0; 

public Foetus f;   // Declare Foetus

FoetusParameter m_Scale;

void setup()  { 
  size(800, 600, OPENGL);  
  initializeFoetus();
} 
 
void initializeFoetus()
{
  f = new Foetus(this); // Instantiate foetus object here

  m_Scale = new FoetusParameter(f, 1.0f, "/Scale", "f");
}

void draw()  { 
  f.startDrawing();
  noStroke();
  clear();
  
  pushMatrix();
 
  translate(width/2, height/2, -30); 
  scale(m_Scale.getValue());
  
  /*
  newXmag = mouseX/float(width) * TWO_PI;
  newYmag = mouseY/float(height) * TWO_PI;
  
  float diff = xmag-newXmag;
  if (abs(diff) >  0.01) { 
    xmag -= diff/4.0; 
  }
  
  diff = ymag-newYmag;
  if (abs(diff) >  0.01) { 
    ymag -= diff/4.0; 
  }
  */
  ymag+= 0.005 * TWO_PI;
  xmag+= 0.005 * TWO_PI;
  
  rotateX(-ymag); 
  rotateY(-xmag); 
  
  scale(90);
  beginShape(QUADS);

  fill(0, 255, 255); vertex(-1,  1,  1);
  fill(255, 255, 255); vertex( 1,  1,  1);
  fill(255, 0, 255); vertex( 1, -1,  1);
  fill(0, 0, 255); vertex(-1, -1,  1);

  fill(255, 255, 255); vertex( 1,  1,  1);
  fill(255, 255, 0); vertex( 1,  1, -1);
  fill(255, 0, 0); vertex( 1, -1, -1);
  fill(255, 0, 255); vertex( 1, -1,  1);

  fill(255, 255, 0); vertex( 1,  1, -1);
  fill(0, 255, 0); vertex(-1,  1, -1);
  fill(0, 0, 0); vertex(-1, -1, -1);
  fill(255, 0, 0); vertex( 1, -1, -1);

  fill(0, 255, 0); vertex(-1,  1, -1);
  fill(0, 255, 255); vertex(-1,  1,  1);
  fill(0, 0, 255); vertex(-1, -1,  1);
  fill(0, 0, 0); vertex(-1, -1, -1);

  fill(0, 255, 0); vertex(-1,  1, -1);
  fill(255, 255, 0); vertex( 1,  1, -1);
  fill(255, 255, 255); vertex( 1,  1,  1);
  fill(0, 255, 255); vertex(-1,  1,  1);

  fill(0, 0, 0); vertex(-1, -1, -1);
  fill(255, 0, 0); vertex( 1, -1, -1);
  fill(255, 0, 255); vertex( 1, -1,  1);
  fill(0, 0, 255); vertex(-1, -1,  1);

  endShape();
  
  popMatrix();
 
  f.endDrawing(); 
}


/**
 * This method is called when an OSC message is received by the synth.
 */
void oscEvent(OscMessage theOscMessage)
{
}
