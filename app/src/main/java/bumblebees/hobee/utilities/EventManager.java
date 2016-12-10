package bumblebees.hobee.utilities;


import java.util.ArrayList;
import java.util.HashMap;

import bumblebees.hobee.objects.Event;

public class EventManager {

    private ArrayList<Event> hostedEvents = new ArrayList<>();
    private ArrayList<Event> acceptedEvents = new ArrayList<>();
    private ArrayList<Event> pendingEvents = new ArrayList<>();
    private HashMap<String, ArrayList<Event>> eligibleEventList = new HashMap<>();




    public ArrayList<Event> getAcceptedEvents() {
        return acceptedEvents;
    }

    public ArrayList<Event> getHostedEvents() {
        return hostedEvents;
    }

    public ArrayList<Event> getPendingEvents() {
        return pendingEvents;
    }

    public void addHostedEvent(Event event){
        if(hostedEvents.contains(event)){
            hostedEvents.remove(event);
        }
        hostedEvents.add(event);
    }

    public void addAcceptedEvent(Event event){
        if(acceptedEvents.contains(event)){
            acceptedEvents.remove(event);
        }
        acceptedEvents.add(event);
    }

    public void addPendingEvent(Event event){
        if(pendingEvents.contains(event)){
            pendingEvents.remove(event);
        }
        pendingEvents.add(event);
    }

    public void removePendingEvent(Event event){
        if(pendingEvents.contains(event)){
            pendingEvents.remove(event);
        }
    }

    public HashMap<String, ArrayList<Event>> getEligibleEventList() {
        if(eligibleEventList.isEmpty()){
            //we pretend these are the hobbies for now
            String[] hobbies = {"basketball", "football", "fishing", "cooking"};
            for(int i=0; i<hobbies.length;i++) {
                eligibleEventList.put(hobbies[i], new ArrayList<Event>());
            }
        }
        return eligibleEventList;
    }

    public void addEligibleEvent(String hobby, Event event){
        if(eligibleEventList.get(hobby).contains(event)){
            eligibleEventList.get(hobby).remove(event);
        }
        eligibleEventList.get(hobby).add(event);
    }

    public void removeEligibleEvent(String hobby, Event event){
        if(eligibleEventList.get(hobby).contains(event)){
            eligibleEventList.get(hobby).remove(event);
        }
    }

    public void removeHostedEvent(Event event){
        if(hostedEvents.contains(event)){
            hostedEvents.remove(event);
        }
    }

}
