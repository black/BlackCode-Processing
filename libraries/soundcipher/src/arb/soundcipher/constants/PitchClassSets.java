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

/** An interface storing pitchclass [scales and modes] constants.
 * These constants can be used to make the code more readable and 
 * to relive the programmer from having to regularly look up the
 * interval patterns for the pitch classes. All sets are for scales
 * or modes that have C as a root pitch. Use simple offset math for
 * scles in other keys.<br>
 * The constants are implemented by both the SoundCipher and
 * SCScore classes so they can be accessed using 'dot' notation
 * from instances of any of these classes, for example;<br><br>
 * after: SoundCipher sc = new SoundCipher(this);<br>
 * access contants using: sc.MAJOR<br>
<br>
 * after: SCSCore score = new SCScore();<br>
 * access constants using: score.IONIAN<br>
 * <br>
 * This class was borrows from the jMusic Scales interface.
 *
 * @author Andrew Sorensen, Andrew Brown, Andrew Troedson, Adam Kirby
 */
public interface PitchClassSets {

    public static final float[] 
	CHROMATIC = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11},
	WHOLETONE = {0, 2, 4, 6, 8, 10},
	AUGMENTED = {0, 3, 4, 7, 8, 11},
	BLUES = {0, 3, 5, 6, 7, 10},
	MAJOR = {0, 2, 4, 5, 7, 9, 11},
	MINOR = {0, 2, 3, 5, 7, 8, 10},
	HARMONIC_MINOR = {0, 2, 3, 5, 7, 8, 11},
	MELODIC_MINOR = {0, 2, 3, 5, 7, 8, 9, 10, 11}, // mix of ascend and descend
	NATURAL_MINOR = {0, 2, 3, 5, 7, 8, 10},
	DIATONIC_MINOR = {0, 2, 3, 5, 7, 8, 10},
	AEOLIAN = {0, 2, 3, 5, 7, 8, 10},
	IONIAN = {0, 2, 4, 5, 7, 9, 11},
	DORIAN = {0, 2, 3, 5, 7, 9, 10},
	PHRYGIAN = {0, 1, 3, 5, 7, 8, 10},
	LOCRIAN = {0, 1, 3, 5, 6, 8, 10},	
	LYDIAN = {0, 2, 4, 6, 7, 9, 11},
	MIXOLYDIAN = {0, 2, 4, 5, 7, 9, 10},
	PENTATONIC = {0, 2, 4, 7, 9},
	MAJOR_PENTATONIC = {0, 2, 4, 7, 9},
	MINOR_PENTATONIC = {0, 3, 5, 7, 10},
	TURKISH = {0, 1, 3, 5, 7, 10, 11},
	INDIAN = {0, 1, 1, 4, 5, 8, 10},
	MAJOR_TRIAD = {0, 4, 7},
	MINOR_TRIAD = {0, 3, 7},
	ROOT_FIFTH = {0, 7},
	ROOT = {0};
}