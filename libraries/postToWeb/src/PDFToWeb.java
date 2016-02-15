/**
 * Class for posting PDF to Web
 * author: seltar / Yonas Sandbak
 * website: http://seltar.org
 */

package org.seltar.Bytes2Web;

import processing.core.*;
import processing.pdf.*;

public class PDFToWeb extends PostToWeb 
{
  public int pageCount;  
  public boolean recording = false;
  private BytePGraphicsPDF recorder;
  public PDFToWeb(PApplet _papplet){
    super(_papplet);
    pageCount = 0;
    cType = "application/pdf";
  }

  /**
   * Starts the pdf-recorder
   */
  public void startRecording(){
    recorder = (BytePGraphicsPDF)createGraphics(papplet.width,papplet.height, "org.seltar.Bytes2Web.BytePGraphicsPDF");
    
    papplet.beginRecord(recorder);
    recording = true;
  }

  /**
   * Adds a page to the current recorder if it is recording
   * Increments the current pageCount
   */
  public void addPage()
  {
    if(!recording) return;

    if(pageCount > 1){
      PGraphicsPDF pdfg = (PGraphicsPDF) recorder;  // Get the renderer
      pdfg.nextPage();  // Tell it to go to the next page
    }
    pageCount++;
  }

   
  /** 
   * Stops the pdf-recorder
   */
  public void stopRecording()
  {
    if(!recording) return;
    
    papplet.endRecord();
    
    //papplet.recorder = null;
    
    recording = false;
    pageCount = 0;
  }
  
  /**
   * Check to see if the recorder is on
   * @return if the recorder is on
   */
  public boolean isRecording()
  {
    return recording;
  }
  
  /**
   * Special PDF post, which stops the recorder if it's on
   * Also adds .pdf to the filename
   * @param project the project folder
   * @param url the url to the file that recieves the data
   * @param filename the filename this is file is supposed to have
   * @param popup wether or not to open a link to the image
   */
  public void post(String _project, String url, String filename,  boolean popup)
  {
    stopRecording();
    super._post(_project,url,filename+".pdf",popup);
  }

  /** 
   * Special PDF save, which stops the recorder if it's on
   * Assumes the filename is valid
   * @param filename the filename
   */
  public void save(String filename)
  {
    stopRecording();
    super._save(filename);
  }
  
  /**
   * Special PDF save, which stops the recorder if it's on
   * Also adds date if requested, and .pdf to the filename
   * @param prefix the filename
   * @param useDate prefix with date 
   */
  public void save(String prefix,boolean useDate){
    stopRecording();
    super._save(prefix,"pdf",useDate);
  }

  /**
   * Special PDF getBytes, which gets the current bytearray for the pdf-recorder
   */
  public byte[] getBytes(){
    //BytePGraphicsPDF impgpdf = (BytePGraphicsPDF) papplet.recorder;  // Get the real renderer
    return recorder.getBytes();
  }
}

