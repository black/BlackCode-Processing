/**
 * Class for posting JPG, TIFF, GIF or PNG to Web
 * author: seltar / Yonas Sandbak
 * website: http://seltar.org
 * 
 * some code is borrowed and adapted from PhilHo's uploader
 */
package org.seltar.Bytes2Web;

import java.io.*;
import java.awt.image.BufferedImage;
import processing.core.*;
import javax.imageio.*;
import javax.imageio.stream.*;

public class ImageToWeb extends PostToWeb
{
  String imageType;
  public int type;
  public final static int JPEG = 0;
  public final static int PNG  = 1;
  public final static int GIF  = 2;
  public final static int TIFF = 3;

  public ImageToWeb(PApplet _papplet){
    super(_papplet);
    setType(ImageToWeb.JPEG);
  }

  /**
   * Set the filetype to save / post as
   * @param _type the image type; ImageToWeb.JPEG, ImageToWeb.PNG, ImageToWeb.GIF, ImageToWeb.TIFF
   */
  public void setType(int _type)
  {
    switch(_type){
    case ImageToWeb.JPEG:
      type = ImageToWeb.JPEG;
      cType = "image/jpeg";
      imageType = "jpg";
      break;
    case ImageToWeb.PNG:
      type = ImageToWeb.PNG;
      cType = "image/png";
      imageType = "png";
      break;
    case ImageToWeb.GIF:
      type = ImageToWeb.GIF;
      cType = "image/gif";
      imageType = "gif";
      break;
    case ImageToWeb.TIFF:
      type = ImageToWeb.TIFF;
      cType = "image/tiff";
      imageType = "tiff";
      break;
    default:
      println("Unknown type");
      return;
    }
  }

  /**
   * Special Image Post function, automatically adds the right extension
   * @param project the project folder
   * @param url url to the file that receives the data
   * @param filename the filename it's supposed to save as
   * @param popup wether or not to open the link with the file
   */
  public void post(String project, String url, String filename,  boolean popup)
  {
    super._post(project,url,filename+"."+imageType,popup);
  }

  /**
   * Special Image Post function, automatically adds the right extension
   * Takes a byte array, from other images
   * @param project the project folder
   * @param url url to the file that receives the data
   * @param filename the filename it's supposed to save as
   * @param popup wether or not to open the link with the file
   * @param bytes the byte array to post
   */
  public void post(String project, String url, String filename,  boolean popup, byte[] bytes)
  {
    super._post(project,url,filename+"."+imageType,popup,bytes);
  }
  
  /**
   * Special Image Post function
   * Adds date if requested, and automatically adds the right extension
   * @param prefix the filename 
   * @param useDate wether or not to prefix with a date
   */
  public void save(String prefix, boolean useDate){
    super._save(prefix,imageType,useDate);
  }
  
  /**
   * Just save as a file
   * @param filename the filename to save as
   */
  public void save(String filename){
    super._save(filename);
  }

  /**
   * Just save a bytearray as a file
   * @param filename the filename to save as
   * @param bytes the byte array to save
   */
  public void save(String filename, byte[] bytes){
    super._save(filename, bytes);
  }

  /**
   * Get the current screen's bytearray
   */
  public byte[] getBytes(){
    return getBytes(papplet.g);
  }

  /**
   * Get the given PGraphics bytearray
   * @param src the source graphics
   */
  public byte[] getBytes(PGraphics src){

    switch(type){
    case ImageToWeb.JPEG:
      // We need a new buffered image without the alpha channel
      BufferedImage imageNoAlpha = new BufferedImage(src.width, src.height, BufferedImage.TYPE_INT_RGB);
      src.loadPixels();
      imageNoAlpha.setRGB(0, 0, src.width, src.height, src.pixels, 0, src.width);
      return getBytesJPEG(imageNoAlpha);

    case ImageToWeb.PNG:
    case ImageToWeb.GIF:
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try
      {
        ImageIO.write((BufferedImage)src.image, imageType, baos);
      }  
      catch (Exception e)
      {
        e.printStackTrace();
        return new byte[0]; // Problem
      }
      return baos.toByteArray();

    case ImageToWeb.TIFF:
      return getBytesTIFF(src);

    default:
      println("Unknown type");
      return new byte[0];
    }
  }

  /* ------------------------------ JPEG ------------------------------ */

  /**
   * Get the image as a jpeg byte array
   * @param image BufferedImage to create the byte array from
   * @see BufferedImage
   */
  protected byte[] getBytesJPEG(BufferedImage image){
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    java.util.Iterator iter = ImageIO.getImageWritersByFormatName("jpeg");
    if (iter.hasNext())
    {
      ImageWriter writer = (ImageWriter) iter.next();
      ImageWriteParam iwp = writer.getDefaultWriteParam();
      iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
      iwp.setCompressionQuality(1.0f);

      ImageOutputStream ios = new MemoryCacheImageOutputStream(baos);
      writer.setOutput(ios);
      try
      {
        writer.write(image);
      }  
      catch (Exception e)
      {
        e.printStackTrace();
        return new byte[0]; // Problem
      }
      return baos.toByteArray();
    }
    return new byte[0];
  }


  /* ------------------------------ TIFF ------------------------------ */

  static byte TIFF_HEADER[] = {
    77, 77, 0, 42, 0, 0, 0, 8, 0, 9, 0, -2, 0, 4, 0, 0, 0, 1, 0, 0,
    0, 0, 1, 0, 0, 3, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 0, 3, 0, 0, 0, 1,
    0, 0, 0, 0, 1, 2, 0, 3, 0, 0, 0, 3, 0, 0, 0, 122, 1, 6, 0, 3, 0,
    0, 0, 1, 0, 2, 0, 0, 1, 17, 0, 4, 0, 0, 0, 1, 0, 0, 3, 0, 1, 21,
    0, 3, 0, 0, 0, 1, 0, 3, 0, 0, 1, 22, 0, 3, 0, 0, 0, 1, 0, 0, 0, 0,
    1, 23, 0, 4, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0, 8, 0, 8
  };

  /**
   * Get the image as a tiff byte array
   * @param srcimg PImage to create the byte array from
   * @see PImage
   */
  protected byte[] getBytesTIFF(PImage srcimg) {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    try {
      byte tiff[] = new byte[768];
      System.arraycopy(TIFF_HEADER, 0, tiff, 0, TIFF_HEADER.length);

      tiff[30] = (byte) ((srcimg.width >> 8) & 0xff);
      tiff[31] = (byte) ((srcimg.width) & 0xff);
      tiff[42] = tiff[102] = (byte) ((srcimg.height >> 8) & 0xff);
      tiff[43] = tiff[103] = (byte) ((srcimg.height) & 0xff);

      int count = srcimg.width*srcimg.height*3;
      tiff[114] = (byte) ((count >> 24) & 0xff);
      tiff[115] = (byte) ((count >> 16) & 0xff);
      tiff[116] = (byte) ((count >> 8) & 0xff);
      tiff[117] = (byte) ((count) & 0xff);

      // spew the header to the disk
      output.write(tiff);

      srcimg.loadPixels();
      for (int i = 0; i < srcimg.pixels.length; i++) {
        output.write((srcimg.pixels[i] >> 16) & 0xff);
        output.write((srcimg.pixels[i] >> 8) & 0xff);
        output.write(srcimg.pixels[i] & 0xff);
      }

    } 
    catch (IOException e) {
      e.printStackTrace();
    }
    return output.toByteArray();
  }
}







