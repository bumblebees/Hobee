package bumblebees.hobee.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Hobby implements Parcelable {
    private String skill;


    public Hobby(){

    }

    public Hobby(Parcel in) {
        String[] data = new String[1];
        this.skill = data[0];
    }


    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeStringArray(new String[]{this.skill});
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