package bumblebees.hobee.hobbycategories.hobbies;

import bumblebees.hobee.objects.Hobby;

/**
 * Created by Trixie on 2016-11-30.
 */

class Basketball extends Hobby {

    private String position;
    private double height;

    public Basketball (double id, String name) {
        super(1.2, "Ballroom");
    }

    public void setPosition(String position){
        this.position = position;
    }
    public void setHeight(double height) {
        this.height = height;
    }

    public String getPosition() {
        return position;
    }
    public double getHeight() {
        return height;
    }

}

