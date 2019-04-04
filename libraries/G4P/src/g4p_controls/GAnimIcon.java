/*
  Part of the G4P library for Processing 
  	http://www.lagers.org.uk/g4p/index.html
	http://sourceforge.net/projects/g4p/files/?source=navbar

  Copyright (c) 2016 Peter Lager

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
 */

package g4p_controls;

import java.util.HashMap;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * 
 * Class for the creation of animated icons to be used with GButton, GLabel, GCheckbox and GOption controls. <br>
 * 
 * All animated icons will have an animation clip called 'ALL' which displays all the 
 * frames starting at zero with the user specified frame interval. <br>
 * 
 * Introduced in v4.1
 * @author Peter Lager
 *
 */
public class GAnimIcon extends GIcon {

	protected GTimer timer;
	protected Map<String, AnimClip> clips = new HashMap<String, AnimClip>();

	protected AnimClip anim_clip = null;

	/**
	 * This constructor is not to be used.
	 */
	protected GAnimIcon(){ 
		super();
	}

	/**
	 * Create an animated icon
	 * 
	 * @param papp the PApplet object that will be used to display this icon
	 * @param fname the name of the file containing the tiled image.
	 * @param nbrCols number of horizontal tiles
	 * @param nbrRows number of vertical tiles
	 * @param interval the time between displaying frames in milliseconds
	 */
	public GAnimIcon(PApplet papp, String fname, int nbrCols, int nbrRows, int interval){
		super(papp, fname, nbrCols, nbrRows);
		anim_clip = new AnimClip("ALL", 0, img.length-1, interval); 
		clips.put(anim_clip.id, anim_clip);
		timer = new GTimer(app, this, "advanceAnimationFrame", 10, interval);
	}

	/**
	 * Create an animated icon
	 * 
	 * @param papp the PApplet object that will be used to display this icon
	 * @param image the tiled image containing the frames
	 * @param nbrCols number of horizontal tiles
	 * @param nbrRows number of vertical tiles
	 * @param interval the time between displaying frames in milliseconds
	 */
	public GAnimIcon(PApplet papp, PImage image, int nbrCols, int nbrRows, int interval){
		super(papp, image, nbrCols, nbrRows);
		anim_clip = new AnimClip("ALL", 0, img.length-1, interval); 
		clips.put(anim_clip.id, anim_clip);
		timer = new GTimer(app, this, "advanceAnimationFrame", 10, interval);
	}

	/**
	 * @return returns this icon.
	 */
	GAnimIcon me(){
		return this;
	}
	
	/**
	 * @ return true if there is already an animation clip called 'id'
	 */
	boolean hasClip(String id){
		return clips.containsKey(id);
	}

	/**
	 * Returns a copy of this animated icon to be used with another control. <br>
	 * <b>Note:</b> animated icons must only be used with one second control. Use copy()
	 * to get a duplicate icon which can be used with another control.
	 * @return a copy of this icon
	 */
	public GAnimIcon copy(){
		GAnimIcon icon = new GAnimIcon();
		// GIcon class stuff
		icon.app = app;
		icon.img = img;
		icon.width = width;
		icon.height = height;
		icon.nbrFrames = nbrFrames;
		// GAnimIcon class stuff
		icon.clips = new HashMap<String, AnimClip>();
		for(String key : clips.keySet()){
			icon.clips.put(key, clips.get(key).copy());
		}
		icon.anim_clip = icon.clips.get(anim_clip.id);
		icon.timer = new GTimer(app, icon, "advanceAnimationFrame", 10, icon.anim_clip.interval);
		return icon;
		
	}
	/**
	 * Store details of an animation sequence for later use. <br>
	 * 
	 * @param id unique id for this animation clip
	 * @param start first frame for this clip
	 * @param end last frame for this clip
	 * @param interval the time (ms) between frames
	 * @return the icon being stored
	 */
	public GAnimIcon storeAnim(String id, int start, int end, int interval){
		return storeAnim(id, start, end, interval, 0);
	}

	/**
	 * 	
	 * Store details of an animation sequence for later use. <br>
	 * 
	 * @param id unique id for this animation clip
	 * @param start first frame for this clip
	 * @param end last frame for this clip
	 * @param interval the time (ms) between frames
	 * @param nbrLoops the number of times this clip is played, (0 = play forever)
	 * @return the icon being stored
	 */
	public GAnimIcon storeAnim(String id, int start, int end, int interval, int nbrLoops){
		AnimClip ac = new AnimClip(id, start, end, interval, nbrLoops);
		clips.put(id,  ac);
		return this;
	}

	/**
	 * Animate the entire sequence.
	 * @return the icon being animated
	 */
	public GAnimIcon animate(){
		if(hasClip("ALL"))
			animate("ALL");
		return this;
	}

	/**
	 * Animate the icon using store clip details but use the specified 
	 * frame interval instead of the stored value. <br>
	 * You can also decide on the number of times the clip is to be shown.
	 * 
	 * @param id the unique id for the clip
	 * @return the icon being animated
	 */
	public GAnimIcon animate(String id){
		AnimClip c = clips.get(id);
		if(c != null){
			timer.stop();
			anim_clip = c;
			currFrame = anim_clip.start;
			timer.setDelay(anim_clip.interval);
			timer.setNbrRepeats(anim_clip.size);
			timer.start(c.size);
		}
		return this;
	}

	/**
	 * Change the interval between frames for the current animation clip.
	 * 
	 * @param interval the time between frames in milliseconds
	 * @return this icon
	 */
	public GAnimIcon setInterval(int interval){
		if(anim_clip != null){
			anim_clip.interval = interval;
			timer.setDelay(interval);
		}
		return this;
	}

	/**
	 * Change the interval between frames for the animation clip with 
	 * specified id.
	 * @param id the animation id
	 * @param interval the time between frames in milliseconds
	 * @return this icon
	 */
	public GAnimIcon setInterval(String id, int interval){
		AnimClip c = clips.get(id);
		if(c != null){
			c.interval = interval;
			if(c == anim_clip)
				timer.setDelay(interval);
		}
		return this;
	}

	/**
	 * Resume an animation that has previously been stopped with stop()
	 * @return this icon
	 */
	public GAnimIcon start(){
		if(!timer.isRunning())
			timer.start();
		return this;
	}

	/**
	 * Stop the current animation. The animation can be resumed with start()
	 * @return this icon
	 */
	public GAnimIcon stop(){
		timer.stop();
		return this;
	}

	/**
	 * Set the current frame to be displayed. The supplied frame number will be constrained 
	 * to a valid value. <br>
	 * This is ignored if the animation clip is running.
	 * 
	 * @param fn the frame number to display.
	 * @return this icon
	 */
	public GAnimIcon setFrame(int fn){
		if(!timer.isRunning())
			currFrame = Math.abs(fn) % nbrFrames;
		return this;
	}

	/**
	 * Required by GTimer to advance frame
	 * @param timer
	 */
	public void advanceAnimationFrame(GTimer timer){
		currFrame = (currFrame == anim_clip.end) ? anim_clip.start : currFrame + anim_clip.dir;
		if(owner != null) owner.bufferInvalid = true;
	}

	/**
	 * A class to store the details of a simple animation clip.
	 * 
	 * @author Peter Lager
	 *
	 */
	protected class AnimClip {
		String id;
		int start;
		int end;
		int interval; // delay between frames
		int dir; //  dir +1 forwards, -1 backwards
		int nLoops;
		int size; // Number of frames to display based on nbr loops


		/**
		 * 
		 * @return a copy of this animation clip
		 */
		AnimClip copy(){
			return new AnimClip(id, start, end, interval, nLoops);
		}
		
		/**
		 * Create an animation clip
		 * 
		 * @param id unique id for a stored clip for use.
		 * @param start start frame number
		 * @param end end frame number
		 * @param interval time between frames in milliseconds
		 * @param nbrLoops the number of times the clip will be played before stopping
		 */
		public AnimClip(String id, int start, int end, int interval) {
			this(id, start, end, interval, 0);
		}

		/**
		 * Create an animation clip
		 * 
		 * @param id unique id for a stored clip for use.
		 * @param start start frame number
		 * @param end end frame number
		 * @param interval time between frames in milliseconds
		 * @param nLoops the number of times the clip will be played before stopping
		 */
		public AnimClip(String id, int start, int end, int interval, int nLoops) {
			super();
			start = Math.abs(start) % nbrFrames;
			end = Math.abs(end) % nbrFrames;
			this.id = id;
			this.start = start;
			this.end = end;
			this.interval = interval;
			this.nLoops = nLoops;
			dir = (end == start) ? 0 : (end - start > 0) ? 1 : -1;
			size = Math.abs(end - start) + 1;
			size = nLoops <= 0 ? 0 : nLoops * size - 1;
		}

		/**
		 * @return textual description of animclip
		 */
		public String toString(){
			return id + "  [ " + start + " > " + end + "   step " + dir + "   interval " + interval + "ms ]";
		}
	}

}
