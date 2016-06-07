class TentaclePart {
  PVector position;
  float width;
  float height;
  color clr;
};

class Tentacle {

  PVector position;
  float orientation;

  int nbParts;
  float compactness;
  ArrayList<TentaclePart> parts;

  Tentacle(PVector pos, int nb, float w, float h, float o, float c) {
    position = pos;
    nbParts = nb;
    float headWidth = w;
    float headHeight = h;
    compactness = c;
    orientation = o;

    parts = new ArrayList<TentaclePart>();

    for (int i = 0; i < nbParts; i++) {
      TentaclePart part = new TentaclePart();
      part.width = (nbParts-i) * headWidth / (float)nbParts;
      part.height = (nbParts-i) * headHeight / (float)nbParts;
      part.position = position.get();
      part.position.x += compactness * i * cos( orientation );
      part.position.y += compactness * i * sin( orientation );
      part.clr = color(230 - (i * (230 / nbParts)));
      parts.add( part );
    }
  }

  void update() {
    PVector pos0 = parts.get(0).position;
    PVector pos1 = parts.get(1).position;
    pos0.set(position.get());
    pos1.x = pos0.x + ( compactness * cos( orientation ) );
    pos1.y = pos0.y + ( compactness * sin( orientation ) );
    for (int i = 2; i < nbParts; i++) {
      PVector currentPos = parts.get(i).position.get();
      PVector dist = PVector.sub( currentPos, parts.get(i-2).position.get() );
      float distmag = dist.mag();
      PVector pos = parts.get(i - 1).position.get();
      PVector move = PVector.mult(dist, compactness);
      move.div(distmag);
      pos.add(move);
      parts.get(i).position.set(pos);
    }
  }

  void draw() {
    for (int i = nbParts - 1; i >= 0; i--) {
      TentaclePart part = parts.get(i);
      noStroke();
      fill(part.clr);
      ellipse(part.position.x, part.position.y, part.width, part.height);
    }
  }
};
