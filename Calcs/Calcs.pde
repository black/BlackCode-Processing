String[] val = { 
  "1", "2", "3", "+", 
  "4", "5", "6", "-", 
  "7", "8", "9", "x", 
  "del", "0", "clr", "="
};

String screenumber="";
String[] number = {
  "", "", ""
}; 
int[] var = new int[3];
String operator ="";
boolean plus, minus, multi;
void setup() {
  size(300, 400);
}

void draw() {
  background(-1);
  for (int i=0; i<4; i++) {
    for (int j=0; j<4; j++) {
      int  num = i+j*4;
      if (i*75<mouseX && mouseX<i*75+75 && j*75+100<mouseY && mouseY<j*75+75+100) {
        fill(100, 203, 100);
      } else fill(200, 203, 100);
      rect(i*75, j*75+100, 75, 75);
      fill(0);
      textSize(20);
      text(val[num], i*75+35, j*75+100+50);
    }
  }
  fill(0);
  text(screenumber, 5, 40);
  if (k==2) {
    screenumber="";
  }
}

int k=0;
void mousePressed() {
  for (int i=0; i<4; i++) {
    for (int j=0; j<4; j++) {
      int  num = i+j*4;   
      if (i*75<mouseX && mouseX<i*75+75 && j*75+100<mouseY && mouseY<j*75+75+100) {
        screenumber = screenumber + val[num];
        if (val[num].equals("+") ||  val[num].equals("-")||  val[num].equals("x")) {
          var[k]=Integer.parseInt(number[k]);
          operator = val[num]; 
          k++; 
          if (operator.equals("+")) {  
            plus =true; 
            operator ="";
          }
          if (operator.equals("-")) {
            minus = true; 
            operator ="";
          }
          if (operator.equals("x")) {
            multi = true; 
            operator ="";
          }
        } else if (val[num].equals("=")) { 
          var[k]=Integer.parseInt(number[k]); 
          if (plus) var[2]=var[0]+var[1];
          if (minus) var[2]=var[0]-var[1];
          if (multi) var[2]=var[0]*var[1]; 
          screenumber = var[2]+"";
          k=0;
          plus=minus=multi=false;
          var[0]=var[1]=var[2]=0;
        } else if ( val[num].equals("del") ) {
          number[k] = number[k].substring( 0, number[k].length()-1 );
        } else if (val[num].equals("clr") ) {
          number[0]=number[1]=number[2]="";
          k=0;
          screenumber ="";
          var[0]=var[1]=var[2]=0;
        } else {
          number[k] = number[k]+val[num];
        }
      }
    }
  }
} 

