/**
 * In this sketch each window needs its own data to calculate and store 
 * its own Mandelbrot plot. G4P allows the user to create their own data-objects 
 * and associate them with a window. The user must design and create a class 
 * for the data-object and that class must inherit from the class GWinData.
 *
 * This example is fairly sophisticated because it also implements the 
 * Runnable interface enabling the complex calculations to be performed 
 * in a separate thread.
 * 
 * The code for creating instances of this class and adding them to a window 
 * instance can be seen In the method makeNewBrotWindow(…) 
 *
 */
class MandelbrotData extends GWinData  implements Runnable {

  public int msx, msy, mex, mey;
  public final double sx, sy, ex, ey;
  public final int w, h;
  public PGraphics pg;
  public boolean working = true;
  // used to count iteration range used.
  public int minC = 999, maxC = -999;

  /**
   * Create the Mandelbrot plot based on the complex plane
   * coordinates provided
   * @param sx minimum real value
   * @param sy maximum real value
   * @param ex minimum imaginary value
   * @param ey maximum imaginary vale
   * @param w pixel width for the plot
   * @param h pixel height for the plot
   */
  public MandelbrotData(double sx, double sy, double ex, double ey, int w, int h) {
    super();
    this.sx = sx;
    this.sy = sy;
    this.ex = ex;
    this.ey = ey;
    this.w = w;
    this.h = h;
    pg = createGraphics(this.w, this.h, JAVA2D);
    pg.beginDraw();
    pg.background(60);
    pg.textSize(24);
    pg.textAlign(CENTER, CENTER);
    pg.fill(0, 255, 0);
    pg.text("Working ...", 0, 0, this.w, this.h);
    pg.endDraw();
  }

  // This is a separate thread to calculate the Mandelbrot plot
  public void run() {
    calcMandlebrot();
    working = false;
  }

  private void calcMandlebrot() {
    double x0, x1, y0, y1, deltaX, deltaY;
    double colX, rowY;
    x0 = sx;
    x1 = ex;
    y0 = sy;
    y1 = ey;
    deltaX = (x1 - x0)/w;
    deltaY = (y1 - y0)/h;
    PGraphics pg = createGraphics(w, h, JAVA2D);
    pg.beginDraw();
    pg.loadPixels();
    int count = 0;
    Complex c = new Complex();
    Complex z = new Complex();

    colX = x0;
    rowY = y0;
    for (int row = 0; row < h; row++) {
      colX = x0;
      for (int col = 0; col < w; col++) {
        count = 0;
        c.set(colX, rowY);
        z.set(colX, rowY);
        while (count < MAX_ITERATE-1 && z.sizeSquared () < 4.0) {
          z = z.squared().add(c); 
          count++;
        }
        if (count < minC) minC = count;
        if (count != MAX_ITERATE - 1 && count > maxC) maxC = count;
        pg.pixels[col + row * w] = colors[count];
        colX += deltaX;
      }
      rowY += deltaY;
    }
    pg.updatePixels();
    this.pg = pg;
    System.out.println(toString());
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Image size [" + w + "," + h +"]\n");
    sb.append("  X: " + sx + " --> " + ex + "     (Range " + (ex-sx) + ")\n");
    sb.append("  Y: " + sy + " --> " + ey + "     (Range " + (ey-sy) + ")\n");
    sb.append("  Counts from " + minC + " to " + maxC + "   (Max. possible = " + MAX_ITERATE + ")\n");
    sb.append("-----------------------------------------------------------------------------\n");
    return sb.toString();
  }
}
