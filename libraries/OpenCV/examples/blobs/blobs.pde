import hypermedia.video.*;
import java.awt.Rectangle;
import java.awt.Point;

OpenCV opencv;

int w = 320;
int h = 240;
int threshold = 80;

boolean find=true;

PFont font;

void setup() {

  size( w*2+30, h*2+30 );

  opencv = new OpenCV( this );
  opencv.capture(w, h);

  font = loadFont( "AndaleMono.vlw" );
  textFont( font );

  println( "Drag mouse inside sketch window to change threshold" );
  println( "Press space bar to record background image" );
}



void draw() {

  background(0);
  opencv.read();
  //opencv.flip( OpenCV.FLIP_HORIZONTAL );

  image( opencv.image(), 10, 10 );	            // RGB image
  image( opencv.image(OpenCV.GRAY), 20+w, 10 );   // GRAY image
  image( opencv.image(OpenCV.MEMORY), 10, 20+h ); // image in memory

  opencv.absDiff();
  opencv.threshold(threshold);
  image( opencv.image(OpenCV.GRAY), 20+w, 20+h ); // absolute difference image


  // working with blobs
  Blob[] blobs = opencv.blobs( 100, w*h/3, 20, true );

  noFill();

  pushMatrix();
  translate(20+w, 20+h);

  for ( int i=0; i<blobs.length; i++ ) {

    Rectangle bounding_rect	= blobs[i].rectangle;
    float area = blobs[i].area;
    float circumference = blobs[i].length;
    Point centroid = blobs[i].centroid;
    Point[] points = blobs[i].points;

    // rectangle
    noFill();
    stroke( blobs[i].isHole ? 128 : 64 );
    rect( bounding_rect.x, bounding_rect.y, bounding_rect.width, bounding_rect.height );


    // centroid
    stroke(0, 0, 255);
    line( centroid.x-5, centroid.y, centroid.x+5, centroid.y );
    line( centroid.x, centroid.y-5, centroid.x, centroid.y+5 );
    noStroke();
    fill(0, 0, 255);
    text( area, centroid.x+5, centroid.y+5 );


    fill(255, 0, 255, 64);
    stroke(255, 0, 255);
    if ( points.length>0 ) {
      beginShape();
      for ( int j=0; j<points.length; j++ ) {
        vertex( points[j].x, points[j].y );
      }
      endShape(CLOSE);
    }

    noStroke();
    fill(255, 0, 255);
    text( circumference, centroid.x+5, centroid.y+15 );
  }
  popMatrix();
}

void keyPressed() {
  if ( key==' ' ) opencv.remember();
}

void mouseDragged() {
  threshold = int( map(mouseX, 0, width, 0, 255) );
}

public void stop() {
  opencv.stop();
  super.stop();
}

