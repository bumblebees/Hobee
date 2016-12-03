package bumblebees.hobee.objects;


import com.google.gson.Gson;

import org.json.JSONObject;

import bumblebees.hobee.utilities.SocketIO;

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


    public User getUser(){
        User user = null;
        //TODO: connect to the database and retrieve the full user, create the object and return it

        Gson gson = new Gson();
        JSONObject json = null;
        SocketIO socket = SocketIO.getInstance();
        /*try {
            String str = "{ \"_id\" : ObjectId(\"58361486bdac62536292118f\"), \"bio\" : \"Just beat it\", \"birthday\" : \"1958/8/29\",\"created\" : \"Nov 23, 2016 23:13:25\", \"email\" : \"gusdebana@student.gu.se\", \"firstName\" : \"Michael\", \"gender\" : \"male\", \"hobbies\" : [ ], \"lastName\" : \"Jackson\", \"loginId\" : \"107506649956007618745\", \"origin\" : \"google\", \"rank\" : { \"globalRank\" : 0, \"hostRank\" : 0, \"noShows\" : 0 }, \"userID\" : \"37727c0b-9f40-4d74-bdb9-b2cda873dfe2\" }";
            json = new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        */
        user = gson.fromJson(String.valueOf(json),User.class);

        return user;
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
