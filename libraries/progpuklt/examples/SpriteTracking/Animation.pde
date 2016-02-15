// Class for animating a sequence of GIFs

class Animation {
  PImage[] images;
  int imageCount;
  int frame;
  
  Animation(String imagePrefix, int count) {
    imageCount = count;
    images = new PImage[imageCount];

    for (int i = 0; i < imageCount; i++) {
      // Use nf() to number format 'i' into four digits
      String filename = imagePrefix + nf(i, 4) + ".gif";
      images[i] = loadImage(filename);
    }
  }

  void advanceFrame() {
    frame = (frame+1) % imageCount;    
  }

  void display(float xpos, float ypos) {
    image(images[frame], xpos, ypos);
  }
  
  PImage getImage() {
    return images[frame];  
  }
  
  int getWidth() {
    return images[0].width;
  }
}
