/*
 * @name Mouse Functions
 * @description Click on the box and drag it across the screen.
 */
var bx;
var by;
var boxSize = 75;
var overBox = false;
var locked = false;
var xOffset = 0.0; 
var yOffset = 0.0; 

function setup() {
  createCanvas(720, 400);
  bx = width/2.0;
  by = height/2.0;
  rectMode(RADIUS);
  strokeWeight(2);
}

function draw() { 
  background(237,34,93);
  
  // Test if the cursor is over the box 
  if (mouseX > bx-boxSize && mouseX < bx+boxSize && 
      mouseY > by-boxSize && mouseY < by+boxSize) {
    overBox = true;  
    if(!locked) { 
      stroke(255); 
      fill(244,122,158);
    } 
  } else {
    stroke(156,39,176);
    fill(244,122,158);
    overBox = false;
  }
  
  // Draw the box
  rect(bx, by, boxSize, boxSize);
}

function mousePressed() {
  if(overBox) { 
    locked = true; 
    fill(255, 255, 255);
  } else {
    locked = false;
  }
  xOffset = mouseX-bx; 
  yOffset = mouseY-by; 

}

function mouseDragged() {
  if(locked) {
    bx = mouseX-xOffset; 
    by = mouseY-yOffset; 
  }
}

function mouseReleased() {
  locked = false;
}
