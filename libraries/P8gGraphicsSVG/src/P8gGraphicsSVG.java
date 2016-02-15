/*
P8gGraphicsSVG is to SVG what PGraphicsPDF is to PDF: a vector renderer for sketches,
with outputs to the SVG format, via the Apache Batik library.

Uses Batik 1.8 jars, experimentally defining the minimal subset needed to generate static SVG files.

by Philippe Lhoste <PhiLho(a)GMX.net> http://Phi.Lho.free.fr & http://PhiLho.deviantART.com
*/
/* File/Project history:
 2.00.000 -- 2012/10/14 (PL) -- Changed name, package and some methods. See README...
 1.01.000 -- 2012/08/08 (PL) -- Support of saveFrame and renaming of discard() to clear().
 1.00.000 -- 2012/08/04 (PL) -- Creation.
*/
/* Copyright notice: For details, see the following file:
http://Phi.Lho.free.fr/softwares/PhiLhoSoft/PhiLhoSoftLicense.txt
This program is distributed under the zlib/libpng license.
Copyright (c) 2012 Philippe Lhoste / PhiLhoSoft
*/
package org.philhosoft.p8g.svg;

import java.awt.Graphics2D;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.ImageHandler;
import org.apache.batik.svggen.GenericImageHandler;
import org.apache.batik.svggen.CachedImageHandlerBase64Encoder;
import org.apache.batik.svggen.CachedImageHandlerPNGEncoder;
import org.apache.batik.svggen.CachedImageHandlerJPEGEncoder;
import org.apache.batik.dom.GenericDOMImplementation;

import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PGraphicsJava2D;

/**
 * Similar to PGraphicsPDF, allows to write a SVG file from a Processing sketch.
 */
public class P8gGraphicsSVG extends PGraphicsJava2D
{
	/** Shortcut to the fully qualified class name.	*/
	// The name is a bit rendundant (P8gGraphicsSVG.SVG), I fear, but consistent with the PDF name (I cannot change PConstants! to add this one...)
	public static final String SVG = "org.philhosoft.p8g.svg.P8gGraphicsSVG";

	public enum ImageFileFormat
	{
		INTERNAL,
		EXTERNAL_PNG,
		EXTERNAL_JPEG,
	}

	public int savedFrameCount;

	/** File being written, if it is a file. */
	protected File file;
	/** OutputStream being written to. Created from file,
	 * or with a provided OutputStream, eg. in save to Web. */
	protected OutputStream output;

  private static final String EXTENSION = ".svg";
  private static final String LIB_NAME = "P8gGraphicsSVG";

  /** Alllows to see some details on inner workings. */
	private static boolean bDebug;

	/** Avoid dispose() to write empty files. */
	private boolean bHasDrawn;

  /** The Batik graphics context. */
	private SVGGraphics2D svgG2D;

  /** true (default) if we want to use CSS style attributes. */
	private boolean bUseInlineCSS = true;

  /** The format used to save bitmap images. Default to external PNG. */
	private ImageFileFormat imageFileFormat = ImageFileFormat.EXTERNAL_PNG;

	/**
	 * Constructor, for internal use.
	 * Use createGraphics() instead.
	 */
	public P8gGraphicsSVG()
	{
		debugPrint(LIB_NAME);

		// SVG always likes native fonts. Always.
		hint(ENABLE_NATIVE_FONTS);
	}

	//===== The public API =====

	/**
	 * Sets the way attributes are generated in the SVG file.
	 *
	 * @param b  if true (default), the generated SVG will use CSS to define properties,
	 *        otherwise, the properties will be defined by XML attributes.
	 */
	public void setUseInlineCSS(boolean b)
	{
		if (bUseInlineCSS == b)
			return; // No change, skip

		bUseInlineCSS = b;

		dispose();
		allocate();
	}

	/**
	 * Sets the way bitmap images are managed within the SVG file.
	 * Can be internal in Base64 (can grow a lot the file),
	 * external in PNG format (preserves transparency) or in Jpeg format (smaller).
	 *
	 * @param iff  one of the P8gGraphicsSVG.ImageFileFormat:<ul>
	 *        <li>P8gGraphicsSVG.ImageFileFormat.INTERNAL to embed the image in Base64 format in the SVG file (can make a big file!)
	 *        <li>P8gGraphicsSVG.ImageFileFormat.EXTERNAL_PNG to save them as a PNG external file
	 *        <li>P8gGraphicsSVG.ImageFileFormat.EXTERNAL_JPEG to save them as a Jpeg external file.</ul>
	 */
	public void setImageFileFormat(ImageFileFormat iff)
	{
		if (imageFileFormat == iff)
			return;

		imageFileFormat = iff;

		dispose();
		allocate();
	}

	/**
	 * Changes the textMode() to either SHAPE or MODEL (default).
	 * Since JAVA2D renderer wants only SCREEN or MODEL, use SCREEN as synonym of SHAPE...
	 * <p>
	 * This resets all renderer settings, and therefore must
	 * be called <em>before</em> any other command that sets the fill()
	 * or the textFont() or anything. Unlike other renderers,
	 * use textMode() directly after the size() command.
	 *
	 * @param mode  if SHAPE, the used font glyphs will be saved as SVG shapes in the file;
	 *              if MODEL, the file will refer to the font name and will need the font to be
	 *        installed in the system rendering the SVG for proper look. Smaller file but less portable.
	 */
	@Override // PGraphics
	public void textMode(int mode)
	{
		if (mode == SCREEN)
			mode = SHAPE;
		if (mode != SHAPE && mode != MODEL)
			throw new RuntimeException("textMode(" + mode + ") does not exist");

		if (textMode == mode) // Ignore non changes
			return;

		textMode = mode;

		dispose();
		allocate();
	}

	/** Throws away the current drawing, clear the document, ready for new content. */
	public void clear()
	{
		debugPrint("clear");
		bHasDrawn = false;
		dispose();

		// Erase the current content of the document
		Element root = svgG2D.getRoot();
    NodeList children = root.getChildNodes();
//~ 		debugPrint(children);
		if (children != null)
		{
			// Looks like there is only the comment and the main g element.
			for (int i = 0; i < children.getLength(); i++)
			{
//~ 				debugPrint("> " + children.item(i));
				root.removeChild(children.item(i));
			}
		}
 	}

	/**
	 * Sets the library to write to an output stream instead of a file.
	 */
	public void setOutput(OutputStream os)
	{
		output = os;
	}

	/**
	 * Saves the current SVG image to the existing path (given with the renderer).
	 */
	public void endRecord()
	{
		debugPrint("endRecord");

		endDraw();
		dispose(); // Save if has drawn
	}

	/**
	 * Saves the current SVG image to the given path.
   *
   * @param filePath  the path of the file (absolute, or relative to the sketch path)
	 */
	public void endRecord(String filePath)
	{
		// Mostly there for symmetry with beginRecord()
		debugPrint("endRecord " + filePath);

		setPath(filePath);
		endRecord();
	}

	/**
   * On a P8gGraphicsSVG renderer, saves to a .svg file what havee been drawn so far
   * on the current frame.
	 * <p>
	 * Similar to endRecord(), with a path. Best used just at the end of your draw().
   *
   * @param filePath  the path of the file (absolute, or relative to the sketch path),
	 *        replacing a ### sequence (minimum 2 #) with the number of images saved so far (starting at 1).
	 */
	public void recordFrame(String filePath)
	{
		debugPrint("recordFrame " + filePath);

		endRecord(insertFrameNumber(filePath));
	}

	/**
	 * Sets the save path to the given file path.
	 * The .svg extension is added if not present.
   *
   * @param filePath  the path of the file (absolute, or relative to the sketch path)
	 */
	@Override // PGraphics
	public void setPath(String filePath)
	{
		debugPrint("setPath " + filePath);

		// From PGraphics
		path = filePath;
		if (path != null)
		{
			if (!path.toLowerCase().endsWith(EXTENSION))
			{
				// if no .svg extension, add it..
				path += EXTENSION;
			}
			file = new File(path);
			if (!file.isAbsolute())
			{
				path = parent.sketchPath(path);
				file = new File(path);
			}
		}
	}

	@Override // PGraphics
	public void beginDraw()
	{
//~ 		debugPrint("beginDraw");
		super.beginDraw();

		bHasDrawn = true;
	}

	@Override // PGraphics
	public void endDraw()
	{
//~ 		debugPrint("endDraw");
		// Don't call super.endDraw() (from PGraphicsJava2D) because it calls loadPixels.
		// http://dev.processing.org/bugs/show_bug.cgi?id=1169
	}

	/**
	 * Clean up and save if there is something drawn.
	 */
	@Override // PGraphics
	public void dispose()
	{
		debugPrint("dispose");

		// From PGraphicsJava2D
		g2.dispose();

		if (bHasDrawn)
		{
			saveSVG();
		}
		svgG2D.dispose();
	}

	/**
	 * Don't open a window for this renderer, it won't be used.
	 */
	@Override // PGraphics
	public boolean displayable()
	{
		return false;
	}

	//===== ProtectedAPI (and private methods) =====

	// Called by PGraphics.setSize()
	@Override // PGraphics
	protected void allocate()
	{
		debugPrint("allocate");

		g2 = createGraphics();
	}

	/**
	 * Creates the drawing surface, using the previously set settings.
	 * Creates the Dom document (XML), the SVG context (settings) and the SVG Graphics2D.
	 * Called by allocate() when setSize() is called from PGraphics.
	 */
	protected Graphics2D createGraphics()
	{
		debugPrint("createGraphics");

		savedFrameCount = 0;

		// Get a DOMImplementation.
		DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

		// Create an instance of org.w3c.dom.Document.
		Document document = domImpl.createDocument("http://www.w3.org/2000/svg", "svg", null);

		debugPrint("SVGGeneratorContext.createDefault " + document);
		// Create an instance of the SVG Generator.
		SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(document);

		// General file comment
		ctx.setComment(" Generated by Processing with the " + LIB_NAME + " library ");

		// Text options
		if (textMode == SHAPE)
		{
			// Save the shape of each letter in the SVG file
			ctx.setEmbeddedFontsOn(true);
			debugPrint("textMode SHAPE");
		}
		else if (textMode == MODEL)
		{
			// Just use the system's font, if installed
			ctx.setEmbeddedFontsOn(false);
			debugPrint("textMode MODEL");
		}
		else
		{
			throw new RuntimeException("textMode " + textMode + " does not exist");
		}

        // Image options
		debugPrint("ImageHandler " + imageFileFormat);
		try
		{
			GenericImageHandler ihandler = null;
			switch (imageFileFormat)
			{
				case INTERNAL:
				{
					// Reuse our embedded base64-encoded image data.
					ihandler = new CachedImageHandlerBase64Encoder();
					break;
				}
				case EXTERNAL_PNG:
				{
					// Don't embed images, save to the pointed directory
					ihandler = new CachedImageHandlerPNGEncoder(getPath(), ".");
					break;
				}
				case EXTERNAL_JPEG:
				{
					// Don't embed images, save to the pointed directory
					ihandler = new CachedImageHandlerJPEGEncoder(getPath(), ".");
					break;
				}
				default:
					assert false : "Update this switch with the ImageFileFormat enum!";
			}
			ctx.setGenericImageHandler(ihandler);
		}
		catch (org.apache.batik.svggen.SVGGraphics2DIOException e) // For EXTERNAL_XXX options
		{
			System.err.println(LIB_NAME + " error: Cannot create image handler: " + e.getMessage());
//			e.printStackTrace();
			// Fall back to internal case
			GenericImageHandler ihandler = new CachedImageHandlerBase64Encoder();
			ctx.setGenericImageHandler(ihandler);
		}

		debugPrint("SVGGraphics2D " + ctx);
		// Create an instance of the SVG Generator
		svgG2D = new SVGGraphics2D(ctx, false);
		svgG2D.setSVGCanvasSize(new java.awt.Dimension(width, height));
		return svgG2D;
	}

	/**
	 * Saves the current image to the current path (or output stream).
	 */
	protected void saveSVG()
	{
		debugPrint("save");

		boolean success = false;

		Writer out = null;
		try
		{
			if (file != null)
			{
				output = new BufferedOutputStream(new FileOutputStream(file), 16384);
			}
			else if (output == null)
			{
				throw new RuntimeException(LIB_NAME + " requires a path " +
						"for the location of the output file.");
			}
			out = new OutputStreamWriter(output, "UTF-8");
			svgG2D.stream(out, bUseInlineCSS);
			success = true;
		}
		catch (IOException e)
		{
			showIOException(e);
//			e.printStackTrace();
			success = false;
		}
		catch (SecurityException se)
		{
			System.err.println("Can't save to a file when running in a browser, " +
					"unless using a signed applet.");
		}
		finally
		{
			if (out != null)
			{
				try
				{
					out.close();
				}
				catch (IOException e)
				{
					System.err.println(LIB_NAME + " error: Cannot close output: " + e.getMessage());
//					e.printStackTrace();
					success = false;
				}
			}
			if (!success)
			{
				throw new RuntimeException(LIB_NAME + " error: Cannot save SVG image.");
			}
			bHasDrawn = false;
		}
	}

	/**
	 * Provides a path to save the bitmap images saved as files.
	 * If no path (using output stream), use the sketch path.
	 * Applets should use INLINE images...
	 */
	protected String getPath()
	{
		if (file != null)
			return file.getParent();

		return parent.sketchPath;
	}

	protected void showIOException(IOException e)
	{
		if (file != null)
		{
			System.err.println(LIB_NAME + " error: Cannot write to file '" +
					file.getAbsolutePath() + "': " + e.getMessage());
		}
		else
		{
			System.err.println(LIB_NAME + " error: Cannot write to output: " + e.getMessage());
		}
	}

	protected String insertFrameNumber(String filePath)
	{
		int firstSharp = filePath.indexOf('#');
		if (firstSharp == -1)
			return filePath; // No sharp, nothing to change

		int lastSharp = filePath.lastIndexOf('#');
		if (firstSharp == lastSharp)
			return filePath; // Only one sharp, nothing to change

		int sharpNb = lastSharp - firstSharp + 1;
		String before = filePath.substring(0, firstSharp);
		String after = filePath.substring(lastSharp + 1);
		// Maintain its own number of saved frames
		return before + PApplet.nf(++savedFrameCount, sharpNb) + after;
	}


	//////////////////////////////////////////////////////////////

	@Override public void loadPixels() { nope("loadPixels"); }
	@Override public void updatePixels() { nope("updatePixels"); }
	@Override public void updatePixels(int x, int y, int c, int d) { nope("updatePixels"); }
	//
	@Override public int get(int x, int y) { nope("get"); return 0; }
	@Override public PImage get(int x, int y, int c, int d) { nope("get"); return null; }
	@Override public PImage get() { nope("get"); return null; }
	@Override public void set(int x, int y, int argb) { nope("set"); }
	@Override public void set(int x, int y, PImage image) { nope("set"); }

	// Perhaps make versions specific to SVG
	@Override public void mask(int alpha[]) { nope("mask"); }
	@Override public void mask(PImage alpha) { nope("mask"); }
	//
	@Override public void filter(int kind) { nope("filter"); }
	@Override public void filter(int kind, float param) { nope("filter"); }
	//
	// Defined in PImage
	public void blend(int sx, int sy, int dx, int dy, int mode) { nope("blend"); }
	public void blend(PImage src, int sx, int sy, int dx, int dy, int mode) { nope("blend"); }
	@Override public void blend(int sx1, int sy1, int sx2, int sy2, int dx1, int dy1, int dx2, int dy2, int mode) { nope("blend"); }
	@Override public void blend(PImage src, int sx1, int sy1, int sx2, int sy2, int dx1, int dy1, int dx2, int dy2, int mode) { nope("blend"); }
	//
	@Override public void copy(int sx1, int sy1, int sx2, int sy2, int dx1, int dy1, int dx2, int dy2) { nope("copy"); }
	@Override public void copy(PImage src, int sx1, int sy1, int sx2, int sy2, int dx1, int dy1, int dx2, int dy2) { nope("copy"); }

	//

	protected void nope(String function)
	{
		throw new RuntimeException("No " + function + "() for " + LIB_NAME);
	}

	public static void setDebug(boolean b) { bDebug = b; }
	protected void debugPrint(String msg)
	{
		if (bDebug)
		{
			PApplet.println(LIB_NAME + " - " + msg);
		}
	}
}
