using System.Runtime.InteropServices;

public enum MediaAppState {
  NotRunning = -1,
  Stopped = 0,
  Paused,
  Playing,
  FastForwarding,
  Rewinding
}

public class iTunesConnector {
  [DllImport("iTunesConnector", EntryPoint = "iTunes_retrieveTrackName")]
  private static extern System.IntPtr _retrieveTrackName();

  public static string retrieveTrackName(){
    System.IntPtr p = _retrieveTrackName();
    string s = Marshal.PtrToStringAuto(p);
    iTunes_free(p);

    return s; 
  }

  [DllImport("iTunesConnector", EntryPoint = "iTunes_retrieveArtistName")]
  private static extern System.IntPtr _retrieveArtistName();

  public static string retrieveArtistName(){
    System.IntPtr p = _retrieveArtistName();
    string s = Marshal.PtrToStringAuto(p);
    iTunes_free(p);

    return s; 
  }

  [DllImport("iTunesConnector", EntryPoint = "iTunes_retrieveAlbumName")]
  private static extern System.IntPtr _retrieveAlbumName();

  public static string retrieveAlbumName(){
    System.IntPtr p = _retrieveAlbumName();
    string s = Marshal.PtrToStringAuto(p);
    iTunes_free(p);

    return s; 
  }

  [DllImport("iTunesConnector", EntryPoint = "iTunes_retrieveTrackPosition")]
  public static extern int retrieveTrackPosition();

  [DllImport("iTunesConnector", EntryPoint = "iTunes_retrieveTrackLength")]
  public static extern int retrieveTrackLength();

  [DllImport("iTunesConnector", EntryPoint = "iTunes_backTrack")]
  public static extern int backTrack();

  [DllImport("iTunesConnector", EntryPoint = "iTunes_play")]
  public static extern int play();

  [DllImport("iTunesConnector", EntryPoint = "iTunes_stop")]
  public static extern int stop();

  [DllImport("iTunesConnector", EntryPoint = "iTunes_pause")]
  public static extern int pause();
  
  [DllImport("iTunesConnector", EntryPoint = "iTunes_playPause")]
  public static extern int playPause();

  [DllImport("iTunesConnector", EntryPoint = "iTunes_prevTrack")]
  public static extern int prevTrack();

  [DllImport("iTunesConnector", EntryPoint = "iTunes_nextTrack")]
  public static extern uint nextTrack();

  [DllImport("iTunesConnector", EntryPoint = "iTunes_isRunning")]
  public static extern int isRunning();

  [DllImport("iTunesConnector", EntryPoint = "iTunes_retrieveState")]
  public static extern MediaAppState retrieveState();

  [DllImport("iTunesConnector", EntryPoint = "iTunes_free")]
  private static extern void iTunes_free(System.IntPtr ptr);
}
