package tactu5;


import processing.core.*;

import java.lang.Cloneable.*;
/*
 * TACTU5 by Alessandro Capozzo  
 * www.abstract-codex.net
 */


public class InternalSequence extends ClusterSequence implements Cloneable {
	  
	 public InternalSequence() {
	       
	       super();
	    
	    }
	   public Object clone () {
	          InternalSequence o = null;
	          
	           try {
	             
	               o = (InternalSequence) super.clone();
	             
	             } catch  (CloneNotSupportedException e) {
	               
	                 System.out.println ( " Some error in cloning Cluster Object ");
	               
	            }
	            //
	            o.clustersequence = (Cluster[]) o.clustersequence.clone();
	           
	           for ( int ci = 0; ci < o.clustersequence.length ; ci++ ) {
	             
	                  o.clustersequence[ci] = (Cluster) this.clustersequence[ci].clone();
	             }
	           
	            return o;
	             
	         }
	   
	  public void addCluster (Cluster inote, int pos, float t) {
	           
	               clustersequence=(Cluster[]) PApplet.expand ( clustersequence, clustersequence.length + 1 ) ;
	               clustersequence[clustersequence.length - 1]= (Cluster) inote.clone();
	               updateTime( inote.getDuration());
	           
	      }
	   
	      // insert new cluster in the sequence updating time array
	  public void insertCluster (Cluster inote, int pos, float t){
	      
	        clustersequence = (Cluster[]) AbstractUtilities.AbstractExpand ( clustersequence, pos );
	        clustersequence [pos] =(Cluster) inote.clone();
	        relativeTime = (float[]) AbstractUtilities.AbstractExpand ( relativeTime, pos );
	        relativeTime [pos] = t;
	         
	        
	        // change duration of clusters effected by this operation
	          adjustDuration();
	        for ( int h = 0 ; h < relativeTime.length ; h++ ) {
	          
	           
	          
	          }
	          
	    }
	   // adjust duration in the clusters sequence
	  private void adjustDuration () {
	    
	        
	    for ( int j=0; j < relativeTime.length;  j++ ) {
	          
	        if( j < relativeTime.length-1) {
	             
	            clustersequence[j].forceDuration( relativeTime[j+1] - relativeTime[j] );
	            
	           
	           } else {
	             
	             // last element into the sequence
	           
	          //  clustersequence[j].forceDuration( 250);
	             
	          }
	           
	      
	      
	      
	      }

      }
	  

 }