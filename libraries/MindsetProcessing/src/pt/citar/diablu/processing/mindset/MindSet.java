package pt.citar.diablu.processing.mindset;

import java.lang.reflect.*;

import processing.core.*;
import processing.serial.*;

import com.NeuroSky.ThinkGear.Util.*;


/**
 * Data is passed back to the application via the following callback methods:
 * <br>
 * <br>
 * <code>public void attentionEvent(int attentionLevel)</code><br>
 * Returns the current attention level [0, 100].
 * Values in [1, 20] are considered strongly lowered.
 * Values in [20, 40] are considered reduced levels.
 * Values in [40, 60] are considered neutral.
 * Values in [60, 80] are considered slightly elevated.
 * Values in [80, 100] are considered elevated.
 * <br>
 * <br>
 * <code>public void meditationEvent(int meditationLevel)</code><br>
 * Returns the current meditation level [0, 100].
 * The interpretation of the values is the same as for the attentionLevel.
 * <br>
 * <br>
 * <code>public void poorSignalEvent(int signalLevel)</code><br>
 * Returns the signal level [0, 200]. The greater the value, the more noise is detected in the signal.
 * 200 is a special value  that means that the ThinkGear contacts are not touching the skin.
 * <br>
 * <br>
 * <code>public void eegEvent(int delta, int theta, int low_alpha, int high_alpha, int low_beta, int high_beta, int low_gamma, int mid_gamma) </code><br>
 * Returns the EEG data. The values have no units.
 * 
 * <br>
 * <br>
 * <code>public void rawEvent(int [])</code><br>
 * Returns the the current 512 raw signal samples [-32768, 32767]. 
 * The Mindset model reports values approximately between [-2048, 2047].
 * 
 */ 
public class MindSet implements Runnable, DataListener {

  private PApplet parent;
  private boolean running = true;
 
  private StreamParser parser;
  private Serial myPort;

  private Method attentionEventMethod = null;
  private Method meditationEventMethod = null;
  private Method poorSignalEventMethod = null;
  private Method blinkEventMethod = null;
  private Method eegEventMethod = null;
  private Method rawEventMethod = null;
  
  private int raw[] = new int[512];
  private int index = 0;

  /**
   * Constructs a Mindset object that connects to the specified serial port and 
   * reads the Mindset data. 
   * 
   *
   * @param parent The PApplet object, i.e., most likely 'this'
   * @param serialPort The name of the serial port to where the Mindset is connected
   */
  public MindSet(PApplet parent, String serialPort) {
    this.parent = parent;
    try {
      attentionEventMethod =
        parent.getClass().getMethod("attentionEvent",  new Class[] { 
        int.class
      }   
      );
      
    } 
    catch (Exception e) {
    	System.err.println("attentionEvent() method not defined. ");
    }

    try {
      meditationEventMethod =
        parent.getClass().getMethod("meditationEvent",  new Class[] { 
        int.class
      }   
      );
    } 
    catch (Exception e) {
    	System.err.println("meditationEvent() method not defined. ");
    }
    try {
      poorSignalEventMethod =
        parent.getClass().getMethod("poorSignalEvent",  new Class[] { 
        int.class
      }   
      );
    } 
    catch (Exception e) {
    	System.err.println("poorSignalEvent() method not defined. ");
    }

    try {
      blinkEventMethod =
        parent.getClass().getMethod("blinkEvent",  new Class[] { 
        int.class
      }   
      );
    } 
    catch (Exception e) {
    	System.err.println("blinkEvent() method not defined. ");
    }
    try {
      eegEventMethod =
        parent.getClass().getMethod("eegEvent",  new Class[] { 
        int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class
      }   
      );
    } 
    catch (Exception e) {
    	System.err.println("eegEvent() method not defined. ");
    }

   try {
      rawEventMethod =
        parent.getClass().getMethod("rawEvent",  new Class[] { 
        int[].class
      }   
      );
    } 
    catch (Exception e) {
    	System.err.println("rawEvent() method not defined. ");
    }


    parser = new StreamParser(StreamParser.PARSER_TYPE_PACKETS, this, null );
    //println(Serial.list());
    myPort = new Serial(parent, serialPort, 57600);
    Thread t = new Thread(this);
    t.start();
  }

  private void triggerAttentionEvent(int attentionLevel) {
    if (attentionEventMethod != null) {
      try {
        attentionEventMethod.invoke(parent, new Object[] {
          attentionLevel
        }   
        );
      } 
      catch (Exception e) {
        System.err.println("Disabling attentionEvent()  because of an error.");
        e.printStackTrace();
        attentionEventMethod = null;
      }
    }
  }

  private void triggerMeditationEvent(int meditationLevel) {
    if (meditationEventMethod != null) {
      try {
        meditationEventMethod.invoke(parent, new Object[] {
          meditationLevel
        }   
        );
        //println("Attention: " + attention);
      } 
      catch (Exception e) {
        System.err.println("Disabling meditationEvent()  because of an error.");
        e.printStackTrace();
        meditationEventMethod = null;
      }
    }
  }

  private void triggerPoorSignalEvent(int poorSignalLevel) {
    if (poorSignalEventMethod != null) {
      try {
        poorSignalEventMethod.invoke(parent, new Object[] {
          poorSignalLevel
        }   
        );
        //println("Attention: " + attention);
      } 
      catch (Exception e) {
        System.err.println("Disabling meditationEvent()  because of an error.");
        e.printStackTrace();
        poorSignalEventMethod = null;
      }
    }
  }  

  private void triggerBlinkEvent(int blinkStrength) {
    if (blinkEventMethod != null) {
      try {
        blinkEventMethod.invoke(parent, new Object[] {
          blinkStrength
        }   
        );
      } 
      catch (Exception e) {
        System.err.println("Disabling blinkEvent()  because of an error.");
        e.printStackTrace();
        blinkEventMethod = null;
      }
    }
  }

  private void triggerEEGEvent(int delta, int theta, int low_alpha, int high_alpha, int low_beta, int high_beta, int low_gamma, int mid_gamma) {
    if (eegEventMethod != null) {
      try {
        eegEventMethod.invoke(parent, new Object[] {
          delta, theta, low_alpha, high_alpha, low_beta, high_beta, low_gamma, mid_gamma
        }   
        );
      } 
      catch (Exception e) {
        System.err.println("Disabling eegEvent()  because of an error.");
        e.printStackTrace();
        eegEventMethod = null;
      }
    }
  }


  private void triggerRawEvent(int []values) {
    if (rawEventMethod != null) {
      try {
        rawEventMethod.invoke(parent, new Object[] {
          values
        }   
        );
      } 
      catch (Exception e) {
        System.err.println("Disabling rawEvent()  because of an error.");
        e.printStackTrace();
        rawEventMethod = null;
      }
    }
  }
  
  public void quit() {
	running = false;
    myPort.stop();
  }

  public void run() {

    byte [] buffer = new byte[1024];
    //  int buffer;
    while(running) {
      int read = myPort.readBytes(buffer);
      //println("read: " +read + " ");
      for (int i = 0; i < read; i++) {
        int result = parser.parseByte((int)buffer[i] & 0xFF);
        //println(result + " " + (int)buffer[i]);
        if (result == -2) {
        	System.err.println("Checksum failed.");
        }
      }

      // println("Result: " + result + " Value: " + (char)b + " " + b);
      parent.delay(50);
      
    }
  }

  public void dataValueReceived( int extendedCodeLevel, int code, int numBytes, byte[] valueBytes, Object customData ) {



    if (extendedCodeLevel == 0) {
      switch( code ) {
        case(0x02):
        int poorSignal = ((int)valueBytes[0] & 0xff);
        triggerPoorSignalEvent(poorSignal);
        break;
        case (0x04): //ATTENTION LEVEL
        int attention = ((int)valueBytes[0] & 0xff);
        triggerAttentionEvent(attention);
        break;
        case (0x05):
        int meditation = ((int)valueBytes[0] & 0xff);
        triggerMeditationEvent(meditation);
        break;
        case(0x16):
        int blink = ((int)valueBytes[0] & 0xff);
        triggerBlinkEvent(blink);
       
        break;
        case (0x80):
          int rawValue =  (valueBytes[0]<<8) | valueBytes[1];
          raw[index] = rawValue;
          index++;
          if (index == 512) {
            index = 0;
            int rawCopy[] = new int[512];
            parent.arrayCopy(raw, rawCopy);
            triggerRawEvent(rawCopy);
          }
          break;
        case(0x83):

        int delta = ((valueBytes[0] & 0xFF) << 16  |
          (valueBytes[1] & 0xFF) << 8  |
          (valueBytes[2] & 0xFF) << 0);

        int theta = ((valueBytes[3] & 0xFF) << 16  |
          (valueBytes[4] & 0xFF) << 8  |
          (valueBytes[5] & 0xFF) << 0);

        int low_alpha = ((valueBytes[6] & 0xFF) << 16  |
          (valueBytes[7] & 0xFF) << 8  |
          (valueBytes[8] & 0xFF) << 0);

        int high_alpha = ((valueBytes[9] & 0xFF) << 16  |
          (valueBytes[10] & 0xFF) << 8  |
          (valueBytes[11] & 0xFF) << 0);

        int low_beta = ((valueBytes[12] & 0xFF) << 16  |
          (valueBytes[13] & 0xFF) << 8  |
          (valueBytes[14] & 0xFF) << 0);

        int high_beta = ((valueBytes[15] & 0xFF) << 16  |
          (valueBytes[16] & 0xFF) << 8  |
          (valueBytes[17] & 0xFF) << 0);

        int low_gamma = ((valueBytes[18] & 0xFF) << 16  |
          (valueBytes[19] & 0xFF) << 8  |
          (valueBytes[20] & 0xFF) << 0);

        int mid_gamma = ((valueBytes[21] & 0xFF) << 16  |
          (valueBytes[22] & 0xFF) << 8  |
          (valueBytes[23] & 0xFF) << 0);
//          println("EEG------");
        triggerEEGEvent(delta, theta, low_alpha, high_alpha, low_beta, high_beta, low_gamma, mid_gamma);
       
        break;
      default:
        //println("ad" + e);
      }
    }
  }
}
