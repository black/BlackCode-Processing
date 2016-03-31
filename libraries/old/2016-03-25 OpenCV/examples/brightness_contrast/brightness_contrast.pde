import hypermedia.video.*;
import java.awt.Rectangle;
import java.awt.Point;

OpenCV opencv;

void setup() {

    size( 320, 240 );

    opencv = new OpenCV(this);
    opencv.capture( width, height );
    
    println( "Move the mouse along the x-axis to change the contrast value" );
    println( "Move the mouse along the y-axis to change the brightness value" );
}

void draw() {

    int c = (int) map(mouseX, 0, width, -128, 128);
    int b = (int) map(mouseY, 0, height, -128, 128);

    opencv.read();
    opencv.ROI( 110, 70, 100, 100 );
    opencv.brightness( b );
    opencv.contrast( c );

    image( opencv.image(), 0, 0 );
}

