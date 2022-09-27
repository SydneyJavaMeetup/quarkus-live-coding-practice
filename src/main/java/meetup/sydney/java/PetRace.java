package meetup.sydney.java;

import com.mongodb.client.MongoClient;
import com.mongodb.client.model.UpdateOptions;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.inc;

/**
 * The class is annotated with @ServerEndpoint to indicate that it is a web socket endpoint.
 * The class is also annotated with @ApplicationScoped to indicate that it is a CDI bean and a singleton.
 */
@ApplicationScoped
@ServerEndpoint("/pet-race")
public class PetRace {
    Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Inject
    MongoClient mongoClient;


    public void onChangeStream(@Observes PetVote changeStreamEvent) {
//        System.out.println("Received change stream event from Mongo: " + changeStreamEvent);
        broadcast("%s:%d".formatted(changeStreamEvent._id(), changeStreamEvent.votes()));
    }

    /**
     * Annotated with @OnOpen to indicate that it should be called when a new web socket connection is opened.
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        //print a message to the console
        System.out.printf("New connection opened %s%n", session.getId());
        sessions.put(session.getId(), session);
        mongoClient.getDatabase("SydneyJava")
                .getCollection("petRace", PetVote.class)
                .find()
                .forEach(petVote -> {
                    session.getAsyncRemote().sendText("%s:%d".formatted(petVote._id(), petVote.votes()));
                });
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        //print a message to the console
//        System.out.printf("Message received on session %s: %s%n", session.getId(), message);
        //if the message is a vote for dog, increment the dog vote count in MongoDB
        var pet = switch(message) {
            case "vote:dog" -> "dog";
            case "vote:cat" -> "cat";
            default -> throw new IllegalArgumentException("Unknown pet: " + message);
        };
        mongoClient.getDatabase("SydneyJava")
                .getCollection("petRace")
                .updateOne(eq("_id", pet), inc("votes", 1),
                new UpdateOptions().upsert(true));
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        //print a message to the console
        System.out.printf("Connection closed %s%n", session.getId());
        sessions.remove(session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        //print a message to the console
        System.out.printf("Error %s%n", throwable.getMessage());
    }

    private void broadcast(String message) {
        sessions.values().forEach(s -> {
            s.getAsyncRemote().sendObject(message, result ->  {
                if (result.getException() != null) {
                    if (result.getException().toString() == "io.netty.channel.StacklessClosedChannelException") {
                        System.out.println("Error sending message to closed socket, removing from list of sockets: " + s.getId());
                        sessions.remove(s.getId());
                    }
                    System.out.println("Unable to send message: " + result.getException());
                }
            });
        });
    }


}
