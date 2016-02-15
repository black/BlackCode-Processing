import foetus.*;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.*;
import oscP5.*;

public Foetus f;

FoetusParameter m_Scale;
FoetusParameter m_Position_X;
FoetusParameter m_Position_Y;
FoetusParameter m_NumIter;
FoetusParameter m_MaxDistort;

PGraphics m_destTex;
PShader m_bbc;

/*
* The shader used in this example has been adapted from the shader below:
* www.shadertoy.com/view/XssGz8
*/

public void setup() {
  size(800, 600, OPENGL);
  smooth();

  initializeFoetus();
}

public void initializeFoetus() {    
  // The second (optional) boolean parameter enables drawing a cube 
  // into the incoming PGraphics buffer, to give the effect
  // some input for when running in standalone.
  f = new Foetus(this, true);

  m_Scale       = new FoetusParameter(f, 1.0f, "/Scale", "f");
  m_Position_X  = new FoetusParameter(f, 0.5f, "/Position_X", "f");
  m_Position_Y  = new FoetusParameter(f, 0.5f, "/Position_Y", "f");
  m_NumIter     = new FoetusParameter(f, 12,   "/NumIter", "i");
  m_MaxDistort  = new FoetusParameter(f, -10f, "/MaxDistort", "f");
  
  m_destTex = createGraphics(width, height, OPENGL);
  
  // Note that I specify the path with an absolute reference!
  // If I just wrote: loadShader( "BarrelBlurChroma.glsl");
  // it wouldn't work.
  // A bit of a hack for now, but I don't know of a better way.
  m_bbc = loadShader(dataPath("BarrelBlurChroma.glsl"));
  
  /*
  if (System.getProperty("os.name").toLowerCase().indexOf("mac") != -1) {
    m_bbc = loadShader(this.sketchPath + "/data/BarrelBlurChroma.glsl"); // Mac
  }
  else {
    m_bbc = loadShader(this.sketchPath + "\\data\\BarrelBlurChroma.glsl"); // Win
  }
  */
}

public void draw() {    
  if (f.incoming!=null) {
    try {
      if (f.GetAlpha()>0.0f) {
        m_bbc.set("num_iter", (int)m_NumIter.getValue());
        m_bbc.set("max_distort", m_MaxDistort.getValue());

        m_destTex.beginDraw();
        m_destTex.shader(m_bbc);
        m_destTex.image(f.incoming, 0, 0, width, height);
        m_destTex.endDraw();
      }
      
      f.startDrawing();
      clear();
      if (f.GetAlpha()>0.0f) {
        hint(DISABLE_DEPTH_MASK);
        g.blendMode(REPLACE);
        tint(255, 255, 255, 255);
        image(m_destTex, 0, 0);
      }
      f.endDrawing();
    }
    catch(Exception e) {
      System.out.println("BarrelBlurChroma Effect 0.1: " + e.getLocalizedMessage());
      e.printStackTrace();
    }
  }
  else {
    System.out.println("f.Incoming is NULL");
  }
}

/**
 * This method is called when an OSC message is received by the synth.
 */
public void oscEvent(OscMessage theOscMessage) {
}

