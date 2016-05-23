/* OpenProcessing Tweak of *@*http://www.openprocessing.org/sketch/40523*@* */
/* !do not delete the line above, required for linking your tweak if you upload again */
/**
 * Some fun with edge detection.  This very simple effect, when used on
 * images like cityscapes, looks like a blizzard on a pitch-dark night.
 *
 * Click on the image or press SPACE to start the effect.
 * Hold down mouse to shoot 'snow'...white pixels... where you please.
 * Use the UP and DOWN keys to make it snow more or less.
 *
 * There are two edge detection methods available - Canny and Sobel
 * Canny is much more verbose and complex than the Sobel method,
 * but has better results in general, and is about the same speed.
 * See their files for info/credit.
 **/
import java.util.*;
CannyEdgeDetector canny = new CannyEdgeDetector();
SobelEdgeDetector sobel = new SobelEdgeDetector();
PImage img;
int[] edges;
int[] myPixels;
int snowRate = 6;

void setup() {
  size(700, 500, P2D);
  img = loadImage("sydney-cityscape.jpg");
  img.resize(width, height);
  canny.setImg(img);
  edges = canny.process();
  //to use the Sobel method, use the following line instead of the above line
  //edges = sobel.findEdgesAll(img, 100);
  myPixels = new int[width*height];
  for (int i = 0; i < width*height; ++i)
    myPixels[i] = color(0);
}

void draw() {
  if (img != null)
    image(img, 0, 0);
  else {
    background(0);
    snow();
    if (mousePressed)
      mouseSnow();
    shake();
    loadPixels();
    for (int i = 0; i < width*height; ++i)
      pixels[i] = myPixels[i];
    updatePixels();
  }
}

// Call this method to show the original edge map
void displayEdges() {
  for (int i = 0; i < width*height; ++i)
    set(i%width, i/width, edges[i]);
}

// Drop snow from the top of the frame
void snow() {
  for (int i = 0; i < snowRate; ++i)
    myPixels[(int)random(0, width)] = color(255);
}

// The mouse shoots out snow when held down.
// Drop three 'snowflakes': one on the clicked pixel, one to the left, and one to the right
void mouseSnow() {
  if (mouseX > 0 && mouseX < width - 1 && mouseY > 0 && mouseY < height - 1) {
    int clickedPix = mouseX + mouseY*width;
    for (int i = clickedPix - 1; i <= clickedPix + 1; ++i)
      myPixels[i] = color(255);
  }
}

// Move those white pixels!
void shake() {
  for (int x = 0; x < width; ++x) {
    for (int y = 0; y < height; ++y) {
      int pixel = y*width + x;
      // once an edge is colored white, it is locked, so ignore these pixels, and all empty ones
      if (myPixels[pixel] == color(0) || edges[pixel] == color(255))
        continue;
      int newY = y + (int)random(0, 2);
      int newX = x + (int)random(-2, 2);
      if (newY < 0)
        newY = 0;
      else if (newY >= height)
        newY = height - 1;
      if (newX < 0)
        newX = 0;
      else if (newX >= width)
        newX = width - 1;
      int newPixel = newY*width + newX;
      // if the new space is empty, move the white pixel to a new location
      if (myPixels[newPixel] == color(0)) {
        myPixels[newPixel] = color(255);
        myPixels[pixel] = color(0);
      }
    }
  }
}

void keyPressed() {
  if (keyCode == UP && snowRate < 40) snowRate++;
  else if (keyCode == DOWN && snowRate > 0) snowRate--;
  else if (key == ' ') img = null;
}

void mouseClicked() {
  img = null;
}

