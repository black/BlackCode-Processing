package bluetoothDesktop;


import java.io.*;
import javax.microedition.io.*;

/**
 * This originated from the Mobile Processing project - http://mobile.processing.org
 *
 * Ported to Processing by extrapixel, http://www.extrapixel.ch/bluetooth/
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 *
 * @author  Francis Li
 * @author  extrapixel
 */
public class Client {
    private StreamConnection    con;
    private DataInputStream     is;
    private DataOutputStream    os;
    
    public Device               device;
    
    protected Client(StreamConnection con) {
        this.con = con;
    }
    
    public void open() throws IOException {
        os = con.openDataOutputStream();
        is = con.openDataInputStream();//new DataInputStream(new AvailableInputStream(con.openInputStream()));
    }
    
    public void stop() {
        try {
            os.close();
        } catch (IOException ioe) { }
        try {
            is.close();
        } catch (IOException ioe) { }
        try {
            con.close();
        } catch (IOException ioe) { }
    }
    
    public int available(){
        try {
            return is.available();
        } catch (IOException ioe) {
           return 0;
        }
    }
    
    public int read () {
        try {
            return is.read();
        } catch (IOException ioe) {
            return 0;
        }
    }

    public boolean readBoolean() {
        try {
            return is.readBoolean();
        } catch (IOException ioe) {
            System.err.println("readBoolean() read without checking available()");
            return false;
        }
    }
    
    public char readChar() {
        try {
            return is.readChar();
        } catch (IOException ioe) {
        	System.err.println("readChar() read without checking available()");
            return ' ';
        }
    }

    public void readBytes(byte[] b) {
        readBytes(b, 0, b.length);
    }

    public void readBytes(byte[] b, int offset, int length) {
        try {
            is.readFully(b, offset, length);
        } catch (IOException ioe) {
            //return;
        }
    }

    public int readInt() {
        try {
            return is.readInt();
        } catch (IOException ioe) {
 	       System.err.println("readInt() read without checking available()");
            return 0;
        }
    }

    public String readUTF() {
        try {
            return is.readUTF();
        } catch (IOException ioe) {
        	System.err.println("readUTF() read without checking available()");
            return "";
        }
    }
    
    public int skipBytes(int bytes) {
        try {
            return is.skipBytes(bytes);
        } catch (IOException ioe) {
            return 0;
        }
    }
    
    public void write(int data) {
        try {
            os.write(data);
        } catch (IOException ioe) {
        }
    }
    
    public void write(byte[] data) {
        try {
            os.write(data);
        } catch (IOException ioe) {
        }
    }

    public void writeBoolean(boolean v) {
        try {
            os.writeBoolean(v);
        } catch (IOException ioe) {
        }
    }

    public void writeBytes(String s) {
        try {
            os.write(s.getBytes());
        } catch (IOException ioe) {
        }
    }

    public void writeChar(int v) {
        try {
            os.writeChar(v);
        } catch (IOException ioe) {
        }
    }
    
    public void writeInt(int v) {
        try {
            os.writeInt(v);
        } catch (IOException ioe) {
        }
    }
    
    public void writeUTF(String s) {
        try {
            os.writeUTF(s);
        } catch (IOException ioe) {
        }
    }
    
    public void flush() {
        try {
            os.flush();
        } catch (IOException ioe) {
        }
    }
}
