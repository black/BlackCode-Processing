import processing.serial.*;
Serial myPort;  // Create object from Serial class
/*---------------*/
boolean debug = false;
boolean newPacket = false;
//payloadData[64] = {0};

int poorQuality = 250;
int attention = 0;
int meditation = 0;
int lastReceivedPacket = 0;
int timeOut = 5000;
/*---------------*/
void setup() {
  size(600, 400); 
  String[] portName = Serial.list();
  for (int i=0; i<portName.length; i++) {
    if (portName[i]!=null) {
      myPort = new Serial(this, portName[i], 9600); // baudrate: 57600, 9600, 115400
      myPort.buffer(7);
      println(portName[i]  + " " + i);
    }
  }
}

void draw() {  
  background(255);
}

byte readFirstByte() {
  int byteRead = 0x00;
  if (Serial.available()) {
    byteRead = Serial.read();
  }
  return byteRead;
}

byte readOneByte() {
  int byteRead;
  int n = 0;
  while (!Serial.available () && n < 10000) n++;
  byteRead = Serial.read();
  return byteRead;
}

void update() {
  newPacket = false;
  // Look for sync bytes
  if (readFirstByte() == 170) {
    if (readOneByte() == 170) {

      byte payloadLength = readOneByte();
      if (payloadLength > 169) {                      //Payload length can not be greater than 169
        return;
      }

      byte generatedChecksum = 0;        
      for (int i = 0; i < payloadLength; i++) {  
        payloadData[i] = readOneByte();              //Read payload into memory
        generatedChecksum += payloadData[i];
      }   

      byte checksum = readOneByte();                 //Read checksum byte from stream      
      generatedChecksum = 255 - generatedChecksum;   //Take one's compliment of generated checksum

        if (checksum == generatedChecksum) {
        poorQuality = 200;
        attention = 0;
        meditation = 0;

        for (int i = 0; i < payloadLength; i++) {    // Parse the payload
          switch (payloadData[i]) {
          case 2:
            i++;            
            poorQuality = payloadData[i];
            newPacket = true;            
            break;
          case 4:
            i++;
            attention = payloadData[i];                        
            break;
          case 5:
            i++;
            meditation = payloadData[i];
            break;
          case 0x80:
            i = i + 3;
            break;
          case 0x83:
            i = i + 25;      
            break;
          default:
            break;
          }
        }

        if (newPacket) {
          if (debug) {
            Serial.print("PoorQuality: ");
            Serial.print(poorQuality, DEC);
            Serial.print(" Attention: ");
            Serial.print(attention, DEC);
            Serial.print(" Meditation: ");
            Serial.print(meditation, DEC);
            Serial.print(" Time since last packet: ");
            Serial.print((millis() - lastReceivedPacket)/1000.0, 3);
            Serial.print("\n");
          }
          lastReceivedPacket = millis();
        } else if (millis() - lastReceivedPacket > timeOut) {
          poorQuality = 200;
          attention = 0;
          meditation = 0;
        }
      } else {
        // Checksum Error
      }  // end if else for checksum
    } // end if read 0xAA byte
  } // end if read 0xAA byte
}

