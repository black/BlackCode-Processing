package projms.library.consumer;

import processing.core.*;
import java.lang.reflect.Method;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.log4j.Logger;

/**
 * Class to consume messages received by a JMS message broker
 * 
 * @author Hauke Altmann <hauke@grlg.org>
 */
public class Consumer {
	private transient Connection connection;
    private transient Session session;
    protected static transient ConnectionFactory factory;
    Method onMessageArrival;
    PApplet parent;
    private static final Logger log = Logger.getLogger(Consumer.class.getName());
    public final static String VERSION = "0.1";

    
    /**
     * Constructor 
     * 
     * @param parent the processing applet using the consumer
     * @param brokerURI the URI the consumer should use
     * @param topic the topic the consumer should subscribe to
     */
    public Consumer(PApplet parent, String brokerURI, String topic) {
    	this.parent = parent;
    	parent.registerDispose(this);
    	try{
    	  Class[] parameterTypes = new Class[]{ String.class, String.class }; 
    	  onMessageArrival = parent.getClass().getMethod("onMessageArrival",  parameterTypes);
	
          factory = new ActiveMQConnectionFactory(brokerURI);
      	  connection = factory.createConnection();
          connection.start();
          session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
          Destination destination = this.getSession().createTopic(topic);
      	  MessageConsumer messageConsumer = this.getSession().createConsumer(destination);
      	  
      	  Listener listener = new Listener();
      	  messageConsumer.setMessageListener(listener);
        }catch(JMSException ex){
        	log.error(ex.getMessage());
        } catch (NoSuchMethodException e) {
        	log.error(e.getMessage());
		} catch (SecurityException e) {
			log.error(e.getMessage());
		}
    }
    
    /**
     * Closes all connections
     * 
     * @throws JMSException
     */
    protected void close() throws JMSException {
        if (connection != null) {
            connection.close();
        }
        if (session != null) {
        	session.close();
        }
    }    
    
    /**
     * Closes all connections
     */
    public void dispose(){
    	try {
			this.close();
		} catch (JMSException e) {
			log.error(e.getMessage());
		}
    }
    
    /**
     * Returns the JMS Session
     * 
     * @return session the JMS session used
     */
    public Session getSession() {
    	return session;
    }
    
    /**
     * Dispatches the message received by calling the onMessageArrival method of 
     * the parent object
     * 
     * @param messageText the text of the message received
     * @param messageType the type of the message received
     */
    public void onMessageArrival(String messageText, String messageType){
    	if (onMessageArrival != null) {
    	    try {
    	      onMessageArrival.invoke(parent, messageText, messageType);
    	    } catch (Exception e) {
    	      log.error(e.getMessage());
    	      onMessageArrival = null;
    	    }
    	}

    }
    
	/**
	 * return the version of the library.
	 * 
	 * @return String
	 */
	public static String version() {
		return VERSION;
	}
    
	/**
	 * Listener class for receiving Textmessages
	 * 
	 * @author Hauke Altmann <hauke@grlg.org>
	 */
    class Listener implements MessageListener {
        //do this when a message arrives
    	public void onMessage(Message message) {
    		try {
    	         onMessageArrival(((ActiveMQTextMessage)message).getText(), ((ActiveMQTextMessage)message).getStringProperty("TYPE"));
    	         log.info("Message arrived: TYPE: " + ((ActiveMQTextMessage)message).getStringProperty("TYPE"));
    	         log.debug("TEXT: " + ((ActiveMQTextMessage)message).getText());
    		} catch (Exception e) {
    			log.error(e.getMessage());
    		}
    	}
    }
}


