package hermes.physics;


import hermes.*;
import processing.core.PVector;
import static hermes.HermesMath.*;

/**
 * <p>
 * A <code>Collider<code> that keeps a being's bounding box contained inside of another by projection and impulse collision.
 * If the smaller of the beings in the interaction travels hits the inside edge of the larger it will bounce back inside.
 * <p>
 * This is a multisampled interaction and it is not applied immediately. (See <code>Interactor</code> documentation.)
 */
public class InsideMassedCollider extends Interactor<MassedBeing,MassedBeing> {
	
	public InsideMassedCollider() {
		super(false,true);
	}
	
	public boolean detect(MassedBeing being1, MassedBeing being2) {
		//figure out which MassedBeing is smaller, which is bigger
		float height1 = being1.getBoundingBox().getMax().y - being1.getBoundingBox().getMin().y;
		float height2 = being2.getBoundingBox().getMax().y - being2.getBoundingBox().getMin().y;
		float width1 = being1.getBoundingBox().getMax().x - being1.getBoundingBox().getMin().x;
		float width2 = being2.getBoundingBox().getMax().x - being2.getBoundingBox().getMin().x;
		MassedBeing smallerBeing;
		MassedBeing biggerBeing;
		float smallerBeingHeight, smallerBeingWidth;
		if(width1>width2 && height1>height2) {
			biggerBeing = being1;
			smallerBeing = being2;
			smallerBeingHeight = height2; smallerBeingWidth = width2;
		} else if(width1<width2 && height1<height2) {
			biggerBeing = being2;
			smallerBeing = being1; smallerBeingHeight = height1; smallerBeingWidth = width1;
		} else {//note: one must absolutely contain the other (be bigger on both axes) or will return false
			return false;
		}
		
		// find the projection vector between the bounding boxes of the beings
		// NOTE: always call on smallerBeing -- gives projection vector back IN to biggerBeing
		PVector projection = biggerBeing.getBoundingBox().projectionVector(smallerBeing.getBoundingBox());
		
		if(projection == null || being1==being2) {
			return false;	// if they aren't colliding
		}
		if(projection.x==0 && Math.abs(projection.y)<smallerBeingHeight) {
			projection.sub(makeVector(0, smallerBeingHeight * sign(projection.y)));
			MassedBeing.addImpulseCollision(biggerBeing, smallerBeing, projection);
			return true;
		} else if (projection.y==0 && Math.abs(projection.x)<smallerBeingWidth) { 
			projection.sub(makeVector(smallerBeingWidth * sign(projection.x), 0));
			MassedBeing.addImpulseCollision(biggerBeing, smallerBeing, projection);
			return true;
		}
		else 
			return false;

	}

	public void handle(MassedBeing being1, MassedBeing being2) {
		assert being1 != null : "InsideMassedCollider.handle: being1 must be a valid MassedBeing ";
		assert being2 != null : "InsideMassedCollider.handle: being2 must be a valid MassedBeing ";
		
		ImpulseCollision collision = being1.getImpulseCollisionWith(being2);
		
		assert collision != null : "InsideMassedCollider.handle: internal: no collision found " + being1 + " " + being2;
		
		collision.applyImpulses();
		collision.applyDisplacement();
	}
}