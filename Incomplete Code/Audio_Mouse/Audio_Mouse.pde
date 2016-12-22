import ddf.minim.*;  
Minim minim;  
AudioInput in;

void setup() {   
  size(500, 300);   
  minim = new Minim(this);  
  in = minim.getLineIn(Minim.STEREO, 2048, 192000.0);
}

void draw() { 
  background(-1);
  for (int i = 0; i < in.bufferSize () - 1; i++) {
    line( i, 50 + in.left.get(i)*50, i+1, 50 + in.left.get(i+1)*50 );
    line( i, 150 + in.right.get(i)*50, i+1, 150 + in.right.get(i+1)*50 );
  }
}

