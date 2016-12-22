String address=""; 
int k=0, m=0, num;
int[] set = new int[4];
boolean pressed;
void setup() {
  size(600, 400);
  resetCounter();
}

void draw() {
  background(-1);  
  scanner(k, set); 

  if (m>100) {
    if (k<4) {
      if (pressed) {
        address = address+'1';
        pressed = false;
        set[k]=1;
      } else {
        address = address+'0';
        set[k]=0;
      }
      k++;
    } else {
      num = unbinary(address);
      k=0;
      address = "";
      pressed = false;
      resetCounter();
    }
    m=0;
  } else {
    m++;
  }

  menuCall(num);
}

void mousePressed() {
  pressed = true;
}

