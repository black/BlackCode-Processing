/**
 * Interactive Toroid
 * by Ira Greenberg. 
 * 
 * Illustrates the geometric relationship between Toroid, Sphere,
 * and Helix 3D primitives, as well as lathing principal.
 */

class Toroid {
  int pts = 40; 
  float angle = 0;
  float radius = 60.0;

  // lathe segments
  int segments = 60;
  float latheAngle = 0;
  float latheRadius = 100.0;

  //vertices
  PVector vertices[], vertices2[];

  // for shaded or wireframe rendering 
  boolean isWireFrame = false;

  // for optional helix
  boolean isHelix = false;
  float helixOffset = 5.0;


  boolean modelChange = false;

  public Toroid() {
    fillVertexArrays();
  }

  public void setSegmentDetail(int segments) {
    this.segments = segments; 
    fillVertexArrays();
  }

  public void setEllipseDetail(int points) {
    pts = points;
    fillVertexArrays();
  }

  public void setEllipseRadius(float eradius) {
    radius = eradius;
    fillVertexArrays();
  }

  public void setLatheRadius(float lradius) {
    latheRadius = lradius;
    fillVertexArrays();
  }

  public void setIsHelix(boolean helix) {
    isHelix = helix;
    fillVertexArrays();
  }

  public void setIsWire(boolean wire) {
    isWireFrame = wire;
  }

  void fillVertexArrays() {
    // initialize point arrays
    vertices = new PVector[pts+1];
    vertices2 = new PVector[pts+1];

    // fill arrays
    for (int i=0; i<=pts; i++) {
      vertices[i] = new PVector();
      vertices2[i] = new PVector();
      vertices[i].x = latheRadius + sin(radians(angle))*radius;
      if (isHelix) {
        vertices[i].z = cos(radians(angle))*radius-(helixOffset* segments)/2;
      } else {
        vertices[i].z = cos(radians(angle))*radius;
      }
      angle+=360.0/pts;
    }
  }

  void update(PGraphics3D pg) {
    pg.beginDraw();
    pg.background(230, 170, 80);
    // lighting setup
    pg.ambientLight(60, 80, 10);
    pg.directionalLight(0, 128, 0, 0.5f, 1, -2f);
    //center and spin toroid
    pg.translate(view.width()/2, view.height()/2, -180);
    pg.rotateX(frameCount*PI/133.0);
    pg.rotateY(frameCount*PI/170.0);
    pg.rotateZ(frameCount*PI/90.0);
    // Toroid or helix
    if (isHelix)
      fillVertexArrays();
    // Wire frame or solid?
    if (isWireFrame) {
      pg.stroke(64, 140, 80);
      pg.strokeWeight(1);
      pg.noFill();
    } else {
      pg.noStroke();
      pg.fill(60, 255, 180);
    }
    // Start rendering toroid or helix
    latheAngle = 0;
    for (int i=0; i<=segments; i++) {
      pg.beginShape(QUAD_STRIP);
      for (int j=0; j<=pts; j++) {
        if (i>0) {
          pg.vertex(vertices2[j].x, vertices2[j].y, vertices2[j].z);
        }
        vertices2[j].x = cos(radians(latheAngle))*vertices[j].x;
        vertices2[j].y = sin(radians(latheAngle))*vertices[j].x;
        vertices2[j].z = vertices[j].z;
        // optional helix offset
        if (isHelix) {
          vertices[j].z+=helixOffset;
        } 
        pg.vertex(vertices2[j].x, vertices2[j].y, vertices2[j].z);
      }
      // create extra rotation for helix
      if (isHelix) {
        latheAngle += 720.0/segments;
      } else {
        latheAngle += 360.0/segments;
      }
      pg.endShape();
    }
    pg.endDraw();
  }
}
