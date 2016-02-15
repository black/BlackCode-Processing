/** 
 * Class for posting to web
 * author: seltar / Yonas Sandbak
 * website: http://seltar.org
 * 
 * some code is borrowed and adapted from PhilHo's uploader
 */
package org.seltar.Bytes2Web;

import processing.core.*;
import java.io.*;
import java.net.*;
public class ByteToWeb extends PostToWeb
{
  PApplet papplet;
  String cType;
  
  public ByteToWeb(PApplet _papplet)
  {
    super(_papplet);
    papplet = _papplet;
  }

  
  /* Save function for local use
     Automatically saves to the sketchPath folder, and adds date if requested
  */
  public void save(String prefix, String extension, boolean useDate)
  {
    super._save(prefix,extension,useDate);
  }

  /* Save function for local use
     Saves to a given filename, with the current getBytes() function
  */
  public void save(String filename)
  {
    super._save(filename);
  }
  
  /* Save function for local use
     Saves to a given filename, with a given byte array
  */
  public void save(String filename, byte[] bytes)
  {
    super._save(filename,bytes);
  }

  /* Post function for remote saving
     Saves to a project folder
     Post the getBytes() byte array to the given Url, with the given filename
     Pops up a browser window with a link to the file, if requested
  */
  public void post(String project, String url, String filename,  boolean popup)
  {
    super._post(project,url,filename,popup);
  }
  
  /* Post function for remote saving
     Saves to a project folder
     Post the byte array to the given Url, with the given filename
     Pops up a browser window with a link to the file, if requested
  */
  public void post(String project, String url, String filename,  boolean popup, byte[] bytes)
  {
    super._post(project,url,filename,popup,bytes);
  }


}





