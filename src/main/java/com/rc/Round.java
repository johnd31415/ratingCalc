package com.rc;

import java.util.Date;

public class Round {
    private String tourney;
    private String tier;
    private Date roundDate;
    private int round;
    private int rating;
    private Boolean evaluated;
    private Boolean included;

    public Round(){
        this.tourney = "";
        this.tier = "";
        this.roundDate = null;
        this.round = 0;
        this.rating = 0;
        this.evaluated = null;
        this.included = null;
    }
    public Round(String tourney, String tier, Date roundDate, int round, int rating, Boolean evaluated, Boolean included){
        this.tourney = tourney;
        this.tier = tier;
        this.roundDate = roundDate;
        this.round = round;
        this.rating = rating;
        this.evaluated = evaluated;
        this.included = included;
    }
    public void setTourney(String tourney){
        this.tourney = tourney;
    }
    public String getTourney(){
        return this.tourney;
    }
    public void setTier(String tier){
        this.tier = tier;
    }
    public String getTier(){
        return this.tier;
    }
    public void setRoundDate(Date roundDate){
        this.roundDate = roundDate;
    }
    public Date getRoundDate(){
        return this.roundDate;
    }
    public void setRound(int round){
        this.round = round;
    }
    public int getRound(){
        return this.round;
    }
    public void setRating(int rating){
        this.rating = rating;
    }
    public int getRating(){
        return this.rating;
    }
    public void setIncluded(boolean included){
        this.included = included;
    }
    public Boolean getIncluded(){
        return this.included;
    }
    public void setEvaluated(boolean evaluated){
        this.evaluated = evaluated;
    }
    public Boolean getEvaluated(){
        return this.evaluated;
    }

    @Override
    public String toString(){
        return "Date: " + roundDate.toString() + ", Rating: " + rating + ", Included: " + included + "\n";
    }
}
