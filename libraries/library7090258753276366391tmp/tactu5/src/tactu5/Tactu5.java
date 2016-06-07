package tactu5;

/*
 * TACTU5 by Alessandro Capozzo  
 * ALPHA 0.2
 * www.abstract-codex.net
 */


import processing.core.*;

/**
 * 
 * 
 * 
 * 
 * 
 * 
 */

/**Tactu5 class allows users to interacts with internal sequencer.
 * 
 * 
 * 
 * 
 * 
 */

public class Tactu5 implements Tactu5ConstantsPack {
       
      // class Tactus contains a sequencer and a controller class    
      PApplet parent;
      public Tactu5SimpleSequencer tactu5equencer;
      public Tactu5Controller tactu5controller;
      int c;
     //  Method fancyEventMethod;
     public Tactu5 (PApplet p, InternalSequence s) {
        
    	 
             this (p, s, T5_NOLOOP );
             
              
        }
  
    public  Tactu5 (PApplet p, InternalSequence s,boolean b ) {
        
        //println ("const" + f );
        //generateSequencer ();
        parent = p;
       // parent.registerDispose(this);
        tactu5controller = new Tactu5Controller();
        System.out.println("TACTU5 Initialized");
        tactu5equencer= new Tactu5SimpleSequencer(p, s,tactu5controller,b);
        tactu5controller.setData ( s );
        
   /*      try {
               fancyEventMethod = parent.getClass().getMethod("fancyEventMethod", new Class[] { String.class });
       } catch (Exception e) {
      // no such method, or an error.. which is fine, just ignore
    }*/
        
       // tactusLoopFinish ();
        
                 
       }
       
/*   public void makeEvent() {
    if (fancyEventMethod != null) {
    try {
      fancyEventMethod.invoke(parent, new Object[] { String.class });
    } catch (Exception e) {
      System.err.println("Disabling fancyEvent() for " + name +
                         " because of an error.");
      e.printStackTrace();
      fancyEventMethod = null;
    }
  }
 }*/
       
       /** Start internal sequencer
        * 
        * 
        */
       
     public void start () {
         
         
            tactu5equencer.startMe();
         
         
         }
         
         /** Stop internal sequencer
          * 
          * 
          */
         
      public void stop () {
           
           
             tactu5equencer.stopMe();
           
           
           }
      /**
       * Eliminate sequencer.
       * 
       * 
       */
      public void closeSequencer() {
    	  
    	  stop();
    	  tactu5equencer=null;
    	  
      }
       
       /** Rewind sequncer to initial loop position.
        * 
        * 
        * 
        * 
        */
           
      public void rewind () {
        
        
             tactu5equencer.rewind(); 
        
        
        }
      
      /** Rewind and stopo sequncer to initial loop position
       * 
       * 
       */
        
      public void rewindAndStop () {
        
             tactu5equencer.stopMe();
             tactu5equencer.rewind(); 
        
        
        }
        
      public void setLoop (boolean b) {
        
        
        }
      
      //  *** this methods have to be implemnted ***
        
       public void setStartLoop ( float sl ) {
         
         
         
         }
       public void setEndLoop ( float sl ) {
         
         
         
         }
      //*******************************************
      
       //update clusters sequence to controller
       //sequncer check controller class for updating content to be played
       
       /**
        * This method allows dynamically changing Tactu5 internal sequencer through 
        * Aggregator output.
        * 
        */
       
     public void updateSequencer (InternalSequence s) {
       
                 tactu5controller.setData( s );
       
       }
       
  
  
}