int k=0;
public void drawContour(ArrayList<Pixel> pixel_list, int stroke_color, int fill_color, boolean fill, float stroke_weight) {
  if ( !fill)
    noFill();
  else
    fill(fill_color);
  stroke(stroke_color);
  strokeWeight(stroke_weight);
  beginShape();
  for (int idx = 0; idx < pixel_list.size (); idx++) {
    Pixel p = pixel_list.get(idx);
    float xx = map(p.x_, 0, 640, 0, width);
    float yy = map(p.y_, 0, 480, 0, height);
    vertex(xx, yy);
  }
  endShape();
}

public void printlnFPS() { 
  // fill(100, 200, 255);
}



public void printlnNumberOfBlobs(BlobDetector blob_detector) {
  fill(100, 200, 255);
  //  text("number of blobs: "+blob_detector.getBlobs().size(), 10, 40);
}

