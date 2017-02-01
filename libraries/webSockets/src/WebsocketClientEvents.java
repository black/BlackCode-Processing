package websockets;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import processing.core.PApplet;


/**
 * 
 * @author Lasse Steenbock Vestergaard
 *
 * Class responsible for handling all websocket events 
 *
 */
@WebSocket
public class WebsocketClientEvents {
	private Session session;
	CountDownLatch latch = new CountDownLatch(1);
	private PApplet parent;
	private Method onMessageEvent;

	public WebsocketClientEvents(PApplet p, Method event) {
		parent = p;
		onMessageEvent = event;
	}

	/**
	 * 
	 * Sending incoming messages to the Processing sketch's websocket event function 
	 * 
	 * @param session The connection between server and client
	 * @param message The received message
	 * @throws IOException If no event fonction is registered in the Processing sketch then an exception is thrown, but it will be ignored
	 */
	@OnWebSocketMessage
	public void onText(Session session, String message) throws IOException {
		if (onMessageEvent != null) {
			try {
				onMessageEvent.invoke(parent, message);
			} catch (Exception e) {
				System.err
						.println("Disabling webSocketEvent() because of an error.");
				e.printStackTrace();
				onMessageEvent = null;
			}
		}
	}

	/**
	 * 
	 * Handling establishment of the connection
	 * 
	 * @param session The connection between server and client
	 */
	@OnWebSocketConnect
	public void onConnect(Session session) {
		this.session = session;
		latch.countDown();
	}

	/**
	 * 
	 * Sends message to the websocket server
	 * 
	 * @param str The message to send to the server
	 */
	public void sendMessage(String str) {
		try {
			session.getRemote().sendString(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Handles errors occurring and writing them to the console 
	 * 
	 * @param cause The cause of an error
	 */
	@OnWebSocketError
	public void onError(Throwable cause) {
		System.out.printf("onError(%s: %s)%n",cause.getClass().getSimpleName(), cause.getMessage());
		cause.printStackTrace(System.out);
	}

	public CountDownLatch getLatch() {
		return latch;
	}
}
