package tactu5;

/*
 * TACTU5 by Alessandro Capozzo  
 * www.abstract-codex.net
 */

public class Tactu5Controller {
    
    // Tactu5Controller is useful for manage data flows from user and sequencer avoiding concorency
    private boolean tactu5ControlData;   
    private InternalSequence tactu5NewSeq;
    public Tactu5Controller () {
        tactu5ControlData = false;
     }
     
     // new sequence to put in the sequencer
     
   public synchronized void setData ( InternalSequence s ) {
         if(!tactu5ControlData) {
          tactu5NewSeq=(InternalSequence) s.clone();
          tactu5ControlData = true;
          }
      }
      
      // give feedback of succesful data changing
     public synchronized boolean setDataAndFeedBack ( InternalSequence s ) {
         if(!tactu5ControlData) {
          tactu5NewSeq=(InternalSequence) s.clone();
          tactu5ControlData = true;
          }
          return tactu5ControlData;
      }
      
      // check if the sequences has been updated
      // it's called by sequencer class inside run() method
     public synchronized boolean isThereNewData () {
          
           return tactu5ControlData; 
       
      }
      
      // get the updated sequence
      
     public synchronized InternalSequence getData () {
          tactu5ControlData=false;
          return tactu5NewSeq;  
       
      }


}
