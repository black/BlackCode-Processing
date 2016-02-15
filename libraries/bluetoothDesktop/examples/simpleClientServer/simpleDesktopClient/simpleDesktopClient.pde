/*
 Demonstration of a Processing client searching for and connecting to a service.

  extrapixel, 2007
 http://www.extrapixel.ch/processing/bluetoothDesktop/


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
String msg = "inactive";
Client server;
final String SERVICE_NAME = "simpleService";

void setup() {
  size(600,300);
  font = createFont("Courier", 15);
  textFont(font);
  try {
    bt = new Bluetooth(this, Bluetooth.UUID_RFCOMM); // RFCOMM

    // Start finding the service
    bt.find();
    msg = "searching...";
  } 
  catch (RuntimeException e) {
    msg = "error. is your bluetooth on?";
    println(e);
  }

}

void draw() {
  background(0);
  fill(255);
  text(msg, 10, height/2);
}


// this gets called when the search process is over
void serviceDiscoveryCompleteEvent(Service[] s) {
  Service[] services = (Service[])s;

  msg = "Search completed.";

  // now search for the service we want
  for (int i=0; i<services.length; i++) {
    println(services[i].name);
    if (services[i].name.equals(SERVICE_NAME)) {
      msg = "Service " + SERVICE_NAME + " found";
      
      try {
        // we found our service, so try to connect to it
        // if we try to connect to it more than once, this will throw an error.
        server = services[i].connect();
        msg = "Connected to service " + SERVICE_NAME + " on server " + server.device.name;
        return;
      } 
      catch (Exception e) {
        msg = "Found service " + SERVICE_NAME + " on Server " + server.device.name + ", but connection failed";
        println(e);
        return;
      }
    } 
  }

  msg = "Service " + SERVICE_NAME + " not found.";
}
