package hermes.hshape;

import hermes.Hermes;
import processing.core.PVector;
import static hermes.HermesMath.*;

/**
 * Represents a circle.
 */
public class HCircle extends HShape {

	private PVector _center;
	private float _radius;
	
	/**
	 * Constructor defining center of circle
	 * as position of object.
	 * @param position
	 * @param radius
	 */
	public HCircle(PVector position, float radius) {
		super(position);
		
		assert radius >= 0 : "In HCircle constructor, radius must be positive"; //TODO can radius be 0?
		
		_center = new PVector(0,0);
		_radius = radius;
	}
	
	/**
	 * Constructor defining center of circle
	 * to be a certain distance away from the position.
	 * @param position
	 * @param center
	 * @param radius
	 */
	public HCircle(PVector position, PVector center, float radius) {
		super(position);
		
		assert center != null : "In HCircle constructor, center must be a valid PVector";
		assert radius >= 0 : "In HCircle constructor, radius must be non-negative"; //TODO can radius be 0?
		
		_center = center;
		_radius = radius;
	}
	
	/**
	 * Getter for center
	 */
	public PVector getCenter() {
		return _center;
	}
	
	/**
	 * Getter for radius
	 */
	public float getRadius() {
		return _radius;
	}
	
	@Override
	public boolean contains(PVector point) {
	    float distX = point.x - _position.x;
	    float distY = point.y - _position.y;
	    return distX*distX + distY*distY <= _radius*_radius;
	}
	
	@Override
	public boolean contains(float x, float y) {
	    float distX = x - _position.x;
	    float distY = y - _position.y;
	    return distX*distX + distY*distY <= _radius*_radius;
	}
	
	@Override
	public PVector projectionVector(HShape other) {
		assert other != null : "HCircle.collide: other must be a validHShape";
		PVector opposite = other.projectionVector(this);
		return opposite == null ? null : reverse(opposite);
	}

	@Override
	public PVector projectionVector(HPolygon other) {
		PVector opposite = other.projectionVector(this);
		return opposite == null ? null : reverse(opposite);
	}
	
	@Override
	public PVector projectionVector(HCircle other) {
		//Get the center of this circle
		PVector worldCenterThis = PVector.add(_position, _center);
		//Get the center of the other circle
		PVector worldCenterOther = PVector.add(other.getPosition(), other.getCenter());
		
		//HCircles are colliding if distance between them is less than sum of radii
		PVector dir = PVector.sub(worldCenterOther, worldCenterThis);
		float distance = dir.mag();
		float sumRadii = _radius + other._radius;
		boolean collides = distance <= sumRadii;
		
		//Projection vector is the unit vector pointing from this circle to other scaled by overlap
		if(collides) {
			float magnitude = sumRadii - distance;
			dir.normalize();
			dir.mult(magnitude);
			return dir;
		}
		else return null;
	}
	
	@Override
	public PVector projectionVector(HRectangle other) {
		//Get the center of this circle
		PVector worldCenter = PVector.add(_center, _position);
		//Figure out what voronoi region of the rectangle the circle is in
		PVector min = PVector.add(other._position, other.getMin());
		PVector max = PVector.add(other._position, other.getMax());
		if(min.x <= worldCenter.x) {
			if(worldCenter.x <= max.x) {
				//In regions above or below rectangle,
				//compare y projections
				float minProject = worldCenter.y - _radius;
				float maxProject = worldCenter.y + _radius;
				if(min.y <= maxProject && minProject <= max.y) {
					float topCollide = max.y - minProject;
					float bottomCollide = maxProject - min.y;
					return (topCollide >= bottomCollide ?
							new PVector(0,bottomCollide):
							new PVector(0,-topCollide));
					
				}
			}
			else if(min.y <= worldCenter.y) {
				if(worldCenter.y <= max.y) {
					//In region directly to right of rectangle
					//Compare x projections
					float minProject = worldCenter.x - _radius;
					if(minProject <= max.x) {
						return new PVector(minProject - max.x,0);
					}
				}
				else {
					//In region to the right&up of rectangle
					//Get projection of both along up-right vertex (max)
					return getOverlap(worldCenter,max);
				}
			}
			else {
				//In region to the right&down of rectangle
				//Get projection of both along bottom-right vertex
				PVector brVertex = new PVector(max.x, min.y);
				return getOverlap(worldCenter, brVertex);
			}
		}
		else if(min.y <= worldCenter.y) {
			if(worldCenter.y <= max.y) {
				//In region directly to the left of rectangle
				//Compare x projections
				float maxProject = worldCenter.x + _radius;
				if(min.x <= maxProject) {
					return new PVector(maxProject - min.x,0);
				}
			}
			else {
				//In region to the left&up of rectangle
				//Get projection of both along top-left vertex
				PVector tlVertex = new PVector(min.x, max.y);
				return getOverlap(worldCenter, tlVertex);
			}
		}
		else {
			//In region to the left&down of rectangle
			//Get projection of both along bottom-left vertex (min)
			return getOverlap(worldCenter, min);
		}
		
		return null;
	}
	
	/**
	 * Helper method.
	 * Finds overlap between a circle and the corner of a rectangle.
	 * @param worldCenter
	 * @param vertex
	 * @return projection vector when colliding, null when not
	 */
	private PVector getOverlap(PVector worldCenter, PVector vertex) {
		//Get vector from circle to vertex and overlap of shapes
		PVector axis = PVector.sub(vertex, worldCenter);
		float overlap = _radius - axis.mag();
		if(overlap >= 0) {
			//Get projection vector
			axis.normalize();
			axis.mult(overlap);
			return axis;
		}
		else return null;
	}
	
	@Override
	public HRectangle getBoundingBox() {
		return new HRectangle(PVector.sub(PVector.add(_position, _center),new PVector(_radius,_radius)), 2*_radius, 2*_radius);
	}
	
	@Override
	public void draw() {
		Hermes.getPApplet().ellipse(_center.x, _center.y, 2*_radius, 2*_radius);
	}
	
	@Override
	public String toString() {
		return "Position:" + _position + "\nCenter:" + _center + "\nRadius:" + _radius;
	}

}
