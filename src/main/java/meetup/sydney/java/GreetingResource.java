package meetup.sydney.java;

import com.mongodb.client.MongoClient;
import com.mongodb.client.result.InsertOneResult;
import org.bson.BsonDocument;
import org.bson.BsonValue;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Path("/meetup")
public class GreetingResource {

    @Inject
    MongoClient mongoClient;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String greet() {
        Optional<Meetup> firstMeetup = Optional.ofNullable(mongoClient
                .getDatabase("SydneyJava")
                .getCollection("meetup", Meetup.class)
                .find()
                .first());

        return firstMeetup.orElse(new Meetup("Hello Sydney Java Meetup!")).greeting();
    }

    @GET
    @Path("/insert-greeting")
    @Produces(MediaType.APPLICATION_JSON)
    public Meetup insertGreeting(@QueryParam("greeting") String greeting) {
        if (greeting == null) {
            throw new BadRequestException("Greeting query parameter is required", Response.status(400).entity("Please supply a greeting query parameter").build());
        }
        Meetup meetup = new Meetup(greeting);
        InsertOneResult insertOneResult = mongoClient
                .getDatabase("SydneyJava")
                .getCollection("meetup", Meetup.class)
                .insertOne(meetup);

        if (insertOneResult.wasAcknowledged()) {
            BsonValue insertedId = insertOneResult.getInsertedId();
            meetup = new Meetup(insertedId.asObjectId().getValue(), meetup.greeting());
        } else {
            throw new WebApplicationException("Database insert failed", 500);
        }

        return meetup;
    }


    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Meetup> list() {
        return mongoClient
                .getDatabase("SydneyJava")
                .getCollection("meetup", Meetup.class)
                .find().into(new ArrayList<>());
    }

    @GET
    @Path("/clear-greetings")
    @Produces(MediaType.APPLICATION_JSON)
    public String clearGreetings() {
        mongoClient
                .getDatabase("SydneyJava")
                .getCollection("meetup", Meetup.class)
                .deleteMany(new BsonDocument());

        return "Greetings cleared";
    }
}