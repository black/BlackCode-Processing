import processing.serial.*; 
import java.net.*;
import javax.com.PortInUseException; 
Serial myPort; 

void setup() { 
  size(200, 200);
  println(Serial.list());
  for (int i=0; i<Serial.list ().length; i++) { 
    try {
      myPort = new Serial(this, Serial.list()[i], 9600) ;      // Open port "i" in list
    } 
    catch (PortInUseException piue) { 
      println("exc");
      portAvailable=false;
      myPort.stop();
    }
  }
}

void draw() {
  background(-1); 
  //  for (int i=0; i<Serial.list ().length; i++) {
  //    if (Serial.list()[i]!= null) { 
  fill(0);
  textAlign(CENTER);
  textSize(18);
  port = new Serial(this, Serial.list()[0], 9600);
  text(port+" ", width>>1, height/2);
  //    }
  //  }
}
