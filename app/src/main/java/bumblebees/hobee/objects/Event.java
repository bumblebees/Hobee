package bumblebees.hobee.objects;

import java.security.Timestamp;
import java.util.UUID;



public class Event {
    private UUID eventID;
    private String type;
    private String timestamp;
    private EventDetails event_details;

    public Event(){

    }

    public Event(UUID eventID, String type, String timestamp, EventDetails event_details) {
        this.eventID = eventID;
        this.type = type;
        this.timestamp = timestamp;
        this.event_details = event_details;
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



}



