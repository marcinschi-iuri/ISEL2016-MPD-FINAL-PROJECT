package domain;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created by imarcinschi on 6/30/2016.
 */
public class League {

    private final int id;
    private final String caption;
    private final String league;
    private final String year;
    private final int currentMatchDay;
    private final int numberOfMatchDays;
    private final int numberOfTeams;
    private final int numberOfGames;
    private final Date lastUpdated;
    private final Function<Integer,CompletableFuture<List<Standing>>> standings;

    public League(
            int id,
            String caption,
            String league,
            String year,
            int currentMatchDay,
            int numberOfMatchDays,
            int numberOfTeams,
            int numberOfGames,
            Date lastUpdated,
            Function<Integer,CompletableFuture<List<Standing>>> standings)
    {
        this.id = id;
        this.caption = caption;
        this.league = league;
        this.year = year;
        this.currentMatchDay = currentMatchDay;
        this.numberOfMatchDays = numberOfMatchDays;
        this.numberOfTeams = numberOfTeams;
        this.numberOfGames = numberOfGames;
        this.lastUpdated = lastUpdated;
        this.standings = standings;
    }

    public int getId() {
        return id;
    }

    public String getCaption() {
        return caption;
    }

    public String getLeague() {
        return league;
    }

    public String getYear() {
        return year;
    }

    public int getCurrentMatchDay() {
        return currentMatchDay;
    }

    public int getNumberOfMatchDays() {
        return numberOfMatchDays;
    }

    public int getNumberOfTeams() {
        return numberOfTeams;
    }

    public int getNumberOfGames() {
        return numberOfGames;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public CompletableFuture<List<Standing>> getLeagueTable() {
        return standings.apply(id);
    }

    @Override
    public String toString() {
        return "League{" +
                "id=" + id +
                ", caption='" + caption + '\'' +
                ", league='" + league + '\'' +
                ", year='" + year + '\'' +
                ", currentMatchDay=" + currentMatchDay +
                ", numberOfMatchDays=" + numberOfMatchDays +
                ", numberOfTeams=" + numberOfTeams +
                ", numberOfGames=" + numberOfGames +
                ", lastUpdated=" + lastUpdated +
                "}";
    }
}
