package bumblebees.hobee.objects;

import java.util.UUID;

/**
 * Created by Andres on 2016-11-17.
 */

public class User {
    private UUID userID;
    private String origin, firstName, lastName, birthday, email, gender, pic;

    public UUID getUserID() {
        return userID;
    }
}
