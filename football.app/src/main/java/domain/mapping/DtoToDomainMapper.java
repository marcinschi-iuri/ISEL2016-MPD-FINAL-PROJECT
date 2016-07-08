package domain.mapping;
import domain.League;
import domain.Player;
import domain.Team;
import api.dto.DtoLeague;
import api.dto.DtoLeagueTableStanding;
import api.dto.DtoPlayer;
import api.dto.DtoTeam;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Created by imarcinschi on 6/30/2016.
 */
public interface DtoToDomainMapper {

    List<League> dtoLeagueToLeague(List<DtoLeague> dtoLeagueList,
                                          Function<Integer, CompletableFuture<List<DtoLeagueTableStanding>>> standings,
                                          Function<String, CompletableFuture<DtoTeam>> teamByHref,
                                          Function<String, CompletableFuture<List<DtoPlayer>>> playersByHref );

    List<Team> listOfDtoTeamsToListOfTeams(List<DtoTeam> dto, Function<String, CompletableFuture<List<DtoPlayer>>> playersByHref);

    Team dtoTeamToTeam(DtoTeam dto, Function<String, CompletableFuture<List<DtoPlayer>>> playersByHref);

    Player dtoPlayerToPlayer(DtoPlayer dtoPlayers);

    List<Player> listOfDtoPlayerToListOfPlayer(List<DtoPlayer> listDtoPlayers);
}
