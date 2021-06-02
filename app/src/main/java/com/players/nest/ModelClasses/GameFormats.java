package com.players.nest.ModelClasses;

public class GameFormats {

    String gameID;
    String formatHeading;
    String description;

    public GameFormats() {

    }

    public GameFormats(String gameID, String formatHeading, String description) {
        this.gameID = gameID;
        this.formatHeading = formatHeading;
        this.description = description;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public String getFormatHeading() {
        return formatHeading;
    }

    public void setFormatHeading(String formatHeading) {
        this.formatHeading = formatHeading;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
