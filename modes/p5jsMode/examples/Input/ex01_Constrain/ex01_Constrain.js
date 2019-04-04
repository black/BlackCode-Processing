/*
 * @name Constrain
 * @description Move the mouse across the screen to move 
 * the circle. The program constrains the circle to its box.
 */
var mx = 1;
var my = 1;
var easing = 0.05;
var radius = 24;
var edge = 100;
var inner = edge + radius;

function setup() {
  createCanvas(720, 400);
  noStroke(); 
  ellipseMode(RADIUS);
  rectMode(CORNERS);
}

function draw() {
  background(230);
  
  if (abs(mouseX - mx) > 0.1) {
    mx = mx + (mouseX - mx) * easing;
  }
  if (abs(mouseY - my) > 0.1) {
    my = my + (mouseY- my) * easing;
  }
  
  mx = constrain(mx, inner, width - inner);
  my = constrain(my, inner, height - inner);
  fill(237,34,93);
  rect(edge, edge, width-edge, height-edge);
  fill(255);
  ellipse(mx, my, radius, radius);
}
