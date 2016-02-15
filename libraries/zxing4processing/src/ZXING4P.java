package com.aos.zxing4processing;
import processing.core.*;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MonochromeBitmapSource;
import com.google.zxing.client.j2se.BufferedImageMonochromeBitmapSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.qrcode.*;
import com.google.zxing.qrcode.encoder.*;
import com.google.zxing.common.*;
import com.google.zxing.qrcode.decoder.*;
import com.google.zxing.qrcode.QRCodeReader;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.*;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This library was created to integrate into processing v1.0 the wonderful barcode decoding library
 * called ZXING that you can find n google code here<br />
 * <a href="http://code.google.com/p/zxing/" target="_blank">http://code.google.com/p/zxing/</a><br /><br />
 *
 * So no big deal was created by me, apart from the integration process. Which is also very limited in scope.<br /><br />
 *
 * That's why I am releasing this code with no licensing whatsoever. Do what you want with it, but please respect the
 * licensing of the ZXINg libraries: you can find it in their software distribution packages. Please read it all up.<br /><br />
 *
 * As for this small library: feel free and encouraged to make it better and more functional.<br /><br />
 *
 * And please send me a note if you do something with it, as I'd love to see it and publish it over at Art is Open Source.<br /><br />
 *
 * Best, xDxD.vs.xDxD@gmail.com<br />
 * <a href="http://www.artisopensource.net" target="_blank">http://www.artisopensource.net</a><br /><br />
 *
 * Rolf van Gelder :: <a href="http://www.cage.nl/" target="_blank">http://www.cage.nl/</a> :: <a href="http://www.cagewebdev.com/" target="_blank">http://www.cagewebdev.com/</a><br />
 *
 * 29/05/2010 - Disabled system.out output<br />
 * 03/28/2010 - Recompiled it for Processing 1.1<br /> 
 * 07/26/2009 - Added encoding support: generateQRCode(str,width,height)<br />
 * &nbsp;&nbsp;&nbsp;&nbsp;Will generate a PImage with the encoded QRCode image<br />
 * 07/26/2009 - Added decodeImage(boolean tryharder,PImage img)<br />
 * &nbsp;&nbsp;&nbsp;&nbsp;Decodes the QRCode directly from a PImage instead of from a disk file<br />
 *
 */
 
public class ZXING4P {
  PApplet parent;
  Hashtable<DecodeHintType, Object> hints;
  
  
	/**
	 * Constructor: instantiate the class in procesing's standard way: pass "this" as a parameter, when instantiating in a Processing applet
	 */
  public ZXING4P(PApplet parent) {
    this.parent = parent;
    parent.registerDispose(this);
  }

	/**
	 * Generates a QRCode image from a string (added by: Rolf van Gelder)
	 * @param content 		string to encode
	 * @param width				width of the PImage that will be returned
	 * @param height			height of the PImage that will be returned
	 * @return						PImage with the QRCode image
	 */
	public PImage generateQRCode(String content, int width, int height)
	{
			PImage myPImage = new PImage(width,height);
			
			QRCodeWriter myWriter;
			myWriter = new QRCodeWriter();

			ByteMatrix myByteMatrix = null;

			Byte myPixel;

			int myColor;
			
			try {
				myByteMatrix = myWriter.encode(content, BarcodeFormat.QR_CODE, width, height);

				// COPY THE BYTEMATRIX TO THE PIMAGE
		    for(int i=0; i<width; i++)
		    {  for(int j=0; j<height; j++)
		      { myPixel = myByteMatrix.get(j,i);
		        myColor = 0; // BLACK
		        if(myPixel!=0) myColor = 16777215;	// WHITE
		        myPImage.set(i,j,myColor);
		      }
		    } 				
		  }
		  catch ( Exception e )
		  {	System.out.println("Error generating QRCode image (generateQRCode)");
		  }
			return myPImage;
	}

	/**
	 * Decode the QRCode from a PImage (added by: Rolf van Gelder)
	 * @param tryHarder		if set to true, it tells the software to spend a little more time trying to decode the image
	 * @param img					PImage containing the image to be examinded
	 * @return						String with the found QRCode (empty if nothing found)
	 */
	public String decodeImage(boolean tryHarder, PImage img)
	{
		Decoder decoder = new Decoder();
		  
		String res = "";

		BufferedImage source = new BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB);
		
    int xx, yy;
    
    for (xx = 0; xx < img.width; xx++)
    { for (yy = 0; yy < img.height; yy++)
      {	source.setRGB(xx,yy,img.get(xx,yy));
      }
    }

		hints = null;
		
    if (tryHarder) {
      hints = new Hashtable<DecodeHintType, Object>(3);
      hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
    }
    
    try {
      MonochromeBitmapSource monoImg = new BufferedImageMonochromeBitmapSource(source);
      Result theresult = new QRCodeReader().decode(monoImg, hints);
      // System.out.println(theresult.getText());
      res = theresult.getText();
    } catch (ReaderException e) {
      // System.out.println(uri.toString() + ": No barcode found (RvG)");
      return "";
    }
		
		return res;
	}

	/**
	 * Decode the image of the barcode found at the passed uri
	 * @param tryHarder		if set to true, it tells the software to spend a little more time trying to decode the image
	 * @param uri					the String contains the URI of the image conaining the barcode to decode
	 * @return						a Vector containing the Strings found in the barcode, or an empty Vector if nothing was decoded
	 */
	public Vector decode(boolean tryHarder, String uri) throws Exception{
		
		Vector<String> res = new Vector();
		
		hints = null;
		
    if (tryHarder) {
      hints = new Hashtable<DecodeHintType, Object>(3);
      hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
    }
    
    File inputFile = new File(uri);
    if (inputFile.exists()) {
      if (inputFile.isDirectory()) {
        int successful = 0;
        int total = 0;
        for (File input : inputFile.listFiles()) {
          String filename = input.getName().toLowerCase();
          // Skip hidden files and text files (the latter is found in the blackbox tests).
          if (filename.startsWith(".") || filename.endsWith(".txt")) {
            continue;
          }
          String s = decode(input.toURI(), hints);
          if (s!=null && !s.trim().equals("")) {
            successful++;
            res.addElement(s);
          }
          total++;
        }
        // System.out.println("\nDecoded " + successful + " files out of " + total +
        //    " successfully (" + (successful * 100 / total) + "%)\n");
      } else {
        res.addElement( decode(inputFile.toURI(), hints) );
      }
    } else {
      res.addElement( decode(new URI(uri), hints) );
    }
    
  	return res;
  	
  }
  
  /**
	 * Decode the image of the barcode found at the passed uri. This version of the method can be used inside an Applet.
	 * Remember that java applets can only read in the same domain on which is the web page containing them.
	 * @param tryHarder		if set to true, it tells the software to spend a little more time trying to decode the image
	 * @param uri				  the String contains the URI of the image conaining the barcode to decode
	 * @param codeBase    the codebase of the applet on which the program is running
	 * @return						a Vector containing the Strings found in the barcode, or an empty Vector if nothing was decoded
	 */
  public Vector decodeWeb(boolean tryHarder, String uri, URL codeBase) throws Exception{
		
		Vector<String> res = new Vector();
		
		hints = null;
		
    if (tryHarder) {
      hints = new Hashtable<DecodeHintType, Object>(3);
      hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
    }
    
    res.addElement( decodeWeb(uri, hints,codeBase) );
    
  	return res;
  	
  }
 
  private String decode(URI uri, Hashtable<DecodeHintType, Object> hints) throws IOException {
  	String res = "";
  	
    BufferedImage image;
    try {
      image = ImageIO.read(uri.toURL());
    } catch (IllegalArgumentException iae) {
      throw new FileNotFoundException("Resource not found: " + uri);
    }
    if (image == null) {
      System.err.println(uri.toString() + ": Could not load image");
      return "";
    }
    try {
      MonochromeBitmapSource source = new BufferedImageMonochromeBitmapSource(image);
      Result result = new MultiFormatReader().decode(source, hints);
      // System.out.println(uri.toString() + " (format: " + result.getBarcodeFormat() + "):\n" + result.getText());
      // System.out.println(result.getText());
      res = result.getText();
      return res;
    } catch (ReaderException e) {
      // System.out.println(uri.toString() + ": No barcode found (RvG)");
      return "";
    }
    
    
  }
  
  private String decodeWeb(String uri, Hashtable<DecodeHintType, Object> hints, URL codeBase) throws IOException {
  	String res = "";
  	
    BufferedImage image;
    try {
    	URL url = new URL(codeBase, uri);
      image = ImageIO.read(url);
    } catch (IllegalArgumentException iae) {
      throw new FileNotFoundException("Resource not found: " + uri);
    }
    if (image == null) {
      System.err.println(uri.toString() + ": Could not load image");
      return "";
    }
    try {
      MonochromeBitmapSource source = new BufferedImageMonochromeBitmapSource(image);
      Result result = new MultiFormatReader().decode(source, hints);
      // System.out.println(uri.toString() + " (format: " + result.getBarcodeFormat() + "):\n" + result.getText());
      res = result.getText();
      return res;
    } catch (ReaderException e) {
      // System.out.println(uri.toString() + ": No barcode found (RvG)");
      return "";
    }
    
    
  }
  
  public void dispose() {
  }
  
}