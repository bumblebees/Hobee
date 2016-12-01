package bumblebees.hobee.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Hobby implements Parcelable {

    private double id;
    private String name;
    private String difficultyLevel;
    private String datePreference;
    private String timePreference;


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
        this.datePreference = datePreference;
    }

    public void setTimePreference(String timePreference){
        this.timePreference = timePreference;
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

    public String getDatePreference(){
        return datePreference;
    }

    public String getTimePreference(){
        return timePreference;
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