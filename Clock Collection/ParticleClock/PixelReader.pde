void getPixlesHour() {
  pgH.loadPixels();
  for (int x = 0; x < pgH.width; x+=4) {
    for (int y = 0; y < pgH.height; y+=4) {
      if (pgH.get(x, y) != color(0)) {
        particleH.add(new Particle(x, y));
      }
    }
  }
}

void getPixlesMinute() {
  pgM.loadPixels();
  for (int x = 0; x < pgM.width; x+=4) {
    for (int y = 0; y < pgM.height; y+=4) {
      if (pgM.get(x, y) != color(0)) {
        particleM.add(new Particle(2*width/6+x, y));
      }
    }
  }
}

void getPixlesSecond() {
  pgS.loadPixels();
  for (int x = 0; x < pgS.width; x+=4) {
    for (int y = 0; y < pgS.height; y+=4) {
      if (pgS.get(x, y) != color(0)) {
        particleS.add(new Particle(4*width/6+x, y));
      }
    }
  }
}

