//import class to set up serial connection with wiring board
import processing.serial.*;
Serial port;
//button setup
color currentcolor;
RectButton rect1, rect2;
boolean locked = false;

void setup() {
  //set up window
  size(200, 200);
  color baseColor = color(102, 102, 102);
  currentcolor = baseColor;
  // List all the available serial ports in the output pane.
  // You will need to choose the port that the Wiring board is
  // connected to from this list. The first port in the list is
  // port #0 and the third port in the list is port #2.
  println(Serial.list());
  // Open the port that the Wiring board is connected to (in this case 1
  // which is the second open port in the array)
  // Make sure to open the port at the same speed Wiring is using (9600bps)
  for (int i=0; i<Serial.list ().length; i++) {
    if (Serial.list()[i]!= null) {
      port = new Serial(this, Serial.list()[i], 9600);
    }
  }
  // Define and create rectangle button #1
  int x = 30;
  int y = 100;
  int size = 50;
  color buttoncolor = color(153, 102, 102);
  color highlight = color(102, 51, 51);
  rect1 = new RectButton(x, y, size, buttoncolor, highlight);
  // Define and create rectangle button #2
  x = 90;
  y = 100;
  size = 50;
  buttoncolor = color(153, 153, 153);
  highlight = color(102, 102, 102);
  rect2 = new RectButton(x, y, size, buttoncolor, highlight);
}

void draw() {
  background(currentcolor);
  println(Serial.list());
  fill(0);
  for (int i=0; i<Serial.list ().length; i++) {
    if (Serial.list()[i]!= null) {
      port = new Serial(this, Serial.list()[i], 9600);
      text(Serial.list()[i], width>>1, i*10);
    }
  }
  stroke(255);
  update(mouseX, mouseY);
  rect1.display();
  rect2.display();
}

void update(int x, int y) {
  if (locked == false) {
    rect1.update();
    rect2.update();
  } else {
    locked = false;
  }
  //Turn LED on and off if buttons pressed where
  //H = on (high) and L = off (low)
  if (mousePressed) {
    if (rect1.pressed()) { //ON button
      currentcolor = rect1.basecolor;
      port.write('H');
    } else if (rect2.pressed()) { //OFF button
      currentcolor = rect2.basecolor;
      port.write('L');
    }
  }
}

class Button {
  int x, y;
  int size;
  color basecolor, highlightcolor;
  color currentcolor;
  boolean over = false;
  boolean pressed = false;
  void update() {
    if (over()) {
      currentcolor = highlightcolor;
    } else {
      currentcolor = basecolor;
    }
  }
  boolean pressed() {
    if (over) {
      locked = true;
      return true;
    } else {
      locked = false;
      return false;
    }
  }
  boolean over() {
    return true;
  }
  void display() {
  }
}

class RectButton extends Button {
  RectButton(int ix, int iy, int isize, color icolor, color ihighlight) {
    x = ix;
    y = iy;
    size = isize;
    basecolor = icolor;
    highlightcolor = ihighlight;
    currentcolor = basecolor;
  }
  boolean over() {
    if ( overRect(x, y, size, size) ) {
      over = true;
      return true;
    } else {
      over = false;
      return false;
    }
  }
  void display() {
    stroke(255);
    fill(currentcolor);
    rect(x, y, size, size);
  }
}

boolean overRect(int x, int y, int width, int height) {
  if (mouseX >= x && mouseX <= x+width && mouseY >= y && mouseY <= y+height) {
    return true;
  } else {
    return false;
  }
}

