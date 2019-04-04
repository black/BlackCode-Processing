package tactu5;



import processing.core.*;
import java.lang.Cloneable.*;
/*
 * TACTU5 by Alessandro Capozzo  
 * www.abstract-codex.net
 */
/**
 * Sequence class allows to create musical phrases or patterns with Tactu5.
 * It's possible to pass an array of Note to the constructor or to add Note objects afterwards.
 * This class automatically clones the Note objects passed.
 * 
 * 
 * @author Alessandro Capozzo  - www.abstract-codex.net - summer - autumn 2007
 *
 */



public class Sequence implements Cloneable, T5Containers {
	  private Note[] sequence;
	  private float[] relativeTime;
	  private float time;
	  private int id;
	  public Sequence () {

	    this.sequence=new Note[0];
	    this.relativeTime=new float[0];
	    time=0;
	    id=0;

	  }
	  public Sequence (Note[] inseq) {
	    time=0;
	    this.sequence=new Note[inseq.length];
	    // add value to time array
	    for ( int j=0; j<inseq.length; j++ ) {

	      updateTime ( inseq[j].getDuration() );

	    }
	    PApplet.arraycopy(inseq,sequence);

	  }
	  // standard clone method
	  public Object clone () {

	    Sequence o = null;

	    try {

	      o = (Sequence) super.clone();

	    } 
	    catch  (CloneNotSupportedException e) {

	      System.out.println ( " Some error in cloning Sequence Object ");

	    }
	    o.sequence = (Note[]) o.sequence.clone();

	    for ( int ci = 0; ci < o.sequence.length ; ci++ ) {

	      o.sequence[ci] = (Note) this.sequence[ci].clone();
	    }

	    return o;

	  }
	  // add a note to the sequence array
	  
	  /**
	   * Add a note to Sequence internal array.
	   */
	 public void addNote (Note inote) {

	    sequence=(Note[]) PApplet.expand ( sequence, sequence.length + 1 ) ;
	    sequence[sequence.length - 1]=(Note) inote.clone();
	    updateTime(inote.getDuration());
	  }
	  // add value to relative time array
	 public void updateTime(float t){

	    relativeTime=(float[]) PApplet.expand (relativeTime, relativeTime.length + 1);
	    relativeTime[ relativeTime.length - 1 ] = time;
	    time+=t;

	  }
	 /**
	   * Return the time in milliseconds of a specific elements of the internal array.
	   * 
	   * @return float
	   */
	 public float getTimeAtStep(int n) {

	    return relativeTime[n];

	  }
	  // return the notes array
	 /**
	   * Return the internal array of Note objects.
	   * 
	   * @return Note[]
	   */
	 public Note[] getSequence () {

	    return sequence;

	  }
	  // set the sequence durations to the values of an float array
	 
	 /**
	   * Set all the duration values of internal Note array.
	   * It's posible to pass a single float value or an array.
	   * 
	   */
	  public void setDuration ( float[] inArray ) {

	    int tempSeqLength = sequence.length;
	    int tempInputArray = inArray.length;
	    int inputIndex = 0;

	    for ( int event = 0; event < tempSeqLength; event++ ) {

	      if ( inputIndex >= tempInputArray) {

	        inputIndex = 0;

	      }

	      sequence[event].setDuration(inArray[inputIndex]);

	      inputIndex++;


	    } 



	  }
	  public void setDuration ( float inValue ) {

		    int tempSeqLength = sequence.length;
		    

		    for ( int event = 0; event < tempSeqLength; event++ ) {

		      sequence[event].setDuration(inValue);

		    } 



		  }
		 /**
	   * Set all the sustain values of internal Note array.
	   * 
	   * 
	   */
	  public void setSustain ( float[] inArray ) {

	    int tempSeqLength = sequence.length;
	    int tempInputArray = inArray.length;
	    int inputIndex = 0;

	    for ( int event = 0; event < tempSeqLength; event++ ) {

	      if ( inputIndex >= tempInputArray) {

	        inputIndex = 0;

	      }

	      sequence[event].setSustain(inArray[inputIndex]);

	      inputIndex++;


	    } 



	  }
	  public void setSustain ( float inValue ) {

		    int tempSeqLength = sequence.length;
		    

		    for ( int event = 0; event < tempSeqLength; event++ ) {

		      sequence[event].setSustain(inValue);

		    } 



		  }
		 /**
	   * Set all the pan values of internal Note array.
	   * 
	   * 
	   */
	  public void setPan ( float[] inArray ) {

	    int tempSeqLength = sequence.length;
	    int tempInputArray = inArray.length;
	    int inputIndex = 0;

	    for ( int event = 0; event < tempSeqLength; event++ ) {

	      if ( inputIndex >= tempInputArray) {

	        inputIndex = 0;

	      }

	      sequence[event].setPan(inArray[inputIndex]);

	      inputIndex++;


	    } 



	  }
	  public void setPan( float inValue ) {

		    int tempSeqLength = sequence.length;
		    

		    for ( int event = 0; event < tempSeqLength; event++ ) {

		      sequence[event].setPan(inValue);

		    } 



		  }
		 /**
	   * Set all the velocity values of internal Note array.
	   * 
	   * 
	   */
	  public void setVelocity ( float[] inArray ) {

	    int tempSeqLength = sequence.length;
	    int tempInputArray = inArray.length;
	    int inputIndex = 0;

	    for ( int event = 0; event < tempSeqLength; event++ ) {

	      if ( inputIndex >= tempInputArray) {

	        inputIndex = 0;

	      }

	      sequence[event].setVelocity(inArray[inputIndex]);

	      inputIndex++;


	    } 



	  }
	  public void setVelocity ( float inValue ) {

		    int tempSeqLength = sequence.length;
		    

		    for ( int event = 0; event < tempSeqLength; event++ ) {

		      sequence[event].setVelocity(inValue);

		    } 



		  }
		 /**
	   * Set all the frequency values of internal Note array.
	   * 
	   * 
	   */
	  public void setFrequency ( float[] inArray ) {

	    int tempSeqLength = sequence.length;
	    int tempInputArray = inArray.length;
	    int inputIndex = 0;

	    for ( int event = 0; event < tempSeqLength; event++ ) {

	      if ( inputIndex >= tempInputArray) {

	        inputIndex = 0;

	      }

	      sequence[event].setDuration(inArray[inputIndex]);

	      inputIndex++;


	    } 



	  }
	  public void setFrequency ( float inValue ) {

		    int tempSeqLength = sequence.length;
		    

		    for ( int event = 0; event < tempSeqLength; event++ ) {

		      sequence[event].setFrequency(inValue);

		    } 



		  }
		 /**
	   * Set all the channel values of internal Note array.
	   * 
	   * 
	   */
	  public void setChannel ( int[] inArray ) {

	    int tempSeqLength = sequence.length;
	    int tempInputArray = inArray.length;
	    int inputIndex = 0;

	    for ( int event = 0; event < tempSeqLength; event++ ) {

	      if ( inputIndex >= tempInputArray) {

	        inputIndex = 0;

	      }

	      sequence[event].setChannel(inArray[inputIndex]);

	      inputIndex++;


	    } 



	  }
	  public void setChannel( int inValue ) {

		    int tempSeqLength = sequence.length;
		    

		    for ( int event = 0; event < tempSeqLength; event++ ) {

		      sequence[event].setChannel(inValue);

		    } 



		  }
	  /**
	   * This method modifies all events duration in the sequence
	   * 
	   * @param coeficent a float value
	   */
	  public void scaleDuration(float coeficent) {
		  
		  int tempSeqLength = sequence.length;
		 

		    for ( int event = 0; event < tempSeqLength; event++ ) {

		   

		      sequence[event].setDuration(sequence[event].getDuration()*coeficent);

		     


		    } 
		  
		  
	 
	  }
	  /**
	   * This method modifies all events duration in the sequence
	   * 
	   * @param coeficent a float value
	   */
   public void scaleSustain(float coeficent) {
		  
		  int tempSeqLength = sequence.length;
		 

		    for ( int event = 0; event < tempSeqLength; event++ ) {

		   

		      sequence[event].setDuration(sequence[event].getDuration()*coeficent);

		     


		    } 
		  
		  
	 
	  }

	  private void setFloatArray(float[] inArray) {



	  }
	  /**
	   * Return a float array with all velocity values in the sequence.
	   * 
	   * 
	   */
	  public float[] getVelocity() {

	    int tempSeqLength = sequence.length;
	    float[] tempFreqArray = new float[tempSeqLength];

	    for ( int event = 0 ; event < tempSeqLength ; event++ ) {

	      tempFreqArray[event] = sequence[event].getVelocity();

	    }

	    return tempFreqArray;

	  }
	  // return a float array with all pan values in the sequence
	  /**
	   * Return a float array with all pan values in the sequence.
	   * 
	   * @return float[]
	   */
	  public float[] getPan() {

	    int tempSeqLength = sequence.length;
	    float[] tempFreqArray = new float[tempSeqLength];

	    for ( int event = 0 ; event < tempSeqLength ; event++ ) {

	      tempFreqArray[event] = sequence[event].getPan();

	    }

	    return tempFreqArray;

	  }
	  /**
	   * Return a float array with all sustain values in the sequence.
	   * 
	   * @return float[]
	   */
	  public float[] getSustain() {

	    int tempSeqLength = sequence.length;
	    float[] tempFreqArray = new float[tempSeqLength];

	    for ( int event = 0 ; event < tempSeqLength ; event++ ) {

	      tempFreqArray[event] = sequence[event].getSustain();

	    }

	    return tempFreqArray;

	  }
	  /**
	   * Return a float array with all duration values in the sequence.
	   * 
	   * @return float[]
	   */
	  public float[] getDuration() {

	    int tempSeqLength = sequence.length;
	    float[] tempFreqArray = new float[tempSeqLength];

	    for ( int event = 0 ; event < tempSeqLength ; event++ ) {

	      tempFreqArray[event] = sequence[event].getDuration();

	    }

	    return tempFreqArray;

	  }
	  /**
	   * Rreturn a float array with all frequency values in the sequence.
	   * 
	   * @return float[]
	   */
	  public float[] getFrequency() {

	    int tempSeqLength = sequence.length;
	    float[] tempFreqArray = new float[tempSeqLength];

	    for ( int event = 0 ; event < tempSeqLength ; event++ ) {

	      tempFreqArray[event] = sequence[event].getFrequency();

	    }

	    return tempFreqArray;

	  }
	  // return the total notes number
	  /**
	   * Return the number of sequence notes.
	   * 
	   * @return float[]
	   */
	  public int getSequenceLength () {

	    return sequence.length;

	  }
	 public int getContainerNum() {

	    return sequence.length;

	  }
	  // return the total sequence duration in milliseconds
	 /** Return the sequence duration in milliseconds.
	   * 
	   * @return float[]
	   */
	 public float getSequenceMilliSec () {
	    float milli=0;
	    for (int index=0; index<sequence.length; index++) {

	      milli+=sequence[index].getDuration();


	    }
	    return milli;

	  }
	  /** return a specific note of the sequence
	   * 
	   */
	  public Note getNote(int index) {

	    return sequence[index];

	  }
	  // set object id, useful for finding instances in aggregator class
	  protected void setID ( int newid) {

	    id = newid;


	  }
	  // get instance, default value == 0 
	  protected int getID () {

	    return id;


	  }
	  
	 // public void inversion() 
	}