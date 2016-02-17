import java.io.*;
import java.io.File;
import sojamo.drop.*;

SDrop drop;

void setup() {
  size(300, 300);
  drop = new SDrop(this);
}

void draw() {
  background(-1);
}

String file ="";
void dropEvent(DropEvent theDropEvent) { 
  if (theDropEvent.file()!=null ) {   
    file = theDropEvent.file().toString();
    println(file);
    File ff=new File(file);
    ff.renameTo(new File(file +".jpg"));
  }
}
