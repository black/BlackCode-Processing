/*Packet Structure
 [SYNC] [SYNC] [PLENGTH]      [PAYLOAD...]          [CHKSUM]
 _______________________     _____________        ____________
 ^^^^^^^^(Header)^^^^^^^       ^^(Payload)^^      ^(Checksum)^
       single byte           up to 169 bytes       single byte 

Packet Header
[SYNC] [SYNC] [PLENGTH]
_______________________
^^^^^^^^(Header)^^^^^^^
1.[SYNC] bytes are used to signal the beginning of a new arriving Packet 
2.[PLENGTH] byte indicates the length of the Packet's Data Payload ( in bytes) 

Payload
1.[CHKSUM] Byte must be used to verify the integrity of the Packet's
2. Parsing of the Data Payload typically should not even be attempted until after the Payload Checksum Byte [CHKSUM] is verified as described in the following section.

Payload Checksum
The [CHKSUM] Byte must be used to verify the integrity of the Packet's data payload -
1.Summing all the bytes of the Packet's Data Payload
2.Taking the lowest 8 bits of the sum
3.Performing the bit inverse (one's compliment inverse) on those lowest 8 bits

Data Payload Structure
nce the Checksum of a Packet has been verified, the bytes of the Data Payload can be parsed.
DataRow Format
([EXCODE]...) [CODE]  ([VLENGTH])  [VALUE...]
____________________ ____________ ___________
^^^^(Value Type)^^^^ ^^(length)^^ ^^(value)^^
 
 byte: value // Explanation
 [ 0]: 0xAA  // [SYNC]
 [ 1]: 0xAA  // [SYNC]
 [ 2]: 0x08  // [PLENGTH] (payload length) of 8 bytes
 [ 3]: 0x02  // [CODE] POOR_SIGNAL Quality
 [ 4]: 0x20  // Some poor signal detected (32/255)
 [ 5]: 0x01  // [CODE] BATTERY Level
 [ 6]: 0x7E  // Almost full 3V of battery (126/127)
 [ 7]: 0x04  // [CODE] ATTENTION eSense
 [ 8]: 0x12  // eSense Attention level of 18%
 [ 9]: 0x05  // [CODE] MEDITATION eSense
 [10]: 0x60  // eSense Meditation level of 96%
 [11]: 0xE3  // [CHKSUM] (1's comp inverse of 8-bit Payload sum of 0x1C)
 

 http://developer.neurosky.com/docs/doku.php?id=thinkgear_communications_protocol#thinkgear_packets
 http://developer.neurosky.com/docs/doku.php?id=thinkgear_communications_protocol#step-by-step_guide_to_parsing_a_packet
 
 */
