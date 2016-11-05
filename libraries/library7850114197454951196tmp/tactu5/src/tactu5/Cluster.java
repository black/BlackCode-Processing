package tactu5;


import processing.core.*;
/*
 * TACTU5 by Alessandro Capozzo  
 * www.abstract-codex.net
 */
/**
 * Cluster class allows to create chord instances with Tactu5.
 * It's possible to pass an array of Note to the constructor or to add Note objects afterwards.
 * This class automatically clones the Note objects passed.
 * 
 * 
 * @author Alessandro Capozzo  - www.abstract-codex.net - summer - autumn 2007
 *
 */

public class Cluster implements Cloneable, T5Containers {
	  protected Note[] cluster;
	  private float duration;

	 public Cluster () {
	    cluster=new Note[0];
	    duration=0;
	  }
	 public Cluster (Note[] inseq) {
		  
	    this.cluster=new Note[inseq.length];
	    
	    PApplet.arraycopy(inseq,cluster);
	    
	    

	  }
	  // standard clone method
	  public Object clone () {
	    Cluster o = null;

	    try {

	      o = (Cluster) super.clone();

	    } 
	    catch  (CloneNotSupportedException e) {

	      System.out.println ( " Some error in cloning Cluster Object ");

	    }
	    o.cluster = (Note[]) o.cluster.clone();

	    for ( int ci = 0; ci < o.cluster.length ; ci++ ) {

	      o.cluster[ci] = (Note) this.cluster[ci].clone();
	    }

	    return o;

	  }
	  
	  // Add a note to the sequence array
	  
	  /** Add a note to the cluster.
	   * 
	   * */
	  
	  public void addNote (Note inote) {

	    cluster=(Note[]) PApplet.expand ( cluster, cluster.length + 1 ) ;
	    cluster[cluster.length - 1]= (Note)inote.clone();
	    setDuration();

	  }
	  /** Get the elements number of the cluster.
	   * 
	   * */
	  public int getClusterNumber() {

	    return  cluster.length;  

	  }
	  public int getContainerNum() {

	    return cluster.length;

	  }
	  /** Return a Note, an int parameter defines its position.
	   *
	   *@return Note
	   * */
	  public Note getNote (int n) {

	    return cluster[n];

	  }
     
	  // find the longer notes in the cluster
	  private void setDuration(){

	    float d=0;
	    for (int i=0; i<cluster.length; i++) {

	      if(cluster[i].getDuration()>d) {

	        d=cluster[i].getDuration();

	      }

	    }
	    duration=d;



	  }
	  /**
	   * Force cluster duration in milliseconds.
	   * 
	   * 
	   */
	  void forceDuration(float d){

	    duration=d;



	  }
	  void addSequence() {




	  }
	  /**
	   * Get cluster duration in milliseconds.
	   * 
	   * @return float
	   */
	  public float getDuration() {


	    return duration;


	  }


	}