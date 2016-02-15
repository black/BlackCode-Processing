package guru.ttslib;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.AudioPlayer;

/**
 * @author guru
 * 
 * a freetts wrapper for processing
 */

public class TTS {
    private Voice voice;
    private StereoAudioPlayer audioPlayer;

    public TTS() {
        System.setProperty("com.sun.speech.freetts.voice.defaultAudioPlayer", "guru.ttslib.StereoAudioPlayer");
        audioPlayer = new StereoAudioPlayer();
        VoiceManager voiceManager = VoiceManager.getInstance();
        voice = voiceManager.getVoice( "kevin16" );
        voice.setAudioPlayer( audioPlayer );
        voice.allocate();
    }

    public TTS( String name ) {
        System.setProperty("com.sun.speech.freetts.voice.defaultAudioPlayer", "guru.ttslib.StereoAudioPlayer");
        VoiceManager voiceManager = VoiceManager.getInstance();
        voice = voiceManager.getVoice( name );
        voice.setAudioPlayer( audioPlayer );
        voice.allocate();
    }

    public void speak( String in ) {
        voice.setAudioPlayer( audioPlayer );
        ((StereoAudioPlayer)voice.getAudioPlayer()).setLeft(true);
        ((StereoAudioPlayer)voice.getAudioPlayer()).setRight(true);
        voice.speak( in );
    }

    public void speakRight( String in  ) {
        voice.setAudioPlayer( audioPlayer );
        ((StereoAudioPlayer)voice.getAudioPlayer()).setLeft(false);
        ((StereoAudioPlayer)voice.getAudioPlayer()).setRight(true);
        voice.speak( in );
    }

    public void speakLeft( String in  ) {
        voice.setAudioPlayer( audioPlayer );
        ((StereoAudioPlayer)voice.getAudioPlayer()).setLeft(true);
        ((StereoAudioPlayer)voice.getAudioPlayer()).setRight(false);
        voice.speak( in );
    }

    public void setPitch( float p ) {
        voice.setPitch( p );    
    }    

    public void setPitchShift( float p ) {
        voice.setPitchShift( p );    
    }    
    public void setPitchRange( float p ) {
        voice.setPitchRange( p );    
    }    

}

