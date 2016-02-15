package guru.ttslib;

import com.sun.speech.freetts.util.BulkTimer;
import com.sun.speech.freetts.util.Utilities;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;


import com.sun.speech.freetts.audio.*;

/**
 * a stereo streaming audio player. Its based on the StreamingAudioPlayer from 
 * the freetts package. Instead of the original AudioPlayer the audioStream is 
 * reused between the textparts. 
 */

public class StereoAudioPlayer implements AudioPlayer {

    private volatile boolean paused;
    private volatile boolean done = false;
    private volatile boolean cancelled = false;
    
    private boolean is_left = true;
    private boolean is_right = true;

    private SourceDataLine line;
    private float volume = 1.0f; 
    private long timeOffset = 0L;
    private BulkTimer timer = new BulkTimer();

    private AudioFormat currentFormat = new AudioFormat(8000f, 16, 2, true, true);

    private boolean debug = false;
    private boolean audioMetrics = false;
    private boolean firstSample = true;

    private Object openLock = new Object();
    private Object lineLock = new Object();

    public void setLeft( boolean left ) {
        this.is_left = left;
    }

    public void setRight( boolean right ) {
        this.is_right = right;
    }

    
    private final static int AUDIO_BUFFER_SIZE = 8192;
    private final static int BYTES_PER_WRITE = 160; 

    public StereoAudioPlayer() {
        line = null;
        setPaused(false);
    }

    public synchronized void setAudioFormat(AudioFormat format) {
        currentFormat = new AudioFormat(format.getSampleRate(), format.getSampleSizeInBits(), 2, true, format.isBigEndian());
    }


    public AudioFormat getAudioFormat() {
        return currentFormat;
    }

    public void startFirstSampleTimer() {
        timer.start("firstAudio");
        firstSample = true;
    }


    private synchronized void openLine(AudioFormat format) {
        synchronized (lineLock) {
            if (line != null) {
                return;
            }
        }
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

        boolean opened = false;
        long totalDelayMs = 0;

        try {
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.addLineListener(new JavaStreamLineListener());
            line.open(format, AUDIO_BUFFER_SIZE);
            opened = true;

        } catch (LineUnavailableException lue) {
            System.err.println("LINE UNAVAILABLE: " + "Format is " + currentFormat);
        }

        if (opened) {
            setVolume(line, volume);
            resetTime();
            if (isPaused() && line.isRunning()) {
                line.stop();
            } else {
                line.start();
            }
        } else {
            if (line != null) {
                line.close();
            }
            line = null;
        }
    }


    public synchronized void pause() {
        if (!isPaused()) {
            setPaused(true);
            if (line != null) {
                line.stop();
            }
        }
    }

    public synchronized void resume() {
        if (isPaused()) {
            setPaused(false);
            if (!isCancelled() && line != null) {
                line.start();
                notify();
            }
        }
    }


    public void cancel() {
        if (audioMetrics) {
            timer.start("audioCancel");
        }

        synchronized (lineLock) {
            if (line != null && line.isRunning()) {
                line.stop();
                line.flush();
            }
        }

        /* sets 'cancelled' to false, which breaks the write while loop */
        synchronized (this) {
            cancelled = true;
            notify();
        }

        if (audioMetrics) {
            timer.stop("audioCancel");
            timer.getTimer("audioCancel").showTimesShortTitle("");
            timer.getTimer("audioCancel").showTimesShort(0);
        }
    }

    public synchronized void reset() {
        timer.start("audioOut");
        if (line != null) {
            waitResume();
            if (isCancelled() && !isDone()) {
                cancelled = false;
                line.start();
            }
        }
    }

    public synchronized void close() {
        done = true;
        if (line != null && line.isOpen()) {
            line.close();
            line = null;
            notify();
        }
    }


    public float getVolume() {
        return volume;
    }          

    public void setVolume(float volume) {
        if (volume > 1.0f) {
            volume = 1.0f;
        }
        if (volume < 0.0f) {
            volume = 0.0f;
        }
        this.volume = volume;
    }

    private void setPaused(boolean state) {
        paused = state;
    }

    private boolean isPaused() {
        return paused;
    }

    private void setVolume(SourceDataLine line, float vol) {
        if (line != null && line.isControlSupported (FloatControl.Type.MASTER_GAIN)) {
            FloatControl volumeControl = (FloatControl) line.getControl (FloatControl.Type.MASTER_GAIN);
            float range = volumeControl.getMaximum() - volumeControl.getMinimum();
            volumeControl.setValue(vol * range + volumeControl.getMinimum());
        }
    }

    public void begin(int size) {
        openLine(currentFormat);
        reset();
    }

    public synchronized boolean end()  {
        return true;
    }

    public boolean drain()  {
        return !isCancelled();
    }

    public synchronized long getTime()  {
        return (line.getMicrosecondPosition() - timeOffset) / 1000L;
    }


    public synchronized void resetTime() {
        timeOffset = line.getMicrosecondPosition();
    }

    public boolean write(byte[] audioData) {
        return write(audioData, 0, audioData.length);
    }

    public boolean write(byte[] bytes_in, int offset, int size) {
        if (line == null) {
            return false;
        }
        byte[] bytes = new byte[ bytes_in.length *2 ];
        for ( int i = 0; i < bytes_in.length/2; i++) {
            if ( is_left ) {
                bytes[ i * 4] = bytes_in[i*2];
                bytes[ i * 4 + 1] = bytes_in[i*2+1];
            } else {
                bytes[ i * 4]  = 0;
                bytes[ i * 4 + 1] = 0;
            }

            if ( is_right ) {
                bytes[ i * 4 + 2] = bytes_in[i*2];
                bytes[ i * 4 + 3] = bytes_in[i*2+1];

            } else {
                bytes[ i * 4 + 2] = 0;
                bytes[ i * 4 + 3] = 0;
            }
        }
        

        int bytesRemaining = size * 2;
        int curIndex = offset;

        if (firstSample) {
            firstSample = false;
            timer.stop("firstAudio");
            if (audioMetrics) {
                timer.getTimer("firstAudio").showTimesShortTitle("");
                timer.getTimer("firstAudio").showTimesShort(0);
            }
        }
        while  (bytesRemaining > 0 && !isCancelled()) {

            if (!waitResume()) {
                return false;
            }

            int bytesWritten;

           synchronized (lineLock) {
                bytesWritten = line.write
                    (bytes, curIndex, 
                     Math.min(BYTES_PER_WRITE, bytesRemaining));

                if (bytesWritten != bytesWritten) {
                    debugPrint
                        ("RETRY! bw" +bytesWritten + " br " + bytesRemaining);
                }
            
                curIndex += bytesWritten;
                bytesRemaining -= bytesWritten;
            }
        }
        return !isCancelled() && !isDone();
    }


    /**
     * Waits for resume. If this audio player
     * is paused waits for the player to be resumed.
     * Returns if resumed, cancelled or shutdown.
     *
     * @return true if the output has been resumed, false if the
     *     output has been cancelled or shutdown.
     */
    private synchronized boolean waitResume() {
        while (isPaused() && !isCancelled() && !isDone()) {
            try {
                debugPrint("   paused waiting ");
                wait();
            } catch (InterruptedException ie) {
            }
        }

        return !isCancelled() && !isDone();
    }


    /**
     * Returns the name of this audioplayer
     *
     * @return the name of the audio player
     */
    public String toString() {
        return "StereoAudioPlayer";
    }


    /**
     * Outputs a debug message if debugging is turned on
     *
     * @param msg the message to output
     */
    private void debugPrint(String msg) {
        if (debug) {
            System.out.println(toString() + ": " + msg);
        }
    }

    /**
     * Shows metrics for this audio player
     */
    public void showMetrics() {
        timer.show("StereoAudioPlayer");
    }

    /**
     * Determines if the output has been cancelled. Access to the
     * cancelled variable should be within a synchronized block such
     * as this to ensure that access is coherent.
     *
     * @return true if output has been cancelled
     */
    private synchronized boolean isCancelled() {
        return cancelled;
    }

    /**
     * Determines if the output is done. Access to the
     * done variable should be within a synchronized block such
     * as this to ensure that access is coherent.
     *
     * @return true if output has completed
     */
    private synchronized boolean isDone() {
        return done;
    }

    /**
     * Provides a LineListener for this clas.
     */
    private class JavaStreamLineListener implements LineListener {

        /**
         * Implements update() method of LineListener interface. Responds
         * to the line events as appropriate.
         *
         * @param event the LineEvent to handle
         */
        public void update(LineEvent event) {
            if (event.getType().equals(LineEvent.Type.OPEN)) {
            }
        }
    }
}

