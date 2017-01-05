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
    private int globalRep, hostRep;
    private int noShows;



    public Rank() {
        globalRep = 0;
        hostRep= 0;
        noShows = 0;
    }

    public void rankHost(int review){
        if (review < -3) review = -3;
        if (review > 3) review = 3;
        reputation = reputation + (150*review);
    }

    public int getGlobalRep(){
        return globalRep;
    }

    public int getHostRep(){
        return hostRep;
    }

    public int getNoShows(){
        return noShows;
    }



    //Really ugly but needed to pass the user object between activities
    //the string needs to look like "globalRank:151,514,hostRank:258,15"
    public Rank(String rank){
        String[] str =rank.split(",",2);
        String[] gRank, hRank;
        gRank = str[0].split(":",2);
        this.globalRep = parseInt(gRank[1]);
        hRank = str[1].split(":",2);
        this.hostRep = parseInt(hRank[1]);
    }

    public String toString(){
        return "globalRank:"+ globalRep + ",hostRank:" + hostRep;
    }

}
