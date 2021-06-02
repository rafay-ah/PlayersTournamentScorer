package com.players.nest.ModelClasses;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class TournamentDetail implements Parcelable {
    private String tournamentName;
    private String tournamentFormat;
    private String createdBy;
    private String console;
    private String minRating;
    private int playersJoined;
    private int maxPlayers;
    private String timeToStart;



    private double entryFee, winningAmt;
    private List<TournamentMatches> matchDetails;
    private String hostUserId;
    private String tournamentID;
    private String tournamentWinner;
    private String timeCreated;
    private Games game;
    private String tournamentStatus;
    private List<String> joinedUserID;
    private String tournamentRule;

    public TournamentDetail(String tournamentName, String tournamentFormat,String tournamentRule , String console, String minRating, int playersJoined,
                            int maxPlayers, String timeToStart, double entryFee, double winningAmt,
                            List<TournamentMatches> matchDetails, String hostUserId, String tournamentID,
                            String tournamentWinner, String timeCreated, Games game, String tournamentStatus,
                            List<String> joinedUserID) {
        this.tournamentName = tournamentName;
        this.tournamentFormat = tournamentFormat;
        this.console = console;
        this.minRating = minRating;
        this.playersJoined = playersJoined;
        this.maxPlayers = maxPlayers;
        this.timeToStart = timeToStart;
        this.entryFee = entryFee;
        this.winningAmt = winningAmt;
        this.matchDetails = matchDetails;
        this.hostUserId = hostUserId;
        this.tournamentID = tournamentID;
        this.tournamentWinner = tournamentWinner;
        this.timeCreated = timeCreated;
        this.game = game;
        this.tournamentStatus = tournamentStatus;
        this.joinedUserID = joinedUserID;
        this.tournamentRule = tournamentRule;
        this.createdBy = createdBy;
    }

    public TournamentDetail() {
    }



    protected TournamentDetail(Parcel in) {
        tournamentName = in.readString();
        tournamentFormat = in.readString();
        createdBy = in.readString();
        console = in.readString();
        minRating = in.readString();
        playersJoined = in.readInt();
        maxPlayers = in.readInt();
        timeToStart = in.readString();
        entryFee = in.readDouble();
        winningAmt = in.readDouble();
        matchDetails = in.createTypedArrayList(TournamentMatches.CREATOR);
        hostUserId = in.readString();
        tournamentID = in.readString();
        tournamentWinner = in.readString();
        timeCreated = in.readString();
        game = in.readParcelable(Games.class.getClassLoader());
        tournamentStatus = in.readString();
        joinedUserID = in.createStringArrayList();
        tournamentRule = in.readString();
    }

    public static final Creator<TournamentDetail> CREATOR = new Creator<TournamentDetail>() {
        @Override
        public TournamentDetail createFromParcel(Parcel in) {
            return new TournamentDetail(in);
        }

        @Override
        public TournamentDetail[] newArray(int size) {
            return new TournamentDetail[size];
        }
    };

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public String getTournamentFormat() {
        return tournamentFormat;
    }

    public void setTournamentFormat(String tournamentFormat) {
        this.tournamentFormat = tournamentFormat;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getConsole() {
        return console;
    }

    public void setConsole(String console) {
        this.console = console;
    }

    public String getMinRating() {
        return minRating;
    }

    public void setMinRating(String minRating) {
        this.minRating = minRating;
    }

    public int getPlayersJoined() {
        return playersJoined;
    }

    public void setPlayersJoined(int playersJoined) {
        this.playersJoined = playersJoined;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getTimeToStart() {
        return timeToStart;
    }

    public void setTimeToStart(String timeToStart) {
        this.timeToStart = timeToStart;
    }

    public double getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(double entryFee) {
        this.entryFee = entryFee;
    }

    public double getWinningAmt() {
        return winningAmt;
    }

    public void setWinningAmt(double winningAmt) {
        this.winningAmt = winningAmt;
    }

    public List<TournamentMatches> getMatchDetails() {
        return matchDetails;
    }

    public void setMatchDetails(List<TournamentMatches> matchDetails) {
        this.matchDetails = matchDetails;
    }

    public String getHostUserId() {
        return hostUserId;
    }

    public void setHostUserId(String hostUserId) {
        this.hostUserId = hostUserId;
    }

    public String getTournamentID() {
        return tournamentID;
    }

    public void setTournamentID(String tournamentID) {
        this.tournamentID = tournamentID;
    }

    public String getTournamentWinner() {
        return tournamentWinner;
    }

    public void setTournamentWinner(String tournamentWinner) {
        this.tournamentWinner = tournamentWinner;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Games getGame() {
        return game;
    }

    public void setGame(Games game) {
        this.game = game;
    }

    public String getTournamentStatus() {
        return tournamentStatus;
    }

    public void setTournamentStatus(String tournamentStatus) {
        this.tournamentStatus = tournamentStatus;
    }

    public List<String> getJoinedUserID() {
        return joinedUserID;
    }

    public void setJoinedUserID(List<String> joinedUserID) {
        this.joinedUserID = joinedUserID;
    }
    public String getTournamentRule() {
        return tournamentRule;
    }

    public void setTournamentRule(String tournamentRule) {
        this.tournamentRule = tournamentRule;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tournamentName);
        dest.writeString(tournamentFormat);
        dest.writeString(createdBy);
        dest.writeString(console);
        dest.writeString(minRating);
        dest.writeInt(playersJoined);
        dest.writeInt(maxPlayers);
        dest.writeString(timeToStart);
        dest.writeDouble(entryFee);
        dest.writeDouble(winningAmt);
        dest.writeTypedList(matchDetails);
        dest.writeString(hostUserId);
        dest.writeString(tournamentID);
        dest.writeString(tournamentWinner);
        dest.writeString(timeCreated);
        dest.writeParcelable(game, flags);
        dest.writeString(tournamentStatus);
        dest.writeStringList(joinedUserID);
        dest.writeString(tournamentRule);
    }
}
