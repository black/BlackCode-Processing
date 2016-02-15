package sures.remotevlc;

import processing.core.*;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;


public class VlcConnection implements Runnable {

  PApplet parent;
  Method clientEventMethod;
  Method disconnectEventMethod;

  Thread thread;
  Socket socket;
  String ip;
  int port;
  String host;

  public InputStream input;
  public OutputStream output;

  byte buffer[] = new byte[32768];
  int bufferIndex;
  int bufferLast;

  String vlc_pause_cmd;
  
  public VlcConnection(PApplet parent, String host, int port) {
    this.parent = parent;
    this.host = host;
    this.port = port;

    try {
      socket = new Socket(this.host, this.port);
      input = socket.getInputStream();
      output = socket.getOutputStream();

      thread = new Thread(this);
      thread.start();

      parent.registerDispose(this);

      try {
        clientEventMethod =
          parent.getClass().getMethod("clientEvent",
                                      new Class[] { VlcConnection.class });
      } catch (Exception e) {

      }
      try {
        disconnectEventMethod =
          parent.getClass().getMethod("disconnectEvent",
                                      new Class[] { VlcConnection.class });
      } catch (Exception e) {

      }

    } catch (ConnectException ce) {
      ce.printStackTrace();
      dispose();

    } catch (IOException e) {
      e.printStackTrace();
      dispose();
    }
  }


  public VlcConnection(PApplet parent, Socket socket) throws IOException {
    this.socket = socket;

    input = socket.getInputStream();
    output = socket.getOutputStream();

    thread = new Thread(this);
    thread.start();
  }

  public void stop() {
    dispose();
    if (disconnectEventMethod != null) {
      try {
        disconnectEventMethod.invoke(parent, new Object[] { this });
      } catch (Exception e) {
        e.printStackTrace();
        disconnectEventMethod = null;
      }
    }
  }


  public void dispose() {
    thread = null;
    try {
      if (input != null) input.close();
      if (output != null) output.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
    input = null;
    output = null;

    try {
      if (socket != null) socket.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
    socket = null;
  }

  public void run() {
    while (Thread.currentThread() == thread) {
      try {
        while ((input != null) &&
               (input.available() > 0)) {  
          synchronized (buffer) {
            if (bufferLast == buffer.length) {
              byte temp[] = new byte[bufferLast << 1];
              System.arraycopy(buffer, 0, temp, 0, bufferLast);
              buffer = temp;
            }
            buffer[bufferLast++] = (byte) input.read();
          }
        }
        if (clientEventMethod != null) {
          try {
            clientEventMethod.invoke(parent, new Object[] { this });
          } catch (Exception e) {
            System.err.println("error, disabling clientEvent() for " + host);
            e.printStackTrace();
            clientEventMethod = null;
          }
        }

        try {
          Thread.sleep(10);
        } catch (InterruptedException ex) { }

      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public boolean active() {
    return (thread != null);
  }

  public String ip() {
    return socket.getInetAddress().getHostAddress();
  }

  public int available() {
    return (bufferLast - bufferIndex);
  }

  public void clear() {
    bufferLast = 0;
    bufferIndex = 0;
  }

  public int read() {
    if (bufferIndex == bufferLast) return -1;

    synchronized (buffer) {
      int outgoing = buffer[bufferIndex++] & 0xff;
      if (bufferIndex == bufferLast) {  
        bufferIndex = 0;
        bufferLast = 0;
      }
      return outgoing;
    }
  }

  public char readChar() {
    if (bufferIndex == bufferLast) return (char)(-1);
    return (char) read();
  }

  public byte[] readBytes() {
    if (bufferIndex == bufferLast) return null;

    synchronized (buffer) {
      int length = bufferLast - bufferIndex;
      byte outgoing[] = new byte[length];
      System.arraycopy(buffer, bufferIndex, outgoing, 0, length);

      bufferIndex = 0; 
      bufferLast = 0;
      return outgoing;
    }
  }

  public int readBytes(byte outgoing[]) {
    if (bufferIndex == bufferLast) return 0;

    synchronized (buffer) {
      int length = bufferLast - bufferIndex;
      if (length > outgoing.length) length = outgoing.length;
      System.arraycopy(buffer, bufferIndex, outgoing, 0, length);

      bufferIndex += length;
      if (bufferIndex == bufferLast) {
        bufferIndex = 0; 
        bufferLast = 0;
      }
      return length;
    }
  }

  public byte[] readBytesUntil(int interesting) {
    if (bufferIndex == bufferLast) return null;
    byte what = (byte)interesting;

    synchronized (buffer) {
      int found = -1;
      for (int k = bufferIndex; k < bufferLast; k++) {
        if (buffer[k] == what) {
          found = k;
          break;
        }
      }
      if (found == -1) return null;

      int length = found - bufferIndex + 1;
      byte outgoing[] = new byte[length];
      System.arraycopy(buffer, bufferIndex, outgoing, 0, length);

      bufferIndex += length;
      if (bufferIndex == bufferLast) {
        bufferIndex = 0; // rewind
        bufferLast = 0;
      }
      return outgoing;
    }
  }

  public int readBytesUntil(int interesting, byte outgoing[]) {
    if (bufferIndex == bufferLast) return 0;
    byte what = (byte)interesting;

    synchronized (buffer) {
      int found = -1;
      for (int k = bufferIndex; k < bufferLast; k++) {
        if (buffer[k] == what) {
          found = k;
          break;
        }
      }
      if (found == -1) return 0;

      int length = found - bufferIndex + 1;
      if (length > outgoing.length) {
        System.err.println("readBytesUntil() byte buffer is" +
                           " too small for the " + length +
                           " bytes up to and including char " + interesting);
        return -1;
      }
      System.arraycopy(buffer, bufferIndex, outgoing, 0, length);

      bufferIndex += length;
      if (bufferIndex == bufferLast) {
        bufferIndex = 0;  // rewind
        bufferLast = 0;
      }
      return length;
    }
  }

  public String readString() {
    if (bufferIndex == bufferLast) return null;
    return new String(readBytes());
  }

  public String readStringUntil(int interesting) {
    byte b[] = readBytesUntil(interesting);
    if (b == null) return null;
    return new String(b);
  }

  public void command(int what) {  
    try {
      output.write(what & 0xff);  
      output.flush();  

    } catch (Exception e) { 
      e.printStackTrace();
      stop();
    }
  }


  public void command(byte bytes[]) {
    try {
      output.write(bytes);
      output.flush();   

    } catch (Exception e) { 
      e.printStackTrace();
      stop();
    }
  }

  public void command(String what) {
    command(what.getBytes());
  }
 
  public void pause() {	  
      vlc_pause_cmd = "pause\r"; 
      command(vlc_pause_cmd.getBytes());
  }
  
  public void play() {	  
      vlc_pause_cmd = "play\r";
      command(vlc_pause_cmd.getBytes());
  }

  public void vlcstop() {	  
      vlc_pause_cmd = "stop\r";
      command(vlc_pause_cmd.getBytes());
  }
  
  public void gotosec(int seeksec) {	  
      vlc_pause_cmd = "seek " + seeksec + "\r";
      command(vlc_pause_cmd.getBytes());
  }
  
  public void next() {	  
      vlc_pause_cmd = "next\r";
      command(vlc_pause_cmd.getBytes());
  }
  
  public void previous() {	  
      vlc_pause_cmd = "prev\r";
      command(vlc_pause_cmd.getBytes());
  }
  
  public void repeat() {	  
      vlc_pause_cmd = "repeat\r";
      command(vlc_pause_cmd.getBytes());
  }
  
  public void vlcloop() {	  
      vlc_pause_cmd = "loop\r";
      command(vlc_pause_cmd.getBytes());
  }
  
  public void vlcrandom() {	  
      vlc_pause_cmd = "random\r";
      command(vlc_pause_cmd.getBytes());
  }
  
  public void fastforward() {	  
      vlc_pause_cmd = "fastforward\r";
      command(vlc_pause_cmd.getBytes());
  }
  
  public void rewind() {	  
      vlc_pause_cmd = "rewind\r";
      command(vlc_pause_cmd.getBytes());
  }
  
  public void fullscreen() {	  
      vlc_pause_cmd = "f\r";
      command(vlc_pause_cmd.getBytes());
  }
  
}
