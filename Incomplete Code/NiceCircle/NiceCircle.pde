/**
 * Copyright (C) 2016 Ilyas Shafigin
 */
int mouseArea = 6;
int area = 2;
int count;
float offset;
int w = 5, hw = w / 2;
float lev = -30.0f, k1 = 0.06f, k2 = 0.09f;
float damping = 0.96f;
float mouseAdd = 20.0f, mouseArea1 = mouseArea - 1.0f;
float[] value, speed;
int fps = 60;
 
void setup() {
    size(800, 600);
    smooth(2);
    frameRate(fps);
     
    count = (int) (360 / w);
    offset = height / 2.5;
 
    value = new float[count];
    speed = new float[count];
 
    for(int i = 0; i < count; i++) {
      value[i] = 0.0f;
      speed[i] = 0.0f;
    }
}
 
void update() {
    for(int i = 0; i < count; i++) {
      speed[i] -= k1 * (value[i] - lev);
      for(int j = 1; j <= area; j++) {
        int i2 = i - j;
        int i3 = i + j;
        if (i2 < 0) i2 += count;
        if (i3 >= count) i3 -= count;
        speed[i] += k2 * (value[i2] + value[i3] - 2 * value[i]) / j;
      }
    }
 
    for(int i = 0; i < count; i++) {
      speed[i] *= damping;
      value[i] += speed[i];
    }
}
 
void mousePressed() {
  int x = (int) (atan2(mouseY-height/2,mouseX-width/2)/PI*180);
  int ix = (x - hw) / w;
  if (ix < 0) ix += count;
  float m = mag(mouseX-width/2,mouseY-height/2) > offset ? mouseAdd : -mouseAdd;
  speed[ix] += m;
 
  for(int i = 1; i < mouseArea; i++) {
    int i2 = ix - i;
    int i3 = ix + i;
     
    if (i2 < 0) i2 += count;
    if (i3 >= count) i3 -= count;
     
    speed[i2] += m * herm((mouseArea1 - i) / mouseArea1);
    speed[i3] += m * herm((mouseArea1 - i) / mouseArea1);
  }
}
 
void draw() {
    background(64);
     
    update();
     
    noStroke();
    fill(59, 180, 250);
     
    pushMatrix();
    translate(width/2, height/2);
     
    beginShape(POLYGON);
    for(int i = 0; i < count+1; i++) {
      float a2 = (i % count) * w / 180.0 * PI;
      float r2 = offset - value[i % count];
      vertex(r2*cos(a2), r2*sin(a2));
    }
    endShape(CLOSE);
    popMatrix();
}
 
float herm(float t) {
    return t * t * (3.0f - 2.0f * t);
}

