package org.ivpr.pgui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class Container extends Component {
	protected List<Component> children = new LinkedList<Component>();
	protected Map<Integer, Component> childrenUnderTouches = new HashMap<Integer, Component>();
	public boolean layoutDirty = true;

	public Container(PGui p, float size) {
		super(p, size);
	}

	public void add(Component c) {
		children.add(c);
		layoutDirty = true;
	}

	public void remove(Component c) {
		children.remove(c);
		layoutDirty = true;
	}

	public void _touchPressed(int touchX, int touchY, int touchId) {
		for (Component c : children) {
			if (c.contains(touchX, touchY)) {
				childrenUnderTouches.put(touchId, c);
				c._touchPressed(touchX, touchY, touchId);
				break;
			}
		}
	}

	public void _touchDragged(int touchX, int touchY, int touchId) {
		Component c = childrenUnderTouches.get(touchId);
		if (c != null)
			c._touchDragged(touchX, touchY, touchId);
	}

	public void _touchReleased(int touchX, int touchY, int touchId) {
		Component c = childrenUnderTouches.get(touchId);
		if (c != null)
			c._touchReleased(touchX, touchY, touchId);
		childrenUnderTouches.remove(touchId);
	}

	public boolean hasChildren() {
		return children.size() != 0;
	}
}
