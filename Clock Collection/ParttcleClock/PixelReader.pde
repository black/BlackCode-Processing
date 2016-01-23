void getPixlesHour() {
  pgH.loadPixels();
  for (int x = 0; x < pgH.width; x++) {
    for (int y = 0; y < pgH.height; y++) {
      if (pgH.get(x, y) != color(0)) {
        particleH.add(new Particle(x, y));
      }
    }
  }
}

void getPixlesMinute() {
  pgM.loadPixels();
  for (int x = 0; x < pgM.width; x++) {
    for (int y = 0; y < pgM.height; y++) {
      if (pgM.get(x, y) != color(0)) {
        particleM.add(new Particle(2*width/6+x, y));
      }
    }
  }
}

void getPixlesSecond() {
  pgS.loadPixels();
  for (int x = 0; x < pgS.width; x++) {
    for (int y = 0; y < pgS.height; y++) {
      if (pgS.get(x, y) != color(0)) {
        particleS.add(new Particle(4*width/6+x, y));
      }
    }
  }
}

