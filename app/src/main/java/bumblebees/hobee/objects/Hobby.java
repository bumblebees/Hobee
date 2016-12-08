package bumblebees.hobee.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to represent a hobby.
 * All specific hobbies must extend this class,
 * some additional fields migth have to added to the subclasses
 */
public class Hobby implements Parcelable {

    private double id;
    private String name;
    private String difficultyLevel;
    private List<String> datePreference = new ArrayList<>();
    private Double timeFrom;
    private Double timeTo;

    public Hobby(){
    }

    public Hobby(String name){
        this.name = name;
    }
    public Hobby(double id, String name){
        this.id = id;
        this.name = name;
    }

    public Hobby(Parcel in) {
        String[] data = new String[1];
        this.difficultyLevel = data[0];
    }

    public void setId(int id){
         this.id = id;
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

    public void setTimeFrom(Double timeFrom){
        this.timeFrom = timeFrom;
    }

    public void setTimeTo(Double timeTo){
        this.timeTo = timeTo;
    }

    public double getId(){
        return id;
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

    public Double getTimeFrom(){
        return timeFrom;
    }

    public Double getTimeTo(){
        return timeTo;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeStringArray(new String[]{this.difficultyLevel});
    }

    public static final Parcelable.Creator<Hobby> CREATOR= new Parcelable.Creator<Hobby>(){
        @Override
        public Hobby createFromParcel(Parcel source){
            return new Hobby(source);
        }

        @Override
        public Hobby[] newArray(int size){
            return new Hobby[size];
        }
    };

}