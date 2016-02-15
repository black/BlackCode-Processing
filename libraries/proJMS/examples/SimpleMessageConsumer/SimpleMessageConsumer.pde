import projms.library.consumer.Consumer;
import org.apache.log4j.PropertyConfigurator;
import java.util.UUID;

Consumer messageConsumer;
//The topic can be any string, the publisher that sends messages needs to use the same topic
String messagingTopic = "proJMS.SimpleExample";
String t = "";

void setup() {
  PropertyConfigurator.configure(sketchPath+"/log4j.properties");
  size(400,400);
  smooth();
  
  //initialize the message consumer
  //- first parameter is this applet
  //- second parameter connects the client to the group "group1" and generates a random id as client id.
  //  Check the documentation for other protocols that can be used here.
  //- third parameter specifies for which topic messages are received. This needs to be the same topic 
  //  the publisher is using for sending messages
  messageConsumer = new Consumer(this, "peer://group1/" + UUID.randomUUID().toString(), messagingTopic);
}

void draw() {
  background(0);
  fill(255);
  text(t, 125, 200);
}

//Whenever a message arrives this method is called
void onMessageArrival(String messageText, String messageType){
	//different message types can be handled differently here
	if(messageType.equals("STATUS")){
    	t = messageText;
	}
}
