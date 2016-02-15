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
public class PostToWeb extends PApplet
{
  PApplet papplet;
  String cType;
  
  public PostToWeb(PApplet _papplet)
  {
    papplet = _papplet;
  }

  protected byte[] getBytes(){
    return new byte[0];
  }
  
  /* Save function for local use
     Automatically saves to the sketchPath folder, and adds date if requested
  */
  protected void _save(String prefix, String extension, boolean useDate)
  {
    if(useDate){
      String date = year()+"_"+nf(month(),2)+"_"+nf(day(),2)+"_"+nf(hour(),2)+"_"+nf(minute(),2)+"_"+nf(second(),2);
      prefix = date+"_"+prefix;
    }
    _save(papplet.sketchPath(prefix+"."+extension));
  }

  /* Save function for local use
     Saves to a given filename, with the current getBytes() function
  */
  protected void _save(String filename)
  {
    _save(filename,getBytes());
  }
  
  /* Save function for local use
     Saves to a given filename, with a given byte array
  */
  protected void _save(String filename, byte[] bytes)
  {
    if(!papplet.online){
      papplet.saveBytes(filename,bytes);
    }else{
      println("Can't save to disk as an applet");
    }
  }

  /* Post function for remote saving
     Saves to a project folder
     Post the getBytes() byte array to the given Url, with the given filename
     Pops up a browser window with a link to the file, if requested
  */
  protected void _post(String project, String url, String filename,  boolean popup)
  {
    _post(project,url,filename,popup,getBytes());
  }
  
  /* Post function for remote saving
     Saves to a project folder
     Post the byte array to the given Url, with the given filename
     Pops up a browser window with a link to the file, if requested
  */
  protected void _post(String project, String url, String filename,  boolean popup, byte[] bytes)
  {
    String boundary = "|X-seltars-image-and-pdf-byte-array-uploader-X|";
    try{
      URL u = new URL(url);
      HttpURLConnection c = (HttpURLConnection)u.openConnection();

      // post multipart data
      c.setDoOutput(true);
      c.setDoInput(true);
      c.setUseCaches(false);

      // set request headers
      c.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);

      // open a stream which can write to the url
      DataOutputStream dstream = new DataOutputStream(c.getOutputStream());

      // write content to the server, begin with the tag that says a content element is comming
      dstream.writeBytes("--"+boundary+"\r\n");

      // discribe the content
      dstream.writeBytes("Content-Disposition: form-data; name=\""+project+"\"; filename=\""+filename+"\"\r\nContent-Type: "+cType+"\r\nContent-Transfer-Encoding: binary\r\n\r\n");
      dstream.write(bytes,0,bytes.length);

      // close the multipart form request
      dstream.writeBytes("\r\n--"+boundary+"--\r\n\r\n");
      dstream.flush();
      dstream.close();

      StringBuffer response = new StringBuffer();
      // read the output from the URL
      try{
        BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
        // Get the server response code
        int responseCode = -1;
        try
        {
          responseCode = c.getResponseCode();
        }
        catch (IOException ioe)
        {
        }
        if(responseCode == 200){
          // Everything has gone well so far
          // Get the server response
          String responseLine = null;
          do{
            responseLine = in.readLine();
            if (responseLine != null)
            {
              response.append(responseLine + "\n");
              if(responseLine.substring(0,4).equals(("http").substring(0,4))){ // if it's an url
                if(popup) papplet.link(responseLine, "_blank"); 
              }
            }
          }
          while(responseLine!=null);
          // output the response
          println(response.toString());
        }
      }
      catch(Exception e){
        e.printStackTrace();
      }
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }


}





