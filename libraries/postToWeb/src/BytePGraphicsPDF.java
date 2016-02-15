/**
 * Helper class for getting the byte array from a PDF recorder
 * author: PhilHo
 */

package org.seltar.Bytes2Web;
import java.io.*;
  
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
 
import processing.core.*;
import processing.pdf.*;

public class BytePGraphicsPDF extends PGraphicsPDF
{
  public BytePGraphicsPDF() { 
    super();
    setOutput(new ByteArrayOutputStream());
  }

  
  public byte[] getBytes() {
    return ((ByteArrayOutputStream) output).toByteArray();
  }
} 

