package tactu5;

import processing.core.*;
import java.lang.Cloneable.*;
/*
 * TACTU5 by Alessandro Capozzo  
 * www.abstract-codex.net
 */

/** Aggregator class generate objects to feed the sequencer.
 * It's possible to pass it Sequence and ClusterSequence datatype.
 * You could create many Aggregator objects and use them to change internal sequencer contents in real time
 * 
 * 
 * 
 * @author Alessandro
 *
 */


//generate clusters sequences for the sequencer 
public class Aggregator {
  // the container of all the stored sequence 
  private Sequence[] sequencesContainer;
  // the container of all the stored sequence of clusters
  private ClusterSequence[] clustersequencesContainer;
  // the result of aggregration process
  private InternalSequence score;
  // unique id store
  private int idSequenceCounter;
  private int idClusterSequenceCounter;

  public Aggregator () {

    // init containers
    sequencesContainer= new Sequence [0];
    clustersequencesContainer=new ClusterSequence[0];
    score=new InternalSequence();
    // init counters
    idSequenceCounter = 0;
    idClusterSequenceCounter = 0;


  }

  /** Add sequence with no offset declaired 
   * 
   * 
   * @param s Sequence datatype
   */ 
  public void addSequence ( Sequence s) {

    addSequence ( s, 0.0f );


  }

  /** Add simple sequence to Aggregator container, it allows to define a time offset in millieconds.
   * 
   * 
   * @param s  Sequence datatype.
   * @param offSet float, milliseconds.
   */

  public void addSequence ( Sequence s, float offSet ) {

    // trasform sequence in clustersequence 

    int seqLength = s.getSequenceLength ();
    ClusterSequence tempCSeq = new ClusterSequence();
    Cluster tempCluster ;

    for (int inxS = 0 ; inxS < seqLength ; inxS ++ ) {

      tempCluster = new Cluster ();
      tempCluster.addNote (s.getNote(inxS));
      tempCSeq.addCluster(tempCluster);


    }

    addClusterSequence ( tempCSeq, offSet );



  }
  /** Add ClusterSequence datatype to Agregator container.
   * 
   * 
   * @param cs
   */
  public void addClusterSequence ( ClusterSequence cs ) {

    addClusterSequence ( cs, 0.0f );

  }
/** Add ClusterSequence to Aggregator container, it allows to define a time offset in millieconds.
 * 
 * 
 * @param cs  ClusterSequence datatype
 * @param offSet float, offset in milliseconds
 */
  public void addClusterSequence ( ClusterSequence cs, float offSet) {
    // add sequence to the score
    if(score.getClusterNumber()>0){

      insertClusterSequence(cs,offSet);

    } 
    else {
      // fill the score with first sequnce     

        for( int j=0; j<cs.getClusterNumber(); j++) {

        score.addCluster(cs.getCluster(j));

      }

    }

  }

  // ad a clusters sequence to the score, starting from a offSet time
  private void insertClusterSequence ( ClusterSequence cs, float offSet) {

    //
    int scoreIndex=0;
    float clusterTime;
    for ( int i=0; i < cs.getClusterNumber() ; i++ ) {
      clusterTime=cs.getTimeAtStep(i)+offSet;
      System.out.println(clusterTime+"eccolo");
      for (int j=scoreIndex; j <  score.getClusterNumber() ; j++ ) {
        // ERRORE CONTROLLARE
        if ( clusterTime ==  score.getTimeAtStep(j) ) {
          // add notes to existing cluster
          //aggiunger elemnti al cluster esistente, per ora forza bruta
          // score.
          
          score.addToCluster(cs.getCluster(i),j);
          scoreIndex=j;
          break;

        } 
        else if ( j==score.getClusterNumber()-1 )  {
          
          // it creates a new cluster in the internal sequence
          score.insertCluster(cs.getCluster(i),j+1,clusterTime);
          scoreIndex=j;
          break;

        } 
        else if (( j==0 )&&(clusterTime<score.getTimeAtStep(0)))  {
        
          // it creates a new cluster in the internal sequence
          score.insertCluster(cs.getCluster(i),0,clusterTime);
          scoreIndex=j;
          break;

        } 
        else if (( clusterTime >  score.getTimeAtStep(j) )&&( clusterTime <  score.getTimeAtStep(j+1) )) {
          
          // it creates a new cluster in the internal sequence
          score.insertCluster(cs.getCluster(i),j+1,clusterTime);
          scoreIndex=j;
          break;

        } 

      }
    }        



  }

  int addAndStoreSequence ( Sequence s) {


    idSequenceCounter++;
    
     addSequence(s , 0.0f);
     
    
    return idSequenceCounter;
  }
  int addAndStoreClusterSequence ( ClusterSequence cs) {
    //int id;
    return 1;
  }
  int addAndStoreSequence ( Sequence s, float offSet) {


    idSequenceCounter++;
    addSequence(s,offSet);
    return idSequenceCounter;
  }
  int addAndStoreClusterSequence ( ClusterSequence cs, float offSet) {
    //int id;
    return 1;
  } 
  /** Return the aggragated score, it's necessary to call this method to feed Tactu5 internal sequencer.
   * 
   * 
   * @return
   */
  public InternalSequence getScore() {

    return score;


  }
  /** Reset all.
   * 
   * 
   * 
   */
  public void resetAll () {

    new Aggregator ();

  }


}