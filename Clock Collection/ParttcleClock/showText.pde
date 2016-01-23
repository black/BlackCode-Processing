void creatGraphicsH() {
  //  pgH.loadPixels();
  pgH.background(0);
  pgH.beginDraw();
  pgH.fill(255);
  pgH.textFont(f);
  pgH.textAlign(CENTER);
  pgH.textSize(80);
  pgH.text(nf(h, 2), pgH.width>>1, 30+pgH.height>>1);
  pgH.endDraw();
  //  pgH.updatePixels();
}

void creatGraphicsM() {
  //  pgM.loadPixels();
  pgM.background(0);
  pgM.beginDraw();
  pgM.fill(255);
  pgM.textFont(f);
  pgM.textAlign(CENTER);
  pgM.textSize(80);
  pgM.text(nf(m, 2), pgM.width>>1, 30+pgM.height>>1);
  pgM.endDraw();
  //  pgM.updatePixels();
}

void creatGraphicsS() {
  //  pgS.loadPixels();
  pgS.background(0);
  pgS.beginDraw();
  pgS.fill(255);
  pgS.textFont(f);
  pgS.textAlign(CENTER);
  pgS.textSize(80);
  pgS.text(nf(s, 2), pgS.width>>1, 30+pgS.height>>1);
  pgS.endDraw();
  //  pgS.updatePixels();
}

