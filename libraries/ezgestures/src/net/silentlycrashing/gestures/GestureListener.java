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
import net.silentlycrashing.util.*;
import processing.core.*;

/**
 * Listens for a specific gesture and calls the appropriate RegisteredAction when it becomes active or inactive.
 */
/* $Id: GestureListener.java 31 2008-11-12 16:01:48Z prisonerjohn $ */
public abstract class GestureListener {
	protected PApplet p;
	protected GestureAnalyzer ga;
	
	protected boolean active;
	protected RegisteredAction onAction;
	protected RegisteredAction offAction;
	
	protected PointInTime startPoint;
	protected Rectangle bounds;
	protected boolean inBounds;
	
	/**
	 * Builds a GestureListener covering the entire canvas.
	 * 
	 * @param parent the parent PApplet
	 * @param analyzer the linked GestureAnalyzer
	 */
	public GestureListener(PApplet parent, GestureAnalyzer analyzer) {
		this(parent, analyzer, new Rectangle(0, 0, parent.width, parent.height));
	}
	
	/**
	 * Builds a bounded GestureListener.
	 * 
	 * @param parent the parent PApplet
	 * @param analyzer the linked GestureAnalyzer
	 * @param bounds the bounding Rectangle of the first mouse press
	 */
	public GestureListener(PApplet parent, GestureAnalyzer analyzer, Rectangle bounds) {
		p = parent;
		
		ga = analyzer;
		ga.registerStartAction("startListening", this, new Class[] { PointInTime.class });
		ga.registerUpdateAction("keepListening", this, new Class[] { PointInTime.class });
		ga.registerStopAction("stopListening", this, new Class[] { PointInTime.class });	
		
		active = false;
		this.bounds = bounds;
		inBounds = true;
	}
	
	/** 
	 * Checks that the gesture is starting in the correct bounds and sets the start point.
	 * 
	 * @param pt the first PointInTime of the gesture
	 */
	public void startListening(PointInTime pt) {
		if (bounds.contains(pt)) {
			startPoint = pt;
			inBounds = true;
		} else {
			inBounds = false;
		}
	}
	
	/** 
	 * Holds actions to be performed when a new move occurs in the gesture.
	 * 
	 * @param pt the current PointInTime of the gesture
	 */
	public abstract void keepListening(PointInTime pt);
	
	/** 
	 * Holds actions to be performed when a gesture completes.
	 * 
	 * @param pt the last PointInTime of the gesture
	 */
	public abstract void stopListening(PointInTime pt);
	
	/**
	 * Registers the given method with the on action.
	 *
	 * @param name the name of the method to register
     * @param o the Object holding the method
     */
    public void registerOnAction(String name, Object o) {
        onAction = new RegisteredAction(name, o);
    }
	 	   
    /**
     * Registers the given method with the off action.
     *
     * @param name the name of the method to register
     * @param o the Object holding the method
     */
    public void registerOffAction(String name, Object o) {
    	offAction = new RegisteredAction(name, o);
    }
	
    public void activate() {
    	if (!active) {
    		active = true;
    		if (onAction != null) {
        		onAction.invoke();
        	}
    	}
    }
    
    public void deactivate() {
    	if (active) {
    		active = false;
        	if (offAction != null) {
        		offAction.invoke();
        	}
		}
    }
    
    public boolean isActive() { return active; }
    public PointInTime getStartPoint() { return startPoint; }
    public void setBounds(Rectangle newBounds) { bounds = newBounds; }
}
