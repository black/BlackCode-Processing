package hermes.hshape;

import hermes.Hermes;
import hermes.HermesMath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import processing.core.PApplet;
import processing.core.PVector;
import static hermes.HermesMath.*;

/**
 * Represents an arbitrary convex polygon.
 * <p>
 * Position represents 'center.'
 * <p>
 * Vertex Points are positioned relative center.
 * Each point is assumed to be next to points before and after it in list.
 * Make sure your List of points is ordered correctly!
 * <p>
 * A HPolygon must also be convex,
 * concave polygons will break collision detection.
 *
 */
public class HPolygon extends HShape {
	
	//Stores unit vectors representing direction of axes normal to edges of polygon
	//Used for collision detection (SAT)
	private ArrayList<PVector> _axes;
	//Stores the vertex points defining the polygon
	private ArrayList<PVector> _points;
	
	/**
	 * Creates a new HPolygon.
	 * <p>List of vertex points must be ordered such that
	 * each point is connected to points before and after it in list.
	 * @param position - Reference to Shape's position
	 * @param points - List of vertex points defined relative to position, must be ordered
	 */
	public HPolygon(PVector position, ArrayList<PVector> points) {
		super(position);
		
		assert points != null : "In HPolygon constructor, points must be valid List";
		assert points.size() > 2 : "In HPolygon constructor, points must contain at least three point";
		
		_points = points;
		
		//Create the list of lines in the polygon
		_axes = new ArrayList<PVector>();
		Iterator<PVector> pit = _points.iterator();
		PVector first = pit.next();
		PVector pre2 = first;
		PVector second = pit.next();
		PVector pre = second;
		while(pit.hasNext()) {
			PVector p = pit.next();
			addAxis(p, pre, pre2);
			pre2 = pre;
			pre = p;
		}

		//Make the final lines between the first and the last point and first and second points
		addAxis(first, pre, second);
		addAxis(second,first,pre);
	}
	
	/**
	 * Creates a new HPolygon.
	 * <p>Takes a variable number of PVectors (must give at least 3) for vertices.
	 * <p>Vertex points must be given in order such that
	 * each point is connected to points given before and after it.
	 * @param position - Reference to Shape's position
	 * @param points - the PVectors defining the verticies of the polygon
	 */
	public HPolygon(PVector position, PVector... points) {
		super(position);
		
		assert points != null : "In HPolygon constructor, points must be valid PVectors";
		assert points.length > 2 : "In HPolygon constructor, points must contain at least three point";
		
		_points = (ArrayList<PVector>) Arrays.asList(points);
		
		//Create the list of lines in the polygon
		_axes = new ArrayList<PVector>();
		Iterator<PVector> pit = _points.iterator();
		PVector first = pit.next();
		PVector pre2 = first;
		PVector second = pit.next();
		PVector pre = second;
		while(pit.hasNext()) {
			PVector p = pit.next();
			addAxis(p, pre, pre2);
			pre2 = pre;
			pre = p;
		}

		//Make the final lines between the first and the last point and first and second points
		addAxis(first, pre, second);
		addAxis(second,first,pre);
	}
	
	/**
	 * Given two points, finds the 'axis' normal to the line between them
	 * and adds it to an internal list.
	 * @param start
	 * @param end
	 * @param preStart - the extra point used to correctly orient axis
	 */
	private void addAxis(PVector start, PVector end, PVector preStart) {
		PVector axis = PVector.sub(start, end);
		axis.normalize();
		axis = HermesMath.rotate(axis,Math.PI/2);
		float project1 = axis.dot(start);
		float projectpre = axis.dot(preStart);
		assert project1 != projectpre : "HPolygon must be convex!";
		if(project1 < projectpre) {
			reverse(axis);
		}
		_axes.add(axis);
	}
	
	/**
	 * @return List of vertex points.
	 */
	public ArrayList<PVector> getPoints() {
		return _points;
	}
	
	/**
	 * @return Copy of list of vertex points.
	 */
	public ArrayList<PVector> getPointsCopy() {
		ArrayList<PVector> copy = new ArrayList<PVector>();
		for(PVector p : _points) {
			copy.add(new PVector(p.x, p.y));
		}
		return copy;
	}
	
	/**
	 * Adds a point to the polygon.
	 * <p>
	 * Point is assumed to be connected to the last point added
	 * and the first point added.
	 * @param point - point to be added
	 */
	public void addPoint(PVector point) {
		//Remove the axis for the edge between the current first and last points
		_axes.remove(_axes.size());
		
		PVector first = _points.get(0);
		PVector last = _points.get(_points.size()-1);
		
		addAxis(first, point, last);
		addAxis(point, last, first);
		_points.add(point);
	}
	
	/**
	 * Getter for axes list - only for internal use within shape classes
	 * @return Axes list, do not modify contents!
	 */
	protected ArrayList<PVector> getAxes() {
		return _axes;
	}
	
	/**
	 * @return Copy of axes list.
	 */
	public ArrayList<PVector> getAxesCopy() {
		ArrayList<PVector> copy = new ArrayList<PVector>();
		for(PVector p : _axes) {
			copy.add(new PVector(p.x, p.y));
		}
		return copy;
	}
	
	/**
	 * Rotates polygon counter-clockwise around polygon's position
	 * ((0,0) in polygon coordinates).
	 * @param theta		Angle to rotate by
	 */
	public void rotate(double theta) {
		for(PVector p : _points) {
			HermesMath.rotate(p,theta);
		}
		for(PVector p : _axes) {
			HermesMath.rotate(p,theta);
		}
	}
	
	/**
	 * Rotates polygon counter-clockwise around given position in polygon coordinates
	 * ((0,0) is polygon's position).
	 * @param pivotLoc	The point to rotate polygon around
	 * @param theta		Angle to rotate by
	 */
	public void rotate(PVector pivotLoc, double theta) {
		for(PVector p : _points) {
			//translate points into coordinates where given position is now (0,0)
			PVector translatedP = PVector.sub(p, pivotLoc);
			HermesMath.rotate(translatedP, theta);
			//translate back
			p.add(pivotLoc);
		}
		for(PVector p : _axes) {
			HermesMath.rotate(p,theta);
		}
	}
	
//	/**
//	 * Rotates polygon counter-clockwise around given position in world coordinates
//	 */
//	public void rotateInWorld(PVector position, double theta) {
//		//map given world location into polygon coordinates
//		PVector polyLoc = PVector.sub(_position, position);
//		rotate(polyLoc,theta);
//	}
	
	@Override
	public boolean collide(HShape other) {
		assert other != null : "HPolygon.collide: other must be a valid Shape";
		return other.projectionVector(this) != null;
	}
	
	public boolean collide(HRectangle other) {
		return projectionVector(other) != null;
	}
	public boolean collide(HCircle other) {
		return projectionVector(other) != null;
	}
	public boolean collide(HPolygon other) {
		return projectionVector(other) != null;
	}
	
	@Override
	public PVector projectionVector(HShape other) {
		assert other != null : "HPolygon.projectionVector: other must be a valid Shape";
		PVector opposite = other.projectionVector(this);
		return opposite == null ? null : reverse(opposite);
	}

	@Override
	public PVector projectionVector(HRectangle other) {
		/*//Get distance between shapes
		PVector dist = PVector.sub(_position, other.getPosition());
		//Set up variables for keeping track of smallest resolution
		PVector resolution = null;
		float resolutionSize = Float.MAX_VALUE;
		
		for(PVector axis: _axes) {
			PVector result = checkSepAxis(axis, dist, other);
			if(result == null) {
			    return null;
			} else { //Determine if result is smaller than current min resolution
			    float temp = mag2(result);
			    if(temp < resolutionSize) {
		            resolution = result;
		            resolutionSize = temp;
                }
			}
		}
		
		PVector xAxis = new PVector(1,0,0);
		PVector projectX = getProjection(xAxis, this);
		projectX.add(dist.x, dist.x, 0);
		float right = projectX.x - other.getMax().x;
		float left = other.getMin().x - projectX.y;
		if(right > 0 ||  left > 0) {
			//Found a separating axis! Not colliding.
			return null;
		} else {
			PVector result = (right < left ?
							PVector.mult(xAxis, -left):
							PVector.mult(xAxis, right));
			float temp = mag2(result);
			if(temp < resolutionSize) {
				resolution = result;
	            resolutionSize = temp;
			}
		}
		
		PVector yAxis = new PVector(0,1,0);
		PVector projectY = getProjection(yAxis, this);
		projectY.add(dist.x, dist.x, 0);
		float top = projectY.x - other.getMax().y;
		float bottom = other.getMin().y - projectY.y;
		if(top > 0 ||  bottom > 0) {
			//Found a separating axis! Not colliding.
			return null;
		} else {
			PVector result = (top < bottom ?
					PVector.mult(xAxis, -bottom):
					PVector.mult(xAxis, top));
			float temp = mag2(result);
			if(temp < resolutionSize) {
				resolution = result;
				resolutionSize = temp;
			}
		}
		
		return resolution;*/
		
		//Turn HRectangle into a HPolygon
		PVector otherPos = other.getPosition();
		PVector min = other.getMin();
		PVector max = other.getMax();
		PVector v2 = new PVector(min.x, max.y);
		PVector v4 = new PVector(max.x, min.y);
		ArrayList<PVector> points = new ArrayList<PVector>();
		points.add(min);
		points.add(v2);
		points.add(max);
		points.add(v4);	
		HPolygon rect = new HPolygon(otherPos, points);
		
		return projectionVector(rect);
	}
	
	@Override
	public PVector projectionVector(HCircle other) {
		//Get distance between shapes
		PVector dist = PVector.sub(_position, other.getPosition());
		//Set up variables for keeping track of smallest resolution
        PVector resolution = null;
        float resolutionSize = Float.MAX_VALUE;
		
		PVector center = other.getCenter();
		float radius = other.getRadius();
		
		//Check for collision along all axes in this polygon
		for(PVector axis : _axes) {
			PVector result = checkSepAxis(axis, dist, center, radius);
			if(result == null) {
			    return null;
			} else { //Determine if result is smaller than current min resolution
			    float temp = mag2(result);
			    if(temp < resolutionSize) {
		            resolution = result;
		            resolutionSize = temp;
                }
			}
		}
		
		//Check for collisions along axes between circle center and vertices
		for(PVector p : _points) {
			PVector axis = PVector.sub(center, PVector.add(p, dist));
			axis.normalize();
			PVector result = checkSepAxis(axis, dist, center, radius);
			if(result == null) {
			    return null;
			} else { //Determine if result is smaller than current min resolution
			    float temp = mag2(result);
			    if(temp < resolutionSize) {
		            resolution = result;
		            resolutionSize = temp;
                }
			}
		}
		
		return resolution;
	}
	
	@Override
	public PVector projectionVector(HPolygon other) {
		//Get distance between polygons
		PVector dist = PVector.sub(_position, other.getPosition());
		//Set up variables for keeping track of smallest resolution
        PVector resolution = null;
        float resolutionSize = Float.MAX_VALUE;
		
		//Check for collision along all axes in this polygon
		for(PVector axis : _axes) {
			PVector result = checkSepAxis(axis, dist, other);
			if(result == null) {
			    return null;
			} else { //Determine if result is smaller than current min resolution
			    float temp = mag2(result);
			    if(temp < resolutionSize) {
		            resolution = result;
		            resolutionSize = temp;
                }
			}
		}
		
		//Check for collision along all axes in other polygon
		ArrayList<PVector> axes = other.getAxes();
		for(PVector axis : axes) {
			PVector result = checkSepAxis(axis, dist, other);
			if(result == null) {
			    return null;
			} else { //Determine if result is smaller than current min resolution
			    float temp = mag2(result);
			    if(temp < resolutionSize) {
		            resolution = result;
		            resolutionSize = temp;
                }
			}
		}
		
		return resolution;
	}

	/*private PVector checkSepAxis(PVector axis, PVector dist, HRectangle other) {
		
	}*/
	
	/**
	 * Checks for collision between a polygon and a circle along a certain axis
	 * @param axis - axis to check along
	 * @param dist - distance between shapes, vector points from other to this
	 * @param center - center of circle
	 * @param radius - radius of circle
	 * @return
	 */
	private PVector checkSepAxis(PVector axis, PVector dist, PVector center, float radius) {
		PVector project1 = getProjection(axis, this);
		PVector project2 = getProjection(axis, center, radius);
		
		//Offset projection of this away from other
		float offset = PVector.dot(dist,axis);
		project1.add(offset, offset, 0);
		
		//Check if they are separated along axis
		float top = project1.x - project2.y;
		float bottom = project2.x - project1.y;
		if(top > 0 ||  bottom > 0) {
			//Found a separating axis! Not colliding.
			return null;
		}
		
		else {
			return (top < bottom ?
					PVector.mult(axis, -bottom):
					PVector.mult(axis, top));
		}
	}
	
	/**
	 * Checks if this polygon and other polygon collide along given axis
	 * @param axis - axis to check projections on
	 * @param dist - distance between polygons
	 * @param other - the other polygon
	 * @return PVector - the "projection vector" of the two shapes along specific axis if colliding, null otherwise 
	 */
	private PVector checkSepAxis(PVector axis, PVector dist, HPolygon other) {
		PVector project1 = getProjection(axis, this);
		PVector project2 = getProjection(axis, other);
		
		//Offset projection of this away from other
		float offset = PVector.dot(dist,axis);
		project1.add(offset, offset, 0);
		
		//Check if they are separated along axis
		float top = project1.x - project2.y;
		float bottom = project2.x - project1.y;
		if(top > 0 ||  bottom > 0) {
			//Found a separating axis! Not colliding.
			return null;
		} else {
			return (top < bottom ?
					PVector.mult(axis, -bottom):
					PVector.mult(axis, top));
		}
	}
	
	/**
	 * Projects polygon onto given axis
	 * @param axis
	 * @param poly
	 * @return PVector with min as x, max as y 
	 */
	private PVector getProjection(PVector axis, HPolygon poly) {
		float min;
		float max;
		
		Iterator<PVector> points = poly.getPoints().iterator();
		PVector pInit = points.next();
		min = pInit.dot(axis);
		max = min;
		
		while(points.hasNext()) {
			PVector p = points.next();
			float project = p.dot(axis);
			if(project < min) min = project;
			if(max < project) max = project;
		}
		
		return new PVector(min,max);
	}
	
	/**
	 * Projects circle onto given axis
	 * @param axis
	 * @param center
	 * @param radius
	 * @return PVector with min as x, max as y
	 */
	private PVector getProjection(PVector axis, PVector center, float radius) {
		float project = center.dot(axis);
		return new PVector(project - radius, project + radius);
	}

	/*
	 * NO LONGER USED but still works
	 * Checks if circle is in a voronoi region of polygon side specified by pre, linePre, and line
	 * @param circlePos - position of circle
	 * @param pre - Point in common between linePre and line
	 * @param p - current point
	 * @param line - line between pre and p (was already calculated in method)
	 * @return true if circle is in voronoi region, otherwise false
	 */
	// / I disabled the javadoc for this - re-ebable if you change this method
//	private boolean check(PVector circlePos, PVector pre, PVector p, PVector line) {
//		
//		float projPos = circlePos.dot(line);
//		float projPre = pre.dot(line);
//		float projP = p.dot(line);
//		
//		return (projPos <= projP && projPre <= projPos);
//	}

    // /*
    //  * Checks if circle is in an edge/vertex voronoi region of polygon specified by pre, linePre, and line
    //  * @param circlePos - position of circle
    //  * @param pre - Point in common between linePre and line
    //  * @param linePre - line defining previous edge
    //  * @param line - line defining current edge
    //  * @return true if circle is in voronoi region, otherwise false
    //  */
	// / I disabled the javadoc for this - re-ebable if you change this method
    // private boolean checkEdge(PVector circlePos, PVector pre, PVector linePre, PVector line) {
    //  
    //  float projPre1 = pre.dot(linePre);
    //  float projPre2 = pre.dot(line);
    //  float projPos1 = circlePos.dot(linePre);
    //  float projPos2 = circlePos.dot(line);
    //  
    //  return (projPos1 > projPre1 && projPos2 < projPre2);
    // }
	
	@Override
	public boolean contains(PVector point) {
	    PVector dist = PVector.sub(point, _position);
	    for(PVector axis : _axes) {
	    	PVector project1 = getProjection(axis, this);
	    	float projectP = PVector.dot(axis, dist);
	    	if(!(project1.x <= projectP && projectP <= project1.y)) {
	    		return false;
	    	}
	    }
	    
	    return true;
	}
	
	@Override
	public boolean contains(float x, float y) {
		return contains(new PVector(x,y,0));
	}
	
	@Override
	public HRectangle getBoundingBox() {
		float xMax = Float.NEGATIVE_INFINITY;
		float xMin = Float.POSITIVE_INFINITY;
		float yMax = Float.NEGATIVE_INFINITY;
		float yMin = Float.POSITIVE_INFINITY;
		for(Iterator<PVector> iter = _points.iterator(); iter.hasNext(); ) {
			PVector point = iter.next();
			if(point.x < xMin)
				xMin = point.x;
			if(point.x > xMax)
				xMax = point.x;
			if(point.y < yMin)
				yMin = point.y;
			if(point.y > yMax)
				yMax = point.y;
		}
		PVector min = makeVector(xMin, yMin);
		PVector max = makeVector(xMax, yMax);
		return new HRectangle(_position, min, max);
	}
	
	@Override
	public void draw() {
		PApplet papp = Hermes.getPApplet();
		papp.beginShape(PApplet.POLYGON);
		for(PVector p : _points) {
			papp.vertex(p.x, p.y);
		}
		PVector vert = _points.get(0);
		papp.vertex(vert.x,vert.y);
		papp.endShape();
	}
	
	@Override
	public String toString() {
		String output = "Position:" + _position;
		for(PVector p : _points) {
			output += "\nPoint:" + p;
		}
		return output;
	}
	
	///////////////////////////////
	//Factories for HPolygons
	///////////////////////////////
	/**
	 * Creates a new regular polygon with a given number of sides at the given location.
	 * <p>
	 * Radius determines how far away the vertices are from the center.
	 * @param pos		position of polygon
	 * @param sides		number of sides in the polygon
	 * @param radius	determines size of polygon
	 */
	public static HPolygon createRegularHPolygon(PVector pos, int sides, float radius) {
		ArrayList<PVector> points = new ArrayList<PVector>();
		PVector vertex = new PVector(0,-radius);
		points.add(vertex);
		double rot = 2*Math.PI / sides;
		for(int i = 1; i < sides; i++) {
			PVector next = getRotate(vertex,rot);
			points.add(next);
			vertex = next;
		}
		return new HPolygon(pos,points);
	}
}
