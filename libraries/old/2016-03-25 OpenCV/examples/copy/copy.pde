import hypermedia.video.*;
import java.awt.Rectangle;
import java.awt.Point;

OpenCV opencv;
PImage img;


void setup() {

  size( 640, 480 );

  // load image
  img = loadImage( "UFO.png" );

  // initialize opencv
  opencv = new OpenCV( this );
  opencv.allocate( width, height );
  opencv.ROI( 70, 70, 320, 240 );
  opencv.interpolation( OpenCV.INTER_NN );
}


void draw() {

  float factor = .5;
  int destw    = int(img.width*factor);
  int desth    = int(img.height*factor);

  opencv.copy( "000035.jpg" );        // copy the entire image in background
  //opencv.convert( GRAY );  // convert to gray
  opencv.copy( img );        // copy the entire image in background
  opencv.copy( "UFO.png",  100, 30, img.width-100, img.height-30, mouseX, mouseY, destw*3, desth*3 );
  opencv.copy( img, 0, 0, img.width, img.height, mouseX, mouseY, destw, desth );
  opencv.invert();           // invert buffer -> only the region of interest

  // show the result
  image( opencv.image(), 0, 0 );
  
  // show selection
  /*noFill();
  stroke(255,0,0);
  rect( 0, 0, mouseX,mouseY );*/

}
