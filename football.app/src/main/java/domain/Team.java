package domain;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Created by imarcinschi on 6/30/2016.
 */
public class Team {
    private final int teamId;
    private final String playersHRef;
    private final String name;
    private final String code;
    private final String shortName;
    private final String squadMarketValue;
    private final String crestUrl;
    private final Function<String,CompletableFuture<List<Player>>> playersSupplier;

    public Team(int teamId, String playersHRef, String name, String code, String shortName, String squadMarketValue,
                String crestUrl, Function<String, CompletableFuture<List<Player>>> playersSupplier) {
        this.teamId = teamId;
        this.playersHRef = playersHRef;
        this.name = name;
        this.code = code;
        this.shortName = shortName;
        this.squadMarketValue = squadMarketValue;
        this.crestUrl = crestUrl;
        this.playersSupplier = playersSupplier;
    }

    public String getPlayersHRef() {
        return playersHRef;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getShortName() {
        return shortName;
    }

    public String getSquadMarketValue() {
        return squadMarketValue;
    }

    public String getCrestUrl() {
        return crestUrl;
    }

    public int getTeamId() {
        return teamId;
    }

    public CompletableFuture<List<Player>> getPlayersSupplier() {
        return playersSupplier.apply(playersHRef);
    }


    @Override
    public String toString() {
        return "Team{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", shortName='" + shortName + '\'' +
                ", squadMarketValue='" + squadMarketValue + '\'' +
                ", crestUrl='" + crestUrl + '\'' +
                '}';
    }
}
