import java.util.ArrayList;
import java.awt.Point;
import java.awt.event.MouseEvent;

import net.nexttext.Book;
import net.nexttext.TextObject;
import net.nexttext.TextObjectGroup;
import net.nexttext.TextObjectGlyph;
import net.nexttext.TextObjectGlyphIterator;
import net.nexttext.input.InputSource;
import net.nexttext.property.BooleanProperty;

import processing.core.*;
        

/**
 * An interface for the mouse. It listens to every mouse event and stores them in 
 * a list as {@link MouseEvent} objects.  It also keeps the current status of 
 * the mouse buttons and the current x and y position.</p>
 * 
 * <p>SnakeMousePath saves an array of the points when the mouse is dragged.
 * The points are spaced by a given distance and the array is reset
 * when the button is released.</p>
 *
 * <p>SnakeMousePath also registers and releases the 'Leader', by cycling through
 * the glyphs in the book and checking if any collide with the mouse coordinates
 * when dragged.</p> 
 */
public class SnakeMousePath extends InputSource {
  
    double pointDistance;
    ArrayList path = new ArrayList();
    
    Book book;
    TextObject theLeader = null;
    
    public SnakeMousePath(PApplet pApplet, Book theBook) {
        pApplet.registerMouseEvent(this);
        book = theBook;
        pointDistance = 10.0;
    }

    
    /**
     * Creates a new instance of SnakeMousePath with custom point distance
     *
     * @param component the component the mouse is added to
     * @param theBook the book the mouse is added to
     * @param ptDist the point distance
     */
    public SnakeMousePath(PApplet pApplet, Book theBook, double ptDist) {
        pApplet.registerMouseEvent(this);
        book = theBook;
        pointDistance = ptDist;
    }
    
    public void mouseEvent(MouseEvent event) {
        switch (event.getID()) {
            case MouseEvent.MOUSE_PRESSED:
                mousePressed(event);
                break;
            case MouseEvent.MOUSE_RELEASED:
                mouseReleased(event);
                break;
            case MouseEvent.MOUSE_DRAGGED:
                mouseDragged(event);
                break;
        }
    }

    
    /**
     * Adds the current point to the path if the mouse button 1 is pressed
     *
     * @param event the mouse event
     */
    public void mousePressed(MouseEvent event) {
        synchronized (path) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                // button 1 was pressed, add the point to the path    
                path.add(new Point(event.getX(), event.getY()));
            }
        }
    }

    
    /**
     * Clears the path if the mouse button 1 is released
     *
     * @param event the mouse event
     */
    public void mouseReleased(MouseEvent event) {
        synchronized (path) {
            // button 1 was released, clear the path
            if (event.getButton() == MouseEvent.BUTTON1) {
                path.clear();
                
                 if (theLeader != null) {
                    // clear the 'Leader' property
                    ((BooleanProperty)theLeader.getProperty("Leader")).set(false);
                    setFollower(theLeader, false);

                    // reset the leader pointer
                    theLeader = null;
                }
            }
        } 
    }
    
    
    /**
     * Updates the local mouse coordinates and adds points to the path
     *
     * @param event the mouse event
     */
    public void mouseDragged(MouseEvent event) {
        int distance;
        synchronized (path) {
            Point lastPoint = (Point)path.get(path.size()-1);
            distance = (int)lastPoint.distance(event.getX(), event.getY());
            
            // add interpolating points up until the current point over the distance from the last path point
            for (int i=0; i < (int)(distance/pointDistance); i++) {
                path.add(new Point((int)((double)(event.getX() - lastPoint.x)/distance*pointDistance*(i+1) + lastPoint.x),
                                   (int)((double)(event.getY() - lastPoint.y)/distance*pointDistance*(i+1) + lastPoint.y)));
            }
        }
        
        // look for a collision with a glyph if there is no leader already set
        if ((theLeader == null) && (distance >= pointDistance)) {
           TextObjectGlyph currGlyph;
           
           TextObjectGlyphIterator i = book.getTextRoot().glyphIterator();
           while (i.hasNext()) {
               currGlyph = i.next();
               
               if (currGlyph.getBoundingPolygon().contains(event.getX(), event.getY())) {
                   // set the leader pointer
                   theLeader = currGlyph;
                   
                   // set the 'Leader' property to true
                   ((BooleanProperty)currGlyph.getProperty("Leader")).set(true);
                   setFollower(currGlyph, true);
                   
                   break;
               }
           }
        }
    }
    
    
    /** 
     * Recursively sets the 'Follower' property for the TextObject and its right siblings
     *
     * @param to the TextObject to act upon
     * @param follow the value of the Follower property
     */
    public void setFollower(TextObject to, boolean follow) {
    	if (to == null) return ;
        
    	BooleanProperty followProperty = (BooleanProperty)to.getProperty("Follower");
  	followProperty.set(follow);

        TextObject rightSibling = to.getRightSibling();
        if (rightSibling == null) {
            if (to.getParent() == null) return;
            else {
                TextObjectGroup rightParent = (TextObjectGroup)to.getParent().getRightSibling();
                if (rightParent == null) return;
                rightSibling = rightParent.getLeftMostChild();
            }
        }

        setFollower(rightSibling, follow);
    }

    
    /**
     * Gets a copy of the path
     *
     * @return the path
     */
    public ArrayList getPath() {
        synchronized (path) {
            ArrayList copy = new ArrayList();
            copy.addAll(path);
            
            return copy;
        }
    }
}