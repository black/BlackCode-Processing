import picking.*;

Picker picker;
Cube[] cubes;
Cube pickedCube;

void setup() {
  size(300, 300, P3D);
  picker = new Picker(this);

  smooth();

  cubes = new Cube[3];
  cubes[0] = new Cube(0, 50, width*0.50, height*0.50, color(200, 100, 100));
  cubes[1] = new Cube(1, 30, width*0.80, height*0.50, color(100, 200, 100));
  cubes[2] = new Cube(2, 30, width*0.20, height*0.50, color(100, 200, 200));
}

void mousePressed() {
  int id = picker.get(mouseX, mouseY);
  if (id >= 0) {
    pickedCube = cubes[id];
  }
}

void mouseReleased() {
  pickedCube = null;
}

void draw() {
  if (mousePressed && pickedCube != null) {
    pickedCube.rotate(
      radians((pmouseX - mouseX)%360),
      radians((pmouseY - mouseY)%360)
    );
  }

  background(255);
  fill(200, 100, 100);

  for (Cube cube : cubes) {
    picker.start(cube.id);
    cube.draw();
  }

  picker.stop();
}