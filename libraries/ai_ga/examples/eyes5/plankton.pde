// Dna carrying Particles. The dna is expressed in the fronds around the Plankton
// Allocation to devouring Amoebas is handled through an internal reference to
// said Amoeba (this makes life really bloody easy).

class Plankton{
  Particle body;
  Amoeba allocatedTo;
  Soup.Chromosome gene;
  int allocationNum = -1;
  color col, highCol, lowCol;
  boolean allocated = false;
  // Random Plankton constructor
  Plankton(){
    body = ether.makeParticle(1.0, random(-wide * 0.5, wide * 0.5), random(-high * 0.5, high * 0.5), 0.0);
    gene = soup.makeChromosome();
    col = colorTable[gene.dna[0]];
    highCol = col + color(50, 50, 50);
    lowCol = color(red(col) - 50, green(col) - 50, blue(col) - 50);
  }
  // Amoeba generated Plankton constructor
  Plankton(float x, float y, float theta, Soup.Chromosome gene){
    body = ether.makeParticle(1.0, x, y, 0.0);
    body.addVelocity(cos(theta), sin(theta), 0.0);
    this.gene = gene;
    col = color(150);
          if(gene.dna[0] < colorTable.length){
            col = colorTable[gene.dna[0]];
          }
    highCol = col + color(50, 50, 50);
    lowCol = color(red(col) - 50, green(col) - 50, blue(col) - 50);
  }
  void draw(){
    // Sensors
    if(allocated){
      if(collide()){
        allocatedTo.makeSkin(1);
        allocatedTo.iris = 0.5;
        allocatedTo.state = STARING;
        allocatedTo.gene.crossOver(gene);
        allocatedTo.newSkin = (allocatedTo.gene.dna[2] % 4) + 5;
        allocatedTo.irisTheta = -HALF_PI;
        gene.kill();
        body.kill();
        plankton.remove(plankton.indexOf(this));
      }
    }
    if(body.position().x() > (wide * 0.5) + 8){
       body.addVelocity(-1.0, 0.0, 0.0);
    }
    if(body.position().y() > (high * 0.5) + 8){
       body.addVelocity(0.0, -1.0, 0.0);
    }
    if(body.position().x() < -(wide * 0.5) - 8){
       body.addVelocity(1.0, 0.0, 0.0);
    }
    if(body.position().y() < -(high * 0.5) - 8){
       body.addVelocity(0.0, 1.0, 0.0);
    }
    // Drawing stuff
    strokeWeight(1.0);
    int spokes = gene.dna.length * soup.traitBits;
    float rad = TWO_PI / spokes;
    stroke(lowCol);
    for(int i = 0; i < gene.dna.length; i++){
      for(int j = 0; j < soup.traitBits; j++){
        if((gene.dna[i] & soup.mutationMask[j]) > 0){
          float ix = body.position().x() + cos(rad * (j + i * soup.traitBits)) * 7;
          float iy = body.position().y() + sin(rad * (j + i * soup.traitBits)) * 7;
          line(body.position().x(), body.position().y(), ix, iy);
        }
      }
    }
    stroke(col);
    fill(highCol);
    ellipse(body.position().x(), body.position().y(), 7, 7);
  }
  // Set to Amoeba
  void allocate(Amoeba allocation, int num){
    allocatedTo = allocation;
    allocatedTo.iris = 0.8;
    allocationNum = num;
    allocated = true;
  }
  // Collision
  boolean collide(){
    Particle temp = (Particle)allocatedTo.engine.get(allocationNum);
    allocatedTo.irisTheta = atan2(body.position().y() - allocatedTo.nucleus.position().y(), body.position().x() - allocatedTo.nucleus.position().x());
    if(dist(temp.position().x(), temp.position().y(), body.position().x(), body.position().y()) < allocatedTo.mySize * 0.3){
      return true;
    }
    return false;
  }
}
