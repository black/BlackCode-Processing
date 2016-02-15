/**
 * PolarLines demo shows the difference between lines in normal cartesian
 * and polar coordinate systems, where a line is actually a curve/spiral.
 * Both "lines" are shown in parallel and the only difference is the order
 * of the .toCartesian() conversion (either inside or outside the loop).
 *
 * Usage: move mouse to adjust line endpoint
 */

/* 
 * Copyright (c) 2006-2008 Karsten Schmidt
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * http://creativecommons.org/licenses/LGPL/2.1/
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

import toxi.geom.*;

void setup() {
  size(400,400);
  smooth();
}

void draw() {
  background(255);
  translate(width/2,height/2);
  stroke(255,0,0);
  strokeWeight(2);

  // start point at radius 10, 0 degrees
  Vec2D a=new Vec2D(10,0);

  // end point at mouse position
  Vec2D b=new Vec2D(mouseX,mouseY).sub(width/2,height/2).toPolar();
  // force at least 1 full turn to create spiral
  b.y+=4*PI;

  // draw a "line" from a -> b in POLAR space
  // calculate intermediate points
  for(int i=0, num=200; i<num; i++) {
    // interpolate in polar space
    // and only convert result into cartesian
    Vec2D p=a.interpolateTo(b,(float)i/num).toCartesian();
    point(p.x,p.y);
  }

  // draw a line from a -> b in CARTESIAN space
  // transfer points into cartesian space
  a.toCartesian();
  b.toCartesian();
  stroke(0,0,255);
  // calculate intermediate points
  for(int i=0,num=50; i<num; i++) {
    // interpolation already happens in cartesian space
    // no further conversion needed
    Vec2D p=a.interpolateTo(b,(float)i/num);
    point(p.x,p.y);
  }
}

