package com.players.nest.Tournament.Model;

import java.io.Serializable;

public class MatchData implements Serializable {
    private CompetitorData competitorOne;
    private CompetitorData competitorTwo;
    private int height;
    private int sectionNumber;


    public MatchData(CompetitorData competitorOne, CompetitorData competitorTwo, int height, int sectionNumber) {
        this.competitorOne = competitorOne;
        this.competitorTwo = competitorTwo;
        this.height = height;
        this.sectionNumber = sectionNumber;
    }

    public MatchData(CompetitorData competitorOne, CompetitorData competitorTwo) {
        this.competitorOne = competitorOne;
        this.competitorTwo = competitorTwo;
    }

    public CompetitorData getCompetitorOne() {
        return competitorOne;
    }

    public void setCompetitorOne(CompetitorData competitorOne) {
        this.competitorOne = competitorOne;
    }

    public CompetitorData getCompetitorTwo() {
        return competitorTwo;
    }

    public void setCompetitorTwo(CompetitorData competitorTwo) {
        this.competitorTwo = competitorTwo;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getSectionNumber() {
        return sectionNumber;
    }

    public void setSectionNumber(int sectionNumber) {
        this.sectionNumber = sectionNumber;
    }

}
