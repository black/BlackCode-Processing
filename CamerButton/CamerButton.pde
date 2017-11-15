import processing.video.*; 
Capture video;  

void setup() {
  size(640, 480); 
  video = new Capture(this, 160, 120);  
  video.start();
}

void draw() {
  background(0); 
  image(video, 0, 0);
  video.loadPixels();
  for (int y = 1; y < video.height; y++) { 
    for (int x = 0; x < video.width; x++) {
      int index = x+y*video.width;
      int pixelColor = video.pixels[index]; 
      int r = (pixelColor >> 16) & 0xff;
      int g = (pixelColor >> 8) & 0xff;
      int b = pixelColor & 0xff; 
      int pixelBright = max(r, g, b);
    }
  }
}  

void captureEvent(Capture c) {
  c.read();
}

