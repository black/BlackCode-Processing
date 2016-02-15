//
// StickyGhost.java
// Ghost (v.0.1.3) is released under the MIT License.
//
// Copyright (c) 2012, Tim Pulver http://timpulver.de
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
//

package de.timpulver.ghost;

import processing.core.PApplet;
import processing.core.PConstants;

/**
 * This class can be used to create a transparent window which sticks to one side of the screen 
 * and uses the whole width / height. For example <i>StickyGhost(this, "top", 200)</i> creates 
 * a transparent window which sticks to the top of the screen with height 300. 
 * @author Tim Pulver
 *
 */
public class StickyGhost extends Ghost {
	
	/**
	 * Default constructor. Creates a new transparent window, which sticks to a screen side.
	 * @param p pass 'this' from within your Processing sketch
	 * @param side which side of the screen the window should stick to
	 * @param w_h width / height of the window
	 */
	public StickyGhost(PApplet p, String side, int w_h){
		this(p, side, w_h, DEFAULT_RENDERER);
	}

	/**
	 * Default constructor. Creates a new transparent window, which sticks to a screen side.
	 * @param p pass 'this' from within your Processing sketch
	 * @param side which side of the screen the window should stick to
	 * @param w_h width / height of the window
	 * @param renderer the renderer to use (JAVA2D, P2D, P3D, OPENGL), 
	 * currently only JAVA2D supported!
	 * @see PConstants
	 */
	public StickyGhost(PApplet p, String side, int w_h, String renderer){
		String lowerCaseSide = side.toLowerCase();
		if(lowerCaseSide.equals("top")){
			init(p, 0, 0, p.displayWidth, w_h, renderer);
		}
		else if(lowerCaseSide.equals("right")){
			init(p, p.displayWidth-1-w_h, 0, w_h, p.displayHeight, renderer);
		}
		else if(lowerCaseSide.equals("bottom")){
			init(p, 0, p.displayHeight-1-w_h, p.displayWidth, w_h, renderer);
		}
		else if(lowerCaseSide.equals("left")){
			init(p, 0, 0, w_h, p.displayHeight, renderer);
		}
		else{
			System.err.println("Wrong argument! Side should be 'top', 'right', 'bottom' or 'left'. You passed '" + side + "'.");
		}
	}
}
