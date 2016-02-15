//
// WindowedGhost.java
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
 * This class can be used to create a transparent windows with a certain width, height and position.
 * @author Tim Pulver
 *
 */
public class WindowedGhost extends Ghost {
	
	/**
	 * Creates a transparent window using a screenshot of the underlying desktop / windows. 
	 * Will use the default renderer.
	 * @param p Processing Sketch (pass "this" from within Processing)
	 * @param x x-position of the window
	 * @param y y-position of the window
	 * @param w width of the window
	 * @param h height of the window
	 */
	public WindowedGhost(PApplet p, int x, int y, int w, int h){
		init(p, x, y, w, h, DEFAULT_RENDERER);
	}

	/**
	 * Creates a transparent window using a screenshot of the underlying desktop / windows.
	 * @param p Processing Sketch (pass "this" from within Processing)
	 * @param x x-position of the window
	 * @param y y-position of the window
	 * @param w width of the window
	 * @param h height of the window
	 * @param renderer the renderer to use (JAVA2D, P2D, P3D, OPENGL), 
	 * currently only JAVA2D supported!
	 * @see PConstants
	 */
	public WindowedGhost(PApplet p, int x, int y, int w, int h, String renderer){
		init(p, x, y, w, h, renderer);
	}
}
