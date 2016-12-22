import java.io.*;
import java.util.*;
import java.io.IOException;
import java.lang.ProcessBuilder;
import java.lang.Process;
import java.net.*;
import java.lang.Object.*;

ArrayList<String>ssids=new ArrayList<String>();
ArrayList<String>signals=new ArrayList<String>();
String ssid;
void setup() {
  size(250, 400);
}

void draw() {
  background(-1);
  wifiList(); 
  wifiList2(); 
  fill(0);
  text(ssid, width/2, 50);
}


void wifiList() {
  ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "netsh wlan show all");
  //builder.redirectErrorStream(true);
  Process p = builder.start();
  BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
  String line;
  while (true) {
    line = r.readLine();
    if (line.contains("SSID")||line.contains("Signal")) {
      if (!line.contains("BSSID"))
        if (line.contains("SSID")&&!line.contains("name")&&!line.contains("SSIDs"))
        {
          line=line.substring(8);
          ssids.add(line);
        }
      if (line.contains("Signal"))
      {
        line=line.substring(30);
        signals.add(line);
      }

      if (signals.size()==7)
      {
        break;
      }
    }
  }
  for (int i=1; i<ssids.size (); i++)
  {
    System.out.println("SSID name == "+ssids.get(i)+"   and its signal == "+signals.get(i)  );
  }
}


//try {
//  BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));    
//  String userInput;    
//  while ( (userInput = stdIn.readLine ()) != null) {
//    System.out.println(userInput);
//  }
//} 
//catch(IOException ie) {
//  ie.printStackTrace();
//}   


void wifiList2() {
  ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "netsh wlan show interfaces");
  builder.redirectErrorStream(true);
  Process p = builder.start();
  BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
  String line;
  while (true) {
    line = r.readLine();
    if (line.contains("SSID")) {
      ssid = line.split("\\s+")[3];
      System.out.println(ssid);
    }
  }
}

