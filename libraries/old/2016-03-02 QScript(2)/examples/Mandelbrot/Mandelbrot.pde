import org.qscript.eventsonfire.*;
import org.qscript.events.*;
import org.qscript.editor.*;
import org.qscript.*;
import org.qscript.operator.*;
import org.qscript.errors.*;

import java.util.*;

/**
 * This example demonstrates how you might use QScript to 
 * calculate the points in the Mandelbrot set.
 * 
 * It creates a new function (PlotFunction) and a new event
 * (PlotEvent) so the script can inform the main sketch
 * when it has calculated a position in the set. The sketch 
 * would then paint the appropriate colour.
 * 
 * @author Peter Lager
 *
 */


String[] algor = new String[] {
  "# The variables origin, delta, plotW, plotH and maxIter must", 
  "# all be initialised before evaluating this script.", 
  "# origin = complex value of top-left corner ", 
  "# plotW & plotH the number of horizontal and vertical steps", 
  "# delta the distnace on the plot represented by 1 step", 
  "# maxIter the number of iterations", 
  "x = real(origin); y = imag(origin); py = 0", 
  "WHILE(py < plotH)", 
  "  px = 0", 
  "  WHILE(px < plotW)", 
  "    c = complex(x + px * delta, y + py * delta)", 
  "    z = complex(0,0); n = 0", 
  "    WHILE(n < maxIter && mag(z) < 2)", 
  "      z = z * z + c", 
  "      n = n + 1", 
  "    WEND", 
  "    plot(px, py, n) # User defined operator", 
  "    px = px + 1", 
  "  WEND", 
  "  py = py + 1", 
  "WEND"
};

String algorText = PApplet.join(algor, "\n");
boolean showAlgor = false;

final int OFFSET_TOP = 20;
final int OFFSET_LEFT = 50;
// List of plots
List<Plot> plots = new ArrayList<Plot>();
Plot plot = null;
int plotIdx;
// Selection box data
boolean selecting = false;
int x0, y0, x1, y1;
boolean validSelection;

final int nbrColors = 32;
final int resolution = 1;
int[] colors = new int[nbrColors +1];


public void setup() {
  size(500, 550);
  cursor(CROSS);
  // Must be done before we attempt to use the script
  addOperators();
  initColors();
  textAlign(LEFT, TOP);
  plot = new Plot(new Complex(-2f, -1.25f), 2.5f, 2.5f );
  plots.add(plot);
  plotIdx = 0;
}

public void draw() {
  background(48);
  noStroke();
  fill(64);
  rect(OFFSET_LEFT, OFFSET_TOP, 400, 400);
  if (showAlgor) {
    fill(60, 255, 60);
    text(algorText, OFFSET_LEFT+4, OFFSET_TOP+48);
  } else if (plot != null) {
    image(plot.pg, plot.drawX, plot.drawY);
    if (selecting && validSelection) {
      noFill();
      strokeWeight(2);
      stroke(255);
      rect(min(x0, x1), min(y0, y1), abs(x1-x0), abs(y1-y0));
    }
  }
  drawInstructions();
}

public void drawInstructions() {
  translate(0, 440);
  noStroke();
  fill(0, 0, 128);
  rect(0, 0, width, 20);
  fill(255, 255, 0);
  text("Plot "+ (plotIdx + 1) + " of " + plots.size(), 220, 0);
  fill(0, 0, 192);
  rect(0, 20, width, 120);
  fill(200, 200, 255);
  text("To zoom into the current image click and drag the mouse to create a white frame arround the area you are interested in.", 240, 20, 240, 200);
  text("KEY LEGEND", 10, 20);
  text("[A] script <> plot view", 10, 36);
  if (plots.size() > 1) {
    text("[1] previous plot", 10, 52);
    text("[2] next plot", 10, 68);
  }
  if (plotIdx > 0)
    text("[DEL] delete current plot", 10, 84);
}

public void keyTyped() {
  if (!selecting) {
    if (key == 'a' || key =='A')
      showAlgor = ! showAlgor;
    else if (key == '1') {
      plotIdx = (plotIdx + plots.size() - 1) % plots.size();
      plot = plots.get(plotIdx);
    } else if (key == '2') {
      plotIdx = (plotIdx + 1) % plots.size();
      plot = plots.get(plotIdx);
    } else if (plotIdx > 0 && (key == 8 || key == 127)) {
      plots.remove(plotIdx);
      if (plotIdx == plots.size())
        plotIdx--;
      plot = plots.get(plotIdx);
    }
  }
}

public void addOperators() {
  OperatorSet opSet = OperatorSet.get();
  // All operator constructors have 4 parameters
  // 1) the symbol to be used for this operator (must be unique)
  // 2) the number of parameters required by this operator
  // 3) the priority for this operator [30 for a FUNCTION and 40 for a CONSTANT]
  // 4) the operator type - Operator.FUNCTION or Operator.CONSTANT
  opSet.addOperator(new PlotFunction("plot", 3, 30, Operator.FUNCTION));
}

public void mousePressed() {
  if (!showAlgor && plot.isDone() && plot.isOver(mouseX, mouseY) ) {
    x0 = x1 = mouseX; 
    y0 = y1 = mouseY;
    selecting = true;
  }
}

public void mouseDragged() {
  if (selecting && plot.isOver(mouseX, mouseY)) {
    x1 = mouseX; 
    y1 = mouseY;
    validSelection = abs(x1-x0) >= 2 && abs(y1-y0) >= 2;
  }
}

public void mouseReleased() {
  if (selecting) {
    selecting = false;
    int w = abs(x1-x0);
    int h = abs(y1-y0);
    if (w >= 2 && h >= 2) {
      Complex origin = plot.getPlotPosition(min(x0, x1), min(y0, y1));
      plot = new Plot(origin, plot.getSize(w), plot.getSize(h));
      plots.add(plot);
      plotIdx = plots.size()-1;
    }
  }
}

public void initColors() {
  colorMode(HSB, 1.0f, 1.0f, 1.0f);
  float delta = 0.96f / nbrColors;
  for (int i = 0; i < colors.length; i++)
    colors[i] = color( 1 - i * delta, 1, 0.9f);
  colorMode(RGB, 255);
  colors[colors.length - 1] = color(0);
}


// Represents a single plot of all or part of the Mandelbrot set
public class Plot {

  Complex origin;
  float scale;

  int drawX, drawY, drawW, drawH;
  PGraphics pg;
  boolean done = false;

  Script script;

  public Plot(Complex origin, float plotw, float ploth) {
    this.origin = origin;
    scale = 200 / max(plotw, ploth);
    drawW = round(plotw * scale);
    drawH = round(ploth * scale);
    drawX = OFFSET_LEFT + 200 - drawW;
    drawY = OFFSET_TOP + 200 - drawH;
    pg = createGraphics(2*drawW, 2*drawH);
    pg.beginDraw();
    pg.background(0);
    pg.noStroke();
    pg.endDraw();
    Script script = new Script(algor);
    script.storeVariable("origin", this.origin);
    script.storeVariable("delta", 1/scale);
    script.storeVariable("plotW", drawW);
    script.storeVariable("plotH", drawH);
    script.storeVariable("maxIter", resolution * nbrColors);
    script.setTimeLimit(20);
    script.addListener(this);
    script.parse();
    Solver$.evaluate(script);
  }

  public boolean isOver(int x, int y) {
    return (x >= drawX && x < drawX + 2*drawW && y >= drawY && y < drawY + 2*drawH);
  }

  public Complex getPlotPosition(int px, int py) {
    float real = (float) (origin.real + 0.5f * (px - drawX) / scale);
    float imag = (float) (origin.imag + 0.5f * (py - drawY) / scale);
    return new Complex(real, imag);
  }
  public float getSize(int size) {
    return 0.5f * size/scale;
  }

  public boolean isDone() {
    return done;
  }

  @EventHandler
    public void onScriptEvent(ScriptEvent event) {
    synchronized(this) {
      if (event instanceof PlotEvent) {
        int px = (Integer) event.extra[0];
        int py = (Integer) event.extra[1];
        int n = (Integer) event.extra[2];
        pg.beginDraw();
        pg.fill(colors[n/resolution]);
        pg.rect(px * 2, py * 2, 2, 2);
        pg.endDraw();
      } else if (event instanceof ScriptFinishedEvent) {
        done = true;
      }
    }
  }
}


// The event fired by the PlotFunction so the main program 
// can update the Mandelbrot displaydisplay
public static class PlotEvent extends ScriptEvent {

  public PlotEvent(Script script, ErrorType etype, int lineNo, int pos, int width, Object[] extra) {
    super(script, etype, lineNo, pos, width, extra);
  }
}


// A new operator used to indicate when value for a particular
// in the Mandelbrot has been calculated
public static class PlotFunction extends Operator {

  public PlotFunction(String symbol, int nbrArgs, int priority, int type) {
    super(symbol, nbrArgs, priority, type);
  }

  public Argument resolve(Script script, Token token, Argument[] args, 
  Object... objects) throws EvaluationException {
    testForUninitialisedVars(script, args);
    Argument a0 = args[0];
    Argument a1 = args[1];
    Argument a2 = args[2]; 
    if (a0.isNumeric && a1.isNumeric && a2.isNumeric) {
      Integer px = a0.toInteger();
      Integer py = a1.toInteger();
      Integer n = a2.toInteger();
      script.fireEvent(PlotEvent.class, null, a0, new Object[] { 
        px, py, n
      }
      );
      return null;
    }
    // If we get here then the arguments are invalid
    handleInvalidArguments(script, token);
    return null;
  }
}

