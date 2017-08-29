PImage images[];
int t = 0;

void setup() {
  size(displayWidth,displayHeight);
  frameRate(40);
    imageBank("a", 8);
}

void draw() {
  if(t<images.length){
  image(images[t],0,0);
  t=t+1;
  delay(1000);
  }
}

void imageBank(String index, int filecount)
{
  images= new PImage[filecount];
  for ( int i = 0; i< images.length; i++ )
  {
    images[i] = loadImage(index + (i+1) + ".jpg" ); 
  }
}

