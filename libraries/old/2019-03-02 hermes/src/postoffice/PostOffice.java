package hermes.postoffice;

import hermes.Hermes;
import hermes.Pair;
import hermes.HObject;
import hermes.hshape.HShape;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import javax.swing.SwingUtilities;

import processing.core.PVector;

import com.google.common.collect.HashMultimap;

/**
 * Listens for OSC, mouse, and keyboard messages;
 * can also send OSC messages.
 * <p>
 * Objects can register "subscriptions" with the P.O. 
 * for a particular type of message.
 * When that type of message is received, P.O. tells subscribers
 * and passes along the information stored in the message.
 */
public class PostOffice implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener, 
									com.illposed.osc.OSCListener {
	
	//OSCPorts for listening and receiving
	private com.illposed.osc.OSCPortIn _receive;
	private com.illposed.osc.OSCPortOut _send;
	
	//Maps that associate subscribers with messages they want to receive
	private HashMultimap<Integer, KeySubscriber> _keySubs;
	private HashMultimap<POCodes.Button, Pair<MouseSubscriber,HShape>> _mouseSubs;
	private ArrayList<MouseWheelSubscriber> _mouseWheelSubs;
	private HashMultimap<String, OscSubscriber> _oscSubs;
	
	//Stores messages as they are received, which are then picked off by checkMail()
	private LinkedList<KeyMessage> _keyQueue;
	private LinkedList<MouseMessage> _mouseQueue;
	private LinkedList<MouseWheelMessage> _mouseWheelQueue;
	private LinkedList<OscMessage> _oscQueue;
	
	//Keeps track of which keys are pressed for quick tracking
	private HashSet<Integer> _pressedKeys;
	
	//Keeps track of mouse location for quick tracking
	private PVector _mouseLocation;
	private PVector _pMouseLocation; //previous mouse location
	
	//Boolean stating whether osc is on or off
	private boolean _onOSC;
	
	/* See bottom for key constants */
	
	/**
	 * Constructor with no OSC.
	 */
	public PostOffice() {
		_onOSC = false;
		POInit();
	}
	
	/**
	 * Constructor with OSC running locally.
	 * Messages can be sent to and received from other programs running on the same computer.
	 * @param portIn	port to receive messages on
	 * @param portOut	port to send messages on
	 */
	public PostOffice(int portIn, int portOut) {
		assert portIn > 1000 : "PostOffice constructor: portIn must be a valid port number, greater than 1000";
		assert portOut > 1000 : "PostOffice constructor: portOut must be a valid port number, greater than 1000";
		
		_onOSC = true;
		POInit();
		//Start OSC and set listener
		try {
			_receive = new com.illposed.osc.OSCPortIn(portIn);
			_receive.startListening();
		} catch (SocketException e) {
			//throw new OscServerException("OSC Port In on " + portIn + " could not start");
			assert false : "PostOffice Error: OSC Port In on " + portIn + " could not start";
		}
		try {
			_send = new com.illposed.osc.OSCPortOut(InetAddress.getLocalHost(),portOut);
		} catch (UnknownHostException e) {
			//throw new OscServerException("OSC Port Out on " + portOut + " could not start");
			assert false : "PostOffice Error: OSC Port In on " + portIn + " could not start";
		} catch (SocketException e) {
			//throw new OscServerException("OSC Port Out on " + portOut + " could not start");
			assert false : "PostOffice Error: OSC Port In on " + portIn + " could not start";
		}
	}
	
	/**
	 * Constructor for PostOffice that sends messages to non-local address (ie over the internet).
	 * @param portIn		port to receive messages on
	 * @param portOut		port to send messages on
	 * @param netAddress	url of location to send messages to
	 */
	public PostOffice(int portIn, int portOut, String netAddress)  {
		assert portIn > 1000 : "PostOffice constructor: portIn must be a valid port number, greater than 1000";
		assert portOut > 1000 : "PostOffice constructor: portOut must be a valid port number, greater than 1000";
		assert netAddress != null : "PostOffice constructor: netAddress must be a valid String";
		
		_onOSC = true;
		POInit();
		//Start OSC and set listener
		try {
			_receive = new com.illposed.osc.OSCPortIn(portIn);
			_receive.startListening();
		} catch (SocketException e) {
		//	throw new OscServerException("OSC Port In on " + portIn + " could not start");
			assert false : "PostOffice Error: OSC Port In on " + portIn + " could not start";
		}
		try {
			_send = new com.illposed.osc.OSCPortOut(InetAddress.getByName(netAddress), portOut);
		} catch (UnknownHostException e) {
			//throw new OscServerException("OSC Port Out on " + portOut + ", net address " + netAddress + " could not start");
			assert false : "PostOffice Error: OSC Port In on " + portIn + " could not start";
		} catch (SocketException e) {
			//throw new OscServerException("OSC Port Out on " + portOut + ", net address " + netAddress + " could not start");
			assert false : "PostOffice Error: OSC Port In on " + portIn + " could not start";
		}
	}
	
	/**
	 * Helper for constructors.
	 * Sets PO as listener on PApplet, and initializes internal lists.
	 */
	private void POInit() {
		//Set PostOffice to listen for events
		Hermes.getPApplet().addKeyListener(this);
		Hermes.getPApplet().addMouseListener(this);
		Hermes.getPApplet().addMouseMotionListener(this);
		Hermes.getPApplet().addMouseWheelListener(this);
		//Initialize subscription list and message queue
		_keySubs = HashMultimap.create();
		_pressedKeys = new HashSet<Integer>();
		_mouseSubs = HashMultimap.create();
		_mouseLocation = new PVector(-100,-100,0);
		_mouseWheelSubs = new ArrayList<MouseWheelSubscriber>();
		_keyQueue = new LinkedList<KeyMessage>();
		_mouseQueue = new LinkedList<MouseMessage>();
		_mouseWheelQueue = new LinkedList<MouseWheelMessage>();
		if(_onOSC) {
			_oscSubs = HashMultimap.create();
			_oscQueue = new LinkedList<OscMessage>();
		}
	}
	
	///////////////////////////////////////
	//Methods for registering subscriptions
	
	/**
	 * Registers a subscription to messages sent by a specific keyboard key.
	 * @param sub	the KeySubscriber signing up
	 * @param key	the code of the keyboard key whose messages the subscriber wants (use value from POConstants)
	 */
	public void subscribe(KeySubscriber sub, int key) {
		assert sub != null : "PostOffice.registerKeySubscription: sub must be a valid KeySubscriber";
		_keySubs.put(key, sub);
	}
	
	/**
	 * Registers a subscription to messages sent by a specific keyboard key.
	 * @param sub	the KeySubscriber signing up
	 * @param key	the char of the keyboard key whose messages the subscriber wants
	 */
	public void subscribe(KeySubscriber sub, char key) {
		assert sub != null : "PostOffice.registerKeySubscription: sub must be a valid KeySubscriber";
		Integer keyCode = Character.getNumericValue(key);
		_keySubs.put(keyCode, sub);
	}
	
	/**
	 * Registers a subscription to messages sent by a specific mouse button.
	 * <p>
	 * Buttons are defined by constants in the POConstants class;
	 * subscribe with "NO_BUTTON" to receive information about mouse movements when no button is pressed.
	 * @param sub		the MouseSubscriber signing up
	 * @param button	the code of the button whose messages the subscriber wants (use value from POContants)
	 */
	public void subscribe(MouseSubscriber sub, POCodes.Button button) {
		assert sub != null : "PostOffice.registerMouseSubscription: sub must be a valid MouseSubscriber";
		_mouseSubs.put(button, new Pair<MouseSubscriber, HShape>(sub, null));
	}
	
	/**
	 * A version of registerMouseSubscription that subscribes only to the requested button events
	 *      that occur in the given region.
	 * @param sub		the MouseSubscriber signing up
	 * @param button	the code of the button whose messages the subscriber wants (use value from POContants)
	 * @param region	the region on screen the subscriber wants to limit its subscription to
	 */
    public void subscribe(MouseSubscriber sub, POCodes.Button button, HShape region) {
        assert sub != null : "PostOffice.registerMouseSubscription: sub must be a valid MouseSubscriber";
        assert region != null : "PostOffice.registerMouseSubscription: region must be a valid Shape";
        _mouseSubs.put(button, new Pair<MouseSubscriber, HShape>(sub, region));
    }
	
	/**
	 * Registers a subscription to the mouse wheel (one subscription gets you everything).
	 * @param sub	the MouseWheelSubscriber signing up
	 */
	public void subscribe(MouseWheelSubscriber sub) {
		assert sub != null : "PostOffice.registerMouseWheelSubscription: sub must be a valid MouseWheelSubscriber";
		_mouseWheelSubs.add(sub);
	}

	/**
	 * Registers a subscription to messages received on a specific OSC address.
	 * @param sub		the OscSubscriber signing up
	 * @param address	the address whose messages the subscriber wants
	 */
	public void subscribe(OscSubscriber sub, String address) {
		assert _onOSC : "PostOffice.registerOscSubscription: cannot register an OSC subscription unless OSC is on";
		assert sub != null : "PostOffice.registerOscSubscription: sub must be a valid OscSubscriber";
		assert address != null : "PostOffice.registerOscSubscription: address must be a valid String";
		_oscSubs.put(address, sub);
		_receive.addListener(address, this);
	}
	
	/**
	 * Removes a mouse subscription
	 * @param sub   the subscriber to be removed
	 * @return      true if subscriber was present and removed, false otherwise
	 */
	public boolean removeMouseSubscriptions(MouseSubscriber sub) {
	  if(_mouseSubs.containsValue(sub)) {
	    // Find key-value pairs containing sub
	    Set<Map.Entry<POCodes.Button, Pair<MouseSubscriber,HShape>>> all = _mouseSubs.entries();
	    Set<Map.Entry<POCodes.Button, Pair<MouseSubscriber,HShape>>> toRemove = new HashSet<Map.Entry<POCodes.Button, Pair<MouseSubscriber,HShape>>>();
	    for(Iterator<Map.Entry<POCodes.Button, Pair<MouseSubscriber,HShape>>> iter = all.iterator(); iter.hasNext(); ) {
	      Map.Entry<POCodes.Button, Pair<MouseSubscriber,HShape>> next = iter.next();
	      if(next.getValue().getFirst() == sub) {
	        toRemove.add(next);
	      }
	    }
	    
	    // Remove references
	    for(Iterator<Map.Entry<POCodes.Button, Pair<MouseSubscriber,HShape>>> iter = toRemove.iterator(); iter.hasNext(); ) {
	      Map.Entry<POCodes.Button, Pair<MouseSubscriber,HShape>> next = iter.next();
	      _mouseSubs.remove(next.getKey(), next.getValue());
	    }
	    return true;
	  } else {
	    return false;
	  }
	}
	
	/**
	 * Removes a key subscription
	 * @param sub   the subscriber to be removed
	 * @return      true if subscriber was present and removed, false otherwise
	 */
	 public boolean removeKeySubscriptions(KeySubscriber sub) {
	   if(_keySubs.containsValue(sub)) {
 	    // Find key-value pairs containing sub
 	    Set<Map.Entry<Integer, KeySubscriber>> all = _keySubs.entries();
 	    Set<Map.Entry<Integer, KeySubscriber>> toRemove = new HashSet<Map.Entry<Integer, KeySubscriber>>();
 	    for(Iterator<Map.Entry<Integer, KeySubscriber>> iter = all.iterator(); iter.hasNext(); ) {
 	      Map.Entry<Integer, KeySubscriber> next = iter.next();
 	      if(next.getValue() == sub) {
 	        toRemove.add(next);
 	      }
 	    }

 	    // Remove references
 	    for(Iterator<Map.Entry<Integer, KeySubscriber>> iter = toRemove.iterator(); iter.hasNext(); ) {
 	      Map.Entry<Integer, KeySubscriber> next = iter.next();
 	      _keySubs.remove(next.getKey(), next.getValue());
 	    }
 	    return true;
 	  } else {
 	    return false;
 	  }
	 }
	 
	 /**
 	 * Removes a mouse wheel subscription
 	 * @param sub   the subscriber to be removed
 	 * @return      true if subscriber was present and removed, false otherwise
 	 */
 	 public boolean removeMouseWheelSubscriptions(KeySubscriber sub) {
 	   return _mouseWheelSubs.remove(sub);
 	 }
 	 
 	 /**
 	 * Removes an OSC subscription
 	 * @param sub   the subscriber to be removed
 	 * @return      true if subscriber was present and removed, false otherwise
 	 */
 	 public boolean removeOSCSubscriptions(KeySubscriber sub) {
 	   if(_onOSC) {
 	     if(_oscSubs.containsValue(sub)) {
  	       // Find key-value pairs containing sub
  	       Set<Map.Entry<String, OscSubscriber>> all = _oscSubs.entries();
  	       Set<Map.Entry<String, OscSubscriber>> toRemove = new HashSet<Map.Entry<String, OscSubscriber>>();
  	       for(Iterator<Map.Entry<String, OscSubscriber>> iter = all.iterator(); iter.hasNext(); ) {
  	         Map.Entry<String, OscSubscriber> next = iter.next();
  	         if(next.getValue() == sub) {
  	           toRemove.add(next);
  	         }
  	       }

  	       // Remove references
  	       for(Iterator<Map.Entry<String, OscSubscriber>> iter = toRemove.iterator(); iter.hasNext(); ) {
  	         Map.Entry<String, OscSubscriber> next = iter.next();
  	         _oscSubs.remove(next.getKey(), next.getValue());
  	       }
  	       return true;
  	     } else {
  	       return false;
  	     }
 	   } else {
 	     return false;
 	   }
 	 }
 	 
 	 /**
 	  * Helper method to remove all subscriptions for given HObject
 	  * @param sub    HObject to remove from PostOffice
 	  * @return       true if HObject had subscriptions that were removed, false otherwise
 	  */
 	 public boolean removeAllSubscriptions(HObject sub) {
 	   boolean key = removeKeySubscriptions(sub);
 	   boolean mouse = removeMouseSubscriptions(sub);
 	   boolean mouseWheel = removeMouseWheelSubscriptions(sub);
 	   boolean osc = removeOSCSubscriptions(sub);
 	   
 	   if(key || mouse || mouseWheel || osc) {
 	     return true;
 	   } else {
 	     return false;
 	   }
 	 }
 	 
 	 /**
 	  * Resets all subscriptions
 	  * Use if passing subscriptions between Worlds
 	  */
 	public void resetSubscriptions() {
 	  _keySubs = HashMultimap.create();
		_mouseSubs = HashMultimap.create();
		_mouseWheelSubs = new ArrayList<MouseWheelSubscriber>();
		if(_onOSC) {
			_oscSubs = HashMultimap.create();
		}
 	}
	//////////////////////////////////
	//Utilities for checking key presses and mouse location quickly
	
	/**
	 * Utility for checking if key is pressed
	 * @param keyCode	the key being checked
	 * @return			true if pressed, false otherwise
	 */
	public boolean isKeyPressed(int keyCode) {
		return _pressedKeys.contains(keyCode);
	}
	
	/**
	 * Utility for checking if mouse is in a region
	 * @param region	region to check
	 * @return			true if mouse is in region
	 */
	public boolean isMouseInRegion(HShape region) {
			return region.contains(_mouseLocation);
	}
	
	/**
	 * Utility for obtaining current mouse location
	 * @return	mouse location
	 */
	public PVector getMouseLocation() {
		return _mouseLocation;
	}
	
	/**
	 * Utility for obtaining previous mouse location
	 * @return mouse location on last update
	 */
	public PVector getPMouseLocation() {
		return _pMouseLocation;
	}
	
	/////////////////////////////////
	//Methods for sending OscMessages
	
	/**
	 * Sends an OscMessage on the given address containing only the given int.
	 * @param address	address message is to be sent on
	 * @param send		integer to be sent
	 */
	public void sendInt(String address, int send) {
		assert _onOSC : "PostOffice.sendInt: cannot send an OSC message while OSC is off";
		assert address != null : "PostOffice.sendInt: address must be a valid String";
		Object[] array = new Object[1];
		array[0] = (Integer) send;
		com.illposed.osc.OSCMessage mail = new com.illposed.osc.OSCMessage(address, array);
		try {
			_send.send(mail);
		}
		catch(Exception e) {
		//	throw new OscSendException("Error sending message " + send + " on " + address);
			assert false : "sendInt Error: Error sending message " + send + " on " + address;

		}
	}
	
	/**
	 * Sends an OscMessage on the given address containing only the given float.
	 * @param address	address message is to be sent on
	 * @param send		float to be sent
	 */
	public void sendFloat(String address, float send)  {
		assert _onOSC : "PostOffice.sendFloat: cannot send an OSC message while OSC is off";
		assert address != null : "PostOffice.sendFloat: address must be a valid String";
		Object[] array = new Object[1];
		array[0] = (Float) send;
		com.illposed.osc.OSCMessage mail = new com.illposed.osc.OSCMessage(address, array);
		try {
			_send.send(mail);
		}
		catch(Exception e) {
			//throw new OscSendException("Error sending message " + send + " on " + address);
			assert false : "sendFloat Error: Error sending message " + send + " on " + address;

		}
	}
	
	/**
	 * Sends an OscMessage on the given address containing only the given boolean.
	 * @param address	address message is to be sent on
	 * @param send		boolean to be sent
	 */
	public void sendBoolean(String address, boolean send) {
		assert _onOSC : "PostOffice.sendBoolean: cannot send an OSC message while OSC is off";
		assert address != null : "PostOffice.sendBoolean: address must be a valid String";
		Object[] array = new Object[1];
		array[0] = (Boolean) send;
		com.illposed.osc.OSCMessage mail = new com.illposed.osc.OSCMessage(address, array);
		try {
			_send.send(mail);
		}
		catch(Exception e) {
			//throw new OscSendException("Error sending message " + send + " on " + address);
			assert false : "sendBoolean Error: Error sending message " + send + " on " + address;

		}
	}
	
	/**
	 * Sends an OscMessage on the given address containing the contents of the given list.
	 * @param address	address message is to be sent on
	 * @param send		List to be sent
	 */
	public void sendList(String address, ArrayList<Object> send) {
		assert _onOSC : "PostOffice.sendList: cannot send an OSC message while OSC is off";
		assert address != null : "PostOffice.sendList: address must be a valid String";
		assert send != null : "PostOffice.sendList: send must be a valid ArrayList";
		int size = send.size();
		Object[] array = new Object[size];
		for(int i = 0; i < size; i++) {
			array[i] = send.get(i);
		}
		com.illposed.osc.OSCMessage mail = new com.illposed.osc.OSCMessage(address, array);
		try {
			_send.send(mail);
		}
		catch(Exception e) {
			String message = "sendList Error: problem sending message ";
			for(Object o : array) {
				message += o + " ";
			}
			message += "on address " + address + "\n";
			
			//throw new OscSendException(message);
			assert false : message;

		}
	}
	
	//////////////////////////////////////////////////////////////////////////
	/**
	 * Command that sends all messages queued by the PostOffice to subscribers.
	 * <p>
	 * Called at end of update loop (for thread safety).
	 */
	public void checkMail() {
		//Send all the messages in each queue to the corresponding subscribers
		synchronized(_keyQueue) {
			_pressedKeys.clear();
			while(!_keyQueue.isEmpty()) {
				KeyMessage m = _keyQueue.poll();
				int key = m.getKeyCode();
				if(m.isPressed()) { //Add to the pressed key array if pressed
					_pressedKeys.add(key);
				}
				Set<KeySubscriber> subs = _keySubs.get(key);
				for(KeySubscriber sub : subs) {
					sub.receive(m);
				}
			}
		}
		synchronized(_mouseQueue) {
			while(!_mouseQueue.isEmpty()) {
				MouseMessage m = _mouseQueue.poll();
				_mouseLocation.x = m.getX();
				_mouseLocation.y = m.getY();
				POCodes.Button button = m.getButton();
				Set<Pair<MouseSubscriber,HShape>> subs = _mouseSubs.get(button);
				for(Pair<?, ?> p : subs) {
				   HShape region = (HShape) p.getSecond();
					if(region == null || region.contains(m.getX(), m.getY()))
						((MouseSubscriber) p.getFirst()).receive(m);
				}
			}
		}
		synchronized(_mouseWheelQueue) {
			while(!_mouseWheelQueue.isEmpty()) {
				MouseWheelMessage m = _mouseWheelQueue.poll();
				for(MouseWheelSubscriber sub : _mouseWheelSubs) {
					sub.receive(m);
				}
			}
		}
		
		if(_onOSC) { //Only check OSC queue is OSC server is running
			synchronized(_oscQueue) {
				while(!_oscQueue.isEmpty()) {
					OscMessage m = _oscQueue.poll();
					
					String address = m.getAddress();
					Set<OscSubscriber> subs = _oscSubs.get(address);

					for(OscSubscriber sub : subs) {
						sub.receive(m);
					}
				}
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////
	//Methods defined by implemented interfaces for handling mouse+keyboard input
	/////////////////////////////////////////////////////////////////////////////
	//Get keyboard events
	
	/**
	 * Ignore keyTyped events.
	 */
	public void keyTyped(KeyEvent e) {
		//VOID
	}
	/**
	 * On a key press, make a new KeyMessage and add it to the queue.
	 * Note: keyPressed events will repeat at rate set by OS if key is held down;
	 * users should control this with keyReleased events.
	 */
	public void keyPressed(KeyEvent e) {
		KeyMessage m = new KeyMessage(e.getKeyCode(), e.getKeyChar(), true);
		synchronized(_keyQueue) {
			_keyQueue.add(m);
		}
	}
	/**
	 * On a key release, make a new KeyMessage and add it to the queue.
	 */
	public void keyReleased(KeyEvent e) {
		KeyMessage m = new KeyMessage(e.getKeyCode(), e.getKeyChar(), false);
		synchronized(_keyQueue) {
			_keyQueue.add(m);
		}
	}
	
	///////////////////////////////
	//Get mouse clicks and releases
	
	/**
	 * Ignore mouseClicked events.
	 */
	public void mouseClicked(MouseEvent e) {
		//VOID
	}
	/**
	 * On a mouse press, make a new MouseMessage and add it to the queue.
	 */
	public void mousePressed(MouseEvent e) {
		MouseMessage m  = new MouseMessage(getMouseButton(e), POCodes.Click.PRESSED, e.getX(), e.getY());
		synchronized(_mouseQueue) {
			_mouseQueue.add(m);
		}
	}
	/**
	 * On a mouse button release, make a new MouseMessage and add it to the queue.
	 */
	public void mouseReleased(MouseEvent e) {
		MouseMessage m  = new MouseMessage(getMouseButton(e), POCodes.Click.RELEASED, e.getX(), e.getY());
		synchronized(_mouseQueue) {
			_mouseQueue.add(m);
		}
	}
	/**
	 * Ignore mouseEntered events.
	 */
	public void mouseEntered(MouseEvent e) {
		//VOID
	}
	/**
	 * Ignore mouseExited events.
	 */
	public void mouseExited(MouseEvent e) {
		//VOID
	}

	///////////////////////////////
	//Get changes in mouse location
	/**
	 * When the mouse is dragged, create a MouseMessage and add it to the group.
	 */
	public void mouseDragged(MouseEvent e) {
		MouseMessage m  = new MouseMessage(getMouseButton(e), POCodes.Click.DRAGGED, e.getX(), e.getY());
		synchronized(_mouseQueue) {
			_mouseQueue.add(m);
		}
	}
	/**
	 * When the mouse is moved, create a MouseMessage and add it to the queue.
	 */
	public void mouseMoved(MouseEvent e) {
		MouseMessage m  = new MouseMessage(getMouseButton(e), POCodes.Click.MOVED, e.getX(), e.getY());
		synchronized(_mouseQueue) {
			_mouseQueue.add(m);
		}
		
	}

	//////////////////////////
	//Get mouse wheel movement
	/**
	 * When the mouse wheel is moved, create a MouseWheelMessage and add it to the queue.
	 */
	public void mouseWheelMoved(MouseWheelEvent e) {
		MouseWheelMessage m = new MouseWheelMessage(e.getWheelRotation());
		synchronized(_mouseWheelQueue) {
			_mouseWheelQueue.add(m);
		}
	}
	
	////////////////////////////////
	//Get OSC messages from illposed
	/**
	 * Accepts and handles messages recieved by osc server.
	 * Called from within illposed library.
	 */
	public void acceptMessage(Date time, com.illposed.osc.OSCMessage message) {
		OscMessage m = new OscMessage(message);
		synchronized(_oscQueue) {
			_oscQueue.add(m);
		}
	}
	
	/**
	 * Finds the proper mouse button code from a Swing mouse event.
	 * Use instead of e.getButton() because it may not work for some events on some systems.
	 * @param e		the MouseEvent
	 * @return		the mouse button code
	 */
	public static POCodes.Button getMouseButton(MouseEvent e) {
		if(SwingUtilities.isLeftMouseButton(e))
			return POCodes.Button.LEFT;
		else if(SwingUtilities.isMiddleMouseButton(e))
			return POCodes.Button.MIDDLE;
		else if(SwingUtilities.isRightMouseButton(e))
			return POCodes.Button.RIGHT;
		else
			return POCodes.Button.NO;
	}
	
}
