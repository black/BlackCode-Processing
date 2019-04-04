package tactu5;


import processing.core.*;
import java.lang.reflect.*;
import java.lang.Thread;
/*
 * TACTU5 by Alessandro Capozzo  
 * www.abstract-codex.net
 */




//****************************************//
//this class is the core of the sequencer //
//it' initialized in Tactu5 class         //
//as a private instance                   //
//****************************************//

public class Tactu5SimpleSequencer extends Thread {

   InternalSequence timeline;
   InternalSequence newtimeline;
   int index;
   int rest;
   int limit;
   Tactu5Controller o1;
   PApplet parent;
   
   ////////////////////////////
   //
   ///////////////////////////
   // records of the time in the sequence
   float milliTime;
   float newMilliTime;
   
   
   
   
   
   
   
   //*********************************//
   //       CONTROLL VARIABLES        //
   //*********************************//
   
   // check if the thread has been started 
   private boolean startControl;
   // check if the thread should be stopped
   private boolean stopControl;
   // set loop mode
   private boolean isThisLoop;
   //it is true when a new sequence income
   private boolean isNewSeq;
   
   //**********************************//
   // checking for noteReceiver app method
   Method fancyEventMethod;
   
   //checking for looping sequencer method
   Method loopingEventMethod;
   
   
   public Tactu5SimpleSequencer (PApplet p,InternalSequence s, Tactu5Controller o, boolean b) {
      
	  parent=p;
	  newtimeline=s;
      limit=newtimeline.getClusterNumber()-1;
      index=0;
      rest=0;
      setPriority(10);
      milliTime=0;
      //check parent method////////////////////////
      
	   try {
		     fancyEventMethod  =  parent.getClass().getMethod("noteReceiver",
		                                    new Class[] { Note.class});
		         
		    } catch (Exception e) {
		    	System.out.println("ALLERT noteReceiver(Note) method is not set!");
		    }
		
		    try {
			     loopingEventMethod  =  parent.getClass().getMethod("looping",
			                                    null);
			         
			    } catch (Exception e) {
			    	System.out.println("ALLERT loop method is not set!");
			    }
      
      //--- control variables ----
      isThisLoop=b;
      startControl=false;
      stopControl=false;
      
      
      
      //--------------------------
      // object controller reference
      o1=o;
      
   }
   // start thread or reactivate after a wait command
   public synchronized void  startMe () {
     
     if (! startControl ) {
        
         // first start
       
          start();
          startControl=true;
         
         } else {
           
           // restart thread after wait()
           notify();
           
         }
     
    }
    // set controll variable stopControll to true
    // in run() method it allows to call setMeWait() method
    public void stopMe () {
        
        
        stopControl=true;
        
     
    }
    // freze thread until start() will be called again
    private synchronized void setMeWait () {
             
             stopControl=false;
             try{
               wait();
             } catch (InterruptedException e) {
               System.out.println("error");
               throw new RuntimeException (e);
               
            }
      
     }
   
   // check for incoming new data
   private boolean checkMe () {
       
           return o1.isThereNewData ();    
           
     }
    // set timeline index  to 0 
    public void rewind () {
         
         milliTime = 0 ;
         index = 0;
      
      }
    
    private synchronized void  checkAndSetSeq() {
         
         // check if a new sequence is ready in controller obj and load it            
         
         if (checkMe ()) {
            
            float oldT;
             // get the "time position" of running current sequence  
            if ( timeline != null ) {
              
              oldT = timeline.getTimeAtStep(index);
                   
           } else {
              
                 oldT = 0;
             
             }
             
             // ? grughf ?
            
            timeline = null;
            timeline =(InternalSequence) o1.getData().clone();
            limit=timeline.getClusterNumber()-1;
            
            
             if (index!=0) {
            
               // if new sequence time length is minor of old time position
               if (oldT > timeline.getTotalTime()) {
                 
                      rewind();
                     
                 
                 } else {
                     
                   for ( int ct = 0; ct < timeline.getClusterNumber(); ct ++ )  {
                        
                        float newTimeAt = timeline.getTimeAtStep(ct);
                        
                        if ( newTimeAt == oldT ) {
                             
                             // define new timeline index
                          
                             index = ct;
                             break;
                          
                          } else if ( newTimeAt > oldT ) {
                            
                             index = ct;
                             
                                   // make a rest to fit to new rythimc pattern
                                     
                                     try{
                                       Float restF  = new Float ( newTimeAt-oldT);
                                       long newRest = (long) restF.longValue();
                                       sleep(newRest);
                                       } catch (InterruptedException e) {
                                         System.out.println("errore");
                                         throw new RuntimeException (e);
               
                                       }
                             
                                 break;
                            
                            
                           }
                          
                     
                     
                     }
                   
                   
                   
                  }
                           
              
              
              }
          
          
          
          }
          
      
      
      
      }
   // main loop
   public void run () {
      while(true) {
       
       // check for iterruption command
       if(stopControl){
           
            setMeWait();
           
         }
         
        // check for a new incoming sequence
        
        
        checkAndSetSeq();
        
        
        
        
        rest= (int) timeline.getCluster(index).getDuration();
        
       
       //  println(rest+"__________________"+index+"_"+timeline.getCluster(index).getNote(0).getFrequency());
        milliTime += rest;
        
        //println ("++++++++++"+ milliTime+"_"+index);
    //play any notes in the cluster 
        if (fancyEventMethod != null) {
   	    	
   	    	
        for (int h=0;  h<timeline.getCluster(index).getClusterNumber(); h++) {
        	
        	   
       	    
       	  
       	    	 
       	    try {
       	      fancyEventMethod.invoke(parent, new Object[] { timeline.getCluster(index).getNote(h) });
       	        } catch (Exception e) {
       	         System.err.println("Disabling fancyEvent() for "  +
       	                         " because of an error.");
       	          e.printStackTrace();
       	      fancyEventMethod = null;
       	    }
          
             // parent.osend(timeline.getCluster(index).getNote(h).getFrequency(),timeline.getCluster(index).getNote(h).getSustain());
           
            // print("_"+timeline.getCluster(index).getNote(h).getFrequency());
           }
        }
        
        
        if(index>=limit) {
        	
          // call looping method inside processing app
        	if(loopingEventMethod !=null) {
        	  try {
        		  loopingEventMethod.invoke(parent, null);
           	        } catch (Exception e) {
           	         System.err.println("Disabling fancyEvent() for "  +
           	                         " because of an error.");
           	          e.printStackTrace();
           	      fancyEventMethod = null;
           	    }
        	}
        	
            rewind();
            
            // if loop mode is disabled it makes thread to stop
            
            if (! isThisLoop ) {
                   
                  setMeWait();                 
                 
              
              }
            
          } else {
            
            index++;
          }
            try{
            sleep(rest);
             } catch (InterruptedException e) {
               System.out.println("errore");
               throw new RuntimeException (e);
               
            }
        
       }
   }

}
   
   

