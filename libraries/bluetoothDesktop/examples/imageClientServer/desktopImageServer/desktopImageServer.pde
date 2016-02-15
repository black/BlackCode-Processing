// a processing sketch that allows bluetooth client connections
// the clients can send images to this server

// by extrapixel, 2007
// http://www.extrapixel.ch/processing/bluetoothDesktop/

/*
    This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 
 */

import bluetoothDesktop.*;

PFont font;
Bluetooth bt;
ArrayList clients = new ArrayList();
ArrayList images = new ArrayList();
PApplet papplet;

// image display properties
final int IMAGE_HEIGHT = 160;
final int IMAGE_WIDTH = 120;
final int IMAGE_BORDER = 10;

void setup() {
  size(660,660);
  font = createFont("Courier", 15);
  textFont(font);
  try {
    bt = new Bluetooth(this); // we go with the standard uuid, so mobile processing sketches can easily connect
    bt.start("imageServer");  // Start the service
  } 
  catch (RuntimeException e) {
    println("bluetooth device is not available. (or license problem if using avetana)"); 
    println(e);
  }
  papplet = this;
}

void draw() {
  background(0);
  fill(255);

  // update all the clients
  for (int i=0; i< clients.size(); i++) {
    ((ImageClient) clients.get(i)).update(); 
  }

  // draw all the images
  for (int i=0; i< images.size(); i++) {
    pushMatrix();
    translate(IMAGE_BORDER + (i%5)*(IMAGE_BORDER+IMAGE_WIDTH), IMAGE_BORDER + int(i/5.0) * (2*IMAGE_BORDER+IMAGE_HEIGHT));
    ((ImageHandler) images.get(i)).draw();
    text(((ImageHandler) images.get(i)).name, 0, IMAGE_HEIGHT+15);
    popMatrix();
  }

}


// callback for the bluetooth library
// gets called when a new client connects
void clientConnectEvent(Client c) {
  clients.add(new ImageClient(c));
  println("new client: " + c.device.name);
}

/*****************************************************
 *
 * Class ImageClient
 * handles the connection to the bluetooth client
 *
 *****************************************************/

class ImageClient {

  Client bluetoothClient;         // the bluetooth client
  ImageHandler currentImage;      // holds the image that is currently being loaded
  boolean loadingImage = false;   // if we are currently loading an image


    ImageClient(Client bluetoothClient) {
    this.bluetoothClient = bluetoothClient;
  } 

  void update() {
    if (loadingImage) {
      // we are loading an image, so check for new bytes
      int nrBytes = bluetoothClient.available();
      if (nrBytes>0) {
        byte[] inBytes = new byte[nrBytes];
        bluetoothClient.readBytes(inBytes);
        // send the new Bytes to the Image-object
        loadingImage = ! currentImage.addBytes(inBytes);
      } 
    } 
    else {
      // we're not yet loading an image, so check for an int 
      // the mobile phone sends the nr of bytes of the new image, if there is one
      if (bluetoothClient.available() > 0) {
        currentImage = new ImageHandler(bluetoothClient.readInt(), bluetoothClient.device.name);
        images.add(currentImage);
        loadingImage = true;
      }
    }
  }
}

/*****************************************************
 *
 * Class ImageHAndler
 * stores & displays a received image
 *
 *****************************************************/
 
 class ImageHandler {

  private PGraphics myRenderer;
  private PImage myImage;
  private int byteSize;
  private int loadedBytes;
  private boolean loaded = false;
  private byte[] imageBytes;
  String name; 

  ImageHandler(int byteSize, String name) {
    this.byteSize = byteSize;
    this.name = name;
    myImage = createImage(100,100, RGB);
    myRenderer = createGraphics(IMAGE_WIDTH, IMAGE_HEIGHT, JAVA2D);
    imageBytes = new byte[0];
  } 

  void draw() {
    image(myImage, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
  }


  // adds the new bytes to the byte-array
  // returns true if the image was loaded completely
  // returns false if the image is not yet loaded
  boolean addBytes(byte[] newBytes) {
    // add new bytes to our byte-array
    for (int i=0; i< newBytes.length; i++ ) {
      imageBytes = (byte[]) append(imageBytes, newBytes[i]);
    }

    // draw the loading progress in my renderer
    myRenderer.beginDraw();
    myRenderer.background(0);
    myRenderer.noFill();
    myRenderer.stroke(255);
    myRenderer.rect(0,0,IMAGE_WIDTH, IMAGE_HEIGHT);
    myRenderer.rect(5, IMAGE_HEIGHT-10, IMAGE_WIDTH-10, 5);
    myRenderer.fill(255);
    myRenderer.rect(5, IMAGE_HEIGHT-10, map(imageBytes.length, 0, byteSize, 0, IMAGE_WIDTH), 5);
    myRenderer.endDraw();

    // get the image from the renderer
    myImage = myRenderer;

    // is the image loaded completely?
    if (imageBytes.length == byteSize) {
      myImage = bytesToPImage(imageBytes);
      loaded = true;
      println(name + " sent " + round(byteSize/1024.0) + " kB");
      return true;
    }
    return false;
  }

  // takes an array of bytes and creates a PImage from it.
  // seen here: http://processing.org/discourse/yabb_beta/YaBB.cgi?board=Integrate;action=display;num=1134385140
  PImage bytesToPImage(byte[] bytes) {
    Image awtImage = Toolkit.getDefaultToolkit().createImage(bytes);
    MediaTracker tracker = new MediaTracker(papplet);
    tracker.addImage(awtImage, 0);
    try {
      tracker.waitForAll();
    } 
    catch (InterruptedException e) { 
    }
    PImage newPImage = new PImage(awtImage);
    return newPImage;
  }

}

