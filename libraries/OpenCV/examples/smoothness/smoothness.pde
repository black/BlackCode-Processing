import hypermedia.video.*;
import java.awt.Rectangle;
import java.awt.Point;



// OpenCV instance
OpenCV opencv;

// blur value
int value = 17;

// image dimensions
final int IMG_WIDTH  = 200;
final int IMG_HEIGHT = 150;

// work with which color space
final int COLOR_SPACE = OpenCV.RGB;
//final int COLOR_SPACE = OpenCV.GRAY;





void setup() {

  size( IMG_WIDTH*5, IMG_HEIGHT );

  opencv = new OpenCV(this);
  opencv.capture(IMG_WIDTH, IMG_HEIGHT);

  println( "Drag mouse inside sketch window to change blur value" );
}



void draw() {



  // grab image
  opencv.read();
  
  // convert color
  if ( COLOR_SPACE!=OpenCV.RGB ) opencv.convert( COLOR_SPACE );



  // SIMLE BLUR
  opencv.blur( OpenCV.BLUR, value );
  image( opencv.image(), IMG_WIDTH, 0 );


  // GAUSSIAN
  opencv.restore( COLOR_SPACE );
  opencv.blur( OpenCV.GAUSSIAN, value );
  image( opencv.image(), IMG_WIDTH*2, 0 );


  // MEDIAN
  opencv.restore( COLOR_SPACE );
  opencv.blur( OpenCV.MEDIAN, value );
  image( opencv.image(), IMG_WIDTH*3, 0 );


  // BILATERAL
  opencv.restore( COLOR_SPACE );
  opencv.blur( OpenCV.BILATERAL, value, value, value/2, value/3 );
  image( opencv.image(), IMG_WIDTH*4, 0 );



  // draw original images after modification
  // to verify 
  image( opencv.image(OpenCV.SOURCE), 0, 0 );
  opencv.restore( COLOR_SPACE );

}


void mouseDragged() {
  value = (int) map(mouseX,0,width,0,255);
  println( "smoothness value -> "+value );
}

public void stop() {
  opencv.stop();
  super.stop();
}
