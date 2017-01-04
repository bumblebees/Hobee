package bumblebees.hobee.objects;


public class PublicUser {
    private String userID, name;

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public PublicUser(String userID, String name) {
        this.userID = userID;
        this.name = name;
    }

    public PublicUser(String userID, String firstName, String lastName){
        this.userID = userID;
        this.name=firstName+" "+lastName;
    }

    public String toString(){
        return this.getUserID() + " " + this.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PublicUser that = (PublicUser) o;

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
