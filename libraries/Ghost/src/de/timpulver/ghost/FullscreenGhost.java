//
// FullscreenGhost.java
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
 * This class can be used to create a transparent fullscreen window.
 * @author Tim Pulver
 *
 */
public class FullscreenGhost extends Ghost{
	
	/**
	 * Creates a transparent fullscreen window.
	 * @param p The Processing sketch (pass "this" from withing Processing)
	 */
	public FullscreenGhost(PApplet p){
		init(p, 0, 0, p.displayWidth, p.displayHeight, DEFAULT_RENDERER);
	}
	
	/**
	 * Creates a transparent fullscreen window.
	 * @param p The Processing sketch (pass "this" from withing Processing)
	 * @param renderer the renderer to use (JAVA2D, P2D, P3D, OPENGL), 
	 * currently only JAVA2D supported!
	 * @see PConstants
	 */
	public FullscreenGhost(PApplet p, String renderer){
		init(p, 0, 0, p.displayWidth, p.displayHeight, renderer);
	}
}
