int[] a = {
  92, 108, 47, 45
};
int i=0;
void draw() {
  background(-1);
  frameRate(10);
  loadOne();
}

void loadOne() {
  if (i<a.length) {  
    // textAlign(CENTER); 
    fill(0);
    textAlign(CENTER);
    text(char(a[i]), width/2, height/2); 
    i++;
  } else i = 0;
}

void loadTwo() {
}

