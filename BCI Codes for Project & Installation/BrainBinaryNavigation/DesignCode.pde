void resetCounter() {
  for (int i=0; i<set.length; i++) {
    set[i]=0;
  }
}

void scanner(int m, int[] set) {
  noStroke();
  for (int i=0; i<4; i++) {
    if (m==i)fill(#FFD608);
    else fill(200);
    if (set[i]==1)fill(#0870FF);
    ellipse(i*20+width/2-30, 20, 15, 15);
  }
}

int w=150, h=90;
void menuCall(int on) {
  for (int i=0; i<4; i++) {
    for (int j=0; j<4; j++) {
      int index = i+j*4;
      if (on==index)fill(#749AFF);
      else fill(#F5E100);
      stroke(-1188644);
      rect(i*w, j*h+40, w, h); 
      menuAddress(index, i*w+50, j*h+120);
    }
  }
}

void menuAddress(int n, int x, int y) {
  String blockAddress = binary(n, 4);
  noStroke();
  for (int i=0; i<4; i++) {
    char  c = blockAddress.charAt(i); 
    if (c=='1')fill(#0870FF);
    else fill(-1); 
    ellipse(x+i*15, y, 10, 10);
  }
}

