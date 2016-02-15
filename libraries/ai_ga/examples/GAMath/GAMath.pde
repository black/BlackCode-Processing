import ai_ga.*;

// A simple program that finds collections of numbers that add up to a given value
// Aaron Steed 2008

PFont font;
Sum sum;
int initialTarget = 30;
String solution = "";
String target = "target: " + initialTarget;

void setup(){
  size(400, 200);
  // initialise the GA with a number to pack other numbers into
  sum = new Sum(initialTarget);
  font = loadFont("ArialMT-30.vlw");
  textFont(font, 30);
}

void draw(){
  background(0);
  fill(255);
  text("Generations:" + sum.generation, 30, 40);
  text(solution, 30, 80);
  text(target, 30, 120);
  if(sum.found){
    Chromosome temp = (Chromosome)sum.solutions.get(0);
    solution = "solution:" + sum.decode(temp);
  }
  else{
    sum.propagate();
  }
  fill(150);
  text("click to reset", 30, 180);
}

// when the user clicks on the applet, restart the GA

void mousePressed(){
  int rand = (int)random(20,40);
  sum = new Sum(rand);
  target = "target: "+rand;
  solution = "";
}

// This extension of GeneticAlgorithm searches for a combination of 5 numbers that add up to a target value

class Sum extends GeneticAlgorithm{

  // Variables specific to your task can go here

  int target;

  Sum(int target){

    // The first line must be a call to GeneticAlgorithm's constructor
    // The constructor defines (dnaLength, valueLength, poolSize) and initialises default variables
    // If you are using the method propagate() the poolSize must be divisible by 2
    // This algorithm is set to split dna at the binary level, experiment to see if you need this feature

    super(5, 32, 30, true);

    // Update your variables here
    // This is also the place to modify the GeneticAlgorithm's fields

    this.target = target;

  }

  // A method overriding scoreFitness must be defined for this task.
  // The better a Chromosome's dna is at doing it's job, the lower it's fitness should be
  // fitness should be a value ranging from 0 (perfect) to 1 (useless)
  // if you don't override scoreFitness, it will always return 1
  // In the following example the values in dna are being added together to see if they match a target

  float scoreFitness(Chromosome o){
    int total = 0;
    for(int i = 0; i < dnaLength; i++){
      if(o.dna[i] < valueLength){
        total += o.dna[i];
      }
    }
    return (1.0 / target) * (target - total);
  }

  // If your dna values are returning an index in an ArrayList of objects
  // or happen to be char values, you may want to put a decode() method here
  // This method returns a string explaining how the dna was added up

  String decode(Chromosome o){
    int total = 0;
    StringBuffer answer = new StringBuffer();
    for(int i = 0; i < dnaLength; i++){
      if(o.dna[i] < valueLength){
        total += o.dna[i];
        answer.append(o.dna[i]);
        if(i < o.dna.length-1){
          answer.append('+');
        }
      }
    }
    return answer.toString();
  }

}
