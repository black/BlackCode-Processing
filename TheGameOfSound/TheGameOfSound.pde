import ddf.minim.*;
import ddf.minim.ugens.*;

//Minim[][] minim;
//AudioOutput[][] out;
SineInstrument[][] sinIn;

Minim minim;
AudioOutput out;

int rows, cols;
int w=8, h=8;

boolean[][] cell;
boolean[][] futureCell;
boolean[][] soundActivate;

int SAMPLERATE = 44100;
int BUFFERSIZE = 512;

void setup() {
  size(600, 400);


  rows = width/h;
  cols = height/w;

  minim = new Minim(this);
  out = minim.getLineOut(Minim.STEREO, BUFFERSIZE, SAMPLERATE);

  //  minim = new Minim[rows][cols];
  //  out = new AudioOutput[rows][cols];

  sinIn = new SineInstrument[rows][cols];
  soundActivate = new boolean[rows][cols];

  cell = new boolean[rows][cols];
  futureCell = new boolean[rows][cols]; 

  for (int i=0; i<rows; i++) { 
    for (int j=0; j<cols; j++) { 
      //      minim[i][j]  = new Minim(this);
      cell[i][j] = (random(100)<25?true:false);
      futureCell[i][j] = false;
      //      soundActivate[i][j] = false;
      //      out[i][j] = minim.getLineOut(Minim.STEREO, BUFFERSIZE, SAMPLERATE);
      sinIn[i][j] = new SineInstrument(random(20, 200));
    }
  }
}

void draw() {
  background(#FF121A); 
  for (int i=1; i<rows-1; i++) {
    for (int j=1; j<cols-1; j++) { 
      int num = checkNeighbours(i, j);
      futureCell[i][j] = checkRules(i, j, num);
    }
  }

  stroke(-1, 10);
  for (int i=0; i<rows; i++) {
    for (int j=0; j<cols; j++) {
      cell[i][j] = futureCell[i][j];
      if (cell[i][j]) { 
        fill(-1);
        soundActivate[i][j] = true;
      } else {
        fill(#FF121A);
        soundActivate[i][j] = false;
      }
      ellipse(i*w, j*h, w, h);
      futureCell[i][j] = cell[i][j] ;
    }
  }


  for (int i=0; i<rows; i++) {
    for (int j=0; j<cols; j++) {
      if (soundActivate[i][j]) {
        out.pauseNotes();
        out.resumeNotes();
        out.playNote( 0.0, 0.9, sinIn[i][j]);
      }
    }
  }
}

int checkNeighbours(int x, int y) {
  int num =0;
  for (int m=-1; m<=1; m++) {
    for (int n=-1; n<=1; n++) {
      if (n==0 && m==0 ) continue;
      else if (cell[x+m][y+n]) num++;
    }
  }
  return num;
}

boolean checkRules(int i, int j, int num) {
  boolean futurelife = false;
  if (!cell[i][j] && num == 3) futurelife = true;
  else if ( cell[i][j] && (num < 2 || num > 3) ) futurelife = false;
  else futurelife = cell[i][j];
  return futurelife;
}

void mousePressed() {
  setup();
  draw();
}


void stop() { 
  out.close(); 
  minim.stop(); 
  super.stop();
}

