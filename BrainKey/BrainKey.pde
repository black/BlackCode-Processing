int cw = 400, ch = 201; 
int rectW, rectH;

//----------------------// 

String[] lable = {
  "A", "B", "C", "D", "E", "F", "G", "H", "I", 
  "J", "K", "L", "M", "N", "O", "P", "Q", "R", 
  "S", "T", "U", "V", "W", "X", "Y", "Z", "SPACE", 
  "ENTER", "DEL", "RAD"
}; 
int k = 0;
int row=0, col=0, position;

//----------------------// 

void setup() {
  size(cw + 1, ch);
  rectH = ch / 4; 
  rectW = cw / 8;
}

//----------------------// 

void draw() {
  background(0);
  drawTiles();
  scanTiles();
}

//----------------------// 

void drawTiles() { 
  noFill();
  stroke(255, 100);
  for (int j = 0; j < 4; j++) {
    if (j < 3) {
      for (int i = 0; i < 8; i++) {
        int index = i + j * 8; 
        setTheContent(index, i * rectW, j * rectH, rectW, rectH);
      }
    } else {
      for (int i = 0; i <6; i++) {
        int index = i + j * 8;
        if (i<2)setTheContent(index, i * rectW, j * rectH, rectW, rectH);
        if (i==2)setTheContent(index, i * rectW, j * rectH, 2*rectW, rectH);
        if (i==3)setTheContent(index, i * rectW+rectW, j * rectH, 2*rectW, rectH);
        if (i>3)setTheContent(index, i * rectW+2*rectW, j * rectH, rectW, rectH);
      }
    }
  }
}

//----------------------// 

void mousePressed() {
  if (k<2)k++;
  else k = 0;
}

//----------------------// 

int timer = 0;
boolean timerFun(int duration) {
  boolean state = false;
  if (millis()-timer >=duration ) {
    state = true;
    timer = millis();
  }
  return state;
}

//----------------------// 

void scanTiles() {
  boolean currState = timerFun(500);
  // println("current state " + row + " " + col);
  if (currState) {
    if (k == 0) { // row scanning
      if (row < 3) row++;
      else row = 0;
    } else if (k == 1) { // col scanning 
      if (row<3) {
        if (col < 7) {
          col++;
        } else col = 0;
      } else {
        if (col < 6) {
          col++;
        } else col = 0;
      }
    } else { // reset scanning
      position = col + row * 8; 
      println(lable[position]);
      k = 0;
      row = 0;
      col = 0;
      position = 0;
    }
  }

  for (int j = 0; j < 4; j++) {
    if (row==j) {
      fill(0, 80);
      stroke(255);
      rect(0, row * rectH, cw, ch / 4);
      if (row<3) {
        for (int i = 0; i < 8; i++) {
          if (col==i) {
            rowHighlighter(col*rectW, row*rectH, rectW, rectH);
          }
        }
      } else {
        for (int i = 0; i <6; i++) {
          if (col == i) { 
            //   else rowHighlighter(col*rectW+rectW, row*rectH, rectW, rectH);
            if (col<2)rowHighlighter(col*rectW, row*rectH, rectW, rectH);
            if (col==2)rowHighlighter(col*rectW, row*rectH, 2*rectW, rectH);
            if (col==3)rowHighlighter(col*rectW+rectW, row*rectH, 2*rectW, rectH);
            if (col>3)rowHighlighter(col*rectW+2*rectW, row*rectH, rectW, rectH);
          }
        }
      }
    }
  }
} 

//----------------------// 

void rowHighlighter(int x, int y, int w, int h) {
  fill(255, 0, 0, 150);
  stroke(255);
  rect(x, y, w, h);
} 

//-----------------------// 

void setTheContent(int index, int x, int y, int w, int h) {
  if (lable[index].length() > 2) {
    fill(0, 200);
    stroke(255, 100);
    rect(x, y, w, h);
    setMenuIcon(index, x, y, w, h);
  } else {
    noFill();
    stroke(255, 40);
    rect(x, y, w, h);
    setletter(index, x, y, w, h);
  }
}

//----------------------// 

void setletter(int index, int x, int y, int w, int h) {
  fill(255);
  textSize(18);
  textAlign(CENTER);
  text(lable[index], x + w / 2, y + 2 * h / 3.5);
}

//----------------------// 

void setMenuIcon(int index, int x, int y, int w, int h) {
  fill(255);
  setletter(index, x, y, w, h);
  imageMode(CENTER);
  //  image(menuIcon[index], x + w / 2, y + 2 * h / 3.5, menuIcon[index].width, menuIcon[index].height);
}

//----------------------// 

