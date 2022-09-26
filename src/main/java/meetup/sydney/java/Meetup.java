package meetup.sydney.java;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.bson.types.ObjectId;

@RegisterForReflection
public record Meetup(ObjectId _id, String greeting) {
    public Meetup(String greeting) {
        this(null, greeting);
    }
}
