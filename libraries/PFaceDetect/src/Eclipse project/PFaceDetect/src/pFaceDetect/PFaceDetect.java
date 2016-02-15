package pFaceDetect;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

public class PFaceDetect implements PConstants {

	static {
		System.loadLibrary("PFaceDetect");
	}

	private int w;

	private int h;

	private PImage img;

	private PApplet parent;

	public PFaceDetect(PApplet _p, int _w, int _h, String _s) {
		parent = _p;
		w = _w;
		h = _h;
		img = parent.createImage(w, h, ARGB);
		String path = parent.dataPath(_s);
		init(w, h, path);
	}

	private native void init(int _w, int _h, String _s);

	private native void check(int[] _i);

	public native int[][] getFaces();

	public long ptr;

	public void findFaces(PImage _i) {
		img.copy(_i, 0, 0, _i.width, _i.height, 0, 0, img.width, img.height);
		img.updatePixels();
		check(img.pixels);
	}
}
