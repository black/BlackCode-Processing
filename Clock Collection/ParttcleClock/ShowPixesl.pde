void showPixlesHour() {
  for (int i = 0; i < particleH.size (); i+=10) {
    Particle p = particleH.get(i);
    p.display();
    p.update();
  }
}

void showPixlesMinute() {
  for (int i = 0; i < particleM.size (); i+=10) {
    Particle p = particleM.get(i);
    p.display();
    p.update();
  }
}

void showPixlesSecond() {
  for (int i = 0; i < particleS.size (); i+=10) {
    Particle p = particleS.get(i);
    p.display();
    p.update();
  }
}

