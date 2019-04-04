/*
 * @name Linear Interpolation
 * @frame 720, 400
 * @description Move the mouse across the screen and the symbol will follow.
 * Between drawing each frame of the animation, the ellipse moves part
 * of the distance (0.05) from its current position toward the cursor using
 * the lerp() function.
 * This is the same as the Easing under input only with lerp() instead..
 */

let x = 0;
let y = 0;

function setup() {
  createCanvas(720, 400);
  noStroke();
}

function draw() {
  background(51);

  // lerp() calculates a number between two numbers at a specific increment.
  // The amt parameter is the amount to interpolate between the two values
  // where 0.0 equal to the first point, 0.1 is very near the first point, 0.5
  // is half-way in between, etc.

  // Here we are moving 5% of the way to the mouse location each frame
  x = lerp(x, mouseX, 0.05);
  y = lerp(y, mouseY, 0.05);

  fill(255);
  stroke(255);
  ellipse(x, y, 66, 66);
}
