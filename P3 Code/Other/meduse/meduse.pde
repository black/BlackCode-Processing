/* OpenProcessing Tweak of *@*http://www.openprocessing.org/sketch/25908*@* */
/* !do not delete the line above, required for linking your tweak if you re-upload */
/*
    Medusa, Denis Hovart, 2011
 License CC BY 2.0
 http://creativecommons.org/licenses/by/2.0/
 */

Medusa m;
void setup() {
  rectMode(CENTER);
  smooth();
  size(800, 600);
  m = new Medusa( new PVector(width / 2, height / 2), 12, 12, 10, 45, 10, 5 );
}

void draw() {
  background(28);
  m.update();
  m.draw();

}

