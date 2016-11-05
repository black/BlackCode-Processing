package tactu5;


import processing.core.*;
import java.lang.Cloneable.*;
/*
 * TACTU5 by Alessandro Capozzo  
 * www.abstract-codex.net
 */
/**
 * Class Note defines a single sound event.<br>
 * It's possible to create a new instance of the object passing a variable numbers of parameters.<br>
 * Though some parameters could seem arbitrary, because Tactu5 does not implement any sound generator,
 * using them in the suggested way allows to utilize properly some advanced methods.
 *
 * @author Alessandro Capozzo  - www.abstract-codex.net - summer - autumn 2007
 * @return      the image at the specified URL
 * @see         T5Notes
 * @see         Sequence
 * @see         Cluster
 */





public class Note implements Cloneable {
    
    private float frequency;
    private float duration;
    private float pan;
    private float velocity;
    private float sustain;
    private String comment;
    private int channel;
    private boolean isRest;
    
      
   
    // constructor for rests
     
    public  Note (float d, boolean r) {
      
          this ( 0, d, 0, 0, 0, r, "",0);
          
      } 
      
    public Note (float d, boolean r, String c) {
      
          this ( 0, d, 0, 0, 0, r, c,0);
          
       }
     
    public Note (float f, float d) {
       
        this(f,d,.5f);     
       
      }
    public Note (float f, float d, float p) {
       
       this(f,d,.5f,false);     
       
      }
    public Note (float f, float d, float p,boolean r) {
       
        this(f,d,p,1,d,r); 
       
      }
    public  Note (float f, float d, float p, float v, float s,boolean r) {
       
          this (f, d, p, v, s, r,"",0); 
       
      }
      
    public  Note (float f, float d, float p, float v, float s,boolean r, String c,int ch) {
       
       this.frequency=f;
       this.duration=d;
       this.pan=p;
       this.isRest=r;
       this.velocity=v;  
       this.sustain=s; 
       this.comment=c;
       this.channel=ch;
         
       
      }
      // clone implementation
    public Object clone () {
          Object o = null;
          
           try {
             
               o = super.clone();
               
             
             } catch  (CloneNotSupportedException e) {
               
                 System.out.println ( " Some error in cloning Cluster Object ");
               
            }
         
            return o;
             
         }
    
     //-----------------------------------------------//
     //-------- return value of note event -----------//
     //-------- true == rest               -----------//
     //-------- false == some sound        -----------//
     //-----------------------------------------------//
    
    
    /**
     * Return a boolean value, true  if the sound value is a rest, false if is a note.
     * 
     * @return A boolean value.
     */
    public boolean isThisARest () {
       
          return isRest;
          
      }
     
    /**
     * Invert the boolean value for identify a rest.
     * 
     * @return Nothing.
     */
    public void invertRest(){
            
             isRest=!isRest;
        }
    /**
     * Get the the frequency value in Hz.
     * 
     * @return float.
     */
    public float getFrequency () {
       
           return frequency;
       
      }
    /**
     * Set the the frequency value in Hz.
     * 
     * @parameter float.
     */
    public  void  setFrequency (float f) {
       
           frequency = f;
           
           
           
    }
    
    /**
     * Get the  duration value in milliseconds.
     * 
     * @parameter float.
     */ 
    public float getDuration () {
               
           return duration;
       
      }
    /**
     * Get the sustain value in milliseconds.
     * 
     * @return float
     */
    public  float getSustain () {
       
           return sustain;
       
      }
    /**
     * Get the pan value.
     * 
     * @return float
     */
    public float getPan() {
       
           return pan;
       
      }
    /**
     * Get the velocity value.
     * 
     * @return float
     */
    public  float getVelocity() {
       
           return velocity;
       
      }
    /**
     * Get a comment. You could use a comment to assign some custom value to the event.
     * 
     * @return String
     */
    public String getComment() {
        
        
             return comment;
        
        
        }
    
    /**
     * Get a channel number. You could use this value as a change channel parameter.
     * 
     * @return int
     */
    public int getChannel () {
    	
    	 return channel;
    	 
    }
    
    
      // set methods
    
    /**
     * Set the duration value of an event.
     */
    
    public  void setDuration (float d) {
       
           duration = d;
       
      }
    /**
     * Set the sustain value of an event.
     */
    public  void setSustain (float s) {
       
           sustain = s;
       
      }
    /**
     * Set the pan value of an event.
     */
    public  void  setPan(float p) {
       
           pan = p;
       
      }
    /**
     * Set the velocity value of an event.
     */
    public void setVelocity(float v) {
       
           velocity = v;
       
      }
    /**
     * Write a comment. You could use a comment to assign some custom value to the event.
     */
    public void setComment(String c) {
        
        
             comment = c;
        
        
        }
    /**
     * Set a channel number. You could use this value as a change channel parameter.
     * 
     * @return int
     */
    public void setChannel(int ch) {
        
        
        channel= ch;
   
   
   }

   }
