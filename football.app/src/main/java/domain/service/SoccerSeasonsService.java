package domain.service;

import api.dto.DtoLeague;
import api.dto.DtoLeagueTable;
import domain.League;
import domain.Player;
import domain.Team;
import domain.mapping.DtoToDomainMapper;
import api.SoccerWebApi;
import api.dto.DtoLeagueTableStanding;
import api.dto.DtoTeam;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Created by imarcinschi on 6/30/2016.
 */
public class SoccerSeasonsService {

    private DtoToDomainMapper mapper;
    private SoccerWebApi soccerWebApi;
    
    public SoccerSeasonsService(DtoToDomainMapper mapper, SoccerWebApi soccerWebApi) {
        this.mapper = mapper;
        this.soccerWebApi = soccerWebApi;
    }


    public CompletableFuture<List<League>> getLeagues() {
        return soccerWebApi
                .getLeagues()
                .thenApply(arrLeagues -> Arrays.asList(arrLeagues))
                .thenApplyAsync(dtoLeagues -> mapper.dtoLeagueToLeague(dtoLeagues,
                        tableStandingsGetFunction(),
                        teamGetFunction(),
                        soccerWebApi.getPlayersByRef()
                ));
    }


    public CompletableFuture<League> getLeague(int leagueId) {

        CompletableFuture<DtoLeague> cfDtoLeague = soccerWebApi.getLeague(leagueId);
        DtoLeague dtoleague = cfDtoLeague.join();

        CompletableFuture<List<League>> cfListOfLeague = cfDtoLeague.thenApply(dtoLeague -> mapper.dtoLeagueToLeague(Arrays.asList(dtoLeague),
                tableStandingsGetFunction(),
                teamGetFunction(),
                soccerWebApi.getPlayersByRef()));

        CompletableFuture<League> cfLeague = cfListOfLeague.thenApply(leagues -> leagues.get(0));

        League league = cfLeague.join();

        return cfLeague;

/*
        return soccerWebApi
                .getLeague(leagueId)
                .thenApply(dtoLeague -> mapper.dtoLeagueToLeague(Arrays.asList(dtoLeague),
                        tableStandingsGetFunction(),
                        teamGetFunction(),
                        soccerWebApi.getPlayersByRef()))
                .thenApply(leagues -> leagues.get(0));
*/

    }

    public CompletableFuture<Team> getTeam(int teamId) {
        return soccerWebApi
                .getTeam(teamId)
                .thenApplyAsync(dtoTeam -> mapper.dtoTeamToTeam(dtoTeam, soccerWebApi.getPlayersByRef()));
    }

    public CompletableFuture<List<Player>> getTeamPlayers(int teamId) {
        return soccerWebApi
                .getPlayers(teamId)
                .thenApplyAsync(arrDtoPlayers -> Arrays.asList(arrDtoPlayers.getPlayers()))
                .thenApplyAsync(listDtoPlayers -> mapper.listOfDtoPlayerToListOfPlayer(listDtoPlayers));
    }

    private Function<Integer, CompletableFuture<List<DtoLeagueTableStanding>>> tableStandingsGetFunction() {
        return leagueId -> soccerWebApi
                .getLeagueTable(leagueId)
                .thenApply(dto -> dto.standing)
                .thenApply(Arrays::asList);
    }

    private Function<String, CompletableFuture<DtoTeam>> teamByRerGetFunction() {

        return (ref) -> soccerWebApi.getTeamByRef(ref);

    }

    private Function<String, CompletableFuture<DtoTeam>> teamGetFunction() {
        return ref -> soccerWebApi.getTeamByRef(ref);
    }
}
