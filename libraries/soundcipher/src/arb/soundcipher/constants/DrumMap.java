/*
 *  Copyright (c) 2008 by Andrew R. Brown
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package arb.soundcipher.constants;

/** An interface storing pitch value constants from the General MIDI drum map.
  * These constants can be used to make the code more readable and 
  * to relive the programmer from having to look up the GM
  * specification for drum key values.<br>
  * The constants are implemented by both the SoundCipher and
  * SCScore classes so they can be accessed using 'dot' notation
  * from instances of any of these classes, for example;<br>
  * after: SoundCipher sc = new SoundCipher(this);<br>
  * access contants using: sc.SNARE<br>
  * after: SCSCore score = new SCScore();<br>
  * access constants using: score.SNARE<br>
  * <br>
  * This class was borrowed from jMusic.
  *
  * @author Andrew Sorensen, Andrew Brown, Andrew Troedson, Adam Kirby
  */
public interface DrumMap {
    
        public static final float
                ACOUSTIC_BASS_DRUM = 35,
	    BASS_DRUM = 36, KICK = 36, KICK_DRUM = 36,
                SIDE_STICK         = 37,
	    ACOUSTIC_SNARE     = 38, SNARE = 38, SNARE_DRUM = 38,
                HAND_CLAP          = 39,
                ELECTRIC_SNARE     = 40,
                LOW_FLOOR_TOM      = 41,
	    CLOSED_HI_HAT      = 42, HIHAT = 42, HI_HAT = 42,
                HIGH_FLOOR_TOM     = 43,
                PEDAL_HI_HAT       = 44,
                LOW_TOM            = 45,
                OPEN_HI_HAT        = 46,
                LOW_MID_TOM        = 47,
                HI_MID_TOM         = 48,
	    CRASH_CYMBAL_1     = 49, CRASH = 49,
                HIGH_TOM           = 50,
	    RIDE_CYMBAL_1      = 51, RIDE = 51,
                CHINESE_CYMBAL     = 52,
                RIDE_BELL          = 53,
                TAMBOURINE         = 54,
                SPLASH_CYMBAL      = 55,
                COWBELL            = 56,
                CRASH_CYMBAL_2     = 57,
                VIBRASLAP          = 58,
                RIDE_CYMBAL_2      = 59,
                HI_BONGO           = 60,
                LOW_BONGO          = 61,
                MUTE_HI_CONGA      = 62,
                OPEN_HI_CONGA      = 63,
                LOW_CONGA          = 64,
                HIGH_TIMBALE       = 65,
                LOW_TIMBALE        = 66,
                HIGH_AGOGO         = 67,
                LOW_AGOGO          = 68,
                CABASA             = 69,
                MARACAS            = 70,
                SHORT_WHISTLE      = 71,
                LONG_WHISTLE       = 72,
                SHORT_GUIRO        = 73,
                LONG_GUIRO         = 74,
                CLAVES             = 75,
                HI_WOOD_BLOCK      = 76,
                LOW_WOOD_BLOCK     = 77,
                MUTE_CUICA         = 78,
                OPEN_CUICA         = 79,
                MUTE_TRIANGLE      = 80,
	    OPEN_TRIANGLE = 81, TRIANGLE = 81;       
}