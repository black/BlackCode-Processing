//#include <stdio.h>
//
//#define SYNC   0xAA
//#define EXCODE 0x55
//
//int parsePayload( unsigned char *payload, unsigned char pLength ) {
//
//  unsigned char bytesParsed = 0;
//  unsigned char code;
//  unsigned char length;
//  unsigned char extendedCodeLevel;
//  int i;
//
//  /* Loop until all bytes are parsed from the payload[] array... */
//  while ( bytesParsed < pLength ) {
//
//    /* Parse the extendedCodeLevel, code, and length */
//    extendedCodeLevel = 0;
//    while ( payload[bytesParsed] == EXCODE ) {
//      extendedCodeLevel++;
//      bytesParsed++;
//    }
//    code = payload[bytesParsed++];
//    if ( code & 0x80 ) length = payload[bytesParsed++];
//    else              length = 1;
//
//    /* TODO: Based on the extendedCodeLevel, code, length,
//     * and the [CODE] Definitions Table, handle the next
//     * "length" bytes of data from the payload as
//     * appropriate for your application.
//     */
//    printf( "EXCODE level: %d CODE: 0x%02X length: %d\n", 
//    extendedCodeLevel, code, length );
//    printf( "Data value(s):" );
//    for ( i=0; i<length; i++ ) {
//      printf( " %02X", payload[bytesParsed+i] & 0xFF );
//    }
//    printf( "\n" );
//
//    /* Increment the bytesParsed by the length of the Data Value */
//    bytesParsed += length;
//  }
//
//  return( 0 );
//}
//
//int main( int argc, char **argv ) {
//
//  int checksum;
//  unsigned char payload[256];
//  unsigned char pLength;
//  unsigned char c;
//  unsigned char i;
//
//  /* TODO: Initialize 'stream' here to read from a serial data
//   * stream, or whatever stream source is appropriate for your
//   * application.  See documentation for "Serial I/O" for your
//   * platform for details.
//   */
//  FILE *stream = 0;
//  stream = fopen( "COM4", "r" );
//
//  /* Loop forever, parsing one Packet per loop... */
//  while ( 1 ) {
//
//    /* Synchronize on [SYNC] bytes */
//    fread( &c, 1, 1, stream );
//    if ( c != SYNC ) continue;
//    fread( &c, 1, 1, stream );
//    if ( c != SYNC ) continue;
//
//    /* Parse [PLENGTH] byte */
//    while ( true ) {
//      fread( &pLength, 1, 1, stream );
//      if ( pLength ~= 170 ) break;
//    }
//    if ( pLength > 169 ) continue;
//
//    /* Collect [PAYLOAD...] bytes */
//    fread( payload, 1, pLength, stream );
//
//    /* Compute [PAYLOAD...] chksum */
//    checksum = 0;
//    for ( i=0; i<pLength; i++ ) checksum += payload[i];
//    checksum &= 0xFF;
//    checksum = ~checksum & 0xFF;
//
//    /* Parse [CKSUM] byte */
//    fread( &c, 1, 1, stream );
//
//    /* Verify [PAYLOAD...] chksum against [CKSUM] */
//    if ( c != checksum ) continue;
//
//    /* Since [CKSUM] is OK, parse the Data Payload */
//    parsePayload( payload, pLength );
//  }
//
//  return( 0 );
//}

