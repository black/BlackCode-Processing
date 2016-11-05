package tactu5;


import processing.core.*;
/*
 * TACTU5 by Alessandro Capozzo  
 * ALPHA 0.2
 * www.abstract-codex.net
 */



//an utility class to make various conversion, sum and so on


/**Tactu5Utility class allows to easily create, modify and convert musical materials.
 * 
 * 
 * 
 */

public class Tactu5Utilities {
	
	
	  
  
     public Tactu5Utilities () {
        
          
          
       }
    
    
     /**
      * Passing a frequency and an octave to transpose this method return a new frequency
      * 
      * @param n  float, frequency
      * @param o  int, octave to transpose 
      * @return  float, new frequency
      */ 
    public float noteToFreq ( float n, int o ) {
    	
    	// get note frequency ( oct 0 ) , an octave and return a new frequency
        // frequency of octave 0 are defined in Tactu5Notes interface
                  
         float  freq = n * PApplet.pow ( 2.0000f , o );
         
     
         return freq ;
     
     
     } 
     
     
     
     
     /**
      * This method transpose all notes included in Sequence and Cluster datatypes
      * for a given interval in semitones
      * 
      * 
      * @param c Cluster or Sequence datatype to transpose.
      * @param s  Int, interval defined in semitones.
      */
     
     public void transposeNotes ( T5Containers c, int s ){
              	//it transpose all the  notes  ( Cluster or Sequence, semitones );
               int n = c.getContainerNum();
               float semi = s * 1.0000f;
               for ( int i = 0 ; i < n; i++ ) {
                 
                    c.getNote(i).setFrequency ( c.getNote(i).getFrequency() * PApplet.pow ( 2.0000f, semi / 12) ) ;
                 
                 
                 }
                    
            
       
     }
     
    /**
     * This method transpose a frequency for an interval defined in semitone.
     * It allows to shift oactave too. 
     * 
     * 
     * 
     * @param f float, income frequency
     * @param s float, interval in semitones
     * @param o int, octave.
     * @return float, new frequency.
     */
     
     public float transpose (float f, float s,int o ) {
          
    	 // it transpose frequency f of  s semitones
       
           return  noteToFreq(f * PApplet.pow ( 2.0000f, s / 12),o);
          
       
       
       }
     
    
    private T5Containers frequencyGenerator (Note note, int[] mode, int[] modeDegrees, T5Containers o ){
      
          
         // cloning tonic, all the parameters excepted frequency will not change in the chord
          Note otherDegree;
          otherDegree =(Note) note.clone();
          
          
          int modeLength = mode.length;
          int deegresNumber = modeDegrees.length;
          float tonicFreq = note.getFrequency();
                   
          // mode degreee for new interval
          
          int newD;
          int octave = 0;
          int halftones;
          
          
          for ( int inxD = 0; inxD < modeDegrees.length; inxD ++ ) {
              
              halftones = 0;
              newD = modeDegrees[inxD];
              octave = 0;
              // check for transporting up or down
              
                
              
              if ( newD >= 0 ) {
                
                // which octave?
                   if ( newD > modeLength ) {
                     
                      octave = (int) PApplet.floor(newD/modeLength);
                      
                      
                      newD =  newD - ( octave * modeLength);
                   
                   }
                
                 
                     } else {
                       
                       // which octave?
                   if (PApplet.abs( newD ) >= modeLength ) {
                     
                      octave =  (int) PApplet.floor(newD/modeLength);
                      newD =  (newD - ( octave * modeLength))+modeLength;
                      octave+=-1;
                   } else {
                     
                        newD = newD + modeLength;
                        octave=-1;
                        
                    }

                  }
                 if ( newD != 0) {
                       for ( int inxM = 0; inxM < newD; inxM ++ ) {
                          
                           halftones+=mode[inxM];
                         
                         }
                       
                       otherDegree.setFrequency (  transpose ( tonicFreq, halftones, octave ) );
                    
                      } else {
                      
                      otherDegree.setFrequency(tonicFreq);
                      
               }
            
               o.addNote(otherDegree);
            
            }
  
            return o;
  
    }   
    /////////////////////////////////////////////////////
    // generate chords:                                //
    // note is the tonic                               //
    // modeDegrees are relative to the passed mode     //
    /////////////////////////////////////////////////////
   
   public Cluster chordGenerator (Note note, int[] mode, int[] modeDegrees, Cluster c) {
     
           if ( c == null) {
             
                c = new Cluster();
             
             }
             c =(Cluster) frequencyGenerator ( note, mode, modeDegrees, c );
             
             return c;
       }
    //////////////////////////////////////////////////////
    // generate note sequences:                         //
    // note is the tonic                                //
    // modeDegrees are relative to the passed mode      //
    //////////////////////////////////////////////////////
   
   /**
    * This method generates new sequences contents.
    * 
    * 
    * 
    */
     public Sequence sequenceGenerator (Note note, int[] mode, int[] modeDegrees, Sequence s) {
     
           if ( s == null) {
             
                s = new Sequence();
             
             }
             s =(Sequence) frequencyGenerator ( note, mode, modeDegrees, s );
             
             return s;
       }
   // harmonizer
     
     /**
      * Harmonize sequences.
      * 
      * 
      * 
      */
   public ClusterSequence harmonizer (Sequence s, int[] mode, int[] modeDegrees, ClusterSequence c) {
           
         if ( c == null ) {
           
              c = new ClusterSequence ();
                         
           }
           
           int seqLength = s.getContainerNum();
           
           Cluster newClust;
           
           for (int inxS = 0 ; inxS < seqLength; inxS++) {
                
                 newClust = new Cluster();
                 newClust = (Cluster) frequencyGenerator ( s.getNote(inxS), mode, modeDegrees, newClust );
                 c.addCluster(newClust);    
             }
           
     
           return c;
     
     
     
     }
   
}

