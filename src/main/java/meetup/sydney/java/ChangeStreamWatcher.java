package meetup.sydney.java;

import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.changestream.FullDocument;
import io.quarkus.mongodb.ChangeStreamOptions;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.subscription.Cancellable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

@ApplicationScoped
public class ChangeStreamWatcher {
    @Inject
    ReactiveMongoClient reactiveMongoClient;

    @Inject
    Event<PetVote> changeStreamEvent;

    private Cancellable cancellableChangeStreamSubscription;

    //observe startup
    public void onStart(@Observes StartupEvent startup) {
        var options = new ChangeStreamOptions()
                .fullDocument(FullDocument.UPDATE_LOOKUP);

        reactiveMongoClient
                .getDatabase("SydneyJava")
                .getCollection("petRace", PetVote.class)
                .updateOne(eq("_id", "init"), set("votes", 0), new UpdateOptions().upsert(true))
                .subscribe()
                .with(updateResult -> {
                    System.out.println("Initialised collection");
                    cancellableChangeStreamSubscription =
                            reactiveMongoClient
                                    .getDatabase("SydneyJava")
                                    .getCollection("petRace", PetVote.class)
                                    .watch(PetVote.class, options)
                                    .subscribe()
                                    .with(changeStreamDocument -> {
                                        //fire the change stream event
                                        PetVote event = changeStreamDocument.getFullDocument();
                                        changeStreamEvent.fire(event);
                                    }, throwable -> {
                                        //print the error to the console
                                        System.out.println(throwable);
                                    });
                }, throwable -> {
                    System.out.println("Error initialising collection");
                    throwable.printStackTrace();
                })
        ;
    }

    //observe shutdown
    public void onStop(@Observes ShutdownEvent shutdownEvent) {
        System.out.println("Stopping watcher");
        //unsubscribe from the change stream
        cancellableChangeStreamSubscription.cancel();
    }
}