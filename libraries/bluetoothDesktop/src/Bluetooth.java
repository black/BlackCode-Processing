package bluetoothDesktop;

import processing.core.*;

import java.io.*;
import java.util.*;
import javax.microedition.io.*;
import javax.bluetooth.*;

/**
 * This originated from the Mobile Processing project - http://mobile.processing.org
 *
 * Ported to Processing by, http://www.extrapixel.ch/bluetooth/
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 *
 * @author  Francis Li
 * @author  extrapixel
 */
public class Bluetooth implements DiscoveryListener, Runnable {
    /** Default fake UUID that will probably never be used. Also used as
     * a synchronization object- wierd behavior was occuring when synchronizing
     * on (this)...
     */
    public static final String UUID_DEFAULT = "102030405060708090A0B0C0D0E0F010";
    /** short UUID assigned to Serial Port Profile */
    public static final long UUID_SERIALPORT = 0x1101;
    public static final long UUID_OBEX = 0x0008;
    public static final long UUID_HTTP = 0x000C;
    public static final long UUID_L2CAP = 0x0100;
    public static final long UUID_BNEP = 0x000F;
    public static final long UUID_RFCOMM = 0x0003;    
    
    /*
    public static final int EVENT_DISCOVER_DEVICE               = 1;
    public static final int EVENT_DISCOVER_DEVICE_COMPLETED     = 2;
    public static final int EVENT_DISCOVER_SERVICE              = 3;
    public static final int EVENT_DISCOVER_SERVICE_COMPLETED    = 4;
    public static final int EVENT_CLIENT_CONNECTED              = 5;
    */
    
    protected PApplet                   parent;
    protected LocalDevice               local;
    protected DiscoveryAgent            agent;
    protected javax.bluetooth.UUID                      uuid;
    
    protected Vector                    devices;
    protected Thread                    serverThread;
    protected Thread                    discoverThread;
    
    /** Transaction id for the service search process, must be positive if searching */
    protected int                       transId;
    /** Whether or not to find exactly this service */
    protected boolean                   find;
    /** List of matching services found */
    protected Vector                    services;
    /** Actual server notifier object that will be used to get client connections */
    protected StreamConnectionNotifier  server;
    
    java.lang.reflect.Method deviceDiscoverEventMethod;
    java.lang.reflect.Method deviceDiscoveryCompleteEventMethod;
    java.lang.reflect.Method serviceDiscoverEventMethod;
    java.lang.reflect.Method serviceDiscoveryCompleteEventMethod;
    java.lang.reflect.Method clientConnectEventMethod;
    
    public Bluetooth(PApplet parent) {
        this(parent, UUID_DEFAULT);
    }
    
    public Bluetooth(PApplet parent, String id) {
        this.parent = parent;
        devices = new Vector();
        services = new Vector();
        try {
            local = LocalDevice.getLocalDevice();
            agent = local.getDiscoveryAgent();
            uuid = new javax.bluetooth.UUID(id, false);
        } catch (BluetoothStateException bse) {
            throw new RuntimeException(bse.getMessage());
        }
        
        registerWithParent();
        
    }
        
    public Bluetooth(PApplet parent, long id) {
        this.parent = parent;
        devices = new Vector();
        services = new Vector();
        try {
            local = LocalDevice.getLocalDevice();
            agent = local.getDiscoveryAgent();
            uuid = new javax.bluetooth.UUID(id);
        } catch (BluetoothStateException bse) {
            throw new RuntimeException(bse.getMessage());
        }
        
		registerWithParent();
    }
    
    
        
    private void registerWithParent() {
    	parent.registerDispose(this);
    	// check to see if the host applet implements
    	// some of our freaky methods
    	try {
      		deviceDiscoverEventMethod =
        	parent.getClass().getMethod("deviceDiscoverEvent",
                                    new Class[] { Device.class });
    	} catch (Exception e) {
      		// no such method, or an error.. which is fine, just ignore
    	}
    	
    	try {
      		deviceDiscoveryCompleteEventMethod =
        	parent.getClass().getMethod("deviceDiscoveryCompleteEvent",
                                    new Class[] { Device[].class });
    	} catch (Exception e) {
      		// no such method, or an error.. which is fine, just ignore
    	}
    	
    	try {
      		serviceDiscoverEventMethod =
        	parent.getClass().getMethod("serviceDiscoverEvent",
                                    new Class[] { Service[].class });
    	} catch (Exception e) {
			// no such method, or an error.. which is fine, just ignore
    	}
    	
    	try {
      		serviceDiscoveryCompleteEventMethod =
        	parent.getClass().getMethod("serviceDiscoveryCompleteEvent",
                                    new Class[] { Service[].class });
    	} catch (Exception e) {
	      // no such method, or an error.. which is fine, just ignore
    	}
    	
    	try {
      		clientConnectEventMethod =
        	parent.getClass().getMethod("clientConnectEvent",
                                    new Class[] { Client.class });
    	} catch (Exception e) {
		    // no such method, or an error.. which is fine, just ignore
    	}
    
    }
    
    // magically called when the PApplet exits
    public void dispose() {
    	stop();
    	//System.out.println("extraBluetooth disposed.");
    }

    public void discover() {
        boolean start = false;
        synchronized (UUID_DEFAULT) {
            if (discoverThread == null) {
                discoverThread = new Thread(this);
                start = true;
            }
        }
        if (start) {
            find = false;
            discoverThread.start();
        }
    }    
    
    public void find() {
        boolean start = false;
        synchronized (UUID_DEFAULT) {
            if (discoverThread == null) {
                discoverThread = new Thread(this);
                start = true;
            }
        }
        if (start) {
            find = true;
            discoverThread.start();
        }
    }
    
    public void cancel() {
        boolean cancelled = false;
        synchronized (UUID_DEFAULT) {
            if (discoverThread != null) {
                discoverThread = null;
                cancelled = true;
            }
        }
        if (cancelled) {
            agent.cancelInquiry(this);
            cancelled = false;
            synchronized (services) {
                if (transId > 0) {
                    agent.cancelServiceSearch(transId);
                    cancelled = true;
                }
            }
        }
    }
    

    
    public void run() {
        try {
            devices.removeAllElements();
            services.removeAllElements();
            synchronized (devices) {
                //// start inquiry
                agent.startInquiry(DiscoveryAgent.GIAC, this);
                try {
                    //// block and wait until complete
                    devices.wait();                    
                } catch (InterruptedException ie) { }
            }
            //// check if cancelled first
            boolean cancelled = false;
            synchronized (UUID_DEFAULT) {
                if (discoverThread != Thread.currentThread()) {
                    cancelled = true;
                }
            }
            if (!cancelled) {
                //// if not, get name for each device
                Enumeration e = devices.elements();
                Device d;
                String name;
                while (e.hasMoreElements()) {
                    d = (Device) e.nextElement();
                    try {
                        name = d.device.getFriendlyName(false);
                        if (name != null) {
                            d.name = name;
                        }
                    } catch (IOException ioe) { }
                }
            }
            //// copy into array
            Device[] devices = new Device[this.devices.size()];
            this.devices.copyInto(devices);            
            //// fire event back into parent
            deviceDiscoveryCompleteEvent(devices);
            
            //// if find is true, look for this specific service
            if (find) {
                for (int i = 0, length = devices.length; i < length; i++) {
                    synchronized (UUID_DEFAULT) {
                        if (discoverThread != Thread.currentThread()) {
                            cancelled = true;
                        }
                    }
                    if (cancelled) {
                        break;
                    }
                    synchronized (services) {
                        transId = agent.searchServices(new int[] { Service.ATTR_SERVICENAME, Service.ATTR_SERVICEDESC, Service.ATTR_PROVIDERNAME }, 
                                                       new javax.bluetooth.UUID[] { uuid }, devices[i].device, this);      
                        try {
                            services.wait();
                        } catch (InterruptedException ie) { }
                        transId = 0;
                    }                    
                }
                //// fire event
                Service[] data = new Service[services.size()];
                services.copyInto(data);
                serviceDiscoveryCompleteEvent(data);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            //// clear all results so we don't hold any lingering references
            devices.removeAllElements();
            services.removeAllElements();
        }
        synchronized (UUID_DEFAULT) {
            discoverThread = null;
        }
    }
    
    public void start(String name) {
        if (serverThread == null) {
            try {
                local.setDiscoverable(DiscoveryAgent.GIAC);
                String url = "btspp://localhost:" + uuid.toString() + ";name=" + name;
                server = (StreamConnectionNotifier) Connector.open(url);             
                ServiceRecord record = local.getRecord(server);
                //// set availability to fully available
                record.setAttributeValue(0x0008, new DataElement(DataElement.U_INT_1, 0xFF));
                //// set device class to telephony
                //record.setDeviceServiceClasses(0x400000);
                //// set up a service for this record and set it up as the thread
                Service s = new Service(null, record, this);
                serverThread = new Thread(s);
                serverThread.start();
            } catch (Exception e) {
                serverThread = null;
                throw new RuntimeException(e.getMessage());
            }
        }
    }
    
    public void stop() {
        serverThread = null;
    }

    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
        Device device = new Device(btDevice, this);
        devices.addElement(device);
        deviceDiscoverEvent(device);
    }

    public void inquiryCompleted(int discType) {
        synchronized (devices) {
            //// wake up discover thread
            devices.notifyAll();
        }
    }

    public void serviceSearchCompleted(int transId, int respCode) {
        synchronized (services) {
            if (this.transId == transId) {
                services.notifyAll();
            }
        }
    }

    public void servicesDiscovered(int transId, ServiceRecord[] servRecord) {
        boolean valid = false;
        synchronized (services) {
            if (this.transId == transId) {
                valid = true;
            }
        }
        if (valid && (servRecord.length > 0)) {
            //// find matching device object (should change vector to hashtable to optimize this)
            Enumeration e = devices.elements();
            RemoteDevice host = servRecord[0].getHostDevice();
            Device d = null;
            boolean found = false;
            while (e.hasMoreElements()) {
                d = (Device) e.nextElement();
                if (d.device.equals(host)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                //// not sure this should _ever_ happen
                d = new Device(host, this);
            }
            //// build service list for event callback
            DataElement element;
            Service[] data = new Service[servRecord.length];
            for (int i = 0, length = servRecord.length; i < length; i++) {
                data[i] = new Service(d, servRecord[i], this);
                //// add to found services list
                services.addElement(data[i]);
            }
            //// fire event 
            serviceDiscoverEvent(data);
        }
    }
    
	public void deviceDiscoverEvent(Device d) {
		if (deviceDiscoverEventMethod != null) {
    		try {
      			deviceDiscoverEventMethod.invoke(parent, new Object[] { d });
    		} catch (Exception e) {
				System.err.println("Disabling deviceDiscoverEvent() for " + "??" +
                         " because of an error.");
      			e.printStackTrace();
      			deviceDiscoverEventMethod = null;
    		}
  		}
	}
	
	public void deviceDiscoveryCompleteEvent(Device[] d) {
		if (deviceDiscoveryCompleteEventMethod != null) {
    		try {
      			deviceDiscoveryCompleteEventMethod.invoke(parent, new Object[] { d });
    		} catch (Exception e) {
				System.err.println("Disabling deviceDiscoveryCompleteEvent() for " + "??" +
                         " because of an error.");
      			e.printStackTrace();
      			deviceDiscoveryCompleteEventMethod = null;
    		}
  		}
	}
	
	public void serviceDiscoverEvent(Service[] s) {
		if (serviceDiscoverEventMethod != null) {
    		try {
      			serviceDiscoverEventMethod.invoke(parent, new Object[] { s });
    		} catch (Exception e) {
				System.err.println("Disabling serviceDiscoverEvent() for " + "??" +
                         " because of an error.");
      			e.printStackTrace();
      			serviceDiscoverEventMethod = null;
    		}
  		}
	}
	
	public void serviceDiscoveryCompleteEvent(Service[] s) {
		if (serviceDiscoveryCompleteEventMethod != null) {
    		try {
      			serviceDiscoveryCompleteEventMethod.invoke(parent, new Object[] { s });
    		} catch (Exception e) {
				System.err.println("Disabling serviceDiscoveryCompleteEvent() for " + "??" +
                         " because of an error.");
      			e.printStackTrace();
      			serviceDiscoveryCompleteEventMethod = null;
    		}
  		}
	}
	
	public void clientConnectEvent(Client c) {
		if (clientConnectEventMethod != null) {
    		try {
      			clientConnectEventMethod.invoke(parent, new Object[] { c });
    		} catch (Exception e) {
				System.err.println("Disabling clientConnectEvent() for " + "??" +
                         " because of an error.");
      			e.printStackTrace();
      			clientConnectEventMethod = null;
    		}
  		}
	}
}