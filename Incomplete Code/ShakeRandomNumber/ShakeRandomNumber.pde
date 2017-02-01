String[] quote = {
  "Fuck", "Suck", "Luck", "Dick", "Fag", "Vagina"
}; 
int x, y, index;
boolean pressed;
color c;
void setup() {
  size(400, 400);
  c = (color) random(#000000);
  x = width>>1;
  y = height>>1;
}

void draw() {
  background(-1);
  if (mousePressed) {
    pressed = true;
  } else pressed = false;

  if (pressed) {
    x = mouseX;
    y = mouseY;
    index = (int)random(0, 6);
  }
  noStroke();
  fill(c);
  ellipse(x, y, 40, 40);
  text(quote[index], width>>1, height>>1);
}

