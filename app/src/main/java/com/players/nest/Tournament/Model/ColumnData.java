package com.players.nest.Tournament.Model;

import com.players.nest.ModelClasses.TournamentMatches;

import java.io.Serializable;
import java.util.List;

public class ColumnData implements Serializable {
    private List<MatchData> matches;

    public ColumnData(List<MatchData> matches) {
        this.matches = matches;
    }

    public List<MatchData> getMatches() {
        return matches;
    }

    public void setMatches(List<MatchData> matches) {
        this.matches = matches;
    }
}
