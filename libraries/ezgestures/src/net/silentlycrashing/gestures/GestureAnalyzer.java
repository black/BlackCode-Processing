/*
  This file is part of the ezGestures project.
  http://www.silentlycrashing.net/ezgestures/

  Copyright (c) 2007-08 Elie Zananiri

  ezGestures is free software: you can redistribute it and/or modify it under
  the terms of the GNU General Public License as published by the Free Software 
  Foundation, either version 3 of the License, or (at your option) any later 
  version.

  ezGestures is distributed in the hope that it will be useful, but WITHOUT ANY
  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
  A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

  You should have received a copy of the GNU General Public License along with 
  ezGestures.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.silentlycrashing.gestures;

import java.awt.*;
import java.util.*;
import net.silentlycrashing.util.*;
import processing.core.*;

/**
 * Records gestures for analysis by the registered GestureListeners.
 * <p>The analysis algorithm used here is based on the <a href="http://www.smardec.com/products/mouse-gestures.html">MouseGestures</a> library by Smardec.</p>
 * <p>Debugging:<br>
 * The "verbose" flag can be set to print the recorded gestures to standard output.<br>
 * The "debug" flag can be set to plot the key points on the display.</p>
 */
/* $Id: GestureAnalyzer.java 33 2008-11-12 16:42:57Z prisonerjohn $ */
public class GestureAnalyzer {
	public static final String LEFT = "L";
	public static final String RIGHT = "R";
	public static final String UP = "U";
	public static final String DOWN = "D";
	
	public static final int KEY_SIZE = 10;
	
	protected PApplet p;
	
	private int minOffset;
	private Point startPoint;
	private StringBuffer gesture;
	
	private Vector<RegisteredAction> startActions;
	private Vector<RegisteredAction> updateActions;
	private Vector<RegisteredAction> stopActions;
	
	private boolean verbose;
	private boolean debug;
	
	/**
	 * Builds a GestureAnalyzer.
	 * 
	 * @param parent the parent PApplet
	 * @param min the minimum offset
	 */
	public GestureAnalyzer(PApplet parent, int min) {
		p = parent;
		minOffset = min;
		
		startPoint = null;
		gesture = new StringBuffer();
		
		startActions = new Vector<RegisteredAction>();
		updateActions = new Vector<RegisteredAction>();
		stopActions = new Vector<RegisteredAction>();
		
		verbose = false;
		debug = false;
	}
	
	/**
     * Registers the given method as a start action.
     * 
     * @param name the name of the method to register
     * @param o the Object holding the method
     * @param cargs the arguments
     */
	public void registerStartAction(String name, Object o, Class cargs[]) {
        startActions.add(new RegisteredAction(name, o, cargs));
    }
	
	/**
     * Invokes the start actions.
     */
    public void invokeStartActions() {
    	RegisteredAction startAction;
        for (Iterator<RegisteredAction> i = startActions.iterator(); i.hasNext();) {
        	startAction = i.next();
    		startAction.invoke(new Object[] { new PointInTime(startPoint, p.frameCount) });
        }   
    }
    
    /**
     * Registers the given method as an update action.
     * 
     * @param name the name of the method to register
     * @param o the Object holding the method
     * @param cargs the arguments
     */
	public void registerUpdateAction(String name, Object o, Class cargs[]) {
        updateActions.add(new RegisteredAction(name, o, cargs));
    }
	
	/**
     * Invokes the update actions.
     */
    public void invokeUpdateActions() {
    	RegisteredAction updateAction;
        for (Iterator<RegisteredAction> i = updateActions.iterator(); i.hasNext();) {
        	updateAction = i.next();
        	updateAction.invoke(new Object[] { new PointInTime(startPoint, p.frameCount) });
        }
    }
    
    /**
     * Registers the given method as a stop action.
     * 
     * @param name the name of the method to register
     * @param o the Object holding the method
     * @param cargs the arguments
     */
	public void registerStopAction(String name, Object o, Class cargs[]) {
        stopActions.add(new RegisteredAction(name, o, cargs));
    }
	
	/**
     * Invokes the stop actions.
     */
    public void invokeStopActions() {
    	RegisteredAction stopAction;
        for (Iterator<RegisteredAction> i = stopActions.iterator(); i.hasNext();) {
        	stopAction = i.next();
        	stopAction.invoke(new Object[] { new PointInTime(startPoint, p.frameCount) });
        }
    }

	/** 
	 * Sets the start Point and invokes all start RegisteredActions.
	 * <p>Called when the mouse is pressed.</p>
	 * 
	 * @param pt the current mouse Point
	 */
	public void start(Point pt) {
		startPoint = pt;
		invokeStartActions();
	}
	
	/** 
	 * Checks if a new move has occurred and if so, invokes all update RegisteredActions.
	 * <p>Called when the mouse is dragged.</p>
	 * 
	 * @param pt the current mouse Point
	 */
	public void update(Point pt) {
		int dX = pt.x-startPoint.x;
		int dY = pt.y-startPoint.y;
		int dXAbs = Math.abs(dX);
		int dYAbs = Math.abs(dY);

        if ((dXAbs < minOffset) && (dYAbs < minOffset)) {
        	// the points are too close together
        	return;
        }
            
        float tanAbs = ((float)dXAbs)/dYAbs;
        if (tanAbs < 1) {
            if (dY < 0)
                saveMove(UP);
            else
                saveMove(DOWN);
        } else {
            if (dX < 0)
                saveMove(LEFT);
            else
                saveMove(RIGHT);
        }
        
        startPoint = pt;
        invokeUpdateActions();
	}
	
	/** 
	 * Invokes all stop RegisteredActions and resets the GestureAnalyzer.
	 * <p>Called when the mouse is released.</p>
	 * 
	 * @param pt the current mouse Point (unused)
	 */
	public void stop(Point pt) {
		invokeStopActions();
		reset();
	}
	
	/**
	 * Resets the GestureAnalyzer.
	 */
	private void reset() {
    	startPoint = null;
        gesture.delete(0, gesture.length());
        
        if (verbose) 
        	PApplet.println();
    }
	
	/**
     * Adds a move to the buffer.
     *
     * @param move the recognized move
     */
    private void saveMove(String move) {
        if ((gesture.length() > 0) && (gesture.charAt(gesture.length() - 1) == move.charAt(0))) {
        	// the move is identical to the last one, skip it
        	return;
        }

        gesture.append(move);
        invokeUpdateActions();
        
        if (verbose) 
        	PApplet.print(move);
        if (debug) {
        	p.noStroke();
        	p.fill(204, 0, 0);
        	p.ellipse(startPoint.x, startPoint.y, KEY_SIZE, KEY_SIZE);
        }
    }
    
    /**
     * Checks if the passed regular expression matches the saved gesture.
     * 
     * @param regex the regular expression to check
     *
     * @return whether or not there is a match
     */
    public boolean matches(String regex) {
    	return getGesture().matches(regex);
    }
    
    public int getMinOffset() { return minOffset; }
    public void setMinOffset(int m) { minOffset = m; }
    public String getGesture() { return gesture.toString(); }
    public boolean isRecognized() { return (gesture.length() > 0); }
    public void setVerbose(boolean v) { verbose = v; }
    public boolean isVerbose() { return verbose; }
    public void setDebug(boolean d) { debug = d; }
    public boolean isDebug() { return debug; }
}
