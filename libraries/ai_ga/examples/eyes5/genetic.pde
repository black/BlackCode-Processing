abstract class GeneticAlgorithm{
  int [] MultiplyDeBruijnBitPosition = {                                // log(2) calculation table for traitBits
    0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 
    31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9
  };
  int chromosomeLength;                                                 // Number of traits a Chromosome has
  int poolSize;                                                         // Number of Chromosomes to breed  
  int generation = 0;                                                   // Cycles or age of genetic algorithm  
  float target;                                                         // Ideal Chromosomes are bred towards
  float crossOverRate = 0.7;                                            // Frequency of dna cross over
  float mutationRate = 0.001;                                           // Frequency of mutation
  float errorThreshold = 0.001;                                         // Tolerance of deviation from target
  boolean found = false;                                                // Discovery of target
  Vector trait;                                                         // Translation of traits
  int traitBits;                                                        // Number of traits pow(2)
  int [] mutationMask;                                                  // Mutation bit masking table
  int [] crossoverMask;                                                 // Dna swapping bit mask table
  Vector newGenePool;                                                   // Temporary variable for new gene pool
  Vector genePool;                                                      // Current state of breeding colony
  Vector solutions;                                                     // Store of chromosomes that equal target
  
  GeneticAlgorithm(int chromosomeLength, int poolSize){
    this.chromosomeLength = chromosomeLength;
    this.poolSize = poolSize;
  }
  
  void init(Vector trait){
    newGenePool = new Vector(0);
    genePool = new Vector(0);
    solutions = new Vector(0);
    this.trait = trait;
    for(int i = 0; i < poolSize; i++){
      genePool.add(new Chromosome());
    }
    setTraitBits(bitLog(trait.size()) + 1);
  }
  
  void propagate(){
    generation++;
    newGenePool.clear();
    for(int i = genePool.size() - 1; i > -1; i -= 2){
      Chromosome p1 = selection(genePool);
      Chromosome p2 = selection(genePool);
      p1.crossOver(p2);
      p1.mutate();
      p2.mutate();
      scoreFitness(p1);
      scoreFitness(p2);
      newGenePool.add(p1);
      newGenePool.add(p2);
    }
    genePool.addAll(newGenePool);
  }

  // Chromosome class
   abstract void scoreFitness(Chromosome chromosome);

  class Chromosome implements Comparable{
    int [] dna = new int[chromosomeLength];
    float score;

    Chromosome(){
      for(int i = 0; i < chromosomeLength; i++){
        dna[i] = (int)(Math.random()*trait.size());
      }
      scoreFitness(this);
    }

    Chromosome(int [] dna){
      this.dna = dna;
    }

    int compareTo(Object o) {
      Chromosome c = (Chromosome)o;
      return new Float(score).compareTo(new Float(c.score));
    }
    
    void crossOver(Chromosome mate){
      if((float)Math.random() > crossOverRate){
        return;
      }
      int traitPos = (int)(Math.random()*dna.length);
      int bitPos = (int)(Math.random()*crossoverMask.length);
      int temp = dna[traitPos];
      dna[traitPos] = dna[traitPos]^((dna[traitPos]^mate.dna[traitPos])&crossoverMask[bitPos]);
      mate.dna[traitPos] = mate.dna[traitPos]^((mate.dna[traitPos]^temp)&crossoverMask[bitPos]);
      for(int i = traitPos+1; i < dna.length; i++){
        temp = dna[i];
        dna[i] = mate.dna[i];
        mate.dna[i] = temp;
      }
    }

    void mutate(){
      for(int i = 0; i < dna.length; i++){
        for(int j = 0; j < traitBits; j++){
          if((float)Math.random() <= mutationRate){
            dna[i] ^= mutationMask[j];
          }
        }
      }
    }
    void kill(){
      genePool.remove(genePool.indexOf(this));
    }
  }

  Chromosome selection(Vector genePool){
    float totalFitness = 0.0;
    for(int i = genePool.size() - 1; i > -1; i--){
      Chromosome gene = (Chromosome)genePool.get(i);
      totalFitness += gene.score;
    }
    float qualify = (float)(Math.random()*totalFitness);
    totalFitness = 0.0;
    for(int i = genePool.size() - 1; i > -1; i--){
      Chromosome gene = (Chromosome)genePool.get(i);
      totalFitness += gene.score;
      if(totalFitness >= qualify){
        genePool.remove(i);
        return gene;
      }
    }
    return (Chromosome)genePool.remove(genePool.size()-1);
  }
  
  Chromosome makeChromosome(){
    Chromosome temp = new Chromosome();
    genePool.add(temp);
    poolSize++;
    return temp;
  }
  
  Chromosome makeChromosome(int [] dna){
    int [] dnaTemp = new int[dna.length];
    System.arraycopy(dna, 0, dnaTemp, 0, dna.length);
    Chromosome temp = new Chromosome(dnaTemp);
    genePool.add(temp);
    poolSize++;
    return temp;
  }

  // Dynamic modification methods

  void setChromosomeLength(int chromosomeLength){
    if(this.chromosomeLength != chromosomeLength){
      for(int i = 0; i < genePool.size(); i++){
        int [] newDna = new int[chromosomeLength];
        Chromosome temp = (Chromosome)genePool.get(i);
        if(temp.dna.length < chromosomeLength){
          System.arraycopy(temp.dna, 0, newDna, 0, temp.dna.length);
          for(int j = temp.dna.length; j < chromosomeLength; j++){
            newDna[j] = (int)(Math.random()*trait.size());
          }
        }
        else if(temp.dna.length > chromosomeLength){
          System.arraycopy(temp.dna, 0, newDna, 0, chromosomeLength);
        }
        temp.dna = newDna;
        genePool.set(i, temp);
      }
    }
    this.chromosomeLength = chromosomeLength;
  }

  void setPoolSize(int poolSize){
    if(this.poolSize < poolSize){
      for(int i = this.poolSize; i < poolSize; i++){
        genePool.add(new Chromosome());
        println("check add setPoolSize-> genePool.size("+genePool.size()+"), poolSize="+poolSize);
      }
    } 
    else if(this.poolSize > poolSize){
      genePool.setSize(poolSize);
      println("check remove setPoolSize-> genePool.size("+genePool.size()+"), poolSize="+poolSize);
    }        
    this.poolSize = poolSize;
  }

  void setMutationRate(float mutationRate){
    this.mutationRate = mutationRate;
  }

  void setCrossOverRate(float crossOverRate){
    this.crossOverRate = crossOverRate;
  }

  void setErrorThreshold(float errorThreshold){
    this.errorThreshold = errorThreshold;
  }

  void setFound(boolean found){
    this.found = found;
  }

  void setTrait(Vector trait){
    this.trait = trait;
    setTraitBits(bitLog(trait.size()) + 1);
  }

  void setTraitBits(int traitBits){
    mutationMask = new int[traitBits];
    for(int i = 0; i < traitBits; i++){
      mutationMask[i] = 1<<i;
    }
    crossoverMask = new int[traitBits+1];
    for(int i = 0; i < crossoverMask.length; i++){
      crossoverMask[i] = (1<<i)-1;
    }
    this.traitBits = traitBits;
  }

  void setGenePool(Vector genePool){
    this.genePool = genePool;
  }

  void setSolutions(Vector solutions){
    this.solutions = solutions;
  }
  
  void sortGenePool(){
    Collections.sort(genePool);
  }

  // Query methods
  
  int chromosomeLength(){
    return chromosomeLength();
  }
  
  int poolSize(){
    return poolSize();
  }
  
  float mutationRate(){
    return mutationRate();
  }
  
  float crossOverRate(){
    return crossOverRate();
  }
  
  float errorThreshold(){
    return errorThreshold();
  }
  
  float target(){
    return target;
  }
  
  Vector trait(){
    return trait;
  }
  
  int traitBits(){
    return traitBits;
  }
  
  Vector genePool(){
    return genePool;
  }
  
  Vector solutions(){
    return solutions;
  }

  boolean found(){
    return found;
  }

  void printBinary(Chromosome c){
    for(int i = 0; i < c.dna.length; i++){
      print(binary(c.dna[i],traitBits)+" ");
    }
    print("\n");
  }

  // Utilities

  int bitLog(int num){
    num |= num >> 1; // first round down to power of 2 
    num |= num >> 2;
    num |= num >> 4;
    num |= num >> 8;
    num |= num >> 16;
    num = (num >> 1) + 1;
    return MultiplyDeBruijnBitPosition[(num * 0x077CB531) >> 27];
  }
}
