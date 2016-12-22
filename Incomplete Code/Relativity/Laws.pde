class Steller {
  float x, y, v, t, r, m, dm, dt, dr;
  int c;
  Steller(float x, float y, float r, float m) {
    this.x = x;
    this.y = y;
    this.r = r;
    this.m = m;
    c = 5;
  }
  void display() {
    noStroke();
    fill(0);
    ellipse(x, y, 2*r, 2*r);
  }

  void move(float k) {
    v = k;
  }

  void update() {
    float[] data =  getData();
    m = data[0];
    t = data[1];
    r = data[2];
    x = x + v;
  }
  float[] getData() {
    dm = getMass(); 
    dt = getTime();
    dr = getLen();  
    float[] val = {
      dm, dt, dr
    };
    return val;
  }
  float getMass() {
    float m_initial = m;
    float m_final = m_initial/(sqrt(1-sq(v/c)));
    return m_final;
  } 
  float getTime() {
    float t_initial = t;
    float t_final = t_initial/(sqrt(1-sq(v/c)));
    return t_final;
  }
  float getLen() {
    float r_initial = r;
    float r_final = r_initial*(sqrt(1-sq(v/c)));
    return r_final;
  }
}

