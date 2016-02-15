/**
 * Picking
 * Pick an object in a 3D scene easily.
 * http://n.clavaud.free.fr/processing/library/picking/
 *
 * Copyright (c) 2013 Nicolas Clavaud http://n.clavaud.free.fr/
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author      Nicolas Clavaud http://n.clavaud.free.fr/
 * @modified    08/27/2013
 * @version     0.2.1 (6)
 */

package picking;

import processing.core.*;

public class Buffer extends processing.opengl.PGraphics3D {
  protected int current_color = 0;

  public Buffer() {}

  public boolean displayable() { return true; }

  public void callCheckSettings() { super.checkSettings(); }

  public void background(int arg) { super.background(0); }
  public void background(float arg) { super.background(0); }
  public void background(float arg, float arg_1) { super.background(0); }
  public void background(int arg, float arg_1) { super.background(0); }
  public void background(float arg, float arg_1, float arg_2) { super.background(0); }
  public void background(float arg, float arg_1, float arg_2, float arg_3) { super.background(0); }
  public void background(PImage arg) { super.background(0); }

  public void lights() {}
  public void smooth() {}
  public void fill(int arg) {}
  public void fill(float arg) {}
  public void fill(float arg, float arg_1) {}
  public void fill(int arg, float arg_1) {}
  public void fill(float arg, float arg_1, float arg_2) {}
  public void fill(float arg, float arg_1, float arg_2, float arg_3) {}

  public void stroke(int arg) {}
  public void stroke(float arg) {}
  public void stroke(float arg, float arg_1) {}
  public void stroke(int arg, float arg_1) {}
  public void stroke(float arg, float arg_1, float arg_2) {}
  public void stroke(float arg, float arg_1, float arg_2, float arg_3) {}

  public void textureMode(int arg) {}
  public void texture(PImage arg) {}
  public void vertex(float x, float y, float z, float u, float v) { super.vertex(x, y, z); }

  public void image(PImage arg, float arg_1, float arg_2) {}
  public void image(PImage arg, float arg_1, float arg_2, float arg_3, float arg_4) {}
  public void image(PImage arg, float arg_1, float arg_2, float arg_3, float arg_4, int arg_5, int arg_6, int arg_7, int arg_8) {}

  protected void imageImpl(PImage image, float x1, float y1, float x2, float y2, int u1, int v1, int u2, int v2) {}

  public void setCurrentId(int i) {
    // ID 0 to 16777214  => COLOR -16777215 to -1 (white)
    // -16777216 is black
    current_color = i - 16777215;
    super.fill(current_color);
  }

  public int getId(int x, int y) {
    loadPixels();
    // COLOR -16777216 (black) to -1 => ID -1 (no object) to 16777214 
    int c = pixels[y*width+x];

    return (c == -1) ? c : c + 16777215;
  }
}
