// Example of a simple mobile Service Server
//
// this midlet broadcasts a service called "simpleService"
// and let's client connect to it
//
// Export it in Mobile Processing (http://mobile.processing.org)
// and upload it to your phone.
// 
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


final String SERVICE_NAME = "simpleService";
Bluetooth bt;
String[] clients = new String[0];
PFont font;
String msg;  // status message


void setup() {
  //// set up font
  font = loadFont();
  textFont(font);

  // initialize Bluetooth library
  bt = new Bluetooth(this, 0x0003); // RFCOMM

  // start service
  bt.start(SERVICE_NAME);

  msg = "no client connected";
}

void destroy() {
  bt.stop();
}

void draw() {
  background(255);

  fill(0);
  text(msg, 3,height/2);

  // draw connected clients
  for (int i=0; i<clients.length; i++) {
    text(clients[i], 6, height/2+(i+1)*17);
  }
}

// gets called by BT if something happens
void libraryEvent(Object library, int event, Object data) {
  if (library == bt) {
    if (event == Bluetooth.EVENT_CLIENT_CONNECTED) {
      // a new client is connected. 
      clients = (String[]) append(clients, ((Client) data).device.name);
      msg = clients.length + " Clients connected:";  
    }
  }
}
