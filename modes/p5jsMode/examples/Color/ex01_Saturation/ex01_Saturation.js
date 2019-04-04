/*
 * @name Saturation
 * @description Saturation is the strength or purity of the color and
 * represents the amount of gray in proportion to the hue. A "saturated"
 * color is pure and an "unsaturated" color has a large percentage of gray.
 * Move the cursor vertically over each bar to alter its saturation.
 */
const barWidth = 20;
const lastBar = -1;

function setup() {
  createCanvas(720, 400);
  colorMode(HSB, width, height, 100);
  noStroke();
}

function draw() {
  let whichBar = mouseX / barWidth;
  if (whichBar !== lastBar) {
    let barX = whichBar * barWidth;
    fill(barX, mouseY, 66);
    rect(barX, 0, barWidth, height);
    lastBar = whichBar;
  }
}
