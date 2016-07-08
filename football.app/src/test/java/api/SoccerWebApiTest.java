package api;

import api.dto.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;


/**
 * Created by imarcinschi on 6/24/2016.
 */
public class SoccerWebApiTest {

    SoccerWebApi api;

    @Before
    public void setUp() throws Exception {
        api = new SoccerWebApiImpl();
    }

    @Test
    public void testGetLeagues() throws Exception {
        CompletableFuture<DtoLeague[]> leagues = api.getLeagues();
        DtoLeague[] arr = leagues.join();
        Assert.assertNotNull(arr);
        Assert.assertTrue(arr.length > 0);
        Arrays.stream(arr).forEach(System.out::println);

    }

    @Test
    public void testGetLeagueTable() throws Exception {
        CompletableFuture<DtoLeagueTable> leagueTable = api.getLeagueTable(396);
        DtoLeagueTable dtoLeagueTable = leagueTable.join();
        Assert.assertNotNull(dtoLeagueTable);
        DtoLeagueTableStanding[] standing = dtoLeagueTable.standing;
        Assert.assertTrue(standing.length > 0);
        Arrays.stream(standing).forEach(System.out::println);
    }

    @Test
    public void testGetTeamByReference() throws Exception {
        CompletableFuture<DtoTeam> team = api.getTeamByRef("http://api.football-data.org/v1/teams/1");
        DtoTeam dtoTeam = team.join();
        Assert.assertNotNull(dtoTeam);
        String code = dtoTeam.code;
        Assert.assertTrue(code.equals("EFFZEH"));
        System.out.println(dtoTeam);
    }

    @Test
    public void testGetTeamById() throws Exception {
        CompletableFuture<DtoTeam> team = api.getTeam(1);
        DtoTeam dtoTeam = team.join();
        Assert.assertNotNull(dtoTeam);
        String code = dtoTeam.code;
        Assert.assertTrue(code.equals("EFFZEH"));
        System.out.println(dtoTeam);
    }

    @Test
    public void testGetPlayers() throws Exception {
        Function<String, CompletableFuture<List<DtoPlayer>>> playersByRef = api.getPlayersByRef();
        CompletableFuture<List<DtoPlayer>> cfListDtoPlayers =
                                                playersByRef.apply("http://api.football-data.org/v1/teams/9/players");
        List<DtoPlayer> dtoPlayers = cfListDtoPlayers.join();
        Assert.assertNotNull(dtoPlayers);
        Assert.assertFalse(dtoPlayers.size()==0);
        DtoPlayer dtoPlayer = dtoPlayers.get(0);

        Assert.assertNotNull(dtoPlayer);
        Assert.assertTrue(dtoPlayer.name.equals("Sascha Burchert"));

        System.out.println(dtoPlayers);
    }
}