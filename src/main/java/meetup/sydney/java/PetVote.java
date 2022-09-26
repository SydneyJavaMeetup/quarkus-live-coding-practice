package meetup.sydney.java;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record PetVote(String _id, int votes) { }
