package domain;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Created by imarcinschi on 6/30/2016.
 */
public class Standing {
    private final int position;
    private final int teamId;
    private final String teamHRef;
    private final String teamName;
    private final int playedGames;
    private final int points;
    private final int goals;
    private final int wins;
    private final int draws;
    private final int losses;
    private final Function<String, CompletableFuture<Team>> teamGetter;

    public Standing(int position,int teamId, String teamHRef, String teamName, int playedGames, int points, int goals,
                    int wins, int draws, int losses, Function<String, CompletableFuture<Team>> teamGetter) {
        this.position = position;
        this.teamId = teamId;
        this.teamHRef = teamHRef;
        this.teamName = teamName;
        this.playedGames = playedGames;
        this.points = points;
        this.goals = goals;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
        this.teamGetter = teamGetter;
    }

    public int getPosition() {
        return position;
    }

    public String getTeamName() {
        return teamName;
    }

    public int getPlayedGames() {
        return playedGames;
    }

    public int getPoints() {
        return points;
    }

    public int getGoals() {
        return goals;
    }

    public int getWins() {
        return wins;
    }

    public int getDraws() {
        return draws;
    }

    public int getLosses() {
        return losses;
    }

    public String getTeamHRef() {
        return teamHRef;
    }

    public int getTeamId() {
        return teamId;
    }

    public CompletableFuture<Team> getTeamSupplier() {
        return teamGetter.apply(teamHRef);
    }


}
