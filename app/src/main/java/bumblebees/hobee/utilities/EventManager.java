package bumblebees.hobee.utilities;


import java.util.ArrayList;
import java.util.HashMap;

import bumblebees.hobee.objects.Event;
import bumblebees.hobee.objects.Hobby;
import bumblebees.hobee.objects.User;

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

}
