package bumblebees.hobee.objects;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Andres on 2016-11-17.
 */

public class User {

    private String userID, loginId;
    private String origin, firstName, lastName, birthday, email, gender, bio;
    private Date created;
    private Rank rank;
    private List<Hobby> hobbies;

    public String getUserID() {
        return userID;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getOrigin() {
        return origin;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getBio() {
        return bio;
    }

    public Date getCreated() {
        return created;
    }

    public Rank getRank(){
        return rank;
    }

    public SimpleUser getSimpleUser(){
        return new SimpleUser(userID, firstName, lastName);
    }

    public User(String userID, String loginId, String origin, String firstName, String lastName, String birthday, String email, String gender, String bio, Date created, Rank rank, List<Hobby> hobbies) {
        this.userID = userID;
        this.loginId = loginId;
        this.origin = origin;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.email = email;
        this.gender = gender;
        this.bio = bio;
        this.created = created;
        this.rank = rank;
        this.hobbies = hobbies;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }
}
