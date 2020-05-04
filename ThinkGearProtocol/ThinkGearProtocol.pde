/*
1. How to parse the serial data stream of bytes to reconstruct the 
   various types of brainwave data sent by the ThinkGear
2. How to interpret and use the various types of brainwave 
   data that are sent from the ThinkGear (including Attention, 
   Meditation, and signal quality data) in a BCI application
3. How to send reconfiguration Command Bytes to the ThinkGear, 
   for on-the-fly customization of the module's behavior and output
   
1. ThinkGear Data Values
2. ThinkGear Packets 
3. ThinkGear Command Bytes
   
*/

int baudRate = 56000;
int POOR_SIGNAL=0;  // Any non-zero value indicates that some sort of noise contamination is detected
int RAW_MARKER = 0X00;
