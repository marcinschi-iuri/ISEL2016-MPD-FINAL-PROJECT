package api;

import api.dto.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Created by imarcinschi on 7/3/2016.
 */
public interface SoccerWebApi extends AutoCloseable {

    CompletableFuture<DtoLeague[]> getLeagues();

    CompletableFuture<DtoLeagueTable> getLeagueTable(int leagueId);

    CompletableFuture<DtoLeague> getLeague(int leagueId);

    CompletableFuture<DtoTeam> getTeam(int teamId);

    CompletableFuture<DtoTeam> getTeamByRef(String ref);

    Function<String,CompletableFuture<List<DtoPlayer>>> getPlayersByRef();

    CompletableFuture<DtoPlayersList> getPlayers(int teamId);
}
