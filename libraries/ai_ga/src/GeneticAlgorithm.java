/*
*  GeneticAlgorithm abstract class, Aaron Steed 2008
*
*  There are too many changes to list from the 2006 version - it's safe to say that the
*  prior version was riddled with bugs. This version is much better.
*
*  GA Code developed from:
*  <http://www.ai-junkie.com/files/GA.java>
*  <http://www.red3d.com/cwr/evolve.html>
*  <http://www.genarts.com/karl/papers/siggraph91.html>
*  <http://www.shiffman.net/teaching/the-nature-of-code/ga/>
*
*  Bitwise operations taken from:
*  <http://graphics.stanford.edu/~seander/bithacks.html>
*
*/

package ai_ga;

import java.util.ArrayList;
import java.lang.Math;
import java.util.Collections;

public class GeneticAlgorithm{
  
  public int dnaLength;                                                 // Number of dna values in a Chromosome
  public int binaryLength;                                              // The sum of all the binary digits
  public int poolSize;                                                  // Number of Chromosomes
  public int valueLength;                                               // Number of dna values
  public int generation = 0;                                            // Iterations of propagate()  
  public float spliceRate = 0.7f;                                       // Frequency of dna splicing
  public float mutationRate = 0.001f;                                   // Frequency of mutation()
  public float errorThreshold = 0.0f;                                   // Tolerance of deviation from target
  public boolean found = false;                                         // Discovery of target
  public int valueBits;                                                 // Number of binary digits to each dna value
  public boolean subInteger;                                            // Whether the GA operates at sub-integer levels
  public ArrayList newGenePool;                                         // Temporary variable for new gene pool
  public ArrayList genePool;                                            // Current state of breeding colony
  public ArrayList solutions;                                           // Store of chromosomes that equal target
  
  // Mutation bit masking table
  public static int [] singleBitMask = {1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072, 262144, 524288, 1048576, 2097152, 4194304, 8388608, 16777216, 33554432, 67108864, 134217728, 268435456, 536870912, 1073741824, -2147483648};
  // Dna swapping bit mask table
  public static int [] spliceMask = {0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535, 131071, 262143, 524287, 1048575, 2097151, 4194303, 8388607, 16777215, 33554431, 67108863, 134217727, 268435455, 536870911, 1073741823, 2147483647};

  public GeneticAlgorithm(int dnaLength, int valueLength, int poolSize, boolean subInteger){
    this.dnaLength = dnaLength;
    this.poolSize = poolSize;
    this.valueLength = valueLength;
    this.subInteger = subInteger;
    // check for errors here
    checkConstructorArguments();
    newGenePool = new ArrayList();
    genePool = new ArrayList();
    solutions = new ArrayList();
    for(int i = 0; i < poolSize; i++){
      genePool.add(new Chromosome(this));
    }
    setValueBits(base2(valueLength - 1));
  }
  
  // give a constructor to those who don't want the the sub integer stuff
  
  public GeneticAlgorithm(int dnaLength, int valueLength, int poolSize){
    this(dnaLength, valueLength, poolSize, false);
  }
  
  // Throw some errors when people get it wrong to illustrate what they did wrong
  
  protected void checkConstructorArguments(){
    if(dnaLength < 0) throw new NegativeArraySizeException("You cannot set dna length to a negative size, dnaLength was set to "+dnaLength);
    if(poolSize < 0) throw new NegativeArraySizeException("You cannot create a gene pool of a negative size, poolSize was set to "+poolSize);
    if(valueLength < 2) throw new ValueLengthException("You cannot use a number of values less than 2, what would be the point? Negative numbers of values will cause an array exception. valueLength was set to "+valueLength);
  }
  
  // Advance the genetic algorithm one generation
  
  public void propagate(){
    if(poolSize % 2 == 1) throw new ArrayIndexOutOfBoundsException("The propagate method can only operate on a genePool that is divisible by 2. poolSize is currently "+poolSize);
    generation++;
    newGenePool.clear();
    for(int i = poolSize; i > 0; i -= 2){
      Chromosome p1 = selection(genePool);
      Chromosome p2 = selection(genePool);
      p1.splice(p2);
      p1.mutate();
      p2.mutate();
      p1.score = scoreFitness(p1);
      p2.score = scoreFitness(p2);
      if(p1.score <= errorThreshold && p1.score >= -errorThreshold){
        found = true;
        solutions.add(p1.copy());
      }
      if(p2.score <= errorThreshold && p2.score >= -errorThreshold){
        found = true;
        solutions.add(p2.copy());
      }
      newGenePool.add(p1);
      newGenePool.add(p2);
    }
    genePool.addAll(newGenePool);
    poolSize = genePool.size();
  }

  // Chromosome class methods
  
  // scoreFitness must be overriden by extending this class to establish the nature of the GA
  
  public float scoreFitness(Chromosome o){
      return 1;
  }

  // Select a Chromosome based on it's fitness

  public Chromosome selection(ArrayList genePool){
    float totalFitness = 0.0f;
    for(int i = poolSize - 1; i > -1; i--){
      Chromosome gene = (Chromosome)genePool.get(i);
      totalFitness += gene.score;
    }
    float qualify = (float)(Math.random()*totalFitness);
    totalFitness = 0.0f;
    for(int i = poolSize - 1; i > -1; i--){
      Chromosome gene = (Chromosome)genePool.get(i);
      totalFitness += gene.score;
      if(totalFitness >= qualify){
        genePool.remove(i);
        poolSize--;
        return gene;
      }
    }
    poolSize--;
    return (Chromosome)genePool.remove(poolSize);
  }

  // Create a randomised Chromosome

  public Chromosome createChromosome(){
    Chromosome temp = new Chromosome(this);
    genePool.add(temp);
    poolSize++;
    return temp;
  }

  public Chromosome createChromosome(int [] dna){
    int [] dnaTemp = new int[dna.length];
    System.arraycopy(dna, 0, dnaTemp, 0, dna.length);
    Chromosome temp = new Chromosome(this, dnaTemp);
    genePool.add(temp);
    poolSize++;
    return temp;
  }
  
  // kill a chromosome
  
  public Chromosome removeChromosome(Chromosome o){
    poolSize--;
    return (Chromosome)genePool.remove(genePool.indexOf(o));
  }
  
  // Chromosome copying method
  
  public Chromosome copyChromosome(Chromosome o){
    Chromosome temp = o.copy();
    genePool.add(temp);
    poolSize++;
    return temp;
  }

  // Dynamic modification methods

  public void setDnaLength(int dnaLength){
    if(dnaLength < 0) throw new NegativeArraySizeException("You cannot set dna length to a negative size, dnaLength was set to "+dnaLength);
    if(this.dnaLength != dnaLength){
      for(int i = 0; i < poolSize; i++){
        int [] newDna = new int[dnaLength];
        Chromosome temp = (Chromosome)genePool.get(i);
        if(temp.dna.length < dnaLength){
          System.arraycopy(temp.dna, 0, newDna, 0, temp.dna.length);
          for(int j = temp.dna.length; j < dnaLength; j++){
            newDna[j] = (int)(Math.random()*valueLength);
          }
        }
        else if(temp.dna.length > dnaLength){
          System.arraycopy(temp.dna, 0, newDna, 0, dnaLength);
        }
        temp.dna = newDna;
        genePool.set(i, temp);
      }
    }
    this.dnaLength = dnaLength;
    binaryLength = valueBits * dnaLength;
  }
  
  // Modify the pool size - helps to do it this way to keep poolSize updated

  public void setPoolSize(int poolSize){
    if(poolSize < 0) throw new NegativeArraySizeException("You cannot create a gene pool of a negative size, poolSize was set to "+poolSize);
    if(this.poolSize < poolSize){
      for(int i = this.poolSize; i < poolSize; i++){
        genePool.add(new Chromosome(this));
      }
    } 
    else if(this.poolSize > poolSize){
      // thank you Sun for not implementing setSize() or removeRange(), you cretins
      while(genePool.size() > poolSize) genePool.remove(genePool.size()-1);
    }
    this.poolSize = poolSize;
  }
  
  // Sets number of values, if subInteger then sets valueBits as well
  
  public void setValueLength(int valueLength){
    if(valueLength < 2) throw new ValueLengthException("You cannot use a number of values less than 2, what would be the point? Negative numbers of values will cause an array exception. valueLength was set to "+valueLength);
    this.valueLength = valueLength;
    if(subInteger){
      setValueBits(base2(valueLength - 1));
    }
  }
  
  // Set the amount of binary digits we need to contain the values

  public void setValueBits(int valueBits){
    if(valueBits < 1) throw new ValueLengthException("You cannot use a negative number of value bits or zero bits, valueBits was set to "+valueBits);
    if(valueBits > 32) throw new ValueLengthException("You cannot allocate a number of bits greater than 32, 32 bits are all the bits available in the int datatype, valueBits was set to "+valueBits);
    this.valueBits = valueBits;
    valueLength = 1 << valueBits;
    binaryLength = valueBits * dnaLength;
  }

  public void sortGenePool(){
    Collections.sort(genePool);
  }
  
  // utility for finding the number of digits a number has in base 2
  
  public static final int base2(int num){
    return (int)Math.round(Math.log(num)/Math.log(2)+0.5);
  }
  
}
