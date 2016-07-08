package domain.mapping;

import domain.League;
import domain.Player;
import domain.Standing;
import domain.Team;
import api.dto.DtoLeague;
import api.dto.DtoLeagueTableStanding;
import api.dto.DtoPlayer;
import api.dto.DtoTeam;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import static java.util.stream.Collectors.*;

/**
 * Created by imarcinschi on 6/30/2016.
 */

public class DtoToDomainMapperImpl implements DtoToDomainMapper {

    @Override
    public List<League> dtoLeagueToLeague(
                        List<DtoLeague> dtoLeagueList,
                        Function<Integer, CompletableFuture<List<DtoLeagueTableStanding>>> dtoLeagueTableStandingListByLeagueId,
                        Function<String, CompletableFuture<DtoTeam>> dtoTeamByReference,
                        Function<String, CompletableFuture<List<DtoPlayer>>> listOfDtoPlayersByReference ){

        List<League> listOfLeagues = new ArrayList<>();

        dtoLeagueList.forEach(dtoLeague ->
            listOfLeagues.add(
                    new League(
                            dtoLeague.id,
                            dtoLeague.caption,
                            dtoLeague.league,
                            dtoLeague.year,
                            dtoLeague.currentMatchday,
                            dtoLeague.numberOfMatchdays,
                            dtoLeague.numberOfTeams,
                            dtoLeague.numberOfGames,
                            dtoLeague.lastUpdated,
                            (idLeague) -> mapListOfDtoLeagueTableStandingToListOfStandings(
                                                                                    idLeague,
                                                                                    dtoLeagueTableStandingListByLeagueId,
                                                                                    dtoTeamByReference,
                                                                                    listOfDtoPlayersByReference)
                    )
            )
        );
        return listOfLeagues;
    }

    @Override
    public Team dtoTeamToTeam(DtoTeam dtoTeam, Function<String, CompletableFuture<List<DtoPlayer>>> playersByHref){

        String[] splitRef = dtoTeam._links.toString().split("/");
        int teamId = Integer.valueOf(splitRef[splitRef.length-2]);
        return new Team(
                teamId,
                dtoTeam._links.toString(),
                dtoTeam.name,
                dtoTeam.code,
                dtoTeam.shortName,
                dtoTeam.squadMarketValue,
                dtoTeam.crestUrl,
                mapListDtoPlayersToPlayers(playersByHref)
        );
    }

    @Override
    public Player dtoPlayerToPlayer(DtoPlayer dtoPlayer) {
        return new Player(
                dtoPlayer.name,
                dtoPlayer.position,
                dtoPlayer.jerseyNumber,
                dtoPlayer.dateOfBirth,
                dtoPlayer.nationality,
                dtoPlayer.contractUntil,
                dtoPlayer.marketValue
        );
    }

    @Override
    public List<Team> listOfDtoTeamsToListOfTeams(List<DtoTeam> dto, Function<String, CompletableFuture<List<DtoPlayer>>> playersByHref) {
        List<Team> listOfTeams = dto.stream().map(dtoTeam -> dtoTeamToTeam(dtoTeam, playersByHref)).collect(toList());
        return listOfTeams;
    }

    @Override
    public List<Player> listOfDtoPlayerToListOfPlayer(List<DtoPlayer> listDtoPlayers) {
        return listDtoPlayers.stream().map(dtoPlayer -> dtoPlayerToPlayer(dtoPlayer)).collect(toList());
    }




    /**************************Helper Functions******************************/

    private Function<String, CompletableFuture<List<Player>>> mapListDtoPlayersToPlayers(
                                        Function<String, CompletableFuture<List<DtoPlayer>>> playersByHref){
        return (ref) -> playersByHref.apply(ref).thenApply(dtoPlayers -> listOfDtoPlayerToListOfPlayer(dtoPlayers));
    }



    private CompletableFuture<List<Standing>> mapListOfDtoLeagueTableStandingToListOfStandings(
                                        int id,
                                        Function<Integer, CompletableFuture<List<DtoLeagueTableStanding>>> standings,
                                        Function<String, CompletableFuture<DtoTeam>> teamByHref,
                                        Function<String, CompletableFuture<List<DtoPlayer>>> playersByHref) {

        return standings.apply(id)
                .thenApply(dtoList -> mapDtoLeagueTableStandingToListOfStandings(dtoList, teamByHref, playersByHref));

    }



    private List<Standing> mapDtoLeagueTableStandingToListOfStandings(List<DtoLeagueTableStanding> tableLeague,
                                                Function<String, CompletableFuture<DtoTeam>> teamByHref,
                                                Function<String, CompletableFuture<List<DtoPlayer>>> playersByHref){

        List<Standing> listOfStandings = new ArrayList<>();
        tableLeague.forEach(stLeague -> {
            String[] splitRef = stLeague._links.toString().split("/");
            int teamId = Integer.valueOf(splitRef[splitRef.length-1]);
            listOfStandings.add(
                    new Standing(
                            stLeague.position,
                            teamId,
                            stLeague._links.toString(),
                            stLeague.teamName,
                            stLeague.playedGames,
                            stLeague.points,
                            stLeague.goals,
                            stLeague.wins,
                            stLeague.draws,
                            stLeague.losses,
                            (ref) -> teamByHref.apply(ref).thenApply(dtoTeam -> dtoTeamToTeam(dtoTeam, playersByHref))
                    )
            );
        });

        return listOfStandings;
    }
}

