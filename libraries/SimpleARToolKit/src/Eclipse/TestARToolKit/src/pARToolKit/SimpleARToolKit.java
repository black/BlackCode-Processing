package pARToolKit;

import processing.core.*;
import processing.opengl.*;
import net.sourceforge.jartoolkit.core.JARToolKit;
import javax.media.opengl.GL;

import java.lang.reflect.*;

public class SimpleARToolKit implements PConstants {

	private PImage img2;
	private PApplet parent;
	private JARToolKit ar;
	private int width;
	private int height;
	private int sum;
	private double[] camMat;
	private double[] objMat;
	private int pattern;
	private int patt_size;
	private float[] patt_ctr;
	private String fName;
	Method displayFunc;
	private boolean found;

	public SimpleARToolKit(PApplet _p, int _w, int _h) {
		parent = _p;
		width = _w;
		height = _h;
		sum = width * height;
		parent.registerDispose(this);
		img2 = parent.createImage(width, height, ARGB);

		try {
			ar = JARToolKit.create();
		} catch (InstantiationException e) {
			System.err.println(e.toString());
			System.exit(-1);
		}
		int result = ar.paramLoad(parent.dataPath("camera_para.dat"));
		if (result < 0)
			System.exit(-1);
		result = ar.paramChangeSize(width, height);
		if (result < 0)
			System.exit(-1);
		result = ar.initCparam();
		if (result < 0)
			System.exit(-1);
		camMat = ar.getCamTransMatrix();
		fName = "displayFunc";
		found = false;
	}

	public void loadPattern(String _p, int _s, float _x, float _y) {
		pattern = ar.loadPattern(parent.dataPath(_p));
		patt_size = _s;
		patt_ctr = new float[2];
		patt_ctr[0] = _x;
		patt_ctr[1] = _y;
	}

	public void dispose() {
	}

	private void flipV(PImage _i) {
		int h = height - 1;
		for (int i = 0; i < sum; i++) {
			int x = i % width;
			int y = i / width;
			img2.pixels[i] = _i.pixels[(h - y) * width + x];
		}
		img2.updatePixels();
	}

	public void register(String _s) {
		fName = _s;
		try {
			displayFunc = parent.getClass().getMethod(fName,
					new Class[] { SimpleARToolKit.class });
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	public void showObject() {
		PGraphicsOpenGL pgl = (PGraphicsOpenGL) parent.g;
		GL gl = pgl.beginGL();
		gl.glMatrixMode(GL.GL_PROJECTION);
		parent.pushMatrix();
		parent.resetMatrix();
		gl.glLoadMatrixd(camMat, 0);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		parent.pushMatrix();
		parent.resetMatrix();
		gl.glLoadMatrixd(objMat, 0);
		parent.translate(0, 0, 25);
		if (displayFunc != null) {
			try {
				displayFunc.invoke(parent, new Object[] { this });
			} catch (Exception e) {
				System.err.println(e.toString());
				e.printStackTrace();
				displayFunc = null;
			}
		}
		parent.popMatrix();
		gl.glMatrixMode(GL.GL_PROJECTION);
		parent.popMatrix();
		gl.glMatrixMode(GL.GL_MODELVIEW);
		pgl.endGL();
	}

	public boolean findMatch(PImage _i, int _t) {
		flipV(_i);
		boolean match = false;
		int[] id = ar.detectMarker(img2.pixels, _t);
		if (id.length > 0) {
			for (int i = 0; i < id.length; i++) {
				if (id[i] == pattern) {
					match = true;
					if (!found) {
						objMat = ar.getTransMatrix(pattern, patt_size,
								patt_ctr[0], patt_ctr[1]);
						found = true;
					} else {
						objMat = ar.getTransMatrixCont(pattern, patt_size,
								patt_ctr[0], patt_ctr[1], objMat);
					}
				}
			}
			if (!match)
				found = false;
		} else {
			found = false;
		}
		return match;
	}
}
