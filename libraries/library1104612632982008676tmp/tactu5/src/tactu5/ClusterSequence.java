package tactu5;


import processing.core.*;
import java.lang.Cloneable.*;
/*
 * TACTU5 by Alessandro Capozzo  
 * www.abstract-codex.net
 */

/**
 * ClusterSequence class allows to create chord sequences with Tactu5.
 * It's possible to pass an array of Cluster datatype to the constructor or to add Cluster objects afterwards.
 * This class automatically clones the Cluster objects passed.
 * 
 * 
 * @author Alessandro Capozzo  - www.abstract-codex.net - summer - autumn 2007
 *
 */
public class ClusterSequence {

	  protected Cluster[] clustersequence;
	  protected float[] relativeTime;
	  private float time;
	  private int id;

	 public ClusterSequence () {

	    clustersequence=new Cluster[0];
	    relativeTime=new float[0];
	    id = 0;

	  }

	 public ClusterSequence (Cluster[] inseq) {
	    this.clustersequence=new Cluster[inseq.length];
	    relativeTime=new float[0];
	    for ( int j=0; j<inseq.length; j++ ) {

	      updateTime ( inseq[j].getDuration() );

	    }
	    PApplet.arraycopy(inseq,clustersequence);

	  }
	  /** Add a Cluster to the sequence array
	   * 
	   * @param inote Cluster datatype
	   */
	 public void addCluster (Cluster inote) {

	    clustersequence=(Cluster[]) PApplet.expand ( clustersequence, clustersequence.length + 1 ) ;
	    // cloning clusters in local array
	    clustersequence[clustersequence.length - 1]= (Cluster) inote.clone();
	    updateTime( clustersequence[clustersequence.length - 1].getDuration());

	  }
	  // add a cluster in a defined position, DA INTEGRARE
	 public void addCluster (Cluster inote, int pos) {

	    clustersequence=(Cluster[]) PApplet.expand ( clustersequence, clustersequence.length + 1 ) ;
	    clustersequence[clustersequence.length - 1]=(Cluster) inote.clone();
	    updateTime( inote.getDuration());

	  }
	  // add Clusters to existing cluster
	 /** Add a Cluster to an existing cluster
	   * 
	   * @param newC Cluster datatype
	   * @param idC int index of Cluster
	   */
	 public void addToCluster (Cluster newC, int idC) {

	    // get old duration
	    float oldD=clustersequence[idC].getDuration();

	    Cluster tempClust = new Cluster ();

	    

	    for ( int k = 0 ; k < clustersequence[idC].getClusterNumber(); k++ ) {

	      tempClust.addNote( clustersequence[idC].getNote(k) );


	    }

	    for (int j=0; j < newC.getClusterNumber(); j++) { 

	      tempClust.addNote(newC.getNote(j));

	    }

	    // force new cluster duration to old duration, it allows to preserve rythmic pattern even if new cluster contains much longer notes

	    clustersequence[idC]=tempClust; 
	    clustersequence[idC].forceDuration(oldD);


	  }
	 protected void updateTime(float t){

	    relativeTime=(float[]) PApplet.expand (relativeTime, relativeTime.length + 1);
	    relativeTime[ relativeTime.length - 1 ] = time;
	    time+=t;
	    

	  }
	 /**
	  * Return the the time in millisecond at the specified event.
	  * 
	  * @param n
	  * @return float datatype, .
	  */
	 public float getTimeAtStep(int n) {

	    return relativeTime[n];

	  }
	  /** Return the sequence time length
	   * 
	   * @return float
	   */
	 public float getTotalTime() {

	    float totTime = 0;
	    for ( int h =0 ; h < clustersequence.length ; h++ ){

	      totTime+=clustersequence[h].getDuration();

	    }

	    return totTime;

	  }
      /** Return the number of all cluster inside the sequence
       * 
       * @return
       */
	 public int getClusterNumber() {

	    return  clustersequence.length;  

	  }
	 /** Retrogradation of the sequence
	  * 
	  */
	 public void  retrogradation () {
		 
		 clustersequence = (Cluster[]) PApplet.reverse (clustersequence);
		 
		 
	 }
     /** 
      * Return the cluster in the specified position.
      * 
      * @param n
      * @return Cluster
      */
	  Cluster getCluster (int n) {

	    return clustersequence[n];

	  }

	  // set object id, useful for finding instances in aggregator class
	  protected void setID ( int newid) {

	    id = newid;


	  }
	  // get instance, default value == 0 
	  protected int getID () {

	    return id;


	  }



	}
