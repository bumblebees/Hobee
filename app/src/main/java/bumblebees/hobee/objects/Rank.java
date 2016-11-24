package bumblebees.hobee.objects;
import static java.lang.Integer.parseInt;

/**
    User rank
        Works based with reputation, reputation is an integer.
        To achieve a rank of -3, the user needs to have lless than -7000 reputation
        To achieve a rank of -2, the user needs to have between -2501 and -7000 reputation
        To achieve a rank of -1, the user needs to have between -1001 and -2500 reputation
        To achieve a rank of 0, the user needs to have between -1000 and 1000 reputation
        To achieve a rank of 1, the user needs to have between 1001 and 2500 reputation (1500 dif)
        To achieve a rank of 2, the user needs to have between 2501 and 7000 reputation (4500 dif)
        To achieve a rank of 3, the user needs to have higher than 7000 reputation
     */



public class Rank {
    private int reputation;
    private int globalRank, hostRank;
    private int noShows;



    public Rank() {
        globalRank = 0;
        hostRank=0;
        noShows = 0;
    }

    public void rankHost(int review){
        if (review < -3) review = -3;
        if (review > 3) review = 3;
        reputation = reputation + (15*review);

        ////TODO: Implement this on the activity rankUserActivity and update the database
    }

    public void rankGlobal(){

    }



    //Really ugly but needed to pass the user object between activities
    //the string needs to look like "globalRank:151,514,hostRank:258,15"
    public Rank(String rank){
        String[] str =rank.split(",",2);
        String[] gRank, hRank;
        gRank = str[0].split(":",2);
        this.globalRank = parseInt(gRank[1]);
        hRank = str[1].split(":",2);
        this.hostRank = parseInt(hRank[1]);
    }

    public String toString(){
        return "globalRank:"+ globalRank + ",hostRank:" + hostRank;
    }

}
