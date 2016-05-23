class Puck {

  float r=20;   // radius
  float x, y; // location
  float xspeed, yspeed; // speed
  int sets;
  Puck() {
    x = width/2;
    y = r;
    xspeed = 3;
    yspeed = 3;
  }

  void display() {
    noStroke();
    fill(0);
    ellipse(x, y, r, r);
    fill(#F20267);
    ellipse(x, y, r-10, r-10);
  }

  void update() {
    x += xspeed; 
    y += yspeed; 

    if (x +r/2> width || x-r/2 < 0) {
      xspeed *= - 1;
    }

    if (y+r/2 > height || y-r/2 < 0) {
      yspeed *= - 1;
    }

    if (handx - barwidth / 2 <= x && handx + barwidth / 2 >= x && y+r/2 >= height - 150 && y +r/2 < height - 130 ) {
      yspeed *= - 1;
      hits ++;
    }

    if (width/2 -55 <=x+r/2 &&  width/2+55 >=x-r/2 && y+r/2 > height-60) {
      hits=0;
      reset();
    }

    if ( y+r/2 > height-70  && width/2 -60 < x-r/2 && width/2 + 60 > x+r/2  ) {
      xspeed *= - 1;
    }
  }

  void reset() {
    x = width/2;
    y = r;
    xspeed = random( 3, 5);
    yspeed = random( 3, 5);
  }

  void speedup() {
    yspeed+=1;
    xspeed+=1;
  }
}

