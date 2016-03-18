uniform mat4 proscene_transform;

attribute vec4 vertex;
attribute vec4 color;

varying vec4 vertColor;

void main() {
  gl_Position = proscene_transform * vertex;
  vertColor = color;
}
