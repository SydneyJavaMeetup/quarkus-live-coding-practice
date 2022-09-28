package meetup.sydney.java;

import com.mongodb.client.MongoClient;
import com.mongodb.client.model.UpdateOptions;
import io.quarkus.logging.Log;

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
        try {
            broadcast("%s:%d".formatted(changeStreamEvent._id(), changeStreamEvent.votes()));
        } catch (Exception e) {
            Log.error("Error broadcasting from change stream: ", e);
        }
    }

    /**
     * Annotated with @OnOpen to indicate that it should be called when a new web socket connection is opened.
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        //print a message to the console
        Log.info("New connection opened %s%n".formatted(session.getId()));
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
        Log.info("Connection closed %s%n".formatted(session.getId()));
        sessions.remove(session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        //print a message to the console
        Log.error("Error %s%n".formatted(throwable.getMessage()));
    }

    private void broadcast(String message) {
        sessions.values().forEach(s -> {
            s.getAsyncRemote().sendObject(message, result ->  {
                if (result.getException() != null) {
                    if (result.getException().toString().equals("io.netty.channel.StacklessClosedChannelException")) {
                        Log.error("Error sending message (channel closed)), removing from list of sockets: " + s.getId());
                        sessions.remove(s.getId());
                    }
                    if (result.getException().toString().equals("java.io.IOException: Connection reset by peer")) {
                        Log.error("Error sending message (connection reset by peer)), removing from list of sockets: " + s.getId());
                        sessions.remove(s.getId());
                    }
                    Log.error("Unable to send message to " + s.getId() + ": " + result.getException());
                }
            });
        });
    }
}
