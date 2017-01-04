package bumblebees.hobee.objects;

import com.google.gson.annotations.Until;

import java.util.ArrayList;
import java.util.List;

import bumblebees.hobee.R;

/**
 * A class to represent a hobby.
 * All specific hobbies must extend this class,
 * some additional fields migth have to added to the subclasses
 */
public class Hobby {

    //TODO: do something about this ugly hack -> see custom exclusion strategies for Gson
    //the @Until(0.2) annotation is a hack to exclude the fields from the Gson parser when creating the event json
    //create the gson parser and use any version larger than 0.2
    //Gson g = new GsonBuilder().setVersion(0.3).create();

    private String name;
    private String difficultyLevel;
    @Until(0.2)
    private List<String> datePreference = new ArrayList<>();
    private String timeFrom;
    private String timeTo;

    public Hobby(){
    }

    public Hobby(String name){
        this.name = name;
    }

    public Hobby(String name, String difficultyLevel){
        this.name = name;
        this.difficultyLevel = difficultyLevel;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setDifficultyLevel(String difficultyLevel){
        this.difficultyLevel = difficultyLevel;
    }

    public void setDatePreference(String datePreference){

        this.datePreference.add(datePreference);
    }

    public void setTimeFrom(String timeFrom){
        this.timeFrom = timeFrom;
    }

    public void setTimeTo(String timeTo){
        this.timeTo = timeTo;
    }

    public String getName(){
        return name;
    }

    public String getDifficultyLevel(){
        return difficultyLevel;
    }

    public List<String> getDatePreference(){
        return datePreference;
    }

    public String getTimeFrom(){
        return timeFrom;
    }

    public String getTimeTo(){
        return timeTo;
    }

    public int getMilitaryTimeFrom(){
        return Integer.parseInt(timeFrom.replace(".",""));
    }

    public int getMilitaryTimeTo(){
        return Integer.parseInt(timeTo.replace(".",""));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hobby hobby = (Hobby) o;

        return name != null ? name.equals(hobby.name) : hobby.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    /**
     * Get the icon corresponding to the hobby.
     * @return
     */
    public int getIcon() {
        switch(name.toLowerCase()){
            case "basketball":
                return R.drawable.basketball;
            case "football":
                return R.drawable.football;
            case "karaoke":
                return R.drawable.karaoke;
            case "ballroom":
                return R.drawable.ballroom;
            case "museum tour":
                return R.drawable.museumtour;
            case "baking":
                return R.drawable.baking;
            case "cooking":
                return R.drawable.cooking;
            case "chess":
                return R.drawable.chess;
            case "scrabble":
                return R.drawable.scrabble;
            case "monopoly":
                return R.drawable.monopoly1;
            case "camping":
                return R.drawable.camping;
            default:
                return R.drawable.bee;
        }
    }
}