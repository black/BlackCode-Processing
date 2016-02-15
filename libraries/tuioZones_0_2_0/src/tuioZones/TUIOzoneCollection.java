/*
  The tuioZone library provides a way to set zones (or generic objects) within a screen to respond in useful ways to TUIO events.
  
  (c) copyright
  
  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU General Public
  License Version 3 as published by the Free Software Foundation.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  General Public License for more details.

  You should have received a copy of the GNU General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
 */

package tuioZones;

import processing.core.*;
import oscP5.*;
import java.lang.reflect.Method;

/**
 * The TUIOzoneCollection class provides a way to define zones that respond to
 * TUIO events in unique ways. It handles the processing of TUIO events and
 * provides methods to get data about each zone and related TUIO events.
 * 
 * @example _tzDemo1
 * @author jLyst
 * 
 */
public class TUIOzoneCollection implements PConstants {

	PApplet myParent;
	Method hSwipeMethod;
	Method vSwipeMethod;
	Method tReleaseMethod;
	Method clickedMethod;

	OscP5 oscP5;
	TouchPoint tpoint[];// touch point array
	TouchZone tzone[];// touch zone array

	final String VERSION = "0.2.0";

	/**
	 * The Constructor initializes a collection of zones that respond to TUIO
	 * cursor actions. A default zone, named 'canvas', is set that spans the
	 * entire width and height of the sketch. Port 3333 is used by default to
	 * receive TUIO messages
	 * 
	 * @param theParent
	 */
	public TUIOzoneCollection(PApplet theParent) {
		this(theParent, 3333);
	}

	/**
	 * The Constructor initializes a collection of zones that respond to TUIO
	 * cursor actions. A default zone, named 'canvas', is set that spans the
	 * entire width and height of the sketch.
	 * 
	 * @param theParent
	 * @param port
	 *            the port where TUIO messages will be sent to. (for example,
	 *            3333)
	 */
	public TUIOzoneCollection(PApplet theParent, int port) {
		myParent = theParent;
		oscP5 = new OscP5(this, port);
		tpoint = new TouchPoint[1];// start with one point
		tzone = new TouchZone[1];// start with canvas
		tzone[0] = new TouchZone("canvas", 0, 0, myParent.width,
				myParent.height);
		try {
			hSwipeMethod = myParent.getClass().getMethod("hSwipeEvent",
					new Class[] { String.class });
		} catch (Exception e) {
			// no such method, or an error.. which is fine, just ignore
		}
		try {
			vSwipeMethod = myParent.getClass().getMethod("vSwipeEvent",
					new Class[] { String.class });
		} catch (Exception e) {
			// no such method, or an error.. which is fine, just ignore
		}
		try {
			tReleaseMethod = myParent.getClass().getMethod("tReleaseEvent",
					new Class[] { int.class });
		} catch (Exception e) {
			// no such method, or an error.. which is fine, just ignore
		}
		try {
			clickedMethod = myParent.getClass().getMethod("clickEvent",
					new Class[] { String.class });
		} catch (Exception e) {
			// no such method, or an error.. which is fine, just ignore
		}
		myParent.registerPre(this);
	}

	/**
	 * return the version of the library.
	 * 
	 * @return String
	 */
	public String version() {
		return VERSION;
	}

	// /////////////////////////////////////////////////////////////
	// SET METHODS//////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////

	/**
	 * Define a new circular zone that will respond to TUIO events in a unique
	 * way.
	 * 
	 * @param zNameIn
	 *            a name for the zone
	 * @param xIn
	 *            the x-coordinate of the center of the circular zone
	 * @param yIn
	 *            the y-coordinate of the center of the circular zone
	 * @param rIn
	 *            the radius of the circular zone
	 */
	public void setZone(String zNameIn, int xIn, int yIn, int rIn) { // like a
		// constructor
		boolean set = false;
		for (int i = 0; i < tzone.length; i++) {
			if (tzone[i] == null) {
				tzone[i] = new TouchZone(zNameIn, xIn, yIn, rIn);
				set = true;
				break;
			}
		}
		if (!set) {
			tzone = (TouchZone[]) PApplet.append(tzone, new TouchZone(zNameIn,
					xIn, yIn, rIn));// + or -1???
		}
	}

	/**
	 * Define a new rectangular zone that will respond to TUIO events in a
	 * unique way.
	 * 
	 * @param zNameIn
	 *            a name for the zone
	 * @param xIn
	 *            the x-coordinate of the upper left corner of the zone
	 * @param yIn
	 *            the y-coordinate of the upper left corner of the zone
	 * @param wIn
	 *            the width of the zone
	 * @param hIn
	 *            the height of the zone
	 */
	public void setZone(String zNameIn, int xIn, int yIn, int wIn, int hIn) {// like
		// constructor
		boolean set = false;
		for (int i = 0; i < tzone.length; i++) {
			if (tzone[i] == null) {
				tzone[i] = new TouchZone(zNameIn, xIn, yIn, wIn, hIn);
				set = true;
				break;
			}
		}
		if (!set) {
			tzone = (TouchZone[]) PApplet.append(tzone, new TouchZone(zNameIn,
					xIn, yIn, wIn, hIn));// + or -1???
		}
	}

	/**
	 * Modify rectangular zone primitive data.
	 * 
	 * @param zNameIn
	 *            the name of the zone to be modified
	 * @param xIn
	 *            the x-coordinate of the upper left corner of the zone
	 * @param yIn
	 *            the y-coordinate of the upper left corner of the zone
	 * @param wIn
	 *            the width of the zone
	 * @param hIn
	 *            the height of the zone
	 */
	public void setZoneData(String zNameIn, int xIn, int yIn, int wIn, int hIn) {
		for (int i = 0; i < tzone.length; i++) {
			if (tzone[i] != null) {
				if (tzone[i].name.equals(zNameIn)) {
					setZoneData(i, xIn, yIn, wIn, hIn);
				}
			}
		}
	}

	/**
	 * Set zone drag limits.
	 * 
	 * @param zNameIn
	 *            the name of the zone to be modified
	 * @param xlow
	 *            the lower limit of drag x distance
	 * @param ylow
	 *            the lower limit of drag y distance
	 * @param xhigh
	 *            the upper limit of drag x distance
	 * @param yhigh
	 *            the upper limit of drag y distance
	 */
	public void setZoneDragLimits(String zNameIn, int xlow, int xhigh,
			int ylow, int yhigh) {
		for (int i = 0; i < tzone.length; i++) {
			if (tzone[i] != null) {
				if (tzone[i].name.equals(zNameIn)) {
					tzone[i].xDragLow = xlow;
					tzone[i].xDragHi = xhigh;
					tzone[i].yDragLow = ylow;
					tzone[i].yDragHi = yhigh;
				}
			}
		}
	}

	/**
	 * Set zone scale limits.
	 * 
	 * @param zNameIn
	 *            the name of the zone to be modified
	 * @param low
	 *            the lower limit of scale gesture (<=1)
	 * @param high
	 *            the upper limit of scale gesture (>=1)
	 */
	public void setZoneScaleLimits(String zNameIn, float low, float high) {
		for (int i = 0; i < tzone.length; i++) {
			if (tzone[i] != null) {
				if (tzone[i].name.equals(zNameIn)) {
					tzone[i].sclLow = low;
					tzone[i].sclHigh = high;
				}
			}
		}
	}

	/**
	 * Set zone scale sensitivity.
	 * 
	 * @param zNameIn
	 *            the name of the zone to be modified
	 * @param val
	 *            the sensitivity of scale gesture (default=1.0)
	 */
	public void setZoneScaleSensitivity(String zNameIn, float val) {
		for (int i = 0; i < tzone.length; i++) {
			if (tzone[i] != null) {
				if (tzone[i].name.equals(zNameIn)) {
					tzone[i].sclSens = val;
				}
			}
		}
	}

	/**
	 * Modify circular zone primitive data.
	 * 
	 * @param zNameIn
	 *            the name of the zone to be modified
	 * @param xIn
	 *            the x-coordinate of the upper left corner of the zone
	 * @param yIn
	 *            the y-coordinate of the upper left corner of the zone
	 * @param rIn
	 *            the radius of the zone
	 */
	public void setZoneData(String zNameIn, int xIn, int yIn, int rIn) {
		for (int i = 0; i < tzone.length; i++) {
			if (tzone[i] != null) {
				if (tzone[i].name.equals(zNameIn)) {
					setZoneData(i, xIn, yIn, rIn * 2, rIn * 2);
				}
			}
		}
	}

	void setZoneData(int i, int xIn, int yIn, int wIn, int hIn) {
		setChildZoneData(i, xIn, yIn, wIn, hIn);
		if (tzone[i].group != null)
			setGroupedZoneData(i, xIn, yIn, wIn, hIn);
		setZoneDataCore(i, xIn, yIn, wIn, hIn);
	}

	void setZoneDataCore(int i, int xIn, int yIn, int wIn, int hIn) {
		tzone[i].x = xIn;
		tzone[i].y = yIn;
		tzone[i].r = wIn / 2;
		tzone[i].w = wIn;
		tzone[i].h = hIn;
		tzone[i].cx = xIn + wIn / 2;
		tzone[i].cy = yIn + hIn / 2;
	}

	void setChildZoneData(int parent, int xIn, int yIn, int wIn, int hIn) {
		for (int i = 0; i < tzone.length; i++) {
			if (tzone[i].parent != null) {
				if (tzone[i].parent.equals(tzone[parent].name)) {
					float xScale = (float) wIn / tzone[parent].wi;
					float yScale = (float) hIn / tzone[parent].hi;
					setZoneData(
							i,
							xIn
									+ (int) ((tzone[i].xi - tzone[parent].xi) * xScale),
							yIn
									+ (int) ((tzone[i].yi - tzone[parent].yi) * yScale),
							(int) (tzone[i].wi * xScale),
							(int) (tzone[i].hi * yScale));
				}
			}
		}
	}

	void setGroupedZoneData(int parent, int xIn, int yIn, int wIn, int hIn) {
		for (int i = 0; i < tzone.length; i++) {
			if (tzone[i].group != null & i != parent) {
				if (tzone[i].group.equals(tzone[parent].group)) {
					float xScale = (float) wIn / tzone[parent].wi;
					float yScale = (float) hIn / tzone[parent].hi;
					setZoneDataCore(
							i,
							xIn
									+ (int) ((tzone[i].xi - tzone[parent].xi) * xScale),
							yIn
									+ (int) ((tzone[i].yi - tzone[parent].yi) * yScale),
							(int) (tzone[i].wi * xScale),
							(int) (tzone[i].hi * yScale));
				}
			}
		}
	}

	/**
	 * Change zone name.
	 * 
	 * @param zNameOld
	 *            old name for the zone
	 * @param zNameNew
	 *            new name for the zone
	 * 
	 */
	public void changeZoneName(String zNameOld, String zNameNew) { // like a
		// constructor
		for (int i = 0; i < tzone.length; i++) {
			if (tzone[i] != null) {
				if (tzone[i].name.equals(zNameOld)) {
					tzone[i].name = zNameNew;
				}
			}
		}
	}

	/**
	 * Set zone parameters, like 'DRAGGABLE', 'SCALABLE', or 'HSWIPEABLE'.
	 * 
	 * @param zNameIn
	 *            the name of the zone to be modified
	 * @param paramName
	 *            the name of the parameter to be set. Parameters names can be
	 *            'DRAGGABLE', 'SCALABLE', 'VSWIPEABLE', 'HSWIPEABLE', or
	 *            'WINDOW3D'. Note, 'HSWIPEABLE' and 'VSWIPEABLE' can only be
	 *            applied to rectangle zones. 'WINDOW3D' enables a one-finger
	 *            gesture for applying 3D rotations to an object.
	 * @param bool
	 *            Set the parameter to be true or false.
	 * @example _tzDrag
	 * @example _tzThrow
	 * @example _tzSwipe
	 */
	public void setZoneParameter(String zNameIn, String paramName, boolean bool) {
		for (int i = 0; i < tzone.length; i++) {
			if (tzone[i] != null) {
				if (tzone[i].name.equals(zNameIn)) {
					if (paramName.equals("DRAGGABLE"))
						tzone[i].draggable = bool;
					else if (paramName.equals("XDRAGGABLE"))
						tzone[i].xdraggable = bool;
					else if (paramName.equals("YDRAGGABLE"))
						tzone[i].ydraggable = bool;
					else if (paramName.equals("SCALABLE"))
						tzone[i].scalable = bool;
					else if (paramName.equals("XSCALABLE"))
						tzone[i].xscalable = bool;
					else if (paramName.equals("YSCALABLE"))
						tzone[i].yscalable = bool;
					else if (paramName.equals("HSWIPEABLE"))
						tzone[i].hSwipeable = bool;
					else if (paramName.equals("VSWIPEABLE"))
						tzone[i].vSwipeable = bool;
					else if (paramName.equals("THROWABLE"))
						tzone[i].throwable = bool;
					else if (paramName.equals("ROTATABLE"))
						tzone[i].rotatable = bool;
					else if (paramName.equals("WINDOW3D"))
						tzone[i].window3d = bool;
					break;
				}
			}
		}
	}

	/**
	 * Attach a zone to another zone. The attachment is a child/parent
	 * relationship. Changes in the parent's size and position will alter the
	 * child in the same way, but not the other way around.
	 * 
	 * @param child
	 *            the name of the child zone.
	 * @param parent
	 *            the name of the parent zone.
	 * @example _tzAttach
	 */

	public void attachZoneTo(String child, String parent) {
		for (int i = 0; i < tzone.length; i++) {
			if (tzone[i] != null) {
				if (tzone[i].name.equals(child)) {
					tzone[i].parent = parent;
					break;
				}
			}
		}
	}

	/**
	 * Assign a zone to a group. A new group can be created here or an existing
	 * group can be used.
	 * 
	 * @param zone
	 *            the name of the zone to assign to a group.
	 * @param parent
	 *            the name of the group. Can be a new group or existing.
	 * @example _tzGrouping
	 */

	public void assignZoneToGroup(String zone, String group) {
		for (int i = 0; i < tzone.length; i++) {
			if (tzone[i] != null) {
				if (tzone[i].name.equals(zone)) {
					tzone[i].group = group;
					break;
				}
			}
		}
	}

	/**
	 * Pull a zone to the top layer.
	 * 
	 * @param zNameIn
	 *            the name of the zone to pull to the top layer.
	 * 
	 */
	public void pullZoneToTop(String zNameIn) {
		for (int i = 0; i < tzone.length; i++) {
			if (tzone[i] != null) {
				if (tzone[i].name.equals(zNameIn)) { // remove zone
					TouchZone[] front = (TouchZone[]) PApplet.subset(tzone, 0,
							i);
					TouchZone[] back = (TouchZone[]) PApplet.subset(tzone,
							i + 1, (tzone.length) - i - 1);
					TouchZone pulled = tzone[i];
					tzone = (TouchZone[]) PApplet.concat(front, back);
					tzone = (TouchZone[]) PApplet.append(tzone, pulled);
					break;
				}
			}
		}
	}

	/**
	 * Kill a zone.
	 * 
	 * @param zNameIn
	 *            the name of the zone to be killed.
	 * 
	 */
	public void killZone(String zNameIn) {
		for (int i = 0; i < tzone.length; i++) {
			if (tzone[i] != null) {
				if (tzone[i].name.equals(zNameIn)) { // remove zone
					TouchZone[] front = (TouchZone[]) PApplet.subset(tzone, 0,
							i);
					TouchZone[] back = (TouchZone[]) PApplet.subset(tzone,
							i + 1, (tzone.length) - i - 1);
					tzone = (TouchZone[]) PApplet.concat(front, back);
					break;
				}
			}
		}
	}

	/**
	 * Activate a zone so that it responds to touch. A zone is active by
	 * default.
	 * 
	 * @param zNameIn
	 *            the name of the zone to be activated.
	 * @param active
	 *            boolean to set active state. true for set active, false for
	 *            set not active
	 */
	public void setZoneActive(String zNameIn, boolean active) {
		for (int i = 0; i < tzone.length; i++) {
			if (tzone[i] != null) {
				if (tzone[i].name.equals(zNameIn)) {
					tzone[i].active = active;
					removePrimeFromZone(i);
					break;
				}
			}
		}
	}

	/**
	 * Activate a zone so that it responds to touch. A zone is active by
	 * default.
	 * 
	 * @param zNameIn
	 *            the name of the zone to be activated.
	 * @param active
	 *            boolean to set active state. true for set active, false for
	 *            set not active
	 */
	public void setZoneChildrenActive(String zNameIn, boolean active) {
		for (int i = 0; i < tzone.length; i++) {
			if (tzone[i] != null) {
				if (tzone[i].name.equals(zNameIn)) {
					for (int j = 0; j < tzone.length; j++) {
						if (tzone[j].parent != null) {
							if (tzone[j].parent.equals(tzone[i].name)) {
								tzone[j].active = active;
								removePrimeFromZone(i);
							}
						}
					}
					break;
				}
			}
		}
	}

	// /////////////////////////////////////////////////////////////
	// INVOKE EVENT METHODS//////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////

	public void makeHswipeEvent(String zName) {
		if (hSwipeMethod != null) {
			try {
				hSwipeMethod.invoke(myParent, zName);
			} catch (Exception e) {
				System.err
						.println("Disabling hSwipeEvent() for * because of an error.");

				e.printStackTrace();
				hSwipeMethod = null;
			}
		}
	}

	public void makeVswipeEvent(String zName) {
		if (vSwipeMethod != null) {
			try {
				vSwipeMethod.invoke(myParent, zName);
			} catch (Exception e) {
				System.err
						.println("Disabling vSwipeEvent() for * because of an error.");

				e.printStackTrace();
				vSwipeMethod = null;
			}
		}
	}

	public void touchReleased(int index) {
		if (tReleaseMethod != null) {
			try {
				tReleaseMethod.invoke(myParent, index);
			} catch (Exception e) {
				System.err
						.println("Disabling hSwipeEvent() for * because of an error.");

				e.printStackTrace();
				tReleaseMethod = null;
			}
		}
	}

	public void clicked(String zName) {
		if (clickedMethod != null) {
			try {
				clickedMethod.invoke(myParent, zName);
			} catch (Exception e) {
				System.err
						.println("Disabling clickedEvent() for * because of an error.");

				e.printStackTrace();
				clickedMethod = null;
			}
		}
	}

	// /////////////////////////////////////////////////////////////
	// GET METHODS//////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////
	/**
	 * Get the zone x-coordinate. Upper left corner for rectangle. Center for
	 * circle.
	 * 
	 * @param zNameIn
	 *            the name of the zone.
	 * @return an integer
	 * 
	 */
	public int getZoneX(String zNameIn) {
		int x = -999, i = getZoneIndex(zNameIn);
		if (i != -999) {
			x = tzone[i].x;
		}
		return x;
	}

	/**
	 * Get the zone y-coordinate. Upper left corner for rectangle. Center for
	 * circle.
	 * 
	 * @param zNameIn
	 *            the name of the zone.
	 * @return an integer
	 * 
	 */
	public int getZoneY(String zNameIn) {
		int y = -999, i = getZoneIndex(zNameIn);
		if (i != -999) {
			y = tzone[i].y;
		}
		return y;
	}

	/**
	 * Get the zone center y-coordinate.
	 * 
	 * @param zNameIn
	 *            the name of the zone.
	 * @return an integer
	 * 
	 */
	public int getZoneCenterX(String zNameIn) {
		int y = -999, i = getZoneIndex(zNameIn);
		if (i != -999) {
			y = tzone[i].cx;
		}
		return y;
	}

	/**
	 * Get the zone center y-coordinate.
	 * 
	 * @param zNameIn
	 *            the name of the zone.
	 * @return an integer
	 * 
	 */
	public int getZoneCenterY(String zNameIn) {
		int y = -999, i = getZoneIndex(zNameIn);
		if (i != -999) {
			y = tzone[i].cy;
		}
		return y;
	}

	/**
	 * Apply the zone's stored 3d Matrix Transformation. The zone itself is not
	 * rotated, but the matrix can be used to transform an object.
	 * 
	 * @param zNameIn
	 *            the name of the zone.
	 * 
	 */
	public void applyZone3dMatrix(String zNameIn) {
		int i = getZoneIndex(zNameIn);
		if (i != -999) {
			tzone[i].apply3dMatrix();
		}
	}

	/**
	 * Get the zones stored 3d Rotation Matrix. The zone itself is not rotated,
	 * but the matrix can be used to transform an object.
	 * 
	 * @param zNameIn
	 *            the name of the zone.
	 * 
	 * @return a string
	 * 
	 */
	public float[] getZone3dRotationMatrix(String zNameIn) {
		int i = getZoneIndex(zNameIn);
		if (i != -999) {
			return tzone[i].get3dMatrix();
		} else
			return null;
	}

	/**
	 * Get the zone scale factor.
	 * 
	 * @param zNameIn
	 *            the name of the zone.
	 * @return a float
	 * 
	 */
	public float getZoneScale(String zNameIn) {
		float scl = 1;
		int i = getZoneIndex(zNameIn);
		if (i != -999) {
			scl = tzone[i].scl;
		}
		return scl;
	}

	/**
	 * Get an active gesture's scale factor.
	 * 
	 * @param zNameIn
	 *            the name of the zone.
	 * @return a float
	 * 
	 */
	public float getGestureScale(String zNameIn) {
		float scl = 1f;
		int i = getZoneIndex(zNameIn);
		if (i != -999) {
			scl = tzone[i].gestureScale;
		}
		return scl;
	}

	/**
	 * Get an active 1-finger gesture's x-axis translation.
	 * 
	 * @param zNameIn
	 *            the name of the zone.
	 * @return an int
	 * 
	 */
	public float getSwipeXtranslation(String zNameIn) {
		int x = 0;
		int i = getZoneIndex(zNameIn);
		if (i != -999) {
			int j = tzone[i].primePoint;
			if (j > -1){
				int l = tpoint[j].coord.length;
				x = tpoint[j].coord[l - 1][0] - tpoint[j].coord[0][0];
			}
		}
		return x;
	}
	
	/**
	 * Get an active 1-finger gesture's y-axis translation.
	 * 
	 * @param zNameIn
	 *            the name of the zone.
	 * @return an int
	 * 
	 */
	public float getSwipeYtranslation(String zNameIn) {
		int x = 0;
		int i = getZoneIndex(zNameIn);
		if (i != -999) {
			int j = tzone[i].primePoint;
			if (j > -1){
				int l = tpoint[j].coord.length;
				x = tpoint[j].coord[l - 1][1] - tpoint[j].coord[0][1];
			}
		}
		return x;
	}
	
	/**
	 * Get an active 2-finger gesture's x-axis translation.
	 * 
	 * @param zNameIn
	 *            the name of the zone.
	 * @return an int
	 * 
	 */
	public float getGestureXtranslation(String zNameIn) {
		int x = 0;
		int i = getZoneIndex(zNameIn);
		if (i != -999) {
			x = tzone[i].gestureXtranslation;
		}
		return x;
	}

	/**
	 * Get an active 2-finger gesture's y-axis translation.
	 * 
	 * @param zNameIn
	 *            the name of the zone.
	 * @return an int
	 * 
	 */
	public float getGestureYtranslation(String zNameIn) {
		int y = 0;
		int i = getZoneIndex(zNameIn);
		if (i != -999) {
			y = tzone[i].gestureYtranslation;
		}
		return y;
	}

	/**
	 * Get an active 2-finger gesture's rotation factor (0.0 to 1.0 for 360 degree
	 * sweep).
	 * 
	 * @param zNameIn
	 *            the name of the zone.
	 * @return a float
	 * 
	 */
	public float getGestureRotation(String zNameIn) {
		float rot = 0f;
		int i = getZoneIndex(zNameIn);
		if (i != -999) {
			rot = tzone[i].gestureRotation;
		}
		return rot;
	}

	/**
	 * Get the zone width.
	 * 
	 * @param zNameIn
	 *            the name of the zone.
	 * @return an integer
	 * 
	 */
	public int getZoneWidth(String zNameIn) {
		int width = -999, i = getZoneIndex(zNameIn);
		if (i != -999) {
			width = tzone[i].w;
		}
		return width;
	}

	/**
	 * Get the zone height.
	 * 
	 * @param zNameIn
	 *            the name of the zone.
	 * @return an integer
	 * 
	 */
	public int getZoneHeight(String zNameIn) {
		int height = -999, i = getZoneIndex(zNameIn);
		if (i != -999) {
			height = tzone[i].h;
		}
		return height;
	}

	/**
	 * Get zone data.
	 * 
	 * <p>
	 * 0-the x-coordinate of the zone (upper left for rectangles and center for
	 * circles)
	 * <p>
	 * 1-the x-coordinate of the zone (upper left for rectangles and center for
	 * circles)
	 * <p>
	 * 2-the width of the zone (the radius if circle)
	 * <p>
	 * 3-the height of the zone (the radius if circle)
	 * <p>
	 * 4-the primary touch point x-coordinate (-999 if the zone is not being
	 * touched)
	 * <p>
	 * 5-the primary touch point y-coordinate (-999 if the zone is not being
	 * touched)
	 * <p>
	 * 6-the primary touch point index or ID (for use in getTrail method, -999
	 * if none exist)
	 * 
	 * 7-the second touch point x-coordinate (-999 if the zone is not being
	 * touched)
	 * <p>
	 * 8-the second touch point y-coordinate (-999 if the zone is not being
	 * touched)
	 * <p>
	 * 9-the second touch point index or ID (for use in getTrail method, -999 if
	 * none exist)
	 * 
	 * @param zNameIn
	 *            the name of the zone.
	 * @return Array of zone data as integers
	 * 
	 * 
	 */
	public int[] getZoneData(String zNameIn) {
		int[] data = new int[10]; // x y w h primaryX primaryY primaryPointIndex
		for (int i = 0; i < tzone.length; i++) {
			if (tzone[i] != null) {
				if (tzone[i].name.equals(zNameIn)) {
					int ppi = tzone[i].primePoint;// primary point index
					int secondpi = tzone[i].secondPoint;// second point index
					data[0] = tzone[i].x;
					data[1] = tzone[i].y;
					data[2] = tzone[i].w;
					data[3] = tzone[i].h;
					if (ppi > -1) {
						data[4] = tpoint[ppi].coord[tpoint[ppi].coord.length - 1][0];
						data[5] = tpoint[ppi].coord[tpoint[ppi].coord.length - 1][1];
					} else {
						data[4] = data[5] = -999;
					}
					data[6] = tzone[i].primePoint;
					if (secondpi > -1) {
						data[7] = tpoint[secondpi].coord[tpoint[secondpi].coord.length - 1][0];
						data[8] = tpoint[secondpi].coord[tpoint[secondpi].coord.length - 1][1];
					} else {
						data[7] = data[8] = -999;
					}
					data[9] = tzone[i].secondPoint;
					break;
				}
			}
		}
		return data;
	}

	/**
	 * Draw text centered, horizontally and vertically, on a zone.
	 * 
	 * @param zone
	 *            the name of the zone.
	 * @param text
	 *            the text to draw.
	 */

	public void drawText(String zone, String text) {
		for (int i = 0; i < tzone.length; i++) {
			if (tzone[i].name.equals(zone)) {
				myParent.pushStyle();
				myParent.textAlign(CENTER, CENTER);
				myParent.text(text, tzone[i].cx, tzone[i].cy);
				myParent.popStyle();
				break;
			}
		}
	}

	/**
	 * Draw circle based on a zone's position and size.
	 * 
	 * @param zone
	 *            the name of the zone.
	 */

	public void drawCirc(String zone) {
		for (int i = 0; i < tzone.length; i++) {
			if (tzone[i].name.equals(zone)) {
				myParent.ellipse(tzone[i].cx, tzone[i].cy, tzone[i].w,
						tzone[i].h);
				break;
			}
		}
	}

	/**
	 * Draw rectangle based on a zone's position and size.
	 * 
	 * @param zone
	 *            the name of the zone.
	 */

	public void drawRect(String zone) {
		for (int i = 0; i < tzone.length; i++) {
			if (tzone[i].name.equals(zone)) {
				myParent.rect(tzone[i].x, tzone[i].y, tzone[i].w, tzone[i].h);
				break;
			}
		}
	}

	/**
	 * Draw image based on a zone's position and size.
	 * 
	 * @param zone
	 *            the name of the zone.
	 * @param image
	 *            the PImage to be drawn.
	 */

	public void drawImage(String zone, PImage image) {
		for (int i = 0; i < tzone.length; i++) {
			if (tzone[i].name.equals(zone)) {
				myParent.image(image, tzone[i].x, tzone[i].y, tzone[i].w,
						tzone[i].h);
				break;
			}
		}
	}

	/**
	 * Determine if the zone is being touched.
	 * 
	 * @param zNameIn
	 *            the name of the zone.
	 * @return a boolean
	 * @example _tzPressed
	 */
	public boolean isZonePressed(String zNameIn) {
		boolean pressed = false;
		for (int i = 0; i < tzone.length; i++) {
			if (tzone[i] != null) {
				if (tzone[i].name.equals(zNameIn) && tzone[i].primePoint > -1) {
					pressed = true;
					break;
				}
			}
		}
		return pressed;
	}

	/**
	 * Determine the toggle state of the zone. Default is false.
	 * 
	 * @param zNameIn
	 *            the name of the zone.
	 * @return a boolean
	 * @example _tzToggle
	 */
	public boolean isZoneToggleOn(String zNameIn) {
		boolean pressed = false;
		for (int i = 0; i < tzone.length; i++) {
			if (tzone[i] != null) {
				if (tzone[i].name.equals(zNameIn) && tzone[i].touches % 2 == 1) {
					pressed = true;
					break;
				}
			}
		}
		return pressed;
	}

	/**
	 * Determine if zone is active.
	 * 
	 * @param zNameIn
	 *            the name of the zone.
	 * @return a boolean
	 */
	public boolean isZoneActive(String zNameIn) {
		boolean active = false;
		for (int i = 0; i < tzone.length; i++) {
			if (tzone[i] != null) {
				if (tzone[i].name.equals(zNameIn)) {
					if (tzone[i].active = true)
						active = true;
					break;
				}
			}
		}
		return active;
	}

	/**
	 * Get coordinates and touch ID of all the touch points (TUIO cursors) on
	 * the screen.
	 * <p>
	 * [i][0] - x coordinate
	 * <p>
	 * [i][1] - y coordinate
	 * <p>
	 * [i][2] - ID (Useful for getTrail method)
	 * 
	 * 
	 * @return double integer array
	 * 
	 */
	public int[][] getPoints() {
		int[][] coord = new int[0][3];
		for (int i = 0; i < tpoint.length; i++) {
			if (tpoint[i] != null) {
				int[] toAppend = {
						tpoint[i].coord[tpoint[i].coord.length - 1][0],
						tpoint[i].coord[tpoint[i].coord.length - 1][1], i };
				coord = (int[][]) PApplet.append(coord, toAppend);
			}
		}
		return coord;
	}

	/**
	 * Get a trail of points left by a touch.
	 * <p>
	 * [i][0] - x coordinate
	 * <p>
	 * [i][1] - y coordinate
	 * 
	 * @param index
	 *            The index of the point trail.
	 * @return a double integer array of coordinates
	 * 
	 */
	public int[][] getTrail(int index) {
		int[][] coord = new int[0][6];
		if (tpoint[index] != null) {
			coord = tpoint[index].coord;
		}
		return coord;
	}

	// /////////////////////////////////////////////////////////////
	// HANDLE TUIO EVENTS///////////////////////////////////////////
	// /////////////////////////////////////////////////////////////

	void oscEvent(OscMessage theOscMessage) {
		// //////////////////////////////////////alive/////////////////////////////////////////////////////////////////////////
		if (theOscMessage.get(0).stringValue().equals("alive")) {
			int sid;
			boolean found;
			// cycle through points
			for (int i = 0; i < tpoint.length; i++) {
				if (tpoint[i] != null) {
					sid = tpoint[i].SID;
					found = false;
					// see if point is alive
					if (!found) {
						// cycle through alive sid's
						for (int j = 1; j < theOscMessage.arguments().length; j++) {
							if (theOscMessage.get(j).intValue() == sid) {
								found = true;
								break;
							}
						}
					}
					// if point is not alive, kill it
					if (!found) {
						tpoint[i] = null;
						touchReleased(i);
						removePrime(i);
					}
				}
			}
		}

		// //////////////////////////////////////modify or add////////////////
		if (theOscMessage.typetag().startsWith("sifffff") == true) {
			int sid = theOscMessage.get(1).intValue();
			int xIn = (int) (myParent.width * theOscMessage.get(2).floatValue());
			int yIn = (int) (myParent.height * theOscMessage.get(3)
					.floatValue());
			int vxIn = (int) (myParent.width * theOscMessage.get(4)
					.floatValue());
			int vyIn = (int) (myParent.height * theOscMessage.get(5)
					.floatValue());
			boolean modified = false;
			int index = -999;
			int zone = 0;
			String zName = "CANVAS";
			for (int i = 0; i < tpoint.length; i++) {
				if (tpoint[i] != null) {
					if (tpoint[i].SID == sid) {
						index = i;
						// calculate velocities until tbeta includes them in the
						// TUIO message
						// if (vxIn == 0 && vyIn == 0) {
						int l = tpoint[i].coord.length - 1;
						int tIn = myParent.millis();
						vxIn = (int) (1000f * (xIn - tpoint[i].coord[l][0]) / (tIn - tpoint[i].coord[l][4]));
						vyIn = (int) (1000f * (yIn - tpoint[i].coord[l][1]) / (tIn - tpoint[i].coord[l][4]));
						break;
						// }
					}
				}
			}
			// check zones **layers matter--last zone created is on top (wins)
			for (int i = 0; i < tzone.length; i++) {
				if (tzone[i] != null) {
					if (tzone[i].rectangle) {
						if (yIn > tzone[i].y && yIn < (tzone[i].y + tzone[i].h)) {
							// is it a horizontal swipe?
							if (index != -999 && tzone[i].hSwipeable) {
								// offset by 0.1 to avoid 0 delta (double swipe)
								float xcenter = tzone[i].cx + 0.1f;
								int lastX = tpoint[index].getLastPoint()[0];
								// event sent if the swipe crosses the vertical
								// centerline of the zone at a velocity of
								// 1 screen width per 1 second or faster
								if ((xIn - xcenter) * (lastX - xcenter) < 0
										&& PApplet.abs(vxIn) * 2 > myParent.width) {
									makeHswipeEvent(tzone[i].name);
								}
							}
							// is it within a zone?
							if (xIn > tzone[i].x
									&& xIn < (tzone[i].x + tzone[i].w)) {
								zName = tzone[i].name;
								zone = i;
							}
						}
						if (xIn > tzone[i].x && xIn < (tzone[i].x + tzone[i].w)) {
							// is it a vertical swipe?
							if (index != -999 && tzone[i].vSwipeable) {
								// offset by 0.1 to avoid 0 delta (double swipe)
								float ycenter = tzone[i].cy + 0.1f;
								int lastY = tpoint[index].getLastPoint()[1];
								// event sent if the swipe crosses the vertical
								// centerline of the zone at a velocity of
								// 1 screen width per 1 second or faster
								if ((yIn - ycenter) * (lastY - ycenter) < 0
										&& PApplet.abs(vyIn) * 2 > myParent.width) {
									makeVswipeEvent(tzone[i].name);
								}
							}
						}
					} else if (tzone[i].circle) {

						// is it within a zone?
						if (PApplet.dist(xIn, yIn, tzone[i].x, tzone[i].y) < tzone[i].r) {
							zName = tzone[i].name;
							zone = i;
						}
					}
				}
			}
			// update known point using sid
			if (index != -999) {
				tpoint[index].addPoint(sid, xIn, yIn, vxIn, vyIn, zone, index);
				modified = true;
			}
			// create new touch point if not found
			if (!modified) {
				for (int i = 0; i < tpoint.length; i++) {
					if (tpoint[i] == null) {
						tpoint[i] = new TouchPoint(sid, xIn, yIn, zone, zName,
								i);
						modified = true;
						break;
					}
				}
			}
			if (!modified) { // if array is full
				tpoint = (TouchPoint[]) PApplet.append(tpoint, new TouchPoint(
						sid, xIn, yIn, zone, zName, tpoint.length));// + or
				// -1???
			}
		}
	}

	// /////////////////////////////////////////////////////////////
	// PROCESS FUNCTIONS////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////
	void removePrime(int tpointIn) {
		for (int i = 0; i < tzone.length; i++) {
			if (tzone[i] != null) {
				if (tzone[i].primePoint == tpointIn) {
					removePrimeFromZone(i);
					//if (tpoint[tzone[i].secondPoint] != null)//????????????
					//	tpoint[tzone[i].secondPoint] = null;//????????????
				}
				//if (tzone[i].secondPoint == tpointIn) {
				//	removePrimeFromZone(i);
				//	if (tpoint[tzone[i].primePoint] != null)//????????????
				//		tpoint[tzone[i].primePoint] = null;//????????????
				//}
			}
		}
	}

	void removePrimeFromZone(int i) {
		tzone[i].primePoint = -999;
		tzone[i].secondPoint = -999;
		tzone[i].releaseTime = myParent.millis();
		tzone[i].gestureScale = 1.0f;// reset Scale
		tzone[i].gestureRotation = 0.0f; // reset rotation
		tzone[i].gestureXtranslation = 0; // reset translation
		tzone[i].gestureYtranslation = 0; // reset translation
		tzone[i].scli = tzone[i].scl;// set intermediate memory
		tzone[i].sclxi = tzone[i].sclx;// set intermediate memory
		tzone[i].sclyi = tzone[i].scly;// set intermediate memory
		tzone[i].wi = tzone[i].w;// set intermediate memory
		tzone[i].hi = tzone[i].h;// set intermediate memory
		tzone[i].xi = tzone[i].x;// set intermediate memory
		tzone[i].yi = tzone[i].y;// set intermediate memory
		tzone[i].mP = 0;// reset 3drotatable reference
		for (int j = 0; j < tzone.length; j++) {
			// set intermediates for children
			if (tzone[j].parent != null) {
				if (tzone[j].parent.equals(tzone[i].name)) {
					tzone[j].xi = tzone[j].x;// set intermediate memory
					tzone[j].yi = tzone[j].y;// set intermediate memory
					tzone[j].wi = tzone[j].w;// set intermediate memory
					tzone[j].hi = tzone[j].h;// set intermediate memory
					tzone[j].scli = tzone[j].scl;// set intermediate memory
					tzone[j].mP = 0;// reset 3drotatable reference
				}
			}
			// set intermediates for group members
			else if (tzone[j].group != null) {
				if (tzone[j].group.equals(tzone[i].group) && i != j) {
					tzone[j].xi = tzone[j].x;// set intermediate memory
					tzone[j].yi = tzone[j].y;// set intermediate memory
					tzone[j].wi = tzone[j].w;// set intermediate memory
					tzone[j].hi = tzone[j].h;// set intermediate memory
					tzone[j].scli = tzone[j].scl;// set intermediate memory
					tzone[j].mP = 0;// reset 3drotatable reference
				}
			}
		}
	}

	void modifyZone(int xIn, int yIn, int vxIn, int vyIn, int tpointIn, int zone) {
		int[] touches = new int[0];
		// COUNT TOUCHES
		for (int i = 0; i < tpoint.length; i++) {
			if (tpoint[i] != null) {
				if (tpoint[i].zone0 == zone) {
					touches = (int[]) PApplet.append(touches, i);
				}
			}
		}
		// IF DRAGGABLE (single touch action)
		int xn = tzone[zone].xi + (xIn - tpoint[tpointIn].coord[0][0]);
		int yn = tzone[zone].yi + (yIn - tpoint[tpointIn].coord[0][1]);
		if (xn < tzone[zone].xDragLow + tzone[zone].x0)
			xn = tzone[zone].xDragLow + tzone[zone].x0;
		if (xn > tzone[zone].xDragHi + tzone[zone].x0)
			xn = tzone[zone].xDragHi + tzone[zone].x0;
		if (yn < tzone[zone].yDragLow + tzone[zone].y0)
			yn = tzone[zone].yDragLow + tzone[zone].y0;
		if (yn > tzone[zone].yDragHi + tzone[zone].y0)
			yn = tzone[zone].yDragHi + tzone[zone].y0;
		if (tzone[zone].primePoint == tpointIn && tzone[zone].draggable
				&& touches.length == 1 && tpoint[tpointIn].scalar == false) {//
			setZoneData(zone, xn, yn, tzone[zone].w, tzone[zone].h);
			tzone[zone].vx = (tzone[zone].vx + vxIn) / 2;
			tzone[zone].vy = (tzone[zone].vy + vyIn) / 2;
		}
		if (tzone[zone].primePoint == tpointIn && tzone[zone].xdraggable
				&& touches.length == 1 && tpoint[tpointIn].scalar == false) {//
			setZoneData(zone, xn, tzone[zone].yi, tzone[zone].w, tzone[zone].h);
			tzone[zone].vx = (tzone[zone].vx + vxIn) / 2;
			tzone[zone].vy = 0;
		}
		if (tzone[zone].primePoint == tpointIn && tzone[zone].ydraggable
				&& touches.length == 1 && tpoint[tpointIn].scalar == false) {//
			setZoneData(zone, tzone[zone].xi, yn, tzone[zone].w, tzone[zone].h);
			tzone[zone].vx = 0;
			tzone[zone].vy = (tzone[zone].vy + vyIn) / 2;
		}
		// IF window3d (single touch action)
		if (tzone[zone].primePoint == tpointIn && tzone[zone].window3d
				&& touches.length == 1 && tpoint[tpointIn].scalar == false) {
			if (tzone[zone].mP == 0) {
				tzone[zone].mP = 1;
				tzone[zone].copyMatrix();
			}
			int mxi = tpoint[tpointIn].coord[0][0];
			int myi = tpoint[tpointIn].coord[0][1];
			tzone[zone].d = PApplet.sqrt(PApplet.pow(xIn - mxi, 2)
					+ PApplet.pow(yIn - myi, 2));
			tzone[zone].rX = -(float) (yIn - myi) / (tzone[zone].d + 0.001f);
			tzone[zone].rY = (float) (xIn - mxi) / (tzone[zone].d + 0.001f);
			tzone[zone].theta = (tzone[zone].d / tzone[zone].h * PApplet.PI);
			tzone[zone].makeNewMatrix();
			tzone[zone].makeFinMatrix();
		}

		// Two-finger gesture
		if (touches.length == 2) {
			for (int i = 0; i < tpoint.length; i++) {
				if (tpoint[i] != null && tpointIn != i) {
					if (tpoint[i].zone0 == zone) {
						if (tzone[zone].primePoint == i)
							tzone[zone].secondPoint = tpointIn;
						else
							tzone[zone].secondPoint = i;
						tpoint[i].scalar = true;// avoid jump after scaling
						tpoint[tpointIn].scalar = true;
						int[] iLast = tpoint[i].getLastPoint();
						int[] tpointInFirst = tpoint[tpointIn].getFirstPoint();
						int[] iFirst = tpoint[i].getFirstPoint();
						float sclx = (float) (xIn - iLast[0])
								/ (tpointInFirst[0] - iFirst[0]);
						float scly = (float) (yIn - iLast[1])
								/ (tpointInFirst[1] - iFirst[1]);
						float scl = PApplet.dist(iLast[0], iLast[1], xIn, yIn)
								/ PApplet.dist(iFirst[0], iFirst[1],
										tpointInFirst[0], tpointInFirst[1]);
						scl = 1.0f - tzone[zone].sclSens + tzone[zone].sclSens
								* scl;// apply sensitivity
						sclx = 1.0f - tzone[zone].sclSens + tzone[zone].sclSens
								* sclx;// apply sensitivity
						scly = 1.0f - tzone[zone].sclSens + tzone[zone].sclSens
								* scly;// apply sensitivity
						tzone[zone].scly = scly * tzone[zone].sclyi;
						tzone[zone].sclx = sclx * tzone[zone].sclxi;
						tzone[zone].scl = scl * tzone[zone].scli;// useful for
						// more than
						// scalable
						tzone[zone].gestureScale = scl;
						// gestureRotation
						int rx0 = tpointInFirst[0] - iFirst[0];
						int ry0 = tpointInFirst[1] - iFirst[1];
						float phase = 0;
						if (rx0 < 0)
							phase = phase + PI;
						int rxf = xIn - iLast[0];
						int ryf = yIn - iLast[1];
						if (rxf < 0)
							phase = phase - PI;
						float rotation = phase
								+ (PApplet.atan((float) -ryf / rxf))
								- (PApplet.atan((float) -ry0 / rx0));
						tzone[zone].gestureRotation = -2 * rotation
								/ PApplet.PI;

						// gestureTranslation - 2 fingers
						tzone[zone].gestureXtranslation = (iLast[0] - iFirst[0]
								+ xIn - tpointInFirst[0]) / 2;
						tzone[zone].gestureYtranslation = (iLast[1] - iFirst[1]
								+ yIn - tpointInFirst[1]) / 2;
						// limit scale gesture
						if (tzone[zone].scl < tzone[zone].sclLow) {
							tzone[zone].scl = tzone[zone].sclLow;
							scl = tzone[zone].scl / tzone[zone].scli;
						}
						if (tzone[zone].scl > tzone[zone].sclHigh) {
							tzone[zone].scl = tzone[zone].sclHigh;
							scl = tzone[zone].scl / tzone[zone].scli;
						}
						if (tzone[zone].sclx < tzone[zone].sclLow) {
							tzone[zone].sclx = tzone[zone].sclLow;
							sclx = tzone[zone].sclx / tzone[zone].sclxi;
						}
						if (tzone[zone].sclx > tzone[zone].sclHigh) {
							tzone[zone].sclx = tzone[zone].sclHigh;
							sclx = tzone[zone].sclx / tzone[zone].sclxi;
						}
						if (tzone[zone].scly < tzone[zone].sclLow) {
							tzone[zone].scly = tzone[zone].sclLow;
							scly = tzone[zone].scly / tzone[zone].sclyi;
						}
						if (tzone[zone].scly > tzone[zone].sclHigh) {
							tzone[zone].scly = tzone[zone].sclHigh;
							scly = tzone[zone].scly / tzone[zone].sclyi;
						}
						// estimate 'center of mass' of scale gesture
						int midx = (tpointInFirst[0] + iFirst[0]) / 2;
						int midy = (tpointInFirst[1] + iFirst[1]) / 2;
						if (tzone[zone].scalable) {// scale zone
							int newX = tzone[zone].gestureXtranslation + midx
									- (int) (scl * (midx - tzone[zone].xi));
							int newY = tzone[zone].gestureYtranslation + midy
									- (int) (scl * (midy - tzone[zone].yi));
							int newW = (int) (tzone[zone].wi * scl);
							int newH = (int) (tzone[zone].hi * scl);
							// modify zones
							setZoneData(zone, newX, newY, newW, newH);
						}
						if (tzone[zone].xscalable || tzone[zone].yscalable) {
							int newX = tzone[zone].xi;
							int newY = tzone[zone].yi;
							int newW = tzone[zone].wi;
							int newH = tzone[zone].hi;
							if (tzone[zone].xscalable) {// scale zone
								newX = tzone[zone].gestureXtranslation
										+ midx
										- (int) (sclx * (midx - tzone[zone].xi));
								newW = (int) (tzone[zone].wi * sclx);
							}
							if (tzone[zone].yscalable) {// scale zone
								newY = tzone[zone].gestureYtranslation
										+ midy
										- (int) (scly * (midy - tzone[zone].yi));
								newH = (int) (tzone[zone].hi * scly);

							}
							// modify zones
							setZoneData(zone, newX, newY, newW, newH);
						}
						break;
					}
				}
			}
		}
		// if ROTATABLE --- under construction
		if (tzone[zone].rotatable && touches.length == 3) {
			tpoint[touches[0]].scalar = true;// avoid jump after scaling
			tpoint[touches[1]].scalar = true;
			tpoint[touches[2]].scalar = true;
			int[] iFirst = tpoint[touches[0]].getFirstPoint();
			int[] iLast = tpoint[touches[0]].getLastPoint();
			int[] jFirst = tpoint[touches[1]].getFirstPoint();
			int[] jLast = tpoint[touches[1]].getLastPoint();
			int[] kFirst = tpoint[touches[2]].getFirstPoint();
			int[] kLast = tpoint[touches[2]].getLastPoint();
			PApplet.println(kLast[1]);
			myParent.pushMatrix();
			myParent.translate(-kLast[0], -kLast[1]);
			PApplet.println("here" + myParent.screenX(0, 0));
			myParent.popMatrix();

		}
		if (tzone[zone].circle) {
			tzone[zone].cx = tzone[zone].x;
			tzone[zone].cy = tzone[zone].y;
		}
		if (tzone[zone].rectangle) {
			tzone[zone].cx = tzone[zone].x + tzone[zone].w / 2;
			tzone[zone].cy = tzone[zone].y + tzone[zone].h / 2;
		}
	}

	// Give a zone a primary point
	void markZone(int tpointIn, int zone) {
		if (tzone[zone].primePoint == -999) {
			tzone[zone].primePoint = tpointIn;
			tzone[zone].touches++;
			clicked(tzone[zone].name);
		}
	}

	// called by the processing applet at the beginning of each draw
	public void pre() {
		processZoneInertias();
	}

	void processZoneInertias() { // throwable objects
		for (int i = 0; i < tzone.length; i++) {
			if (tzone[i] != null) {
				if (tzone[i].throwable && tzone[i].primePoint == -999
						&& (tzone[i].vx != 0 || tzone[i].vy != 0)) {
					float vratio = (float) tzone[i].vy / tzone[i].vx;
					if (tzone[i].vx > myParent.width) {
						tzone[i].vx = myParent.width;
						tzone[i].vy = (int) (vratio * myParent.width);
					}// governor
					if (tzone[i].vy > myParent.width) {
						tzone[i].vy = myParent.width;
						tzone[i].vx = (int) (myParent.width / vratio);
					}
					float ax = -5000, ay = -5000;// deceleration
					if (tzone[i].vy == 0) {
						ay = 0;
					} else {
						float ratio = (float) tzone[i].vx / tzone[i].vy;
						ay = -PApplet.sqrt(ax * ax / ((ratio * ratio) + 1))
								* PApplet.abs(tzone[i].vy) / tzone[i].vy;
						ax = ratio * ay;
					}
					float t = (myParent.millis() - tzone[i].releaseTime) / 1000f;
					if (PApplet.abs(ax * t) >= PApplet.abs(tzone[i].vx)
							|| PApplet.abs(ay * t) >= PApplet.abs(tzone[i].vy)
							|| tzone[i].cx < 0 || tzone[i].cx > myParent.width
							|| tzone[i].cy < 0 || tzone[i].cy > myParent.height) {// finish
						// movement
						ax = tzone[i].vx = 0;
						tzone[i].xi = tzone[i].x;
						ay = tzone[i].vy = 0;
						tzone[i].yi = tzone[i].y;
					} else {
						tzone[i].x = (int) (tzone[i].xi + tzone[i].vx * t + 0.5f
								* ax * PApplet.pow(t, 2));
						tzone[i].y = (int) (tzone[i].yi + tzone[i].vy * t + 0.5f
								* ay * PApplet.pow(t, 2));
					}
					if (tzone[i].circle) {
						tzone[i].cx = tzone[i].x;
						tzone[i].cy = tzone[i].y;
					}
					if (tzone[i].rectangle) {
						tzone[i].cx = tzone[i].x + tzone[i].w / 2;
						tzone[i].cy = tzone[i].y + tzone[i].h / 2;
					}

				}
			}
		}
	}

	// /////////////////////////////////////////////////////////////
	// HELPER METHODS//////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////

	int getZoneIndex(String zNameIn) {
		int index = -999;
		for (int i = 0; i < tzone.length; i++) {
			if (tzone[i] != null) {
				if (tzone[i].name.equals(zNameIn)) {
					index = i;
					break;
				}
			}
		}
		return index;
	}

	// /////////////////////////////////////////////////////////////
	// TOUCH POINT CLASS//////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////
	class TouchPoint {
		int coord[][];
		int SID, zone, zone0, touchID;
		String zName, zName0;
		boolean scalar = false;

		TouchPoint(int SidIn, int xIn, int yIn, int zoneIn, String zNameIn,
				int tpointIn) {
			coord = new int[1][6];
			coord[0][0] = xIn;// x coordinate
			coord[0][1] = yIn;// y coordinate
			coord[0][2] = 0;// x velocity
			coord[0][3] = 0;// y velocity
			coord[0][4] = myParent.millis();// time created
			coord[0][5] = zoneIn;// zone containing this point
			SID = SidIn;// SID from TUIO
			zName = zName0 = zNameIn;// zone name
			zone = zone0 = zoneIn;// zone array index
			touchID = myParent.millis() + SID; // TODO this should go away.
			// transform zones
			markZone(tpointIn, zone);// try to take ownership of a zone

		}

		// create a trail of points (history) for this touch point
		void addPoint(int SidIn, int xIn, int yIn, int vxIn, int vyIn,
				int zoneIn, int tpointIn) {
			int tIn = myParent.millis();
			int[] toAppend = { xIn, yIn, vxIn, vyIn, tIn, zoneIn };
			coord = (int[][]) PApplet.append(coord, toAppend);
			SID = SidIn;
			zone = zoneIn;
			modifyZone(xIn, yIn, vxIn, vyIn, tpointIn, zone0);
		}

		// get the last point in trail - return x,y coordinates and velocities
		int[] getLastPoint() {
			int[] xy = { coord[coord.length - 1][0],
					coord[coord.length - 1][1], coord[coord.length - 1][2],
					coord[coord.length - 1][3] };
			return xy;
		}

		// get the first point in trail
		int[] getFirstPoint() {
			int[] xy = { coord[0][0], coord[0][1], coord[0][2], coord[0][3] };
			return xy;
		}
	}

	// /////////////////////////////////////////////////////////////
	// TOUCH ZONE CLASS//////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////
	class TouchZone {
		int x, y, x0, y0, xi, yi, r, h, w, r0, h0, w0, ri, hi, wi, vx, vy, cx,
				cy, primePoint = -999, secondPoint = -999, friction,
				touches = 0, releaseTime = 0, gestureXtranslation = 0,
				gestureYtranslation, xDragLow = -9999, xDragHi = 9999,
				yDragLow = -9999, yDragHi = 9999;
		boolean rectangle, circle, gravity = false, draggable = false,
				xdraggable = false, ydraggable = false, scalable = false,
				hSwipeable = false, vSwipeable = false, throwable = false,
				rotatable = false, window3d = false, active = true,
				xscalable = false, yscalable = false;
		String name;
		String parent = null;// parent Zone for attachment
		String group = null;

		float scl = 1.0f, scli = 1.0f, sclx = 1.0f, scly = 1.0f, sclxi = 1.0f,
				sclyi = 1.0f, sclLow = 0.0f, sclHigh = 9999.0f, sclSens = 1.0f,
				gestureScale = 1.0f, gestureRotation = 0.0f;

		// 3d rotation variables
		int mP = 0, mxi = 0, myi = 0;
		float rX = 0, rY = 0, d = 0;
		float mInit[][] = new float[3][3];
		float mFin[][] = new float[3][3];
		float mNew[][] = new float[3][3];
		float mIdent[][] = new float[3][3];
		float theta;

		TouchZone(String zNameIn, int xIn, int yIn, int rIn) {
			name = zNameIn;
			// variables followed by 0 are the original values
			// variables followed by i are intermediate values for a historical
			// reference
			x0 = xi = x = xIn;// center of zone x
			y0 = yi = y = yIn;// center of zone y
			r0 = ri = r = rIn;// radius of circle boundary
			w0 = wi = w = 2 * rIn;// width of the square the circle inscribes
			h0 = hi = h = 2 * rIn;// height of the square the circle inscribes
			vx = vy = 0;// velocities
			cx = x;// centerline x
			cy = y;// centerline y
			circle = true;
			rectangle = false;
			makeIdentity();
			copyMatrix();
		}

		TouchZone(String zNameIn, int xIn, int yIn, int wIn, int hIn) {
			name = zNameIn;
			x0 = xi = x = xIn;// upper left corner x
			y0 = yi = y = yIn;// upper left corner y
			w0 = wi = w = wIn;// width
			h0 = hi = h = hIn;// width
			r0 = ri = r = (int) PApplet.sqrt(PApplet.pow(hIn, 2)
					+ PApplet.pow(wIn, 2)) / 2;
			vx = vy = 0;// velocities
			cx = x + w / 2;
			cy = y + h / 2;
			circle = false;
			rectangle = true;
			makeIdentity();
			copyMatrix();
		}

		void apply3dMatrix() {
			myParent.translate(cx, cy, 0);
			myParent.applyMatrix(mFin[0][0], mFin[0][1], mFin[0][2], 0,
					mFin[1][0], mFin[1][1], mFin[1][2], 0, mFin[2][0],
					mFin[2][1], mFin[2][2], 0, 0, 0, 0, 1);

		}

		float[] get3dMatrix() {
			float[] matrix = { mFin[0][0], mFin[0][1], mFin[0][2], 0f,
					mFin[1][0], mFin[1][1], mFin[1][2], 0f, mFin[2][0],
					mFin[2][1], mFin[2][2], 0f, 0f, 0f, 0f, 1 };
			return matrix;

		}

		void makeIdentity() { // for 3-d rotation
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					if (i == j) {
						mFin[i][j] = 1f;
						mIdent[i][j] = 1f;
					} else {
						mFin[i][j] = 0f;
						mIdent[i][j] = 0f;
					}
				}
			}
		}

		void makeNewMatrix() {// need to simplify, do not need to multiply by
			// Identity
			for (int i = 0; i < 3; i++) {
				mNew[i][0] = mIdent[i][0]
						* (PApplet.cos(theta) + (1f - PApplet.cos(theta)) * rX
								* rX) + mIdent[i][1]
						* ((1f - PApplet.cos(theta)) * rY * rX) + mIdent[i][2]
						* (-PApplet.sin(theta) * rY);
				mNew[i][1] = mIdent[i][0]
						* ((1f - PApplet.cos(theta)) * rY * rX)
						+ mIdent[i][1]
						* (PApplet.cos(theta) + (1f - PApplet.cos(theta)) * rY
								* rY) + mIdent[i][2] * PApplet.sin(theta) * rX;
				mNew[i][2] = mIdent[i][0] * PApplet.sin(theta) * rY
						+ mIdent[i][1] * (-PApplet.sin(theta) * rX)
						+ mIdent[i][2] * PApplet.cos(theta);
			}
		}

		void makeFinMatrix() {
			for (int i = 0; i < 3; i++) {
				mFin[i][0] = mNew[i][0] * mInit[0][0] + mNew[i][1]
						* mInit[1][0] + mNew[i][2] * mInit[2][0];
				mFin[i][1] = mNew[i][0] * mInit[0][1] + mNew[i][1]
						* mInit[1][1] + mNew[i][2] * mInit[2][1];
				mFin[i][2] = mNew[i][0] * mInit[0][2] + mNew[i][1]
						* mInit[1][2] + mNew[i][2] * mInit[2][2];
			}
		}

		void copyMatrix() {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					mInit[i][j] = mFin[i][j];
				}
			}
		}

	}

}
