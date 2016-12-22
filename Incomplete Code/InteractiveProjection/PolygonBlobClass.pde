
class PolygonBlob extends Polygon { 
  void createPolygon() { 
    ArrayList<ArrayList> contours = new ArrayList<ArrayList>(); 
    int selectedContour = 0;
    int selectedPoint = 0;

    for (int n=0; n<theBlobDetection.getBlobNb (); n++) { 
      Blob b = theBlobDetection.getBlob(n); // for each substantial blob... 
      if (b != null && b.getEdgeNb() > 100) {
        // create a new contour arrayList of PVectors
        ArrayList<PVector> contour = new ArrayList();
        // go over all the edges in the blob
        for (int m=0; m<b.getEdgeNb (); m++) { // get the edgeVertices of the edge 
          EdgeVertex eA = b.getEdgeVertexA(m); 
          EdgeVertex eB = b.getEdgeVertexB(m); // if both ain't null... 
          if (eA != null && eB != null) { // get next and previous edgeVertexA 
            EdgeVertex fn = b.getEdgeVertexA((m+1) % b.getEdgeNb()); 
            EdgeVertex fp = b.getEdgeVertexA((max(0, m-1))); // calculate distance between vertexA and next and previous edgeVertexA respectively 
            // positions are multiplied by kinect dimensions because the blob library returns normalized values 
            float dn = dist(eA.x*kinectWidth, eA.y*kinectHeight, fn.x*kinectWidth, fn.y*kinectHeight); 
            float dp = dist(eA.x*kinectWidth, eA.y*kinectHeight, fp.x*kinectWidth, fp.y*kinectHeight); // if either distance is bigger than 15 
            if (dn > 15 || dp > 15) {
              // if the current contour size is bigger than zero
              if (contour.size() > 0) {
                contour.add(new PVector(eB.x*kinectWidth, eB.y*kinectHeight)); // add current contour to the arrayList
                contours.add(contour);  // start a new contour arrayList
                contour = new ArrayList();  // if the current contour size is 0 (aka it's a new list)
              } else {
                contour.add(new PVector(eA.x*kinectWidth, eA.y*kinectHeight));// add the point to the list
              }
            } else { // if both distance are smaller than 15 (aka the points are close)
              contour.add(new PVector(eA.x*kinectWidth, eA.y*kinectHeight)); // add the point to the list
            }
          }
        }
      }
    }

    // at this point in the code we have a list of contours (aka an arrayList of arrayLists of PVectors)
    // now we need to sort those contours into a correct polygon. To do this we need two things:
    // 1. The correct order of contours
    // 2. The correct direction of each contour

    // as long as there are contours left...    
    while (contours.size () > 0) {

      // find next contour
      float distance = 999999999;
      // if there are already points in the polygon
      if (npoints > 0) {
        // use the polygon's last point as a starting point
        PVector lastPoint = new PVector(xpoints[npoints-1], ypoints[npoints-1]);
        // go over all contours
        for (int i=0; i<contours.size (); i++) {
          ArrayList c = (ArrayList) contours.get(i);  // get the contour from the list of countorus         
          PVector fp = (PVector)c.get(0);  // get the contour's first point
          PVector lp = (PVector)c.get(c.size()-1);  // get the contour's last point

          if (fp.dist(lastPoint) < distance) { // if the distance between the current contour's first point and the polygon's last point is smaller than distance
            distance = fp.dist(lastPoint); // set distance to this distance
            selectedContour = i;// set this as the selected contour
            selectedPoint = 0; // set selectedPoint to 0 (which signals first point)
          }
          // if the distance between the current contour's last point and the polygon's last point is smaller than distance
          if (lp.dist(lastPoint) < distance) {
            distance = lp.dist(lastPoint); // set distance to this distance
            selectedContour = i;   // set this as the selected contour
            selectedPoint = 1; // set selectedPoint to 1 (which signals last point)
          }
        }
        // if the polygon is still empty
      } else {
        // use a starting point in the lower-right
        PVector closestPoint = new PVector(width, height);
        for (int i=0; i<contours.size (); i++) {
          ArrayList c = (ArrayList)contours.get(i);  // get the contoru from the list of countorus         
          PVector fp = (PVector)c.get(0);  // get the contour's first point
          PVector lp = (PVector)c.get(c.size()-1);  // get the contour's last point

          if (fp.y > kinectHeight-5 && fp.x < closestPoint.x) { // set closestPoint to first point closestPoint = fp; // set this as the selected contour selectedContour = i; // set selectedPoint to 0 (which signals first point) selectedPoint = 0; } // if the last point is in the lowest 5 pixels of the (kinect) screen and more to the left than the current closestPoint if (lp.y > kinectHeight-5 && lp.x < closestPoint.y) {
            closestPoint = lp;  // set closestPoint to last point
            selectedContour = i; // set this as the selected contour
            selectedPoint = 1;  // set selectedPoint to 1 (which signals last point)
          }
        }
      }

      // add contour to polygon
      ArrayList contour = contours.get(selectedContour);
      if (selectedPoint > 0) {  // if selectedPoint is bigger than zero (aka last point) then reverse the arrayList of points
        reverse(contour);
      }
      for (int i=0; i<contour.size (); i++) {      // add all the points in the contour to the polygon
        PVector p = (PVector)contour.get(i);
        addPoint(int(p.x), int(p.y));
      }
      contours.remove(selectedContour);       // remove this contour from the list of contours
      // the while loop above makes all of this code loop until the number of contours is zero
      // at that time all the points in all the contours have been added to the polygon... in the correct order (hopefully)
    }
  }
}

