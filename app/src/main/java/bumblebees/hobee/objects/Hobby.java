package bumblebees.hobee.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.Until;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to represent a hobby.
 * All specific hobbies must extend this class,
 * some additional fields migth have to added to the subclasses
 */
public class Hobby implements Parcelable {

    //TODO: do something about this ugly hack -> see custom exclusion strategies for Gson
    //the @Until(0.2) annotation is a hack to exclude the fields from the Gson parser when creating the event json
    //create the gson parser and use any version larger than 0.2
    //Gson g = new GsonBuilder().setVersion(0.3).create();

    @Until(0.2)
    private double id;
    private String name;
    private String difficultyLevel;
    @Until(0.2)
    private List<String> datePreference = new ArrayList<>();
    @Until(0.2)
    private Double timeFrom;
    @Until(0.2)
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

    public Hobby(String name, String difficultyLevel){
        this.name = name;
        this.difficultyLevel = difficultyLevel;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hobby hobby = (Hobby) o;

        if (Double.compare(hobby.id, id) != 0) return false;
        return name != null ? name.equals(hobby.name) : hobby.name == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(id);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}