import hypermedia.video.*;
import java.awt.Rectangle;
import java.awt.Point;



// OpenCV instance
OpenCV opencv;

// threshold threshold
float threshold = 80f;

// image dimensions
final int IMG_WIDTH  = 120;
final int IMG_HEIGHT = 90;

// work with which color space
final int COLOR_SPACE = OpenCV.RGB;
//final int COLOR_SPACE = OpenCV.GRAY;





void setup() {

  size( IMG_WIDTH*6, IMG_HEIGHT*2 );

  opencv = new OpenCV(this);
  opencv.capture(IMG_WIDTH, IMG_HEIGHT);

  println( "Drag mouse inside sketch window to change threshold" );
}



void draw() {

  float value = 0;
  float otsu  = 0;


  // grab image
  opencv.read();
  
  // convert color
  if ( COLOR_SPACE!=OpenCV.RGB ) opencv.convert( COLOR_SPACE );



  // type BINARY
  opencv.threshold( threshold, 255, OpenCV.THRESH_BINARY );
  image( opencv.image(), IMG_WIDTH, 0 );

  // type BINARY and OTSU
  opencv.restore( COLOR_SPACE );
  opencv.threshold( threshold, 255, OpenCV.THRESH_BINARY + OpenCV.THRESH_OTSU );
  image( opencv.image(), IMG_WIDTH, IMG_HEIGHT );


  // type BINARY INVERT
  opencv.restore( COLOR_SPACE );
  opencv.threshold( threshold, 255, OpenCV.THRESH_BINARY_INV );
  image( opencv.image(), IMG_WIDTH*2, 0 );

  // type BINARY INVERT and OTSU
  opencv.restore( COLOR_SPACE );
  opencv.threshold( threshold, 255, OpenCV.THRESH_BINARY_INV + OpenCV.THRESH_OTSU );
  image( opencv.image(), IMG_WIDTH*2, IMG_HEIGHT );





  // type TRUNC
  opencv.restore( COLOR_SPACE );
  opencv.threshold( threshold, 255, OpenCV.THRESH_TRUNC );
  image( opencv.image(), IMG_WIDTH*3, 0 );

  // type TRUNC and OTSU
  opencv.restore( COLOR_SPACE );
  opencv.threshold( threshold, 255, OpenCV.THRESH_TRUNC + OpenCV.THRESH_OTSU );
  image( opencv.image(), IMG_WIDTH*3, IMG_HEIGHT );




  // type TO ZERO
  opencv.restore( COLOR_SPACE );
  opencv.threshold( threshold, 255, OpenCV.THRESH_TOZERO );
  image( opencv.image(), IMG_WIDTH*4, 0 );

  // type TO ZERO and OTSU
  opencv.restore( COLOR_SPACE );
  opencv.threshold( threshold, 255, OpenCV.THRESH_TOZERO + OpenCV.THRESH_OTSU );
  image( opencv.image(), IMG_WIDTH*4, IMG_HEIGHT );




  // type TO ZERO INVERT
  opencv.restore( COLOR_SPACE );
  opencv.threshold( threshold, 255, OpenCV.THRESH_TOZERO_INV );
  image( opencv.image(), IMG_WIDTH*5, 0 );

  // type TO ZERO INVERT and OTSU
  opencv.restore( COLOR_SPACE );
  opencv.threshold( threshold, 255, OpenCV. THRESH_TOZERO_INV + OpenCV.THRESH_OTSU );
  image( opencv.image(), IMG_WIDTH*5, IMG_HEIGHT );



  // draw original images after modification
  // to verify 
  image( opencv.image(OpenCV.MEMORY), 0, 0 );
  opencv.restore( COLOR_SPACE );
  image( opencv.image(), 0, IMG_HEIGHT );

}


void mouseDragged() {
  threshold = map(mouseX,0,width,0,255);
  println( "threshold\t-> "+threshold );
}

public void stop() {
  opencv.stop();
  super.stop();
}
