package samples.authentication;

import java.util.HashSet;
import java.util.Set;

import org.webbitserver.BaseWebSocketHandler;
import org.webbitserver.WebSocketConnection;
import org.webbitserver.handler.authentication.BasicAuthenticationHandler;

/**
 * WebSocket handler that keeps track of who is connected and broadcasts to other users.
 */
public class WhoAmIWebSocketHandler extends BaseWebSocketHandler {

    private final Set<WebSocketConnection> connections = new HashSet<WebSocketConnection>();

    @Override
    public void onOpen(WebSocketConnection connection) throws Exception {
        String username = (String) connection.data(BasicAuthenticationHandler.USERNAME);
        connection.send("Hello " + username);

        for (WebSocketConnection otherConnection : connections) {
            otherConnection.send("You have been joined by " + username);
        }
        connections.add(connection);
    }

    @Override
    public void onClose(WebSocketConnection connection) throws Exception {
        String username = (String) connection.data(BasicAuthenticationHandler.USERNAME);
        connections.remove(connection);

        for (WebSocketConnection otherConnection : connections) {
            otherConnection.send(username + " has left");
        }
    }
}
