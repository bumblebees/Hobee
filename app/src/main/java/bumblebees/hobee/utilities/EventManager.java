package bumblebees.hobee.utilities;


import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

import bumblebees.hobee.objects.Event;
import bumblebees.hobee.objects.Hobby;
import bumblebees.hobee.objects.User;

public class EventManager {



    public enum UserStatus {HOST, NEW_ACCEPTED, OLD_ACCEPTED, PENDING, REJECTED, NEW_MATCH, NEW_MATCH_NOTIFICATION, OLD_MATCH, NONE}

    public enum EventStatus {HOSTED_EVENT, ACCEPTED_EVENT, PENDING_EVENT, EVENT_NOT_FOUND}


    private ArrayList<Event> hostedEvents = new ArrayList<>();
    private ArrayList<Event> acceptedEvents = new ArrayList<>();
    private ArrayList<Event> pendingEvents = new ArrayList<>();
    private HashMap<String, ArrayList<Event>> eligibleEventList = new HashMap<>();

    private ArrayList<Event> historyHostedEvents = new ArrayList<>();
    private ArrayList<Event> historyJoinedEvents = new ArrayList<>();

    public EventManager(ArrayList<Event> acceptedEvents, HashMap<String, ArrayList<Event>> eligibleEventList, ArrayList<Event> historyHostedEvents,
                        ArrayList<Event> historyJoinedEvents, ArrayList<Event> hostedEvents, ArrayList<Event> pendingEvents) {
        this.acceptedEvents = acceptedEvents;
        this.eligibleEventList = eligibleEventList;
        this.historyHostedEvents = historyHostedEvents;
        this.historyJoinedEvents = historyJoinedEvents;
        this.hostedEvents = hostedEvents;
        this.pendingEvents = pendingEvents;
    }

    public EventManager() {
    }

    public ArrayList<Event> getAcceptedEvents() {
        return acceptedEvents;
    }

    public ArrayList<Event> getHostedEvents() {
        return hostedEvents;
    }

    public ArrayList<Event> getPendingEvents() {
        return pendingEvents;
    }

    public ArrayList<Event> getHistoryHostedEvents() {
        return historyHostedEvents;
    }

    public ArrayList<Event> getHistoryJoinedEvents() {
        return historyJoinedEvents;
    }


    /**
     * Check if the user belongs to the event, or if the user is eligible for the event.
     * @param user
     * @param event
     * @return enum corresponding to the user's position in the event
     * NEW_MATCH - the event is new for the user, no notification should be sent
     * NEW_MATCH_NOTIFICATION - the event is new for the user, and has been recently added, send a notification
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
        boolean matches = matchesPreferences(event,user);
        if(matches && !event.isFull()){
            if(addEligibleEvent(event.getType(), event)){
                Long currentTime = Calendar.getInstance().getTimeInMillis() / 1000L;
                // check if the event has been recently added
                if(Long.parseLong(event.getTimestamp())+100 > currentTime){
                    return UserStatus.NEW_MATCH_NOTIFICATION;
                }
                else {
                    return UserStatus.NEW_MATCH;
                }
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

    private void addHostedEvent(Event event){
        if(hostedEvents.contains(event)){
            hostedEvents.remove(event);
        }
        hostedEvents.add(event);
    }



    private void addPendingEvent(Event event){
        if(pendingEvents.contains(event)){
            pendingEvents.remove(event);
        }
        pendingEvents.add(event);
    }

    private void removePendingEvent(Event event){
        if(pendingEvents.contains(event)){
            pendingEvents.remove(event);
        }
    }

    public HashMap<String, ArrayList<Event>> getEligibleEventList() {
        return eligibleEventList;
    }

    /**
     * Add the event to the list of eligible events
     * @param hobby - hobby corresponding to the event
     * @param event - event t be added
     * @return true if the event is added for the first time to the list, false if the event is only updated
     */
    private boolean  addEligibleEvent(String hobby, Event event){

        boolean res = true;
        //check if there is a list corresponding to the hobby
        if(eligibleEventList.get(hobby) == null){
            eligibleEventList.put(hobby, new ArrayList<Event>());
        }
        else if(eligibleEventList.get(hobby).contains(event)){
            eligibleEventList.get(hobby).remove(event);
            res = false;
        }
        eligibleEventList.get(hobby).add(event);
        return res;
    }

    public void removeEligibleEvent(String hobby, Event event){
        if(eligibleEventList.get(hobby) != null)
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
    private boolean matchesPreferences(Event event, User user){
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
        if (user.hasHobby(event.getEvent_details().getHobby())) {
            Hobby currentHobby = user.getOneHobby(event.getEvent_details().getHobby());
            // if true, check if day of week and time of day are matching too
            boolean matchDayofWeek = matchDayOfWeek(event,currentHobby);
            boolean matchTimeOfDay = matchTimeOfDay(event,currentHobby);
            if(!matchDayofWeek)
                    return false;
                if(!matchTimeOfDay)
                    return false;

            // also check difficulty level
            if(!currentHobby.getDifficultyLevel().equals(event.getEvent_details().getHobbySkill())){
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @param event
     * @return true if a day matches, false otherwise
     */
    private boolean matchDayOfWeek(Event event, Hobby hobby) {
        return hobby.getDatePreference().contains(event.getEvent_details().getDayOfTheWeek());
    }

    /**
     *
     * @param event
     * @return true if time of day matches, false otherwise
     */
    private boolean matchTimeOfDay(Event event, Hobby hobby){
        int eventTime = event.getEvent_details().getMilitaryTime();
        int hobbyTimeFrom = hobby.getMilitaryTimeFrom();
        int hobbyTimeTo   = hobby.getMilitaryTimeTo();
        if(hobbyTimeFrom <= eventTime)
            if(eventTime <= hobbyTimeTo)
                return true;
        return false;
    }

    /**
     * Remove all events that do not match the given topics or that have expired (they have already happened).
     * @param topics - topics corresponding to the events to be removed
     * @param hobbies - hobbies that the user currently has
     * @return ArrayList of the events that have expired
     */
    public ArrayList<Event> findAndRemoveEvents(HashSet<String> topics, ArrayList<String> hobbies) {
        ArrayList<Event> oldEvents = new ArrayList<>();
        ArrayList<Event> removedEvents = new ArrayList<>();
        for(Event event : hostedEvents){
            if(topics.contains(event.getSubscribeTopic())){
                removedEvents.add(event);
            }
            if(!event.isEventActive()){
                removedEvents.add(event);
                historyHostedEvents.remove(event);
                historyHostedEvents.add(event);
                oldEvents.add(event);
            }
        }
        hostedEvents.removeAll(removedEvents);
        removedEvents.clear();
        for(Event event : acceptedEvents){
            if(topics.contains(event.getSubscribeTopic())){
                removedEvents.add(event);
            }
            if(!event.isEventActive()){
                removedEvents.add(event);
                historyJoinedEvents.remove(event);
                historyJoinedEvents.add(event);
                oldEvents.add(event);
            }
        }
        acceptedEvents.removeAll(removedEvents);
        removedEvents.clear();
        for(Event event : pendingEvents){
            if(topics.contains(event.getSubscribeTopic())){
                removedEvents.add(event);
            }
            if(!event.isEventActive()){
                removedEvents.add(event);
                oldEvents.add(event);
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
                    if (!event.isEventActive()) {
                        removedEvents.add(event);
                        oldEvents.add(event);
                    }
                }
                eligibleEventList.get(hobby).removeAll(removedEvents);
                removedEvents.clear();
            }
        }
        return oldEvents;
    }
}
