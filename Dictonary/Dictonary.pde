String input = "", tempText="", str="";
String lines[], lines2[];
int k=0;
boolean answer = false;

void setup() {
  size(400, 200, P3D);
  lines = loadStrings("positions.txt");
  lines2 = loadStrings("positions2.txt");
  println(lines);
}

void draw() {
  background(0, 20); 
  textSize(16);
  if (answer) {
    for (int i=0; i<lines.length; i++) {
      if (lines[i].equals(tempText) && tempText!="") {
        str = lines2[i];
      } 
    }
    fill(#FFC503);
    text(str, 40, 80);
    fill(#00B0ED);
    text(tempText, 40, 40);
  } else {
    fill(-1);
    text(input, 40, 40);
    fill(255*sin(radians(yy)*12));
    rect(50+input.length()*10, 20, 5, 20);
  }
  yy++;
}
int yy=0;

void keyPressed() {

  if ( ((key>='A')&&(key<='Z')) || ((key>='a')&&(key<='z')) || ((key>='0')&&(key<='9')) ) {
    input+= key+"";
    answer = false;
  } else if (key==BACKSPACE && input.length()>0) {
    input = input.substring( 0, input.length()-1 );
  } else if (key==DELETE) {  
    input = "";
  }

  if ( (key==ENTER) || (key==RETURN) ) {
    answer = true;
    tempText = input;
    input = "";
  }
}

