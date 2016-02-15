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

package arb.soundcipher;

import javax.sound.midi.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;
import java.applet.AudioClip;
import processing.core.PApplet;
import arb.soundcipher.constants.ProgramChanges;
import arb.soundcipher.constants.PitchClassSets;
import arb.soundcipher.constants.DrumMap;
import arb.soundcipher.constants.MidiMessageTypes;

/**
 * The <code>SoundCipher<code> class provides an easy way to play music in 
 * <a href="http://processing.org">Processing</a>.
 * It has methods for playing a single note, a phrase (a sequence of notes), or
 * a chord (a cluster of notes) using JavaSound's built-in synthesizer or via MIDI 
 * output to an external synthesizer. 
 * The class also provides methods for simple playback of a specified audio file. 
 * The {@link SCScore} class is used to provide musical data structure and timing of events.
 * <br>
 * <br>Below is a simple Processing example using the SoundCipher library:
 * <br>
 * <pre>
 * import arb.soundcipher.*;
 * 
 * SoundCipher sc = new SoundCipher();
 * int[] scale = {57, 60, 60, 62, 64, 67, 67, 69, 72};
 * 
 * void setup() {
 *     frameRate(8);
 * }
 * 
 * void draw() {
 *     if (random(1) < 0.8) {
 *         sc.playNote(scale[(int)random(scale.length)], (int)random(80)+40, 0.2);
 *         stroke(color(random(256), random (256), random(256)));
 *         rect(random(100), random(100), random(40), random(40));
 *     }
 * }
 * </pre>
 * SoundCipher does not provide sound synthesis or audio recording functions, 
 * for these we suggest using the <a href="http://code.compartmental.net/tools/minim/">Minim</a> 
 * library which can be integrated with SoundCipher when required.
 *
 * @author <a href="http://www.explodingart.com/arb/">Andrew R. Brown</a>
 */ 

public class SoundCipher extends SCUtilities implements ProgramChanges, DrumMap, PitchClassSets, MidiMessageTypes {
    /** Creates an instance of the SCScore class for use behind the scenes */
    public SCScore score = new SCScore();
    /** The audioClip instance used to play audio files by this class */
    private AudioClip clip;
    /** Specifies the default instrument to 0 [0 - 127] (0 = piano on the JavaSound synthesizer) */
    public double instrument = 0;
    /** Specifies the default MIDI channel to 0 [0-15] */
    public double channel = 0;
    /** Specifies the default tempo (speed) to the 120 beats per minute */
    public double tempo = 120.0;
    /** Specifies the default pan poisition to be 64, in the centre [0 - 127] */
    public double pan = 64;
    /** Specifies the defult number of repeats for score playback [-1 = infinite] */
    public double repeat = 0;
    /** Specifies the amount of duration that will sound, e.g., 0.2 = stacatto, 1.0 = legato */
    public double articulation = 0.8;
    /** A reference to the Processing application used to communicate with it */
    public PApplet app;
    private InputStream is;
    private BufferedInputStream bis;
    private AudioInputStream audioInputStream;
    private Receiver rec;

    /**
     * The normal constructor used for SoundCipher as a Processing Library.
     * The PApplet instance provides a link to the Processing application
     * and is used to access the class path for loading files and so on.
     *
     * @param processingInstance The current Processing instance (normally pass 'this' here).
     */
    public SoundCipher(PApplet processingInstance) {
	this.app = processingInstance;
	try {
	    rec = MidiSystem.getReceiver();
	} catch (javax.sound.midi.MidiUnavailableException e) {e.printStackTrace();}
    }

    /**
     * An alternative constructor for using SoundCipher outside of Processing.
     * A Warning!! When using this constructor the audio methods expect
     * explicit path and file name arguments, rather than filenames assumed
     * to be relative to the Processing 'data' folder associated with the sketch file.
     */
    public SoundCipher() {  }

    /**
     * Creates a single note for immediate playback.
     *
     * @param pitch The MIDI pitch at which the note will play [0-127]
     * @param dynamic The loudness, MIDI velocity, of the note [0-127]
     * @param duration The length that the note will sound, in beats
     */
    public void playNote(double pitch, double dynamic, double duration) {
	this.playNote(0.0, this.channel, this.instrument, pitch, dynamic, 
		      duration, duration * 0.8, this.pan);
    } 

    /**
     * Schedules a single note for playback.
     *
     * @param startBeat Specifies when the note will play, in beats, after code execution
     * @param channel The MIDI channel to use for this note [0-15]
     * @param instrument The JavaSound instrument (sound) to use for this note [0-127]
     * @param pitch The MIDI pitch (frequency) at which the note will play [0-127]
     * @param dynamic The loudness, MIDI velocity, of the note [0-127]
     * @param duration The duration that the note will sound, in beats
     * @param articulation A length multiplier for duration (0.8 by default)
     * @param pan The note's left-right location in the stereo field [0-127]
     */
    public void playNote(double startBeat, double channel, double instrument, 
			 double pitch, double dynamic, double duration,
			 double articulation, double pan) {
	score.stop();	
	score.empty();
	score.addNote(startBeat, channel, instrument, pitch, dynamic, duration, 
		  articulation, pan);
	score.play();
    }

 /**
     * Creates a note sequence for immediate playback.
     *
     * @param pitches The MIDI pitches (frequencies) at which the note will play [0-127]
     * @param dynamics The loudness, MIDI velocity, of each note [0-127]
     * @param durations The length that each note will sound, in beats
     */
    public void playPhrase(float[] pitches, float[] dynamics, float[] durations) {
	double[] dPitches = new double[pitches.length];
	double[] dDynamics = new double[pitches.length];
	double[] dDurations = new double[pitches.length];
	for(int i=0; i<pitches.length; i++) {
	    dPitches[i] = (double)pitches[i];
	    dDynamics[i] = (double)dynamics[i];
	    dDurations[i] = (double)durations[i];
	}
	double[] articulations = new double[durations.length];
	double[] pans = new double[durations.length];
	for(int i=0; i<articulations.length; i++) {
	    articulations[i] = this.articulation;
	    pans[i] = this.pan;
	}
	this.playPhrase(0.0, this.channel, this.instrument, dPitches, dDynamics, 
			dDurations, articulations, pans);
    } 

    /**
     * Creates a note sequence for immediate playback.
     *
     * @param pitches The MIDI pitches (frequencies) at which the note will play [0-127]
     * @param dynamics The loudness, MIDI velocity, of each note [0-127]
     * @param durations The length that each note will sound, in beats
     */
    public void playPhrase(double[] pitches, double[] dynamics, double[] durations) {
	double[] articulations = new double[durations.length];
	double[] pans = new double[durations.length];
	for(int i=0; i<articulations.length; i++) {
	    articulations[i] = this.articulation;
	    pans[i] = this.pan;
	}
	this.playPhrase(0.0, this.channel, this.instrument, pitches, dynamics, 
			durations, articulations, pans);
    } 
    
    /**
     * Schedules a note sequence for playback.
     *
     * @param startBeat Specifies when the notes will play, in beats, after code execution
     * @param channel The MIDI channel to use for these notes [0-15]
     * @param instrument The JavaSound instrument (sound) to use for these notes [0-127]
     * @param pitches The MIDI pitches (frequencies) at which the notes will play [0-127]
     * @param dynamics The loudness, MIDI velocity, of each note [0-127]
     * @param durations The length that each note will sound, in beats
     * @param articulations Length multipliers for each duration (0.8 by default)
     * @param pans Each note's left-right location in the stereo field [0-127]
     */
    public void playPhrase(double startBeat, double channel, double instrument, 
			   double[] pitches, double[] dynamics, double[] durations,
			   double[] articulations, double[] pans) {
	score.stop();	
	score.empty();
	score.addPhrase(startBeat, channel, instrument, pitches, dynamics, 
		    durations, articulations, pans);
	score.play();
    } 

    /**
     * Schedules a note cluster (chord) for immediate playback.
     *
     * @param pitches The MIDI pitches (frequencies) for each note [0-127]
     * @param dynamic The loudness, MIDI velocity, of the notes [0-127]
     * @param duration The length that the notes will sound, in beats
     */
    public void playChord(float[] pitches, double dynamic, double duration) {
	this.playChord(0.0, this.channel, this.instrument, pitches, dynamic, 
		      duration, 0.85, this.pan);
    } 

    /**
     * Schedules a note cluster (chord) for playback.
     *
     * @param startBeat Specifies when the notes will play, in beats, after code execution
     * @param channel The MIDI channel to use for these notes [0-15]
     * @param instrument The JavaSound instrument (sound) to use for these notes [0-127]
     * @param pitches The MIDI pitches (frequencies) for each note [0-127]
     * @param dynamic The loudness, MIDI velocity, of the notes [0-127]
     * @param duration The length that the notes will sound, in beats
     * @param articulation A length multiplier for duration (0.8 by default)
     * @param pan The note's left-right location in the stereo field [0-127]
     */
    public void playChord(double startBeat, double channel, double instrument, 
			  float[] pitches, double dynamic, double duration,
			  double articulation, double pan) {
	score.stop();	
	score.empty();
	score.addChord(startBeat, channel, instrument, pitches, dynamic, 
		   duration, articulation, pan);
	score.play();
    }

    /**
     * Specifies the default instrument number [0-127].
     */
    public void instrument(double instrumentNumber) {
	this.instrument = instrumentNumber;
    }

    /**
     * Specifies the default channel number [0-15].
     */
    public void channel(double channelNumber) {
	this.channel = channelNumber;
    }

    /**
     * Specifies the default tempo (speed).
     */
    public void tempo(double newTempo) {
	this.tempo = newTempo;
	score.tempo(newTempo);
    }

    /**
     * Specifies the default pan position [0-127].
     */
    public void pan(int newPanVal) {
	this.pan = newPanVal;
    }

    /**
     * Specifies the default number of repeatitions of the score on playback.
     * -1 = infinite
     */
    public void repeat(double newRepeatVal) {
	this.repeat = newRepeatVal;
	score.repeat(this.repeat);
    }

    //****************************    
    //**      MIDI methods      **
    //****************************
    /**
     * Report the availible MIDI devices. Prints the list of devices and id info to the console.
     *
     * For external MIDI device info. to be accessible on Mac OSX you need
     * to install Bob Lang's Mandalone Java extension. http://www.mandolane.co.uk/
     * An alternative that uses CoreMIDI but does not work directly with this code is mmj.
     * http://www.humatic.de/htools/mmj.htm - not compatible with OS X Java 1.6 : (
     */
    static public void getMidiDeviceInfo() {
	try {
	    MidiDevice.Info[]  midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
	    for (int i=0; i<midiDeviceInfo.length; i++) {
		System.out.println("Java MIDI Device number " +i + " : Name =  " + midiDeviceInfo[i].getName() + 
				   ". Vendor =  " + midiDeviceInfo[i].getVendor());
	    }
	} catch (Exception e) {
	    System.out.println("SoundCipher error: problem reading midiDeviceInfo() ");
	    e.printStackTrace();
	}
    }

    /**
     * Specify the MIDI device to be used by SoundCipher.
     *
     * To see the availible devices and their device number use getMidiDeviceInfo().
     *
     * @param deviceNumber The id of the MIDI output as returned from getMidiDeviceInfo()
     */
    public void setMidiDeviceOutput(int deviceNumber) {
	score.setMidiDeviceOutput(deviceNumber);
	/*
	  try {
	    MidiDevice.Info[]  midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
	    MidiSystem.getMidiDevice(midiDeviceInfo[deviceNumber]).open();
	    System.out.println("sendMidi output is now directed to: " +midiDeviceInfo[deviceNumber].getName());
	    rec = MidiSystem.getMidiDevice(midiDeviceInfo[deviceNumber]).getReceiver(); //MidiSystem.getReceiver();
	} catch (javax.sound.midi.MidiUnavailableException e) {e.printStackTrace();}
	*/
    }

    public void playMidiFile(String fileName) {
	this.playMidiFile(fileName, this.tempo);
    }

    public void playMidiFile(String fileName, double tempo) {
	//score.addMidiFile(app.dataPath(fileName));
	try {
	    InputStream is = app.createInput(fileName);
	    score.addMidiStream(is);
	} catch (Exception ioe) {
	    System.out.println("SoundCipher IO error: in playMidiFile(): " + ioe.getMessage());
	}
	score.tempo(tempo);
	score.play();
    }	

    /**
     * Transmit a MIDI message immediatly.
     *
     * @param type The MIDI message status value; the type of message to add (control change, pitch bend etc.)
     * @param channel The MIDI channel to use for this message [0-15]
     * @param val1 The first byte of data (e.g., control change number)
     * @param val2 The second byte of data (e.g., control change value)
     */
    

    public void sendMidi(int type, double channel, double val1, double val2) {
	if (val1 > 127) val1 = 127;
	else if (val1 < 0) val1 = 0;
	if (val2 > 127) val2 = 127;
	else if (val2 < 0) val2 = 0;
        try {
	    ShortMessage sMessage = new ShortMessage();
            sMessage.setMessage(type, (int)channel, (int)val1, (int)val2);
	    rec.send(sMessage, (long)0.0);
	} catch (Exception ex) { ex.printStackTrace(); }
    }

    //*************************
    //** direct OSC messages **
    //*************************
    
    /**
     * Transmit an Open Sound Control message immediatly.
     */
    public void sendOSC() {
	// to do
    }

    //***********************
    // ** Audio playback **
    // **********************
    
    /**
     * Stream playback of a specified audio file using JavaSound.
     *
     * Audio files are assumed to be in a folder named 'data' in the processing file directory.
     * Audio reading of MP3 files may not work for Processing Applets. Use the Minim library for
     * more robust audio handling.
     *
     * @param fileName Name of audio file.
     */
    
    public void playAudioFile(String fileName) {
	try {
	     is = app.createInput(fileName);
	     bis = new BufferedInputStream(is);
	     audioInputStream = AudioSystem.getAudioInputStream(bis);
	     new AudioFilePlayThread(audioInputStream).start();
	} catch (IOException ioe) {
	    System.out.println("SoundCipher IO error: in playAudioFile(): " + ioe.getMessage());
	}
	catch (UnsupportedAudioFileException uafe) {
	    System.out.println("SoundCipher unsupported Audio File error: in playAudioFile():" + uafe.getMessage());
	}
    }

    //Inner class to play back the data from the audio file.
    // This class adapted from one by Richard G. Baldwin
    // http://www.developer.com/java/other/article.php/2173111
    /**
     * Inner class for synchronous play back of audio file.
     */
    class AudioFilePlayThread extends Thread {
	byte tempBuffer[] = new byte[1024];
	private AudioInputStream audioInputStream;

	public AudioFilePlayThread(AudioInputStream audioInputStream ) {
	    this.audioInputStream = audioInputStream;
	}
	
	public void run(){
	    try{
		AudioFormat audioFormat  = audioInputStream.getFormat();
		DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
		SourceDataLine sourceDataLine =(SourceDataLine)AudioSystem.getLine(dataLineInfo);
		sourceDataLine.open(audioFormat);
		sourceDataLine.start();
		
		int cnt;
		while((cnt = audioInputStream.read(tempBuffer,0,tempBuffer.length)) != -1){
		    if(cnt > 0){
			sourceDataLine.write(tempBuffer, 0, cnt);
		    }
		}
		sourceDataLine.drain();
		sourceDataLine.stop();
		sourceDataLine.close();
		//soundFilePlaying = false;
		sourceDataLine.close();
		audioInputStream.close();
		bis.close();
		is.close();
	    }catch (Exception e) {
		System.out.println("SoundCipher error: in audio class PlayThread ");
		e.printStackTrace();
	    }
	}
    }

    /**
     * Load a specified audio file using the Applet class.
     * Use playAudioClip(AudioClip clip) to load and play in one process.
     *
     * Audio files are assumed to be in a 'data' folder in the processing file directory.
     * Audio reading of files may not work for Processing Applets. Use the Minim library for
     * more robust audio handling.
     *
     * @param fileLocation Path and name of audio file. Absolute file path required.
     * @return An AudioClip object for use by playAudioClip()
     */
    public AudioClip loadAudioClip(String fileLocation) {
	if (clip != null) {
	    clip.stop();
	    clip = null;
	}
	try {
	    clip = java.applet.Applet.newAudioClip(new java.net.URL ("file://" + app.sketchPath("data/"+fileLocation)));
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return clip;
    }

    /**
     * Playback a previously loaded audio clip.
     *
     * @param clip The audioclip object loaded by loadAudioClip()
     */
    public void playAudioClip(AudioClip clip) {
	clip.play();
    }

    /**
     * Load a specified audio file into memory and play using the Applet class.
     *
     * Audio files are assumed to be in a folder called 'data' in the processing file directory.
     * Audio reading of files may not work for Processing Applets. Use the Minim library for
     * more robust audio handling.
     *
     * @param fileLocation Path and name of audio file. Absolute file path required.
     */
    public void playAudioClip(String fileLocation) {
	if (clip != null) {
	    clip.stop();
	    clip = null;
	}
	try {
	    clip = loadAudioClip(fileLocation);
	    clip.play();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Halt the specified audio clip 
     *
     * @param clip A previously created AudioClip object
     */
    public void stopAudioClip(AudioClip clip) {
	clip.stop();
    }

    /**
     * Halt the playing audio clip 
     */
    public void stopAudioClip() {
	if (clip != null) clip.stop();
    }

    /**
     * Repeat indefinitly the specified audio clip. Use stopAudioClip() to halt playback.
     *
     * @param AudioClip A previously loaded AudioClip object
     */
    public void loopAudioClip(AudioClip clip) {
	clip.loop();
    }

    /**
     * Load a specified audio file into memory and play it repeatedly using the Applet class.
     *
     * Audio files are assumed to be in a folder called 'data' in the processing file directory.
     * Audio reading of files may not work for Processing Applets. Use the Minim library for
     * more robust audio handling. Use stopAudioClip() to halt looping.
     *
     * @param fileLocation Path and name of audio file. Absolute file path required.
     */
    public void loopAudioClip(String fileLocation) {
	if (clip != null) {
	    clip.stop();
	    clip = null;
	}
	try {
	    clip = loadAudioClip(fileLocation);
	    clip.loop();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Halt any playing note, phrase, chord or audioClip.
     */
    public void stop() {
	if (score != null) score.stop();
	if (clip != null) clip.stop();
    }
}