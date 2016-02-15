// A Less Simple Carnivore Client
//
// Note: requires Carnivore Library for Processing (http://r-s-g.org/carnivore)
//
// + Windows people:  first install winpcap (http://winpcap.org)
// + Mac people:      first open a Terminal and execute this commmand: sudo chmod 777 /dev/bpf*
//                    (must be done each time you reboot your mac)
//
// "offline" demo mode requires a log file created by CarnivorePE (see below) 
//

import java.util.Iterator;
import org.rsg.carnivore.*;
import org.rsg.carnivore.net.*;

// Flag for online/offline modes.
boolean isOnline = true; 

// For offline mode must have log file in sketch's "data" folder  
// You can make your own log file in CarnivorePE using the "headers only" channel 
String log_file = "Log_11Jan3a.txt";

HashMap nodes = new HashMap();
HashMap links = new HashMap();
float startDiameter = 120.0;
float halo_radius = 30;
int min_diameter = 70;
float startBrightness = 255.0;
float shrinkSpeed = 0.97;
float dimSpeed_link = 0.9;
float dimSpeed_ripple = 0.6;
String packets[];
PImage b;

color color1  = color(0, 0, 0, 170); 	        //0x000000 black	unknown
color color2  = color(153, 0, 204, 170); 	//0x9900CC purple	ftp
color color3  = color(51, 51, 204, 170); 	//0x3333CC darkblue   	itunes
color color4  = color(102, 102, 255, 170); 	//0x6666FF blue		http
color color5  = color(51, 102, 102, 170); 	//0x336666 darkgreen  	email
color color6  = color(102, 153, 0, 170); 	//0x669900 green	aim
color color7  = color(204, 255, 102, 170); 	//0xCCFF66 lightgreen 	Network Time Protocol
color color8  = color(255, 204, 0, 170); 	//0xFFCC00 tan		telnet/ssh
color color9  = color(255, 153, 0, 170); 	//0xFF9900 orange	BOOTP client
color color10 = color(255, 102, 102, 170); 	//0xFF6666 pink		netbios
color color11 = color(204, 0, 0, 170); 	        //0xCC0000 red		name-domain server

////////////////////////////////////////////////////////////////////////
class Node  {
  String ip;
  int port;
  float x, y, diameter; 
  color c;
  float ripple_color;
  int ripple_diameter;
  PImage icon; 
  float halo;

  Node(String ip, int port) {
    this.port = port;
    this.ip = ip;
    this.x = getXfromIP(ip);
    this.y = getYfromIP(ip);
    this.diameter = startDiameter;
    this.c = port2color(port);
    this.halo = halo_radius;
    this.ripple_color = 100;
    this.ripple_diameter = int(startDiameter + halo_radius);

    int r = int((x + y) % 4); //assign an icon that will be the same for the same x/y poss
    String icon_file = "cube.png";
    if(r == 0) { 
      icon_file = "cube.png"; 
    } else if(r == 1) { 
      icon_file = "message.png"; 
    } else if(r == 2) { 
      icon_file = "firefox.png"; 
    } else { 
      icon_file = "iTunes.png"; 
    }
    this.icon = loadImage(icon_file); // Load the images into the program
  }

  void shrink() {
    if(diameter > min_diameter) { 
      diameter = diameter * shrinkSpeed; 
      halo = halo * shrinkSpeed;
    }
    ripple_color = ripple_color * dimSpeed_ripple;
    ripple_diameter += 30;
  }

  void displayRipple() {
    if((ripple_diameter < (height*2)) && (ripple_color > 2)) { // avoid extreme cases
      stroke(255 - ripple_color);
      noFill();
      ellipse(x, y, ripple_diameter, ripple_diameter);  
    }
  }

  void display() {
    // Node circle
    stroke(0,0,0,100); // Circle rim
    fill(c); // Circle fill
    ellipse(x, y, diameter, diameter);

    // Halo
    noStroke();
    fill(color(100, 100, 100, 50));  // Halo
    ellipse(x, y, diameter + halo, diameter + halo);

    // Icon
    image(icon, x - icon.width/2, y - icon.height/2); 
  }
}

////////////////////////////////////////////////////////////////////////
class Link {
  String from_ip;
  String to_ip;
  float greyscale;
  int from_x, from_y, bez1_x, bez1_y, bez2_x, bez2_y, to_x, to_y;

  Link(String from_and_to) {
    this.from_ip = fromIPfromFromTo(from_and_to);
    this.to_ip = toIPfromFromTo(from_and_to);
    this.from_x = getXfromIP(from_ip);
    this.from_y = getYfromIP(from_ip);
    this.to_x = getXfromIP(to_ip);
    this.to_y = getYfromIP(to_ip);
    this.bez1_x = int((from_x + to_x)/2);
    this.bez1_y = from_y;   
    this.bez2_x = to_x;
    this.bez2_y = int((from_y + to_y)/2);   
    this.greyscale = startBrightness;
  }

  void dim() {
    greyscale = greyscale * dimSpeed_link;
  }

  void display() {
    stroke(int(255 - greyscale));
    noFill();
    bezier(from_x, from_y, bez1_x, bez1_y, bez2_x, bez2_y, to_x, to_y);
  }
}

////////////////////////////////////////////////////////////////////////
// Processing setup and draw loop
void setup(){
  size(625, 300);
  //size(625, 200);
  frameRate(10);
  smooth();
  ellipseMode(CENTER);

  b = loadImage("gradient.gif");

  //online mode
  if(isOnline) {
    CarnivoreP5 c = new CarnivoreP5(this);

  //offline mode
  } else {
    packets = loadStrings(log_file); // Need CarnivorePE "minivore" log file in "data" folder 
  }
}

void draw() {
  image(b, 0, 0);
  
  //stroke(100); noFill(); rect(0,0,width-1,height-1);

  if(!isOnline) {
    // Simulate incoming packets
    if(random(100) < 15) {
      String packet = packets[int(random(packets.length))]; // Get random packet from array of all packets
      String from_and_to = packet.substring(packet.indexOf(" ")+1);
      String from_ip = fromIPfromFromTo(from_and_to);
      int from_port = fromPortfromFromTo(from_and_to);
      String to_ip = toIPfromFromTo(from_and_to);
      int to_port = toPortfromFromTo(from_and_to);

      // Make Node objects and add them to the hashmap 
      nodes.put(from_ip, new Node(from_ip, from_port));
      nodes.put(to_ip, new Node(to_ip, to_port));

      //add packet to links
      links.put(from_and_to, new Link(from_and_to));
    }
  }

  drawRipples(); //draw these separate from the Nodes so they stay in rearground 
  drawLinks();
  drawNodes();
  //println("nodes:"+nodes.size() + " links:"+links.size());
}

// This is the callback for the online mode, i.e. Carnivore library triggers this method
synchronized void packetEvent(CarnivorePacket packet){
  if(isOnline) {
    println("[PDE] packetEvent: " + packet);

    // Make Node objects and add them to the hashmap 
    nodes.put(packet.senderAddress.toString(), new Node(packet.senderAddress.toString(), packet.senderPort));
    nodes.put(packet.receiverAddress.toString(), new Node(packet.receiverAddress.toString(), packet.receiverPort));

    //add packet to links
    String from_and_to = packet.senderSocket() + " > " + packet.receiverSocket();
    links.put(from_and_to, new Link(from_and_to));
  }
}

synchronized void drawNodes() {
  Iterator it = nodes.keySet().iterator();
  while(it.hasNext()){
    String ip = (String)it.next();
    Node n = (Node) nodes.get(ip);
    n.display();
    n.shrink();
  }  
}

synchronized void drawRipples() {
  Iterator it = nodes.keySet().iterator();
  while(it.hasNext()){
    String ip = (String)it.next();
    Node n = (Node) nodes.get(ip);
    n.displayRipple();
  }  
}

synchronized void drawLinks() {
  Iterator it = links.keySet().iterator();
  while(it.hasNext()){
    String from_and_to = (String)it.next();
    Link l = (Link) links.get(from_and_to);

    if(l.greyscale > 25) {
      l.display();
      l.dim();
    }
  }    
}

////////////////////////////////////////////////////////////////////////////
// Helper methods 
int getXfromIP(String ip) {
  // Use last two IP address bytes for x/y coords
  int splitter = ip.lastIndexOf(".");
  int y = int(ip.substring(splitter+1)) * height / 255; // Scale to applet size
  String tmp = ip.substring(0,splitter);
  splitter = tmp.lastIndexOf(".");
  int x = int(tmp.substring(splitter+1)) * width / 255; // Scale to applet size
  return x;
}

int getYfromIP(String ip) {
  // Use last two IP address bytes for x/y coords
  int splitter = ip.lastIndexOf(".");
  int y = int(ip.substring(splitter+1)) * height / 255; // Scale to applet size
  return y;
}


String fromIPfromFromTo(String from_and_to) {
  String from         = from_and_to.substring(0, from_and_to.indexOf(" > "));
  String to           = from_and_to.substring(from_and_to.indexOf(" > ")+3);
  String from_ip      = from.substring(0, from.indexOf(":"));
  return from_ip;
}

int fromPortfromFromTo(String from_and_to) {
  String from         = from_and_to.substring(0, from_and_to.indexOf(" > "));
  String to           = from_and_to.substring(from_and_to.indexOf(" > ")+3);
  String from_ip      = from.substring(0, from.indexOf(":"));
  String from_port    = from.substring(from.indexOf(":")+1);
  return int(from_port);
}

String toIPfromFromTo(String from_and_to) {
  String from         = from_and_to.substring(0, from_and_to.indexOf(" > "));
  String to           = from_and_to.substring(from_and_to.indexOf(" > ")+3);
  String from_ip      = from.substring(0, from.indexOf(":"));
  String from_port    = from.substring(from.indexOf(":")+1);
  String to_ip        = to.substring(0, to.indexOf(":"));
  return to_ip;
}

int toPortfromFromTo(String from_and_to) {
  String from         = from_and_to.substring(0, from_and_to.indexOf(" > "));
  String to           = from_and_to.substring(from_and_to.indexOf(" > ")+3);
  String from_ip      = from.substring(0, from.indexOf(":"));
  String from_port    = from.substring(from.indexOf(":")+1);
  String to_ip        = to.substring(0, to.indexOf(":"));
  String to_port      = to.substring(to.indexOf(":")+1);  
  return int(to_port);
}

boolean eitherPortMatches(String from_and_to, int p) {
  int from_port = fromPortfromFromTo(from_and_to);
  int to_port   = toPortfromFromTo(from_and_to);
  if((from_port == p) || (to_port == p)) {
    return true;
  }
  return false;
}

color port2color(int port) {
  if(port == 21)   { return color2  ; }   //ftp
  if(port == 22)   { return color8  ; }   //ssh
  if(port == 25)   { return color5  ; }   //smtp
  if(port == 53)   { return color11 ; }   //name-domain server
  if(port == 5353) { return color11 ; }   //name-domain server
  if(port == 68)   { return color9  ; }   //BOOTP client
  if(port == 69)   { return color9  ; }   //BOOTP client
  if(port == 80)   { return color4  ; }   //http
  if(port == 8020) { return color4  ; }   //http
  if(port == 443)  { return color4  ; }   //https
  if(port == 110)  { return color5  ; }   //pop3
  if(port == 123)  { return color7  ; }   //Network Time Protocol
  if(port == 137)  { return color10 ; }   //NETBIOS
  if(port == 138)  { return color10 ; }   //NETBIOS
  if(port == 139)  { return color10 ; }   //NETBIOS
  if(port == 427)  { return color3  ; }   //itunes?
  if(port == 5190) { return color6  ; }   //aim
  return color1;
  /*ADD THESE?
   	imap2		143/tcp		imap		# Interim Mail Access Proto
   	6346 gnutella / p2p
   	6348 gnutella / p2p
   	445 Samba
   	2222 (udp)	broadcasts Office on OSX  
   	*/
}

