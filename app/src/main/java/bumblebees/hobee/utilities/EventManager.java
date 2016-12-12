package bumblebees.hobee.utilities;


import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import bumblebees.hobee.objects.Event;
import bumblebees.hobee.objects.Hobby;
import bumblebees.hobee.objects.User;

public class EventManager {



    public enum UserStatus {HOST, NEW_ACCEPTED, OLD_ACCEPTED, PENDING, REJECTED, NEW_MATCH, OLD_MATCH, NONE};
    public enum EventStatus {HOSTED_EVENT, ACCEPTED_EVENT, PENDING_EVENT, EVENT_NOT_FOUND};


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

    /**
     * Check if the user belongs to the event, or if the user is eligible for the event.
     * @param user
     * @param event
     * @return enum corresponding to the user's position in the event
     * NEW_MATCH - the event is new for the user
     * OLD_MATCH - the user has been notified about the event already
     * NONE - the user does not match the event
     */
    public UserStatus processEvent(User user, Event event){
        if(event.getEvent_details().getHost_id().equals(user.getUserID())){
            addHostedEvent(event);
            return UserStatus.HOST;
        }
        if(event.getEvent_details().getUsers_accepted().contains(user.getSimpleUser())){
            if(acceptedEvents.contains(event)){
                return UserStatus.OLD_ACCEPTED;
            }
            else{
                removePendingEvent(event);
                acceptedEvents.add(event);
                return UserStatus.NEW_ACCEPTED;
            }
        }
        if(event.getEvent_details().getUsers_pending().contains(user.getSimpleUser())){
            addPendingEvent(event);
            return UserStatus.PENDING;
        }
        if(pendingEvents.contains(event)){
            removePendingEvent(event);
            return UserStatus.REJECTED;
        }
        if(matchesPreferences(event, user)){
            if(addEligibleEvent(event.getType(), event)){
                return UserStatus.NEW_MATCH;
            }
            else return UserStatus.OLD_MATCH;
        }
        return UserStatus.NONE;
    }

    /**
     * Cancel an event, if it exists in the Manager.
     * @param event
     * @return
     */
    public EventStatus cancelEvent(Event event){
        if(hostedEvents.contains(event)){
            hostedEvents.remove(event);
            return EventStatus.HOSTED_EVENT;
        }
        if(acceptedEvents.contains(event)){
            acceptedEvents.remove(event);
            return EventStatus.ACCEPTED_EVENT;
        }
        if(pendingEvents.contains(event)){
            pendingEvents.remove(event);
            return EventStatus.PENDING_EVENT;
        }
        return EventStatus.EVENT_NOT_FOUND;
    }

    public void addHostedEvent(Event event){
        if(hostedEvents.contains(event)){
            hostedEvents.remove(event);
        }
        hostedEvents.add(event);
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

    //returns true if the event is new
    //false otherwise
    public boolean  addEligibleEvent(String hobby, Event event){
        boolean res = true;
        if(eligibleEventList.get(hobby).contains(event)){
            eligibleEventList.get(hobby).remove(event);
            res = false;
        }
        eligibleEventList.get(hobby).add(event);
        return res;
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

    /**
     * Check if the event matches the user.
     * @param event event to be checked
     * @return true if they match, false otherwise
     */
    public boolean matchesPreferences(Event event, User user){
        //check if the user is already a member of the event or is the host
        if(event.getEvent_details().checkUser(user.getSimpleUser()) ||
                event.getEvent_details().getHost_id().equals(user.getUserID())){
            //user is in the event, does not need a notification
            return false;
        }

        //check if the age is larger than the max age, or smaller than the minimum age
        if(event.getEvent_details().getAge_max()<user.getAge() || event.getEvent_details().getAge_min() > user.getAge()){
            return false;
        }

        //check if there are gender restrictions to the event
        if(!event.getEvent_details().getGender().equals("everyone")){
            //check that the gender does not match the user's gender
            if(!event.getEvent_details().getGender().equals(user.getGender())){
                return false;
            }
        }
        // Check if the user has a hobby matching the event,
        // if true, check if day of week and time of day are matching too
        if (matchHobby(event, user) == true) {
            matchDayOfWeek(event, user);
            matchTimeOfDay(event, user);
        }

        return true;
    }

    /**
     *
     * @param event
     * @return true if hobby matches event, false otherwise
     */
    private boolean matchHobby(Event event, User user){
        for (Hobby hobby : user.getHobbies()){
            if (hobby.getName().equals(event.getEvent_details().getHobbyName())){
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param event
     * @return true if a day matches, false otherwise
     */
    private boolean matchDayOfWeek(Event event, User user) {
        for (Hobby hobby : user.getHobbies()) {
            if (hobby.getName().equals(event.getEvent_details().getHobbyName())) {
                for (String day : hobby.getDatePreference()) {
                    if (day.equals(event.getEvent_details().getDayOfTheWeek())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     *
     * @param event
     * @return true if time of day matches, false otherwise
     */
    private boolean matchTimeOfDay(Event event, User user){
        for (Hobby hobby : user.getHobbies()){
            if (hobby.getName().equals(event.getEvent_details().getHobbyName())){
                double eventTime = Double.parseDouble((event.getEvent_details().getTime()));
                if (eventTime >= hobby.getTimeFrom() && eventTime <= hobby.getTimeTo()){
                    return true;
                }
            }
        }
        return false;
    }


    public void findAndRemoveEvents(HashSet<String> topics, ArrayList<String> hobbies) {
        //TODO: this is complicated, maybe replace with a HashMap or something easier to search in


        ArrayList<Event> removedEvents = new ArrayList<>();
        for(Event event : hostedEvents){
            if(topics.contains(event.getSubscribeTopic())){
                removedEvents.add(event);
            }
        }
        hostedEvents.removeAll(removedEvents);
        removedEvents.clear();
        for(Event event : acceptedEvents){
            if(topics.contains(event.getSubscribeTopic())){
                removedEvents.add(event);
            }
        }
        acceptedEvents.removeAll(removedEvents);
        removedEvents.clear();
        for(Event event : pendingEvents){
            if(topics.contains(event.getSubscribeTopic())){
                removedEvents.add(event);
            }
        }
        pendingEvents.removeAll(removedEvents);
        removedEvents.clear();
        for(String hobby : hobbies) {
            if (eligibleEventList.get(hobby) != null) {
                for (Event event : eligibleEventList.get(hobby)) {
                    if (topics.contains(event.getSubscribeTopic())) {
                        removedEvents.add(event);
                    }
                }
                eligibleEventList.get(hobby).removeAll(removedEvents);
                removedEvents.clear();
            }
        }




    }



}
