package com.players.nest.Tournament.Model;

public class CompetitorData {

    private String name;
    private String score;
    private String rating;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public CompetitorData(String name, String score, String rating) {
        this.name = name;
        this.score = score;
        this.rating = rating;
    }
}
