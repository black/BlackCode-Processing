import seltar.unzipit.*;

UnZipIt z;
import seltar.unzipit.*;
UnZipIt comics;
PImage  page;
void setup() {
  selectInput("Select a file to process:", "fileSelected");
}

void fileSelected(File selection) {
  if (selection == null) {
    println("Window was closed or the user hit cancel.");
  } else {
    String loadComics = selection.getAbsolutePath();
    println("Path:", loadComics);
    comics = new UnZipIt(loadComics, this);
    page = comics.loadImage("png/64x64/add.png");
  }
}

void draw()
{
  background(255);
  // draw the image
  if (page != null) {
    image(page, 0, 0);
  }
}

