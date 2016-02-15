/**
 * Player. 
 * by Alvaro Lopez and Juan Baquero
 * 
 * Create a Player of unPlayer, put the URL of any file of video
 * in the constructor of the Class or in the method "loadVideo()",
 * the URL may be absolute or only the name of the video file, 
 * in this case the video must be in the folder "Data" of Sketch
 * 
 * In the method video() put the position of the player "video(x,y)" 
 * in this case the size of the player depends of size of the video
 *
 */

import unplayer.videoplayer.VideoPlayer;
import codeanticode.gsvideo.GSMovie;

void setup() {
  size(800, 600, P2D);
  background(255);
  
  /* The third parameter indicate show/hide the TitleBar, default is true*/
  
  player = new VideoPlayer(this,"video.avi",true);
  
  // boolean charged = player.videoCharged();
  /* returns false if the player is in the Loading Screen */
    
  // myMovie.disableChargeScreen();
  /*Disable the Loading Screen*/

}

void movieEvent(GSMovie myMovie) {
  myMovie.read();
}

void draw() {
  background(255);
  player.video(0,0);
}

VideoPlayer player;
