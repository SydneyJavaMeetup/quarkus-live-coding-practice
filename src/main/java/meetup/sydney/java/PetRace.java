package meetup.sydney.java;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

/**
 * The class is annotated with @ServerEndpoint to indicate that it is a web socket endpoint.
 * The class is also annotated with @ApplicationScoped to indicate that it is a CDI bean and a singleton.
 */
@ApplicationScoped
@ServerEndpoint("/pet-race")
public class PetRace {

    /**
     * Annotated with @OnOpen to indicate that it should be called when a new web socket connection is opened.
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        //print a message to the console
        System.out.printf("New connection opened %s%n", session.getId());
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        //print a message to the console
        System.out.printf("Message received on session %s: %s%n", session.getId(), message);
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        //print a message to the console
        System.out.printf("Connection closed %s%n", session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        //print a message to the console
        System.out.printf("Error %s%n", throwable.getMessage());
    }
}
