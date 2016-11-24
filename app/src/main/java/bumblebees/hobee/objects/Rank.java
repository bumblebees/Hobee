package bumblebees.hobee.objects;


import static java.lang.Double.parseDouble;

public class Rank {

    private double globalRank, hostRank;
    private int noShows;

    public Rank() {
        globalRank = 0.0;
        hostRank=0.0;
        noShows = 0;
    }


    //Really ugly but needed to pass the user object between activities
    //the string needs to look like "globalRank:151,514,hostRank:258,15"
    public Rank(String rank){
        String[] str =rank.split(",",2);
        String[] gRank, hRank;
        gRank = str[0].split(":",2);
        this.globalRank = parseDouble(gRank[1]);
        hRank = str[1].split(":",2);
        this.hostRank = parseDouble(hRank[1]);
    }

    public String toString(){
        return "globalRank:"+ globalRank + ",hostRank:" + hostRank;
    }

}
