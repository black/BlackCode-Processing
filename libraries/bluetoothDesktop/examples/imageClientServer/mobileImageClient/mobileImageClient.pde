// a mobile processing sketch that looks for a specific service
// on a server and can send captured photos to it

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



import processing.bluetooth.*;
import processing.video.*;

Capture video;
Bluetooth bt;
Client server;
PImage picture;
byte[] pictureData;
PFont font;


final String SERVICE_NAME = "imageServer";
final int STATE_NOT_CONNECTED = 0;
final int STATE_CONNECTING = 1;
final int STATE_CONNECTED = 2;
final int STATE_SENDING = 3;
final int STATE_SENT = 4;
final int STATE_PIC_TAKEN = 5;
int state = STATE_NOT_CONNECTED;
String msg = "Welcome";

void setup() {
  video = new Capture(this);
  video.hide();
  bt = new Bluetooth(this);
  font = loadFont();
  textFont(font);
}

void draw() {

  background(0);
 
  if (state == STATE_CONNECTED) {
    noLoop(); // turning off the draw-loop, to avoid flickering in the video image
  } 
  else if (state == STATE_NOT_CONNECTED) {
    msg = "no connection to server \n press key to search";
  } 
  else if (state == STATE_SENT) {
    msg = "picture sent";
    state = STATE_CONNECTED;
    video.show(0,0,width,height);
    noLoop(); // turning off the draw-loop, to avoid flickering in the video image
  } 
  else if (state == STATE_SENDING) {
    msg = "sending...";
    state = STATE_SENT;
  } 
  else if (state == STATE_PIC_TAKEN) {
    image(picture,0,0); 
    msg = "send?";
  }
text(msg, 5,25, width-10, height-10);
 
}

void libraryEvent(Object library, int event, Object data) {
  if (library == bt) {
    switch (event) {
    case Bluetooth.EVENT_DISCOVER_DEVICE:
      msg = "Found device at: " + ((Device) data).address + "...";
      break;
    case Bluetooth.EVENT_DISCOVER_DEVICE_COMPLETED:
      msg = "Found " + length((Device[]) data) + " devices, looking for service " + SERVICE_NAME + "...";
      break;
    case Bluetooth.EVENT_DISCOVER_SERVICE:     
      msg = "Found Service " + ((Service[]) data)[0].name + "...";
      break;
    case Bluetooth.EVENT_DISCOVER_SERVICE_COMPLETED:
      Service[] services = (Service[]) data;

      for (int i=0; i<services.length; i++) {
        if (services[i].name.equals(SERVICE_NAME)) {
          server = services[i].connect();
          msg = "Server connected";
          state = STATE_CONNECTED;
          video.show(0,0,width,height);
          return;
        }
      }

      msg = "Search complete, Server not found \nAny key to retry.";
      state = STATE_NOT_CONNECTED;
      break;
    }
  }
}

void keyPressed() {
  if (state==STATE_CONNECTED) {
    state = STATE_PIC_TAKEN;
    //pictureData = video.read("image/png", 1024, 768);  //<- does not work in some phones, e.g. on SE W810i
    pictureData = video.read();
    picture = loadImage(pictureData);
    video.hide();
    loop();
  } 
  else if (state==STATE_NOT_CONNECTED) {
    bt.find();
    state = STATE_CONNECTING;
    msg = "searching...";
  } 
  else if (state == STATE_PIC_TAKEN) {
    state=STATE_NOT_CONNECTED; // setting it back if sending works
    try{
      server.writeInt(pictureData.length);
      server.write(pictureData);
      state = STATE_SENDING;
    } 
    catch (Exception e) {
      server.stop();
      msg = "connection lost";
    }
  }

}


