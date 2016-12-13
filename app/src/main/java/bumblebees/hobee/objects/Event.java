package bumblebees.hobee.objects;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

import bumblebees.hobee.utilities.Profile;


public class Event {
    private UUID eventID;
    private String type;
    private String timestamp;
    private EventDetails event_details;
    private String location;

    public Event(){

    }

    public Event(UUID eventID, String type, String timestamp, EventDetails event_details, String location) {
        this.eventID = eventID;
        this.type = type;
        this.timestamp = timestamp;
        this.event_details = event_details;
        this.location = location;
    }

    public String getType(){
        return type;
    }

    public String getTimestamp(){
        return timestamp;
    }

    public EventDetails getEvent_details() {
        return event_details;
    }

    public UUID getEventID() {
        return eventID;
    }

    public String toString(){
        return "Event ID " + eventID.toString() + " Event Type " + type + " Event timestamp " +
                timestamp + event_details;
    }

    /**
     * Retrieves the topic where the event is posted, including the "geo" prefix and the "event/hobby/" prefix.
     * @return
     */
    public String getTopic(){
        return "geo/"+location+"/event/hobby/"+type+"/"+eventID;
    }

    /**
     * Retrieves the topic where the event will be found when subscribing with a wildcard (#) instead of the ID.
     * @return
     */
    public String getSubscribeTopic(){
        return "geo/"+location+"/event/hobby/"+type+"/#";
    }

    //equals method overwritten for use when adding and removing events from ArrayLists
    //an event is considered equal with another when its ID, type and timestamp match
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (eventID != null ? !eventID.equals(event.eventID) : event.eventID != null) return false;
        if (type != null ? !type.equals(event.type) : event.type != null) return false;
        return timestamp != null ? timestamp.equals(event.timestamp) : event.timestamp == null;

    }

    @Override
    public int hashCode() {
        int result = eventID != null ? eventID.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    public boolean isCurrentUserHost(){
        return (event_details.getHost_id().equals(Profile.getInstance().getUserID())) ;
     }

    public Boolean checkUnranked(User user){
       return (this.getEvent_details().getUsers_unranked().contains(user.getUserID()));
    }

}



