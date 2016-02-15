/* Demonstration file for AULib (Andrew's Utilities library).
* Download and documentation:
*   http://www.imaginary-institute.com/resources/AULibrary/AULibrary.php
* Show use of equal spacing of points along an AUCurve
* Version 1 - Andrew - Sept 6, 2014
*/

import AULib.*;

int NumSegments = 5;
float[][] Knots;
boolean DrawKnots = true;
boolean DrawBeads = true;
int BeadsPerSegment = 5;
boolean UniformlySpacedBeads = false;
float KnotRadius, BeadRadius;
color KnotColor = color(255, 0, 0);
color BeadColor = color(0, 255, 255);
float MouseStartX, MouseStartY;
float KnotStartX, KnotStartY;
boolean Dragging = false;
int DragKnotIndex;

void setup() {
  size(500, 500);
  KnotRadius = width * .03;
  BeadRadius = width * .02;  
  makeCurve();
  println("Help:");
  println("click and drag on any knot (in red) to change the curve");
  println("k: toggle drawing of knots (in red)");
  println("b: toggle drawing of the beads along the curve (in cyan)");
  println("u: toggle whether or not the beads are drawn uniformly spaced");
  println("s: decrease the number of beads");
  println("S: increase the number of beads");
  println("C: increase the number of curve segments (this generates a new curve");
  println("c: decrease the number of curve segments (this generates a new curve");
}

void makeCurve() {
  Knots = new float[NumSegments][2];
  // make a roughly circular knot with just a bit of wiggle
  for (int i=0; i<Knots.length; i++) {
    float a = norm(i, 0, Knots.length);
    float theta = TWO_PI * (a + ((1./(2*Knots.length))*random(-1,1)));
    float r = width * random(.35, .45);
    Knots[i][0] = (width/2.)+(r*cos(theta));
    Knots[i][1] = (height/2.)+(r*sin(theta));
  }
}

void draw() {
  background(255);
  noFill();
  beginShape();
    for (int i=0; i<Knots.length+3; i++) {
      int j = i % Knots.length;
      curveVertex(Knots[j][0], Knots[j][1]);
    }
  endShape();
  if (DrawKnots) {
    for (int i=0; i<Knots.length; i++) {
      fill(KnotColor);
      ellipse(Knots[i][0], Knots[i][1], 2*KnotRadius, 2*KnotRadius);
    }
  }
  if (DrawBeads) {
    fill(BeadColor);
    if (UniformlySpacedBeads) {
      float[][] floatKnots = new float[Knots.length][2];
      for (int i=0; i<Knots.length; i++) {
        floatKnots[i][0] = Knots[i%Knots.length][0];
        floatKnots[i][1] = Knots[i%Knots.length][1];
      }
      AUCurve curve = new AUCurve(floatKnots, 2, true);
      int numBeads = BeadsPerSegment * NumSegments;
      for (int b=0; b<numBeads; b++) {
        float t = norm(b, 0, numBeads);
        float bx = curve.getX(t);
        float by = curve.getY(t);
        ellipse(bx, by, 2*BeadRadius, 2*BeadRadius);
      }
      
    } else {
      for (int i=0; i<Knots.length; i++) {
        for (int b=0; b<BeadsPerSegment; b++) {
          float t = norm(b, 0, BeadsPerSegment);
          int v0 = (i  )%Knots.length;
          int v1 = (i+1)%Knots.length;
          int v2 = (i+2)%Knots.length;
          int v3 = (i+3)%Knots.length;
          float bx = curvePoint(Knots[v0][0], Knots[v1][0], Knots[v2][0], Knots[v3][0], t);
          float by = curvePoint(Knots[v0][1], Knots[v1][1], Knots[v2][1], Knots[v3][1], t);
          ellipse(bx, by, 2*BeadRadius, 2*BeadRadius);
        }
      }
    }
  }
}

void keyPressed() {
  if (key == 'k') { DrawKnots = !DrawKnots; println("DrawKnots = "+DrawKnots); }
  if (key == 'b') { DrawBeads = !DrawBeads; println("DrawBeads = "+DrawBeads); }
  if (key == 'u') { UniformlySpacedBeads = !UniformlySpacedBeads; println("UniformlySpacedBeads = "+UniformlySpacedBeads); }
  if (key == 's') { BeadsPerSegment = max(3, BeadsPerSegment-1); println("BeadsPerSegment = "+BeadsPerSegment); }
  if (key == 'S') { BeadsPerSegment++; println("BeadsPerSegment = "+BeadsPerSegment); }
  if (key == 'c') { NumSegments = max(3, NumSegments-1); println("NumSegments = "+NumSegments); makeCurve(); }
  if (key == 'C') { NumSegments++; println("NumSegments = "+NumSegments); makeCurve(); }
}

void mousePressed() {
  Dragging = false;
  MouseStartX = mouseX;
  MouseStartY = mouseY;
  for (int i=0; i<Knots.length; i++) {
    float d = dist(Knots[i][0], Knots[i][1], MouseStartX, MouseStartY);
    if (d < KnotRadius) {
      Dragging = true;
      KnotStartX = Knots[i][0];
      KnotStartY = Knots[i][1];
      DragKnotIndex = i;
      return;
    }
  }
}

void mouseDragged() {
  if (!Dragging) return;
  float dx = mouseX - MouseStartX;
  float dy = mouseY - MouseStartY;
  Knots[DragKnotIndex][0] = KnotStartX + dx;
  Knots[DragKnotIndex][1] = KnotStartY + dy;
}

void mouseReleased() {
  Dragging = false;
}

