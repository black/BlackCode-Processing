/**
 * Camera tracking
 * by Andres Colubri.
 *  
 * Usage example of GPU-KLT feature tracker. This tracker identifies "features" (which are 
 * by definition "corner" pixels, this is, pixels with large changes in intensity along the 
 * X and Y directions) and then tracks them along successive frames. When a feature disappears
 * it is replaced by a new one.
 */
 
import processing.opengl.*;
import codeanticode.glgraphics.*;
import codeanticode.gsvideo.*;
import codeanticode.progpuklt.*;

GSCapture cam;
GLTexture tex, filttex;
GLTextureFilter denoise;
FeatureTracker tracker;
Chronometer chrono;

PFont font;

boolean newFrame;

void setup() {
  size(640, 480, GLConstants.GLGRAPHICS);
  frameRate(60);
  
  cam = new GSCapture(this, 640, 480, 30);
  
  // This should give fps values more reliable than frameRate.
  chrono = new Chronometer(); 
  
  font = loadFont("EstrangeloEdessa-14.vlw");

  // This tracked will look for a maximum of 1000 feature points in the image.
  tracker = new FeatureTracker(this, cam.width, cam.height, 1000);
  
/*  
  // Detailed parameters can be passed to the tracker using a FeatureTrackerParameters object.
  FeatureTrackerParameters params = new FeatureTrackerParameters();
  
  //Number of updates on the position to solve the actually non-linear minimization problem 
  // (i.e. number of inner iterations).   
  params.nIterations = 10;
  
  // Number of pyramid levels generated and used for tracking.
  params.nLevels = 4;
  
  // After getting an initial estimate of the new feature position in the previous pyramid level,
  // the next level used for refinement is level+levelSkip. reasonable values are 1 (all levels) 
  // and nLevels-1 (2 levels). With levelSkip equal to 1 all pyramid levels are fully evaluated; 
  // levelSkip = nLevels-1, only the coarsest and the full resolution level are used.  
  params.levelSkip = 3;
  
  // Size of the window around the feature used for the position update. The used window is 
  // windowWidth x windowWidth with the feature in the center.
  params.windowWidth = 7;
  
  // Features very close to the image border are discarded. You want to have at least 
  // trackBorderMargin>=windowWidth/2 in order to have the feature completely within the image.
  params.trackBorderMargin = 10.0f;
  
  // If the position update after the inner iterations (after nIterations steps) has not converged 
  //(i.e. the length of the update > convergenceThreshold in the final iteration), discard the 
  // feature. It is measured in pixels.
  params.convergenceThreshold = 0.5f;
  
  // If the error function we are minimizing (with respect to the position update) is not small 
  // enough (i.e. >SSDThreshold), discard the feature (no similarly looking patch found).  
  params.SSDThreshold = 80000.0f;
  
  // Should simultaneous gain estimation be enabled (allows some variation in brightness in the 
  // images) or not (faster tracking).
  params.trackWithGain = true;
  
  // Suppress corners/features that are two close. If two corners have distance < minDistance, 
  // then the weaker one is suppressed. This enables a reasonable uniform distribution of 
  // features in the image.
  params.minDistance = 10;
  
  // The minimum value of the cornerness required for a pixel to classify as corner. Highly 
  // textured areas have larger cornerness.
  params.minCornerness = 50.0f;
  
  // Suppress corners very close to the image borders. Setting detectBorderMargin = trackBorderMargin 
  // is fine.
  params.detectBorderMargin = 10.0f;
  
  // Kernel size used in the pyramid with derivatives generation.
  params.kernelSize = 1;
  
  // Enables explicit LOD in the pyramid generation (w/out derivatives).
  params.explicitLod = false;
    
  // Interval for feature re-detection.
  params.nTrackedFrames = 10;
  
  // From all these parameters, SSDThreshold and convergenceThreshold are the most important to
  // ensure that features remain tracked even if the video signal is noisy.
  
  tracker = new FeatureTracker(this, cam.width, cam.height, 1000, params);  
  */

  tex = new GLTexture(this);
  filttex = new GLTexture(this);

  // Different de-noising filters. The median filers are very good, but also
  // very heavy to compute.
  denoise = new GLTextureFilter(this, "blur.xml");
  //denoise = new GLTextureFilter(this, "mean.xml");  
  //denoise = new GLTextureFilter(this, "median3.xml");
  //denoise = new GLTextureFilter(this, "median3-G80.xml");
  //denoise = new GLTextureFilter(this, "median5.xml");
  
  // Parameters used to draw features and their tracks.
  stroke(10, 200, 10);
  fill(200, 10, 10);
  ellipseMode(CENTER);

  textFont(font, 14); 
  
  newFrame = false;
}

void captureEvent(GSCapture c) {
  c.read();
  newFrame = true;
}

void draw() {
  if (newFrame) {
    tex.putPixelsIntoTexture(cam);

    /*
    // Tracking on the original image.
    tracker.track(tex.getTextureID());
    // You can also to the tracking passing a PImage directly:
    //tracker.track(cam);
    image(tex, 0, 0);    
    */
    
    // Applying a denoise filter first, and then tracking on the 
    // filtered image.    
    tex.filter(denoise, filttex);
    tracker.track(filttex.getTextureID());
    image(filttex, 0, 0);
 
    // Draws all the current feature points.
    tracker.drawTracks();
    
    // Draws the tracks or trajectories of each feature point.
    tracker.drawFeatures(5);
   
    fill(255);
    text("Number of detected features in the first frame: " + tracker.getNDetectedFeatures(), 5, 18);
    text("Number of new features found when re-detecting: " + tracker.getNNewFeatures(), 5, 36);
    text("Number of features present in current frame: " + tracker.getNPresentFeatures(), 5, 54);
    text("FPS: " + chrono.fps, width - 60, 18);
    fill(200, 10, 10);    
    newFrame = false;    
  }
  
  chrono.inc();
  chrono.update();
  chrono.printfps();
}

void stop()
{
  // Stop the sketch by hitting the stop button, if you close the output
  // window you will get errors.
  
  // And remember to stop the tracker here.
  tracker.stop();
  
  super.stop();
}

