/**
 * WSProcessor
 * A utility class that allows a Processing server application to talk Websocket.
 * http://victorcheung.net
 *
 * Copyright 2014 Victor Cheung
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
 *  * This is the processor of the websocket connection on the server side. It performs the handshaking and communication on the server side.
 * <p>
 * The code is based on this, but the code does not work anymore
 * https://github.com/analogpixel/PWS
 * <p>
 * For more understanding of how websocket works, read this
 * http://www.developerfusion.com/article/143158/an-introduction-to-websockets/
 * <p>
 * This is the documentation of the websocket protocol
 * http://tools.ietf.org/html/draft-ietf-hybi-thewebsocketprotocol-03
 * <p>
 * Useful resource for websocket communication
 * http://stackoverflow.com/questions/8125507/how-can-i-send-and-receive-websocket-messages-on-the-server-side
 * <p>
 * Useful reference for websocket, not quite complete at the time of reading
 * https://developer.mozilla.org/en-US/docs/WebSockets/Writing_WebSocket_servers
 * <p>
 * Some Java code for reference
 * http://jwebsocket.googlecode.com
 * 
 * @author      Victor Cheung http://victorcheung.net
 * @modified    09/19/2014
 * @version     1.0 (1.0)
 */

package net.victorcheung.WSProcessor;

import processing.core.*;
import processing.net.*;
import java.security.MessageDigest;
import java.math.*;
import javax.xml.bind.DatatypeConverter;
import java.io.*;

/**
 * The example is automatically included in the javadoc.
 *
 * @example SingleClientSample 
 */

public class WSProcessor {

	// myParent is a reference to the parent sketch
	PApplet myParent;	
	public final static String VERSION = "1.0";

	//show debug messages
	protected boolean isDebug = false; 
	//the Client object for sending and receiving data to and from the client 
	protected Client myClient;
	//a bunch of parameters of the client
	String origin, host;


	/**
	 * The constructor, needs a Client reference.
	 *
	 * @param  client The reference to the Client object for sending and receiving data.
	 */
	public WSProcessor(Client client) {
		this(client, false);
	}

	/**
	 * The constructor that also allows the debug mode (printing out stuff to the console).
	 *
	 * @param client The reference to the Client object for sending and receiving data.
	 * @param debug A flag indicating whether debug info should be printed to the console or not.
	 */
	public WSProcessor(Client client, boolean debug) {
		myClient = client;
		isDebug = debug;
	}

	/**
	 * The method establishing a connection to the client, doing all the crazy handshaking.
	 * 
	 * @return True if connection is established, false otherwise.
	 */
	public boolean connect() {

		if(myClient == null) return false;

		//Step 1: Grab everything sent from the client
		byte[] clientBytes = readClientBytes();

		//create a String out of it as it is just a text stream, and will be read this way
		String whatClientSaid = new String(clientBytes);

		//Step 2: Look for the phrase "Sec-WebSocket", do handshaking if it's there
		if (whatClientSaid.indexOf("Sec-WebSocket") > 0)  {
			try {
				origin = getOrigin(whatClientSaid);
				host = getHost(whatClientSaid);
				//create the response message for the server side of the handshake
				String myResponse =  
						"HTTP/1.1 101 Switching Protocols\r\n"
								+ "Upgrade: websocket\r\n"
								+ "Connection: Upgrade\r\n"
								+ "Sec-WebSocket-Accept: " + procClientHeader(whatClientSaid) + "\r\n\r\n";  
				//+ "\r\n" + new String(procClientHeader( whatClientSaid) )  ;

				if(isDebug) System.out.println("Response message: "+myResponse );
				myClient.write(myResponse);

			} catch(Exception e) { 
				System.out.println(e);
				return false;
			}
		} else {
			//not the handshaking package, cannot establish a connection
			return false;
		}

		//made it up to this point, things should be working
		return true;
	}

	/**
	 * The method stopping the connection. Calls the stop method of the Client object.
	 *
	 */
	public void stop() {
		if(isDebug) System.out.println("stopping the connection");
		myClient.stop();
	}

	/**
	 * The method getting the message from the client.
	 *
	 * @return The message in String, null if there is nothing, or unable to unmask.
	 */
	public String getMessageAsString() {

		if(isDebug) System.out.println("getting Message:");
		if(myClient.available() <= 0) return null;

		//Step 1: Grab everything sent from the client
		byte[] clientBytes = readClientBytes();

		//Step 2: Get the masking key to unmask the message part of thebytes 
		byte[] maskingKey = getMaskingKey(clientBytes);

		//Step 3: Do the unmask
		String unMaskedString = "";
		if(clientBytes!=null && maskingKey!=null) {
			byte[] unMasked = decodeMessage(clientBytes, maskingKey);
			//create the decoded message as a String
			if(unMasked != null) {
				unMaskedString = new String(unMasked);
				if(isDebug) System.out.println(unMaskedString);
			} 
			else return null;
		} 
		else return null;

		return unMaskedString;
	}

	/**
	 * The method sending the message to the client.
	 *
	 * @param msg The message in String to be sent out.
	 * @return True if connection is established, false otherwise.
	 * @exception Exception The catch-all exception if the sending doesn't work.
	 */
	public boolean sendMessage(String msg) {
		try {
			myClient.write(generateByteToClient(msg));
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}

		return true;
	}

	/**
	 * A protected helper method for reading bytes from the client.
	 *
	 * @return A byte array containing the data sent from client. It's masked and need {@link decodeMessage(byte[] bArray, byte[] maskingKey)} to decode.
	 */
	protected byte[] readClientBytes() {
		//read from the client
		byte[] clientBytes = myClient.readBytes();
		if(isDebug) System.out.println("Received # of bytes in first round: "+clientBytes.length);

		//keep reading if there are more things
		while(myClient.available() > 0) {
			byte[] moreClientBytes = myClient.readBytes();
			byte[] longerClientBytes = new byte[clientBytes.length+moreClientBytes.length];
			System.arraycopy(clientBytes, 0, longerClientBytes, 0, clientBytes.length);
			System.arraycopy(moreClientBytes, 0, longerClientBytes, clientBytes.length, moreClientBytes.length);
			clientBytes = longerClientBytes;
		}
		if(isDebug) System.out.println("Received # of bytes in total: "+clientBytes.length);

		return clientBytes;
	}

	/**
	 * A protected helper method extracting the Origin of the client.
	 *
	 * @param header The header of decoded message sent from the client.
	 * @return The Origin field as a String if found, null if not. 
	 */
	protected String getOrigin(String header) {
		//String[] data = split( header, '\n');
		String[] data = header.split("\n");
		for (int i=0; i < data.length; i++) {
			if (data[i].indexOf("Origin") != -1 ) {
				return data[i].substring(8).trim();
			}
		}
		return null;
	}

	/**
	 * A protected helper method extracting the Host of the client.
	 *
	 * @param header The header of decoded message sent from the Client.
	 * @return The Host field as a String if found, null if not. 
	 */
	protected String getHost(String header) {
		//String[] data = split( header, '\n');
		String[] data = header.split("\n");
		for (int i=0; i < data.length; i++) {
			if (data[i].indexOf("Host") != -1 ) {
				return data[i].substring(5).trim();
			}
		}
		return null;
	}

	/**
	 * A protected helper method, generates a unique String for the client to accept connection and mask subsequent messages.
	 *
	 * @return The unique String, or empty if something is wrong.
	 */
	protected String procClientHeader(String header) {
		//String[] data = split( header, '\n');
		String[] data = header.split("\n");
		
		String secKey = "";
		// get the Sec key and return it as long
		for ( int i=0; i < data.length; i++) {
			if (data[i].indexOf("Sec-WebSocket-Key:") != -1) { 
				secKey  = data[i].substring(19).trim(); 
				//System.out.println("l1:" + sec1); 
			} 
		}

		//a magic sequence as dictated by the RFC of WebSocket
		String magicEnd = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
		//System.out.println(secKey+magicEnd);
		byte[] sha1hash = messageDigest(secKey+magicEnd,"SHA-1");
		//for(int i=0; i<sha1hash.length; i++) print(hex(sha1hash[i],2)+":");
		//System.out.println("");
		//base64 encode the byte sequence, make a String out of it
		String b64 = DatatypeConverter.printBase64Binary(sha1hash);
		//System.out.println(b64);
		return b64; 
	}

	/** 
	 * A protected helper method encrypting the handshake sequence provided as the first parameter. The second parameter is the encryption method.
	 *
	 * @param message The message representing the handshake sequence. 
	 * @param algorithm The encryption method of choice. For Websocket it is SHA-1.
	 * @return A byte array of the encrypted handshake sequence, or null if something is wrong.
	 * @exception java.security.NoSuchAlgorithmException If the encryption algorithm is not available.
	 */
	protected byte[] messageDigest(String message, String algorithm) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance(algorithm);
			md.update(message.getBytes());
			return md.digest();
		} catch(java.security.NoSuchAlgorithmException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	/**
	 * A protected helper method generating the byte array to be sent back to the client, including all the necessary leading and tailing bytes.
	 * <p>
	 * Code based on http://code.google.com/p/jfraggws/source/browse/trunk/src/GoodExample/JfragGWs.java
	 *
	 * @param msg The message in String to be sent to the client.
	 * @return The byte array of the message, null if something is wrong. 
	 * @exception UnsupportedEncodingException If the encoding method is not supported. For Websocket it's UTF-8.
	 */
	protected byte[] generateByteToClient(String msg) throws UnsupportedEncodingException {
		if(isDebug) System.out.println("Sending this to client: "+msg);

		byte[] msgInBytes = msg.getBytes("UTF-8");
		byte[] someDataByteArray = null;

		if (msgInBytes.length < 126) {
			//the case where payload is less than 126 bytes
			someDataByteArray = new byte[1+1+msgInBytes.length+1];
			someDataByteArray[0] = (byte)0x81;
			someDataByteArray[1] = (byte)msgInBytes.length;
			for(int i=0; i<msgInBytes.length; i++) someDataByteArray[i+2] = msgInBytes[i];
		}
		else if (msgInBytes.length > 125 && msgInBytes.length < 256) {
			//the case where payload is between 126 and 255, inclusive
			someDataByteArray = new byte[1+1+1+1+msgInBytes.length+1];
			someDataByteArray[0] = (byte)0x81;
			someDataByteArray[1] = (byte)126;
			someDataByteArray[2] = (byte)0x00;
			someDataByteArray[3] = (byte)msgInBytes.length;
			for(int i=0; i<msgInBytes.length; i++) someDataByteArray[i+4] = msgInBytes[i];
		} else {
			//the case where payload is above 255, assuming everything still fits in one packet
			someDataByteArray = new byte[1+1+1+1+1+msgInBytes.length+1];
			someDataByteArray[0] = (byte)0x81;
			someDataByteArray[1] = (byte)126;
			someDataByteArray[2] = (byte)(msgInBytes.length/256);
			someDataByteArray[3] = (byte)(msgInBytes.length%256);
			for(int i=0; i<msgInBytes.length; i++) someDataByteArray[i+4] = msgInBytes[i];
		}

		//end it with a 0xFF
		someDataByteArray[someDataByteArray.length-1] = (byte)0xFF;

		return someDataByteArray;
	}

	/** 
	 * A protected utility method returning the payload length as a long, which can either be 7-bit, 16-bit, or 64-bit.
	 *
	 * @param bArray The unmasked byte array of the data sent by the client.
	 * @return The length of the payload sent by the client.
	 */
	protected long getPayloadLength(byte[] bArray) {
		//first get the value from the 7 bits of the payload length in the 2nd byte
		long payloadLength = 0;
		for(int i=6; i>=0; i--) {
			payloadLength *= 2;
			payloadLength += bitAt(bArray[1], i)?1:0;
		}
		if(payloadLength<=125) return payloadLength;
		else if(payloadLength == 126) {
			//the next 16 bits is the payload length
			payloadLength = 0; //reset to zero!
			for(int i=7; i>=0; i--) {
				payloadLength *= 2;
				payloadLength += bitAt(bArray[2], i)?1:0;
			} //now continue with the next byte
			for(int i=7; i>=0; i--) {
				payloadLength *= 2;
				payloadLength += bitAt(bArray[3], i)?1:0;
			}
		} else {
			//should be 127, the next 64 bits is the payload length
			payloadLength = 0; //reset to zero!
			for(int h=2; h<10; h++) {
				for(int i=7; i>=0; i--) {
					payloadLength *= 2;
					payloadLength += bitAt(bArray[h], i)?1:0;
				} //now continue with the next byte
			}
		}
		return payloadLength;
	}

	/** 
	 * A protected utility method returning the opcode as an int, which is the last 4 bits of the first byte.
	 *
	 * @param b The byte being examined.
	 */
	protected int getOpcode(byte b) {
		return b & 0xF;
	}

	/**
	 * A protected utility method returning the masking-key, assuming there is one.
	 * <p>
	 * Strangely sometimes the payload length doesn't match with the packet length, in this case just return null.
	 *
	 * @param bArray The data sent by the client, in the form of a byte array.
	 * @return The masking key being used to unmasking the message part of the data.
	 */ 
	protected byte[] getMaskingKey(byte[] bArray) {

		if(isDebug) System.out.println("getting masking key");

		//something strange has happened, packet is not long enough to have the masking key
		if(bArray.length < 2) return null;

		byte[] maskingKey = new byte[4];
		//first get the value from the 7 bits of the payload length in the 2nd byte
		long payloadLength = 0;
		for(int i=6; i>=0; i--) {
			payloadLength *= 2;
			payloadLength += bitAt(bArray[1], i)?1:0;
		}
		if(payloadLength<=125) {
			if(bArray.length < 6) return null;
			maskingKey[0] = bArray[2];
			maskingKey[1] = bArray[3];
			maskingKey[2] = bArray[4];
			maskingKey[3] = bArray[5];
		} else if(payloadLength == 126) {
			if(bArray.length < 8) return null;
			maskingKey[0] = bArray[4];
			maskingKey[1] = bArray[5];
			maskingKey[2] = bArray[6];
			maskingKey[3] = bArray[7];
		} else {
			if(bArray.length < 14) return null;
			maskingKey[0] = bArray[10];
			maskingKey[1] = bArray[11];
			maskingKey[2] = bArray[12];
			maskingKey[3] = bArray[13];
		}

		return maskingKey; 
	}

	/** 
	 * A protected utility function decoding message from client.
	 * <p>
	 * For some reason sometimes the payloadLength is longer than the bArray, not sure why as it works most of the time,
	 * so for now I'm just skipping those that will cause ArrayIndexOutOfBoundException.
	 *
	 * @param bArray The data sent by the client, in the form of a byte array.
	 * @param maskingKey The key used to unmask the message from the client, in the form of a byte array.
	 * @return The decoded message sent by the client, in the form of a byte array.
	 */
	protected byte[] decodeMessage(byte[] bArray, byte[] maskingKey) {

		if(isDebug) System.out.println("decoding message");

		if(bArray.length < 2) return null;

		//first get the value from the 7 bits of the payload length in the 2nd byte
		long payloadLength = 0;
		for(int i=6; i>=0; i--) {
			payloadLength *= 2;
			payloadLength += bitAt(bArray[1], i)?1:0;
		}
		int startByte = -1;
		if(payloadLength<=125) startByte = 6;
		else if(payloadLength==126) startByte = 8;
		else startByte = 14;
		//get the real length
		payloadLength = getPayloadLength(bArray);
		if(isDebug) System.out.println("in decodeMessage creating decoded message with # of bytes: "+payloadLength);
		byte[] decoded = new byte[(int)payloadLength];
		//System.out.println("Decoded Length: "+decoded.length+"\nPacket Length: "+bArray.length);
		//this is the part I skip those packets that don't have the length as indicated
		if(startByte+payloadLength > bArray.length) return null;
		if(isDebug) System.out.println("unmasking...");
		for (int i=0; i < payloadLength; i++) {
			decoded[i] = (byte)(bArray[startByte+i] ^ maskingKey[i % 4]);
		}
		//System.out.println("done decoding");
		return decoded;
	}
	
	/**
	 * A public static utility method returning the bit at a certain position, from least significant bit of the byte.
	 * 
	 * @param b The byte being examined.
	 * @param pos The position of the bit inside the byte.
	 * @ return True if 1, false if 0.
	 */
	public static boolean bitAt(byte b, int pos) {
		return ((b & (1 << pos)) != 0);
	} 

	/**
	 * return the version of the library.
	 * 
	 * @return String
	 */
	public static String version() {
		return VERSION;
	}

}

