/**
 * @author Nicolas Clavaud <antiplastik@gmail.com>
 */

import picking.*;

Picker picker;
Cube[] cubes;

float rotation = 0;

void setup() {
  size(200, 200, P3D);

  picker = new Picker(this);

  cubes = new Cube[10];
  for (int i = 0; i < cubes.length; i++) {
    cubes[i] = new Cube(
      -15 + (int)random(30),
      -15 + (int)random(30),
      -15 + (int)random(30),
      5 + (int)random(15)
    );
  }
}

void draw() {
  rotation += 0.002;
  if (rotation > TWO_PI) {
    rotation -= TWO_PI;
  }

  camera(-20, -20, 50, 0, 0, 0, 0, 1, 0);
  rotateY(rotation);

  background(255);

  for (int i = 0; i < cubes.length; i++) {
    picker.start(i);
    cubes[i].display();
  }
}

void mouseClicked() {
  int id = picker.get(mouseX, mouseY);
  if (id > -1) {
    cubes[id].changeColor();
  }
}

class Cube {
  int x, y, z, w;
  color c;

  Cube(int x, int y, int z, int w) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
    this.changeColor();
  }

  void changeColor() {
    c = color(
      int(random(0, 255)),
      int(random(0, 255)),
      int(random(0, 255))
    );
  }

  void display() {
    fill(c);
    pushMatrix();
      translate(x, y, z);
      box(w);
    popMatrix();
  }
}
