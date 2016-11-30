package bumblebees.hobee.hobbycategories.hobbies;

import bumblebees.hobee.objects.Hobby;

/**
 * Created by amandahoffstrom on 2016-11-30.
 */

public class Football extends Hobby {

    private String position;
    private double height;


    public void setPosition(String position){
        this.position = position;
    }

    public void setHeight(double height){
        this.height = height;
    }

    public String getPosition(){
        return position;
    }

    public double getHeight(){
        return height;
    }
}
