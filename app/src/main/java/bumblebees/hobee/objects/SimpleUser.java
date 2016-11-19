package bumblebees.hobee.objects;



public class SimpleUser {

    private String userID, name;

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public SimpleUser(String userID, String name) {
        this.userID = userID;
        this.name = name;
    }

    public SimpleUser(String userID, String firstName, String lastName){
        this.userID = userID;
        this.name=firstName+" "+lastName;
    }

    public User getUser(){
        User user = null;
        //TODO: connect to the database and retrieve the full user, create the object and return it
        return user;
    }

    public String toString(){
        return this.getUserID() + " " + this.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleUser that = (SimpleUser) o;

        if (!userID.equals(that.userID)) return false;
        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        int result = userID.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
