final static int [] colorTable = {
#C88C00, #ABC800, #00C869, #00C8C8, #0032C8, #8000CA, #CA00BE, #C80043
};

final static int STARING = 0;
final static int FEEDING = 1;
final static int CLONING = 2;
final static int DYING = 3;

// Automata driven by brownian motion Particles on springs attached to nucleus eye.
// They eat Plankton till fat enough to clone themselves and alter their dna from
// eaten Plankton. Eventually they die spitting out Plankton containing their dna
// (plus mutations). The state of the automata is expressed via the eye (dna is
// a barcode around iris, dilation - feeding, contraction - cloning, roll up -
// dying).

class Amoeba{
  Vector skin, engine;
  Particle nucleus;
  Soup.Chromosome gene;
  int col, highCol, lowCol, blinkTimer, blinkTime, skinNum, newSkin, cloneTime, lifeTime, searchRadius;
  int state = 0;
  float mySize;
  float lid = 1.5;
  float iris = 0.5;
  float irisTemp, irisTheta;
  boolean feeding = false;
  boolean cloning = false;
  boolean dying = false;
  float eyeTemp = 0.0;
  Amoeba(float x, float y, Soup.Chromosome gene){
    skin = new Vector();
    engine = new Vector();
    this.gene = gene;
    for(int i = 0; i < gene.dna.length; i++){
      switch(i){
        // color
      case 0:
        col = color(150);
        if(gene.dna[i] < colorTable.length){
          col = colorTable[gene.dna[i]];
        }
        break;
        // engines (1 - 4?)
      case 1:
        for(int j = 0; j < (gene.dna[i] % 4) + 1; j++){
          float ex = x + random(-mySize * 2.0, mySize * 0.1);
          float ey = y + random(-mySize * 2.0, mySize * 0.1);
          engine.add(ether.makeParticle(1.0, ex, ey, 0.0));
        }
        break;
        // starting skin nodes (5 - 8?)
      case 2:
        skinNum = (gene.dna[i] % 4) + 5;
        newSkin = skinNum;
        break;
        //blink frequency
      case 3:
        blinkTime = 40 + gene.dna[i] * 10;
        break;
        //search radius
      case 4:
        searchRadius = 160 + gene.dna[i] * 10;
        break;
        //life span (in blinks)
      case 5:
        lifeTime = 20 + gene.dna[i] * 5;
        break;
      }
    }
    highCol = col + color(50, 50, 50);
    lowCol = color(red(col) - 50, green(col) - 50, blue(col) - 50);
    mySize = skinNum * 4;
    irisTemp = iris;
    irisTheta = -HALF_PI;
    cloneTime = 0;
    blinkTimer = blinkTime;
    for(int i = 0; i < skinNum; i++){
      float cx = x + (cos((TWO_PI/skinNum)*i) * mySize * 0.1);
      float cy = y + (sin((TWO_PI/skinNum)*i) * mySize * 0.1);
      skin.add(ether.makeParticle(1.0, cx, cy, 0.0));
    }
    nucleus = ether.makeParticle(1.0, x, y, 0.0);
    makeSkin(0);
  }
  void draw(){
    // Animation, sensors and state machine
    if(blinkTimer > 0){
      blinkTimer--;
    }
    else if(blinkTimer == 0){
      lid = lid * 0.5;
      if(lid < 0.01){
        blinkTimer--;
      }
    }
    else if(blinkTimer == -1){
      lid += (1.5 - lid) * 0.5;
      if(lid > 1.49){
        blinkTimer = blinkTime;
        if(lifeTime > 0){
          lifeTime--;
        }
        switch(state){
        case STARING:
          if(lifeTime <= 0){
            state = DYING;
          }
          break;
        case CLONING:
          if(cloneTime > 0){
            cloneTime--;
            makeSkin(-1);
          } 
          else {
            int r = (int)random(engine.size());
            Particle temp = (Particle)engine.get(r);
            if(temp.position().x() < (wide * 0.5) - 10 && temp.position().y() < (high * 0.5) - 10 && temp.position().x() > -(wide * 0.5) + 10 && temp.position().y() > -(high * 0.5) + 10){
              state = STARING;
              iris = 0.5;
              Soup.Chromosome babe = soup.makeChromosome(gene.dna);
              amoeba.add(new Amoeba(temp.position().x(), temp.position().y(), babe));
            }
          }
          break;
        case DYING:
          if(skin.size() > 0){
            makeSkin(-1);
            Soup.Chromosome seed = soup.makeChromosome(gene.dna);
            seed.mutate();
            int engineChoice = (int)random(engine.size());
            Particle temp = (Particle)engine.get(engineChoice);
            float theta = atan2(temp.position().y() - nucleus.position().y(), temp.position().x() - nucleus.position().x());
            plankton.add(new Plankton(temp.position().x(), temp.position().y(), theta, seed));
          } 
          else {
            for(int i = 0; i < engine.size(); i++){
              Particle temp = (Particle)engine.get(i);
              temp.kill();
            }
            nucleus.kill();
            amoeba.remove(amoeba.indexOf(this));
          }
          break;
        }
      }
    }
    if(abs(mySize - eyeTemp) > 0.01){
      eyeTemp += (mySize - eyeTemp) * 0.1;
    }
    if(nucleus.position().x() > wide * 0.5){
      nucleus.addVelocity(-1.0, 0.0, 0.0);
    }
    if(nucleus.position().y() > high * 0.5){
      nucleus.addVelocity(0.0, -1.0, 0.0);
    }
    if(nucleus.position().x() < -wide * 0.5){
      nucleus.addVelocity(1.0, 0.0, 0.0);
    }
    if(nucleus.position().y() < -high * 0.5){
      nucleus.addVelocity(0.0, 1.0, 0.0);
    }
    if(abs(iris - irisTemp) > 0.01){
      irisTemp += (iris - irisTemp) * 0.5;
    }
    cloneCheck();
    sensePlankton();
    // Drawing stuff
    strokeWeight(mySize * 0.04);
    stroke(lowCol);
    fill(col);
    if(skin.size() > 0){
      beginShape(POLYGON);
      for(int i = 0; i < skin.size() + 3; i++){
        Particle temp = (Particle)skin.get(i%skin.size());
        curveVertex(temp.position().x(), temp.position().y());
        if(i < skin.size()){
          ellipse(temp.position().x(), temp.position().y(), mySize * 0.5, mySize * 0.5);
        }
      }
      endShape();
      noStroke();
      fill(highCol);
      beginShape(POLYGON);
      for(int i = 0; i < skin.size() + 3; i++){
        Particle temp = (Particle)skin.get(i%skin.size());
        float theta = atan2(temp.position().y() - nucleus.position().y(), temp.position().x() - nucleus.position().x());
        float sx = nucleus.position().x() + cos(theta) * (dist(temp.position().x(), temp.position().y(), nucleus.position().x(), nucleus.position().y()) * 0.8);
        float sy = nucleus.position().y() + sin(theta) * (dist(temp.position().x(), temp.position().y(), nucleus.position().x(), nucleus.position().y()) * 0.8);
        curveVertex(sx, sy);
        if(i < skin.size()){
          ellipse(sx, sy, mySize * 0.3, mySize * 0.3);
        }
      }
      endShape();
    }
    stroke(lowCol);
    for(int i = 0; i < engine.size(); i++){
      fill(col);
      Particle temp = (Particle)engine.get(i);
      if(random(1.0) > 0.5){
        temp.setVelocity(random(-2.0, 2.0), random(-2.0, 2.0), 0.0);
      }
      line(nucleus.position().x(), nucleus.position().y(), temp.position().x(), temp.position().y());
      ellipse(temp.position().x(), temp.position().y(), mySize * 0.4, mySize * 0.4);
      fill(0);
      ellipse(temp.position().x(), temp.position().y(), mySize * 0.2, mySize * 0.2);
    }
    fill(0);
    boolean alive = true;
    if(state == DYING){
      alive = false;
    }
    bezierEye(nucleus.position().x(), nucleus.position().y(), eyeTemp, irisTemp, irisTheta, alive, eyeTemp * lid, gene.dna, col, highCol, lowCol);
  }
  // Check readiness for cloning
  void cloneCheck(){
    if(skin.size() >= skinNum + newSkin && state == STARING){
      iris = 0.2;
      state = CLONING;
      cloneTime = newSkin;
    }
  }
  // Detects plankton and assigns attraction using a "bagsy" system
  void sensePlankton(){
    if(state == STARING){
      int nearestPlankton = -1;
      float bestDistance = 100000;
      for(int i = 0; i < plankton.size(); i++){
        Plankton temp = (Plankton)plankton.get(i);
        if(!temp.allocated){
          float distance = dist(temp.body.position().x(), temp.body.position().y(), nucleus.position().x(), nucleus.position().y());
          if(distance < searchRadius){
            if(distance < bestDistance){
              bestDistance = distance;
              nearestPlankton = i;
            }
          }
        }
      }
      if(nearestPlankton > -1){
        Plankton temp = (Plankton)plankton.get(nearestPlankton);
        int engineChoice = (int)random(engine.size());
        Particle engineTemp = (Particle)engine.get(engineChoice);
        ether.makeAttraction(engineTemp, temp.body, 300.0, 10.0);
        state = FEEDING;
        temp.allocate(this, engineChoice);
      }
    }
  }
  // Skin node management function. Builds springs and takes care of skin generation / destruction
  void makeSkin(int mod){
    Vector newSkin = new Vector();
    Vector newEngine = new Vector();
    Particle temp = (Particle)skin.get(0);
    for(int i = 0; i < skin.size()+mod; i++){
      if(i < skin.size()){
        temp = (Particle)skin.get(i);
      }
      newSkin.add(ether.makeParticle(1.0, temp.position().x(), temp.position().y(), temp.position().z()));
    }
    for(int i = 0; i < engine.size(); i++){
      temp = (Particle)engine.get(i);
      newEngine.add(ether.makeParticle(1.0, temp.position().x(), temp.position().y(), temp.position().z()));
    }
    for(int i = 0; i < skin.size(); i++){
      temp = (Particle)skin.get(i);
      temp.kill();
    }
    for(int i = 0; i < engine.size(); i++){
      temp = (Particle)engine.get(i);
      temp.kill();
    }
    mySize = newSkin.size() * 4;
    for(int i = 0; i < newSkin.size(); i++){
      temp = (Particle)newSkin.get(i);
      Particle next = (Particle)newSkin.get((i + 1) % newSkin.size());
      ether.makeSpring(nucleus, temp, 0.09, 0.1, mySize * 1.5);
      ether.makeSpring(temp, next, 0.005, 0.1, mySize * 2 * sin(PI / newSkin.size()));
    }
    for(int i = 0; i < newEngine.size(); i++){
      temp = (Particle)newEngine.get(i);
      ether.makeSpring(nucleus, temp, 0.005, 0.005, mySize * 2);
    }
    skin = newSkin;
    engine = newEngine;
  }
}

// Portable eyeball drawing function
void bezierEye(float x, float y, float radius, float dilation, float theta, boolean alive, float lift, int [] dna, color col, color highCol, color lowCol){
  pushMatrix();
  translate(x, y);
  fill(255);
  stroke(200);
  bezierEllipse(0, 0, radius);
  float irisX = cos(theta) * radius * dilation * 0.5;
  float irisY = 0.0;
  if(alive){
    irisY = max(sin(theta) * radius * dilation * 0.5, 0.0);
  }
  else{
    irisY = -radius * dilation * 0.5;
  }
  fill(230, 230, 0);
  stroke(200, 200, 0);
  ellipse(irisX, irisY, radius, radius);
  int spokes = dna.length * soup.traitBits;
  float rad = TWO_PI / spokes;
  stroke(80,80,0);
  for(int i = 0; i < dna.length; i++){
    for(int j = 0; j < soup.traitBits; j++){
      if((dna[i] & soup.mutationMask[j]) > 0){
        float ix = irisX + cos(rad * (j + i * soup.traitBits)) * radius * 0.5;
        float iy = irisY + sin(rad * (j + i * soup.traitBits)) * radius * 0.5;
        line(irisX, irisY, ix, iy);
      }
    }
  }
  fill(0);
  ellipse(irisX, irisY, radius * dilation, radius * dilation);
  fill(255);
  noStroke();
  pushMatrix();
  rotate(-HALF_PI * 0.5);
  ellipse(0, -radius * 0.4, radius * 0.3, radius * 0.9);
  popMatrix();
  fill(col);
  stroke(lowCol);
  beginShape(POLYGON);
  vertex(radius, 0);
  bezierVertex(radius, radius * 0.5, radius * 0.5, radius - lift, 0, radius - lift);
  bezierVertex(-radius * 0.5, radius - lift, -radius, radius * 0.5, -radius, 0);
  bezierVertex(-radius, -radius * 0.5, -radius * 0.5, -radius, 0, -radius);
  bezierVertex(radius * 0.5, -radius, radius, -radius * 0.5, radius, 0);
  endShape();
  fill(highCol);
  noStroke();
  pushMatrix();
  rotate(-HALF_PI * 0.5);
  ellipse(0, -radius * 0.7, radius * 0.6, radius * 0.3);
  popMatrix();
  popMatrix();
}

// bezierEye uses this to draw base white ellipse
void bezierEllipse(float x, float y, float radius){
  beginShape(POLYGON);
  vertex(x + radius, y);
  bezierVertex(x + radius, y + (radius * 0.5), x + (radius * 0.5), y + radius, x, y + radius);
  bezierVertex(x - (radius * 0.5), y + radius, x - radius, y + (radius * 0.5), x - radius, y);
  bezierVertex(x - radius, y - (radius * 0.5), x - (radius * 0.5), y - radius, x, y - radius);
  bezierVertex(x + (radius * 0.5), y - radius, x + radius, y - (radius * 0.5), x + radius, y);
  endShape();
}
