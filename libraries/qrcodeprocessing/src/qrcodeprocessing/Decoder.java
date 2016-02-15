/* Processing QRCode Library
 * Daniel Shiffman, 6/26/2007
 * Based on code by Tom Igoe
 * Generate images from: http://qrcode.kaywa.com/
 * Get QRCode java library from: http://qrcode.sourceforge.jp/
 * Requires: qrcode.jar
 */


package qrcodeprocessing;

import java.awt.image.BufferedImage;
import java.lang.reflect.Method;

import jp.sourceforge.qrcode.codec.QRCodeDecoder;
import jp.sourceforge.qrcode.codec.exception.DecodingFailedException;

import processing.core.PApplet;
import processing.core.PImage;

// A Thread to decode an image
public class Decoder extends Thread {

    String decoded;     // The decoded message
    boolean running;    // Is the thread running?
    boolean available;  // Is the decoded message available?
    boolean decoding;   // Are we currently decoding?
    PImage img;         // The image to be decoded

    Method decoderEventMethod;

    PApplet parent;

    public Decoder(PApplet p) {
        parent = p;
        decoded = "";
        running = false;
        available = false;
        decoding = false;
        img = null;
        // Grab the decoderEvent() method
        try {
            decoderEventMethod = parent.getClass().getMethod("decoderEvent", new Class[] { 
                    Decoder.class             }
            );
        } 
        catch (Exception e) {
            //System.out.println("You need to have a decoderEvent method");
        }
    }

    // Decode the image
    public void decodeImage(PImage img_) {
        available = false;
        decoding = true;
        img = img_;
        if (!running) {
            running = true;
            this.start();
        } else {
            this.interrupt();
        }
    }

    // The thread runs
    public void run() {
        while (running) {
            // As long as there is an image decode it
            if (img != null) {
                decoded = scanImage();
                if (decoderEventMethod != null) {
                    try {
                        decoderEventMethod.invoke(parent, new Object[] { 
                                this                     }
                        );
                    } 
                    catch (Exception e) {
                        System.err.println("There was an error invoking decoderEvent()");
                        e.printStackTrace();
                        decoderEventMethod = null;
                    }
                }
                available = true;
                decoding = false;
                img = null;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
            }

        }
    }

    public boolean available() {
        return available;
    }

    public String getDecodedString() {
        return decoded; 
    }

    public PImage getImage() {
        return img; 
    }

    // Scan the image and return the message
    String scanImage() {
        String decodedString = "NO QRcode image found";
        // Create BufferedImage from PImage:
        BufferedImage bImg = new BufferedImage( img.width, img.height, BufferedImage.TYPE_INT_ARGB);
        // make the BufferedImage the same as the image:
        bImg.setRGB(0,0,img.width,img.height,img.pixels,0,img.width);
        try {
            // initialize the decoder:
            QRCodeDecoder decoder = new QRCodeDecoder();
            // decode:
            decodedString  = new String(decoder.decode(new QRImage(bImg)));
        }
        catch (DecodingFailedException dfe) {
            System.out.println("Error: " + dfe.getMessage());
        }
        return decodedString;
    }
    
    public boolean decoding() {
        return decoding;
    }

}

