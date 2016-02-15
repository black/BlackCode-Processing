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

/** An interface the defines the various constants for status types of 
 * MIDI messages. This interface is implemeted by the SoundCipher and 
 * SCScore classes  and can be acessed as sc.CONTROL_CHANGE; where sc 
 * is an instance of the SoundCipher class, for example.
  *
  * @author Andrew Brown
  */

import javax.sound.midi.ShortMessage;

public interface MidiMessageTypes {
    
        public static final int
	    ACTIVE_SENSING = ShortMessage.ACTIVE_SENSING,
	    CHANNEL_PRESSURE = ShortMessage.CHANNEL_PRESSURE,
	    CONTROL_CHANGE = ShortMessage.CONTROL_CHANGE,
	    END_OF_EXCLUSIVE = ShortMessage.END_OF_EXCLUSIVE,
	    MIDI_TIME_CODE = ShortMessage.MIDI_TIME_CODE,
	    NOTE_OFF = ShortMessage.NOTE_OFF,
	    NOTE_ON = ShortMessage.NOTE_ON,
	    PITCH_BEND = ShortMessage.PITCH_BEND,
	    POLY_PRESSURE = ShortMessage.POLY_PRESSURE,
	    PROGRAM_CHANGE = ShortMessage.PROGRAM_CHANGE,
	    SONG_POSITION_POINTER = ShortMessage.SONG_POSITION_POINTER,
	    SONG_SELECT = ShortMessage.SONG_SELECT,
	    START = ShortMessage.START,
	    STOP = ShortMessage.CONTROL_CHANGE,
	    SYSTEM_RESET = ShortMessage.SYSTEM_RESET,
	    TIMING_CLOCK = ShortMessage.TIMING_CLOCK,
	    TUNE_REQUEST = ShortMessage.TUNE_REQUEST;
}