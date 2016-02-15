/*
*  Chromosome class, Aaron Steed 2008
*  Requires registering with a GeneticAlgorithm extension so they can breed
*  Instanced with myGeneticAlgorithm.createChromosome() it loads the Chromosome into the
*  genePool of the GA
*
*/

package ai_ga;

import java.lang.Comparable;
import java.lang.Math;

public class Chromosome implements Comparable{
  
  public GeneticAlgorithm ga;
  public int [] dna;
  public float score;

  public Chromosome(GeneticAlgorithm ga){
    this.ga = ga;
    dna = new int[ga.dnaLength];
    for(int i = 0; i < ga.dnaLength; i++){
      dna[i] = (int)(Math.random()*ga.valueLength);
    }
    score = ga.scoreFitness(this);
  }

  public Chromosome(GeneticAlgorithm ga, int [] dna){
    this.ga = ga;
    this.dna = dna;
    ga.dnaLength = dna.length;
    score = ga.scoreFitness(this);
  }
  
  // Collections sort comparison

  public int compareTo(Object o) {
    Chromosome c = (Chromosome)o;
    return new Float(score).compareTo(new Float(c.score));
  }
  
  // Standard random splicing method, called by propagate()

  public void splice(Chromosome mate){
    if((float)Math.random() > ga.spliceRate){
      return;
    }
    splice(mate, (int)(Math.random()*ga.dnaLength), ga.subInteger ? (int)(Math.random()*(ga.valueBits)) : ga.valueBits - 1);
  }
  
  // Controlled splice, non subInteger, for those who need to
  
  public void splice(Chromosome mate, int dnaPos){
    splice(mate, dnaPos, ga.valueBits - 1);
  }
  
  // Controlled splice, for the really pedantic
  
  public void splice(Chromosome mate, int dnaPos, int bitPos){
    if(dnaPos < 0 || dnaPos > dna.length - 1) throw new ArrayIndexOutOfBoundsException("A cross over position was attempted that is not within the length of the dna:"+dnaPos+" dna length is:"+dna.length);
    if(bitPos < 0 || bitPos > ga.valueBits - 1) throw new ArrayIndexOutOfBoundsException("A sub integer cross over position was attempted that is not within the length of the bits available, "+bitPos+" valueBits is currently:"+ga.valueBits);
    if(ga.subInteger){
      // Using the masking operation: x = x^((x^y)&mask)
      // the bits from the end of y are overwritten on to x
      int temp = dna[dnaPos];
      dna[dnaPos] = dna[dnaPos]^((dna[dnaPos]^mate.dna[dnaPos])&GeneticAlgorithm.spliceMask[bitPos]);
      mate.dna[dnaPos] = mate.dna[dnaPos]^((mate.dna[dnaPos]^temp)&GeneticAlgorithm.spliceMask[bitPos]);
    }
    for(int i = dnaPos+1; i < ga.dnaLength; i++){
      int temp = dna[i];
      dna[i] = mate.dna[i];
      mate.dna[i] = temp;
    }
  }
  
  // And for spliting the binary with one number...
  
  public void spliceBinary(Chromosome mate, int bitPos){
    if(bitPos < 0 || bitPos > ga.binaryLength - 1) throw new ArrayIndexOutOfBoundsException("A cross over position was attempted that is not within the length of the dna binary:"+bitPos+" binary length is:"+ga.binaryLength);
    splice(mate, bitPos / ga.valueBits, bitPos % ga.valueBits);
  }

  // Flip a random bit here and there or randomise a number to reflect genetic mutation

  public void mutate(){
    for(int i = 0; i < ga.dnaLength; i++){
      if(ga.subInteger){
        // Using a mask with only one bit set, the operation: x^mask
        // results in one bit in the number being flipped: bit = bit == 0 ? 1 : 0
        for(int j = 0; j < ga.valueBits; j++){
          if((float)Math.random() <= ga.mutationRate){
            dna[i] ^= GeneticAlgorithm.singleBitMask[j];
          }
        }
      }
      else{
        if((float)Math.random() <= ga.mutationRate){
          dna[i] = (int)(Math.random()*ga.valueLength);
        }
      }
    }
  }
  
  // Generate an isolated copy
  
  public Chromosome copy(){
    int [] dnaTemp = new int[ga.dnaLength];
    System.arraycopy(dna, 0, dnaTemp, 0, ga.dnaLength);
    return new Chromosome(ga, dnaTemp);
  }
  
}
