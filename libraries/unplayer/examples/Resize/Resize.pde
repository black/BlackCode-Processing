/**
 * Resize. 
 * by Alvaro Lopez and Juan Baquero
 * 
 * You can Resize the Video Player put in the method 
 * video(0,0,x,y); in the method draw and resize to  x * y
 */


import unplayer.videoplayer.VideoPlayer;
import codeanticode.gsvideo.GSMovie;

void setup() {
  size(800, 600, P2D);
  background(255);
  player = new VideoPlayer(this);
  player.loadVideo("video.avi");
  player.showTitle(false);
  
  /*You can show or hide the Title Bar*/
}

void movieEvent(GSMovie myMovie) {
  myMovie.read();
}

void draw() {
  player.video(50,60,450,300);
}

VideoPlayer player;
