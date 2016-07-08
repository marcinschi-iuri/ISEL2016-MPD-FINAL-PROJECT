package api;

import com.google.gson.Gson;
import api.dto.*;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * @author Miguel Gamboa
 *         created on 23-05-2016
 */
public class SoccerWebApiImpl implements SoccerWebApi {

    private static final String HOST = "http://api.football-data.org/";
    private static final String PATH_LEAGUES = "/v1/soccerseasons";
    private static final String PATH_LEAGUE = "/v1/soccerseasons/%d";
    private static final String PATH_LEAGUE_TABLE = "v1/soccerseasons/%d/leagueTable";
    private static final String PATH_TEAM = "/v1/teams/%d";
    private static final String PATH_TEAM_PLAYERS = "/v1/teams/%d/players";
    private static final String API_TOKEN_VALUE = "a748e88e530349f6951e41be76a380ef";
    private static final String API_TOKEN_KEY = "X-Auth-Token";

    private final AsyncHttpClient getter;
    private final Gson gson;

    public SoccerWebApiImpl(AsyncHttpClient getter, Gson gson)
    {
        this.getter = getter;
        this.gson = gson;
    }

    public SoccerWebApiImpl()
    {
        this.getter = new DefaultAsyncHttpClient();
        this.gson = new Gson();
    }

    @Override
    public CompletableFuture<DtoLeague[]> getLeagues()
    {
        return httpGet(HOST + PATH_LEAGUES, DtoLeague[].class);
    }

    @Override
    public CompletableFuture<DtoLeagueTable> getLeagueTable(int leagueId)
    {
        String path = String.format(PATH_LEAGUE_TABLE, leagueId);
        CompletableFuture<DtoLeagueTable> cfDtoLeagueTable = httpGet(HOST + path, DtoLeagueTable.class);
        DtoLeagueTable dtoLeagueTable = cfDtoLeagueTable.join();
        return cfDtoLeagueTable;
    }

    @Override
    public CompletableFuture<DtoLeague> getLeague(int leagueId)
    {
        String path = String.format(PATH_LEAGUE, leagueId);
        return httpGet(HOST + path, DtoLeague.class);
    }

    @Override
    public CompletableFuture<DtoTeam> getTeam(int teamId)
    {
        String path = String.format(PATH_TEAM, teamId);
        return httpGet(HOST + path, DtoTeam.class);
    }
    @Override
    public CompletableFuture<DtoTeam> getTeamByRef(String ref)
    {
        return httpGet(ref, DtoTeam.class);
    }

    @Override
    public Function<String,CompletableFuture<List<DtoPlayer>>> getPlayersByRef()
    {
        return ref -> httpGet(ref, DtoPlayersList.class)
                .thenApply(dtoPlayersList -> dtoPlayersList.getPlayers())
                .thenApply(Arrays::asList);
    }

    @Override
    public CompletableFuture<DtoPlayersList> getPlayers(int teamId)
    {
        String path = String.format(PATH_TEAM_PLAYERS, teamId);
        return httpGet(HOST + path, DtoPlayersList.class);
    }

    private <T> CompletableFuture<T> httpGet(String path, Class<T> arrayType)
    {
        return getter
                .prepareGet(path)
                .addHeader(API_TOKEN_KEY, API_TOKEN_VALUE)
                .execute()
                .toCompletableFuture()
                .thenApply(r -> gson.fromJson(r.getResponseBody(), arrayType));
    }

    @Override
    public void close() throws Exception
    {
        if (!getter.isClosed()) {
            getter.close();
        }
    }

    public boolean isClosed()
    {
        return getter.isClosed();
    }
}
