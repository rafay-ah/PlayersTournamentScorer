package com.players.nest.Application;

import android.app.Application;

public class TournamentApplication  extends Application {
    private int screeHeight ;
    private static TournamentApplication applicationInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationInstance = this;
    }

    public void setScreeHeight(int screeHeight) {
        this.screeHeight = screeHeight;
    }

    public int getScreeHeight() {
        return screeHeight;
    }

    public static synchronized TournamentApplication getInstance() {
        return applicationInstance;
    }
}
