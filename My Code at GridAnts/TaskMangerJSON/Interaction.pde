void keyPressed() {
  if (key == BACKSPACE) {
    if (saveText.length()>0) {
      saveText = saveText.substring(0, saveText.length()-1);
    }
  } else if (key == CODED) {
    if (keyCode == SHIFT) {
    }
  } else {
    saveText+= key;
  }
}

boolean save=false;
void mousePressed() {
  if (dist(mouseX, mouseY, 0, height)<50) {
    save=true;
    println("SAVED");
  }
}
void saveButton() {
  fill(#DE1036);
  noStroke();
  rect(0, height-30, 50, 30);
  fill(-1);
  text("SAVE", 10, height-10);
}

