ArrayList <PVector> path = new ArrayList <PVector> ();
PVector walker;
PVector PV = new PVector(0, 0);
void sortEdgeCoordinates(int A) {
  walker = poop.get(A);
  path.add(walker);
  while (poop.size () > 0) {
    float nearestDistance = width*2;
    int index = -1;
    for (int i=0; i<poop.size(); i++) {
      PVector p = (PVector) poop.get(i);
      float distance = p.dist(walker);
      if (distance < nearestDistance) {
        nearestDistance = distance;
        index = i;
      }
    }
    walker = poop.get(index);
    poop.remove(index);
    path.add(walker);
  }
}
