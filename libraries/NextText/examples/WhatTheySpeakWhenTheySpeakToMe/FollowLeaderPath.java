import net.nexttext.*;
import net.nexttext.property.*;
import processing.core.PVector;

import java.awt.*;
import java.util.ArrayList;


/**
* Behaviour that attracts a leader and its sibling
* on a given path.
* 
* <p>It fetches a path from the SnakeMousePath input source and
* affects the glyph that has the 'Leader' property set to true.  The
* leader and its siblings are then attracted to their respective
* position on the path with equal distance between them.</p>
* 
* <p>The siblings are affected through the leader to minimize
* computation.  One way was to look at each glyph to see if
* they were a sibling of a leader, then compute their position
* backwards on the path.  Much simpler the way it is now, find
* the leader and then compute it and its right neighbours.</p>
*/
public class FollowLeaderPath extends IWillFollowAction {
    // constants
    static final int GLYPH_SPACER = 1;
    static final int WORD_SPACER = 15;

    // mouse path input source
    SnakeMousePath isrcMousePath;
    float pathPointDistance;

    
    /** 
     * Creates a new instance of FollowLeaderPath
     *
     * @param isrcMousePath the SnakeMousePath input source 
     * @param pathPointDistance the minimum distance between two consecutive points on the path
     */
    public FollowLeaderPath(SnakeMousePath isrcMousePath, float pathPointDistance) {
        this.isrcMousePath = isrcMousePath;
        this.pathPointDistance = pathPointDistance;
    }
    
    
    /** 
     * If the TextObject is the 'Leader', moves it and its right siblings on the 
     * path, rotating each TextObject to match the slope of the path segment it 
     * is lying on.
     *
     * @param to the TextObject to act upon
     */
    public ActionResult behave(TextObject to) {
        // get a copy of the path
        ArrayList path = isrcMousePath.getPath();

        // if there is no path, return
        if (path.size() == 0) 
            return new ActionResult(false, false, false);
        
        // if the text object is the leader then attract it and its sibling towards the path
        if (isLeader(to).get()) {
            // start at the end of the path (most recent mouse position)
            int pathIndex = path.size() - 1;

            int pathPixelDistance = 0;  // distance in pixels from the end of the path where this object belongs
            PVector pathPosition;  // current path position, derived from pathIndex

            // start at the object itself as it is the leader
            TextObject rightSibling = to;
            while (rightSibling != null) {
                // find the point on the path prior to the position for this object
                int prevPathIndex = (path.size()-1)-(int)(pathPixelDistance/pathPointDistance);
                if (prevPathIndex <= 0) 
                    break;
                
                // calculate the vector to the previous path point
                Point prevPathPoint = (Point)path.get(prevPathIndex);
                PVector prevPathVector3 = new PVector(prevPathPoint.x, prevPathPoint.y);
                
                // calculate the vector to the next path point
                Point nextPathPoint = (Point)path.get(prevPathIndex - 1);
                PVector nextPathVector3 = new PVector(nextPathPoint.x, nextPathPoint.y);

                // calculate the vector from the previous path point to the object's desired location on the path
                pathPosition = nextPathVector3.get();
                pathPosition.sub(prevPathVector3);
                pathPosition.mult((pathPixelDistance % pathPointDistance) / pathPointDistance);
                pathPosition.add(prevPathVector3);
                
                // move the glyph towards the new position on the path
                PVectorProperty position = (PVectorProperty)rightSibling.getProperty("Position");		
                PVector positionAbs = rightSibling.getPositionAbsolute();
                 
                // calculate the offset from the current position
                PVector move = new PVector(pathPosition.x - positionAbs.x,
                                           pathPosition.y - positionAbs.y);

                // move towards new position by a max of 20 
                if (move.mag() > 20.0) {
                    move.normalize();
                    move.mult(20.0f);
                }

                // translate position
                position.add(move);

                // find the glyph angle by calculating the slope of the points 
                // on the path located before and after the position of the glyph
                NumberProperty rotation = (NumberProperty)rightSibling.getProperty("Rotation");
                if (pathIndex > 0) {
                    float rotate = 0;
                    if (pathIndex < path.size()-1) {
                        rotate = (float)Math.atan2(nextPathPoint.y-prevPathPoint.y,
                                            nextPathPoint.x-prevPathPoint.x);
                    } else if (pathIndex == path.size()-1) {
                        rotate = (float)Math.atan2(prevPathPoint.y-nextPathPoint.y,
                                            prevPathPoint.x-nextPathPoint.x);
                    }

                    // limit the rotations to the 1st and 4th quadrants 
                    // so that the glyphs are always readable
                    if (rotate < 0) 
                        rotate += Math.PI*2;
                    if ((rotate < Math.PI/2*3) && (rotate > Math.PI/2)) 
                        rotate += Math.PI;
                    if (rotate > Math.PI*2) 
                        rotate -= Math.PI*2;

                    rotation.set(rotate);
                }
                
                // if we are moving from right to left, set the offset for the next object based on the current object's width
                if (nextPathVector3.x >= prevPathVector3.x) {
                    pathPixelDistance += GLYPH_SPACER + ((TextObjectGlyph) rightSibling).getLogicalBounds().getWidth();
                }
                
                // get the next sibling
                if (rightSibling.getRightSibling() == null) {
                    if (rightSibling.getParent() == null) {
                        // no parent, we are done
                        rightSibling = null;
                    } else {
                        TextObjectGroup rightParent = (TextObjectGroup)rightSibling.getParent().getRightSibling();
                        if (rightParent == null) {
                            // no more words, we are done
                            rightSibling = null;
                        } else {
                            // go to the first glyph of the next word
                            rightSibling = rightParent.getLeftMostChild();
                            pathPixelDistance += WORD_SPACER;
                        }
                    }
                } else {
                    // go to the next glyph of the current word
                    rightSibling = rightSibling.getRightSibling();
                }
                
                // if we are moving from left to right, set the offset for the next object based on its own width
                if ((rightSibling != null) && (nextPathVector3.x < prevPathVector3.x)) {
                    pathPixelDistance += GLYPH_SPACER + ((TextObjectGlyph) rightSibling).getLogicalBounds().getWidth(); 
                }
            }
        }	

        return new ActionResult(false, false, false);
    }
}