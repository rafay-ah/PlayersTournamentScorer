package com.players.nest.Tournament.Util;

import android.util.DisplayMetrics;

import com.players.nest.Application.TournamentApplication;

public class BracketsUtility {
    public static int dpToPx(int dp) {
        DisplayMetrics displayMetrics = TournamentApplication.getInstance().getBaseContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
