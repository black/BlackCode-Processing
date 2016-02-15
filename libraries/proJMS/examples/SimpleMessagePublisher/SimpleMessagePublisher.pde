import projms.library.publisher.Publisher;
import org.apache.log4j.PropertyConfigurator;
import java.util.UUID;

Publisher messagePublisher;
//The topic can be any string, the publisher that sends messages needs to use the same topic
String messagingTopic = "proJMS.SimpleExample";
//The message type can be any string. Different message can have different types. The consumer
//decides how two deal with different message types
String messageType = "STATUS";

void setup() {
  PropertyConfigurator.configure(sketchPath+"/log4j.properties");
  size(400,400);
  smooth();
  
  //initialize the message publisher
  //- first parameter connects the publisher to the group "group1" and generates a random id as id.
  //  Check the documentation for other protocols that can be used here.
  //- second parameter is this applet
  messagePublisher = new Publisher("peer://group1/" + UUID.randomUUID().toString(), this);
}

void draw() {
  background(0);
  fill(255);
}

void mousePressed() {
	 //sends a message of the type "messageType" to all consumers that listen on the "messagingTopic"
	 messagePublisher.sendMessage("Mouse has been pressed", messageType, messagingTopic);
}

void mouseDragged () {
	//sends a message of the type "messageType" to all consumers that listen on the "messagingTopic"
	messagePublisher.sendMessage("Mouse has been dragged", messageType, messagingTopic);
}

void mouseReleased() {
	//sends a message of the type "messageType" to all consumers that listen on the "messagingTopic"
	messagePublisher.sendMessage("Mouse has been released", messageType, messagingTopic);
}
