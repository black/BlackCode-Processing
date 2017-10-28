using UnityEngine;
using System.Collections;
using System.Threading;

/**
 * MusicPlayerEventController is an event handler and dispatcher for media player
 * integration. This specific implementation is for iTunes (via the iTunesConnector
 * custom library).
 *
 * Dispatched events:
 *   OnMediaAppStateChanged - the state of the media player has changed (from
 *                               "playing" to "stopped" for example)
 *   OnSongChanged - a new song has been selected
 */
public class MusicPlayerEventController: MonoBehaviour {

  // how quickly to poll iTunes for information. 1/4Hz seems optimal;
  // any faster, and too many cycles are consumed waiting for the polling calls 
  // to return
  private float pollingInterval = 4.0f;

  private MediaAppState state = MediaAppState.NotRunning;
  private Song song;

  private Thread t;

	// Use this for initialization
	void Start() {
    DispatchStateChange(MediaAppState.NotRunning);
    DispatchSongChange(song);

    InvokeRepeating("UpdatePlayerInfo", 0.0f, pollingInterval);	
	}

  void UpdatePlayerInfo(){
    if(t == null || !t.IsAlive){
      t = new Thread(GetPlayerInfo);
      t.Start();
    }
  }
	
  void GetPlayerInfo(){
    MediaAppState currentState = MediaAppState.NotRunning;

    if(iTunesConnector.isRunning() == 0)
      currentState = iTunesConnector.retrieveState();

    if(currentState != state){
      state = currentState;
      DispatchStateChange(state);
    }
    
    switch(state){
      case MediaAppState.NotRunning:
      case MediaAppState.Stopped:
        break;
      default:
        Song currentSong = new Song();

        currentSong.title = iTunesConnector.retrieveTrackName();
        currentSong.album = iTunesConnector.retrieveAlbumName();
        currentSong.artist = iTunesConnector.retrieveArtistName();

        if(currentSong != song){
          song = currentSong;
          DispatchSongChange(song);
        }

        break;
    }
  }
   
  /**
   * Convenience method to dispatch a State Change event
   */
  private void DispatchStateChange(MediaAppState newState){
    SendMessage("OnMediaAppStateChanged", newState, SendMessageOptions.DontRequireReceiver);
  }

  /**
   * Convenience method to dispatch a Song Change event
   */
  private void DispatchSongChange(Song newSong){
    SendMessage("OnSongChanged", newSong, SendMessageOptions.DontRequireReceiver);
  }

  void OnApplicationQuit(){
    Cleanup();
  }

  void OnDisable(){
    Cleanup();  
  }

  private void Cleanup(){
    CancelInvoke("UpdatePlayerInfo");

    if(t != null && t.IsAlive)
      t.Abort();
  }
}
