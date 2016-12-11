package bumblebees.hobee.objects;


import java.util.UUID;

public class CancelledEvent {

    private UUID eventID;
    private String type;
    private String timestamp;
    private String status = "cancelled";
    private String reason;
    private String location;

    public CancelledEvent(UUID eventID, String location, String reason, String status, String timestamp, String type) {
        this.eventID = eventID;
        this.location = location;
        this.reason = reason;
        this.status = status;
        this.timestamp = timestamp;
        this.type = type;
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




    public Event getBasicEvent(){
        return new Event(eventID, type, timestamp, new EventDetails(), location);
    }

}
