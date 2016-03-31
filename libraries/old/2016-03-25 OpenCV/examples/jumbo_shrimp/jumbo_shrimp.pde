/**
 * blue = smallest (shrimp) blob
 * red  = largest (jumbo) blob
 */

import hypermedia.video.*;
import java.awt.Rectangle;
import java.awt.Point;

OpenCV opencv;

int w = 320;
int h = 240;
float value = 80;

boolean preview = true;

void setup() {

    size( w, h );

    opencv = new OpenCV( this );
    opencv.capture(w,h);

    println( "Drag mouse inside sketch window to change threshold" );
}



void draw() {


    Blob[] blobs;


    background(0);
    noStroke();

    opencv.read();
    opencv.flip( OpenCV.FLIP_HORIZONTAL );
    opencv.convert( OpenCV.GRAY );

    opencv.absDiff();
    opencv.threshold(value);
    image( opencv.image(OpenCV.MEMORY), 0, 0 );
    if ( preview ) image( opencv.image(), 0, 0 );


    // find jumbo blob
    blobs = opencv.blobs( 1000, w*h, 1, false );

    fill(255,0,0);
    if ( blobs.length>0 ) {
        beginShape();
        for( int i=0; i<blobs[0].points.length; i++ ) {
            vertex( blobs[0].points[i].x, blobs[0].points[i].y );
        }
        endShape(CLOSE);
    }

    // restore image
    opencv.restore();
    opencv.flip( OpenCV.FLIP_HORIZONTAL );
    opencv.convert( OpenCV.GRAY );

    opencv.absDiff();
    opencv.threshold(value);

    // find shrimp blob
    blobs = opencv.blobs( 1, 100, 1, false );

    fill(0,0,255);
    if ( blobs.length>0 ) {
        beginShape();
        for( int i=0; i<blobs[0].points.length; i++ ) {
            vertex( blobs[0].points[i].x, blobs[0].points[i].y );
        }
        endShape(CLOSE);
    }

}

void mouseDragged() {
    value = map(mouseX,0,width,0,255);
    preview  = true;
}

void mouseReleased() {
    preview = false;
}

void keyPressed() {
    opencv.remember();
}

public void stop() {
    opencv.stop();
    super.stop();
}
