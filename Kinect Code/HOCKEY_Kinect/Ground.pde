void Hockey_Ground()
{
  noFill();   
  strokeWeight(2);
  stroke(255, 0, 0);
  pushStyle();
  rectMode(CENTER);
  rect(width/2, height/2-10, width-10, height-10, 0, 0, 20, 20);
  fill(#62CEFF);
  rect(width/2, height-95, 150, 50, 150, 150, 0, 0);
  noFill();
  rect(width/2, height-52, 100, 35);
  popStyle();
  ellipse(width/2, 0, 150, 150);
  stroke(0, 0, 255);
  line(10, 100, width-10, 100);
  for (int i=0;i<2;i++) {
    stroke(255, 0, 0);
    ellipse((i==0)?150:width-150, height-150, 100, 100);
    pushStyle();
    fill(255, 0, 0);
    noStroke();
    ellipse((i==0)?150:width-150, height-150, 20, 20);
    ellipse((i==0)?150:width-150, 50, 10, 10);
    popStyle();
  }
  line(10, height-70, width-10, height-70);
}

