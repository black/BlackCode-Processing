//------------------------------------------Graph Plotter -----------------------------------
void BrainWave() {
  noFill();
  strokeWeight(2);
  //---------HighA--------------------
  beginShape();
  stroke(#FF0000);
  for (int i=0;i<HighA.size();i++) {
    Points P = (Points) HighA.get(i);
    float t = map(P.y, min_AH, max_AH, 0, height/2);
    vertex(P.x, t);
    P.x-=5/10.0;
  }
  endShape();
  //---------LowA-------------------
  beginShape();
  stroke(#FFAF00);
  for (int i=0;i<LowA.size();i++) {
    Points P = (Points) LowA.get(i);
    float t = map(P.y, min_AL, max_AL, 0, height/2);
    vertex(P.x, t);
    P.x-=5/10.0;
  }
  endShape();
  //---------HighB-------------------
  beginShape();
  stroke(#0081FF);
  for (int i=0;i<HighB.size();i++) {
    Points P = (Points) HighB.get(i);
    float t = map(P.y, min_BH, max_BH, 0, height/2);
    vertex(P.x, t);
    P.x-=5/10.0;
  }
  endShape();
  //---------LowB-------------------
  beginShape();
  stroke(#00B9FF);
  for (int i=0;i<LowB.size();i++) {
    Points P = (Points) LowB.get(i);
    float t = map(P.y, min_BL, max_BL, 0, height/2);
    vertex(P.x, t);
    P.x-=5/10.0;
  }
  endShape();
  //---------MidG--------------------
  beginShape();
  stroke(#14FF00);
  for (int i=0;i<MidG.size();i++) {
    Points P = (Points) MidG.get(i);
    float t = map(P.y, min_GM, max_GM, 0, height/2);
    vertex(P.x, t);
    P.x-=5/10.0;
  }
  endShape();
  //---------LowG-------------------
  beginShape();
  stroke(#F6FF00);
  for (int i=0;i<LowG.size();i++) {
    Points P = (Points) LowG.get(i);
    float t = map(P.y, min_GL, max_GL, 0, height/2);
    vertex(P.x, t);
    P.x-=5/10.0;
  }
  endShape();
  //---------DeltaX-------------------
  beginShape();
  stroke(#333431);
  for (int i=0;i<DeltaX.size();i++) {
    Points P = (Points) DeltaX.get(i);
    float t = map(P.y, min_D, max_D, 0, height/2);
    vertex(P.x, t);
    P.x-=5/10.0;
  }
  endShape();
  //---------ThetaX-------------------
  beginShape();
  stroke(0);
  for (int i=0;i<ThetaX.size();i++) {
    Points P = (Points) ThetaX.get(i);
    float t = map(P.y, min_T, max_T, 0, height/2);
    vertex(P.x, t);
    P.x-=5/10.0;
  }
  endShape();
}

