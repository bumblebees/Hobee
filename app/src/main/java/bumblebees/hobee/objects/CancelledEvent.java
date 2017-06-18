package bumblebees.hobee.objects;


import java.util.UUID;

public class CancelledEvent {

    private UUID eventID;
    private String type;
    private String timestamp;
    private String status = "cancelled";
    private String reason;
    private String location;
    private String topic;

    public CancelledEvent(UUID eventID, String type, String timestamp, String status, String reason, String location, String topic) {
        this.eventID = eventID;
        this.type = type;
        this.timestamp = timestamp;
        this.status = status;
        this.reason = reason;
        this.location = location;
        this.topic = topic;
    }

    public UUID getEventID() {
        return eventID;
    }

    public String getLocation() {
        return location;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public String getTopic(){
        return topic;
    }




    public Event getBasicEvent(){
        return new Event(eventID, type, timestamp, new EventDetails(), location);
    }

}
