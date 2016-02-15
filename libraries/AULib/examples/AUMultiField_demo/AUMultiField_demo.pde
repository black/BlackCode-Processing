/* Demonstration file for AULib (Andrew's Utilities library).
* Download and documentation:
*   http://www.imaginary-institute.com/resources/AULibrary/AULibrary.php
* Show use of AUMultiField 
* We simply create a grayscale noise image and threshold it with different colors.
* Version 1 - Andrew - Sept 6, 2014
*/

import AULib.*;

void setup() {
  size(500, 500);
  
  // fill the window up with noise
  for (int y=0; y<height; y++) {
    for (int x=0; x<width; x++) {
      float v = 255 * noise(x*.02, y*.02);
      color c = color(v, v, v);
      set(x, y, c);
    }
  }
  
  // save the noise into a field with 3 planes, 1 each for RGB
  AUMultiField mfield = new AUMultiField(this, 3, width, height);
  mfield.RGBfromPixels();
  
  // modify the field by thresholding red, green, and blue
  for (int y=0; y<mfield.h; y++) {
    for (int x=0; x<mfield.w; x++) {
      float redVal = mfield.fields[0].z[y][x];
      float grnVal = mfield.fields[1].z[y][x];
      float bluVal = mfield.fields[2].z[y][x];
      mfield.fields[0].z[y][x] = (redVal > 192) ? 255 : 0;
      mfield.fields[1].z[y][x] = (grnVal > 128) ? 255 : 0;
      mfield.fields[2].z[y][x] = (bluVal >  64) ? 255 : 0;
    }
  }
  
  // show the modified field
  mfield.RGBtoPixels(0, 0);
}
