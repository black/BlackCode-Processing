// some bluetooth functionality demonstration.

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
String statusMsg = "inactive";
Service[] services = new Service[0];
Device[] devices = new Device[0];
Client[] clients = new Client[0];

void setup() {
  size(600,300);
  font = createFont("Courier", 15);
  textFont(font);
  try {
    bt = new Bluetooth(this, Bluetooth.UUID_RFCOMM); // RFCOMM

    // Start a Service
    bt.start("simpleService");
    bt.find();
    statusMsg = "starting search";
  } 
  catch (RuntimeException e) {
    statusMsg = "bluetooth off?";
    println(e);
  }

}

void draw() {
  background(0);
  fill(255);
  text("Status: " + statusMsg, 10, 30);

  translate(20, 60);
  text("Devices:", 0, 0);
  if (devices!=null) {
    for (int i=0; i<devices.length; i++) {
      text(devices[i].name, 0, 30+i*20);
    }
  }

  translate(160, 0);
  text("Services:", 0, 0);
  if (services!=null) {
    for (int i=0; i<services.length; i++) {
      text(services[i].name, 0,30+ i*20);
    }
  }

  translate(200, 0);
  text("Clients:", 0, 0);
  if (clients!=null) {
    for (int i=0; i<clients.length; i++) {
      text(clients[i].device.name, 0, 30+i*20);
    }
  }

  for (int i=0; i<clients.length; i++) {
    if (clients[i].available()>0) {
      println("Client " + i + " sent: " + clients[i].readUTF());
    } 
  }


}

void deviceDiscoverEvent(Device d) {
  statusMsg = "Found device at: " + d.address + "...";
  devices = (Device[])append(devices, d);
}

void deviceDiscoveryCompleteEvent(Device[] d) {
  statusMsg = "Found " + d.length + " devices found.";
  devices =  d;
}

void serviceDiscoverEvent(Service[] s) {
  statusMsg = "Found Service " + s[0].name + "...";
  services = (Service[])append(services,s[0]);
}

void serviceDiscoveryCompleteEvent(Service[] s) {
  services = (Service[])s;
  statusMsg = "Search complete.";
}

void clientConnectEvent(Client c) {
  clients = (Client[])append(clients, c); 
}

void keyPressed() {
  
  for (int i=0; i<clients.length; i++) {
    clients[i].writeUTF(""+key); 
    println("Server sent to all clients: " + key);
  }
  
}
