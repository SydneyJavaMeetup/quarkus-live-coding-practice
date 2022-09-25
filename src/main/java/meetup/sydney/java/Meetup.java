package meetup.sydney.java;

import org.bson.types.ObjectId;

public record Meetup(ObjectId _id, String greeting) {
    public Meetup(String greeting) {
        this(null, greeting);
    }
}
