/* Demonstration file for AULib (Andrew's Utilities library).
* Download and documentation:
*   http://www.imaginary-institute.com/resources/AULibrary/AULibrary.php
* Show use of AUField for holding a grayscale image
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
  
  // save it in a field
  AUField field = new AUField(this, width, height);
  field.fromPixels(AUField.FIELD_LUM);

  int numGrays = 5;
  // modify the field so it's quantized to this many grays
  for (int y=0; y<field.h; y++) {
    for (int x=0; x<field.w; x++) {
      float v = field.z[y][x];
      float gray = (int)(v/(255.0/numGrays));
      gray *= 255.0/(numGrays-1);
      field.z[y][x] = gray;      
    }
  }
  
  // and show the result
  field.toPixels(0, 0);
}

