// Extension of GeneticAlgorithm abstraction

class Soup extends GeneticAlgorithm{
  Soup(){
    super(6, 0);
    setMutationRate(0.1);
    Vector temp = new Vector();
    for(int i = 0; i < 8; i++){
      temp.add(new Integer(i));
    }
    init(temp);
  }
  void scoreFitness(Chromosome chromosome){
    chromosome.score = 1.0;
  }
}
