// StickyNotes
// Copyright (c) 2009 Jonathan Speicher
// jon.speicher@gmail.com
// Licensed under the MIT license: http://creativecommons.org/licenses/MIT
//
// This class represents a selector.  It's a simple on-screen control that 
// draws itself and knows its own boundaries for hit detection.

class Selector
{
  int centerX, centerY;
  int width, height;
  int minX, maxX, minY, maxY;
  String label;
  PFont font;

  Selector(int x, int y, int w, int h, String l)
  {
    centerX = x;
    centerY = y;
    width = w;
    height = h;
    label = l;
    
    minX = centerX - (width / 2);
    maxX = centerX + (width / 2);
    minY = centerY - (height / 2);
    maxY = centerY + (height / 2);
    
    font = loadFont("Helvetica-12.vlw");
    textFont(font);
    textAlign(CENTER);
  }
  
  boolean hit(int x, int y)
  { 
    if ((x >= minX) && (x <= maxX) && (y >= minY) && (y <= maxY))
    {
      println("Selected " + label);
      return true;
    }
    else
    {
      return false;
    }
  }
  
  void draw()
  {
    noStroke();
    fill(128, 128, 128);
    ellipse(centerX, centerY, width, height);
    fill(255);
    text(label, centerX, centerY + 5);
  }  
}
