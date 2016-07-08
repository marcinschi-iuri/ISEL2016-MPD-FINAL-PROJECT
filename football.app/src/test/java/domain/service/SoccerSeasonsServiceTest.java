package domain.service;

import api.dto.*;
import domain.League;
import domain.Player;
import domain.Team;
import domain.mapping.DtoToDomainMapper;
import domain.mapping.DtoToDomainMapperImpl;
import api.SoccerWebApi;
import api.SoccerWebApiImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Created by imarcinschi on 7/3/2016.
 */
public class SoccerSeasonsServiceTest {

    SoccerSeasonsService service;

    @Before
    public void setUp() throws Exception {
        SoccerWebApi api = new DollSoccerWebApi();
        DtoToDomainMapper mapper = new DtoToDomainMapperImpl();
        service = new SoccerSeasonsService(mapper,api);
    }

    @Test
    public void testGetLeagues() throws Exception {

        CompletableFuture<List<League>> cfLeagues = service.getLeagues();
        Assert.assertNotNull(cfLeagues);
        List<League> leagues = cfLeagues.get();
        Assert.assertNotNull(leagues);
        Assert.assertFalse(leagues.size()==0);
        Assert.assertTrue(leagues.get(0).getId()==398);
        leagues.forEach(System.out::println);

    }

    @Test
    public void testGetLeague() throws Exception {

        CompletableFuture<League> cfLeague = service.getLeague(398);
        Assert.assertNotNull(cfLeague);
        League league = cfLeague.get();
        Assert.assertNotNull(league);
        Assert.assertTrue(league.getId()==398);
        System.out.println(league);

    }

    @Test
    public void testGetTeam() throws Exception {

        CompletableFuture<Team> cfTeam = service.getTeam(66);
        Assert.assertNotNull(cfTeam);
        Team team = cfTeam.get();
        Assert.assertNotNull(team);
        Assert.assertTrue(team.getName().equals("Manchester United FC"));
        System.out.println(team);

    }

    @Test
    public void testGetTeamPlayers() throws Exception {

        CompletableFuture<List<Player>> cfTeamPlayersList = service.getTeamPlayers(66);
        Assert.assertNotNull(cfTeamPlayersList);
        List<Player> listOfPlayers = cfTeamPlayersList.get();
        Assert.assertNotNull(listOfPlayers);
        Assert.assertFalse(listOfPlayers.size()==0);
        Assert.assertTrue(listOfPlayers.get(0).getName().equals("Bastian Schweinsteiger"));
        listOfPlayers.forEach(System.out::println);
    }

    /************************************Helper Methods ****************************************************/

    private class DollSoccerWebApi implements SoccerWebApi{

        @Override
        public CompletableFuture<DtoLeague[]> getLeagues() {


            return CompletableFuture.supplyAsync(()-> dollDtoLeagueList());
        }

        @Override
        public CompletableFuture<DtoLeagueTable> getLeagueTable(int leagueId) {
            DtoLeagueTable dtoLeagueTable = new DtoLeagueTable(dollArrayDtoLeagueTableStanding());
            return CompletableFuture.supplyAsync(()->dtoLeagueTable);
        }

        @Override
        public CompletableFuture<DtoLeague> getLeague(int leagueId) {
            return CompletableFuture.supplyAsync(()->dollDtoLeague());
        }

        @Override
        public CompletableFuture<DtoTeam> getTeam(int teamId) {
            return CompletableFuture.supplyAsync(()->dollDtoTeam());
        }

        @Override
        public CompletableFuture<DtoTeam> getTeamByRef(String ref) {
            return CompletableFuture.supplyAsync(()->dollDtoTeam());
        }

        @Override
        public Function<String, CompletableFuture<List<DtoPlayer>>> getPlayersByRef() {
            return dollFunctionCfListDtoPlayers();
        }

        @Override
        public CompletableFuture<DtoPlayersList> getPlayers(int teamId) {
            return CompletableFuture.supplyAsync(()->dollDtoPlayersList());
        }

        @Override
        public void close() throws Exception {

        }

        private DtoLeague [] dollDtoLeagueList() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

            DtoLeague [] dtoLeagueArr = new DtoLeague[4];
            try {
                dtoLeagueArr[0] = dollDtoLeague(398, "Premier League 2015/16", "PL", "2015", 38, 38, 20, 380, sdf.parse("2016-05-19T15:12:55Z"));
                dtoLeagueArr[1] = dollDtoLeague(424, "European Championships France 2016", "EC", "2016", 5, 7, 24, 48, sdf.parse("2016-07-01T21:11:39Z"));
                dtoLeagueArr[2] = dollDtoLeague(426, "Premiere League 2016/17", "PL", "2016", 1, 38, 20, 380, sdf.parse("2016-06-23T10:42:02Z"));
                dtoLeagueArr[3] = dollDtoLeague(427, "Championship 2016/17", "ELC", "2016", 1, 46, 24, 552, sdf.parse("2016-06-27T12:39:32Z"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return dtoLeagueArr;
        }

        private DtoLeague dollDtoLeague(int id,String caption,String league,String year,int currentMatchday,
                                        int numberOfMatchdays,int numberOfTeams,int numberOfGames,Date lastUpdated) {

            return new DtoLeague(
                    id,caption,league,year,currentMatchday,numberOfMatchdays,numberOfTeams,numberOfGames,lastUpdated);
        }

        private DtoLeagueTableStanding [] dollArrayDtoLeagueTableStanding() {
            //http://api.football-data.org/v1/soccerseasons/398/leagueTable

            DtoLeagueTableStanding.DtoLinksTeam dtoLinksTeam =
                    new DtoLeagueTableStanding.DtoLinksTeam("http://api.football-data.org/v1/teams/338");
            DtoLeagueTableStanding.DtoLinks dtoLinks = new DtoLeagueTableStanding.DtoLinks(dtoLinksTeam);

            return new DtoLeagueTableStanding[]{
                    dollDtoLeagueTableStanding(
                            "http://api.football-data.org/v1/teams/338", 1, "Leicester City FC", 38, 81, 68, 23, 12, 3),
                    dollDtoLeagueTableStanding(
                            "http://api.football-data.org/v1/teams/57", 2, "Arsenal FC", 38, 71, 65, 20, 11, 7),
                    dollDtoLeagueTableStanding(
                            "http://api.football-data.org/v1/teams/73", 3, "Tottenham Hotspur FC", 38, 70, 69, 19, 13, 6),
                    dollDtoLeagueTableStanding(
                            "http://api.football-data.org/v1/teams/65", 4, "Manchester City FC", 38, 66, 71, 19, 9, 10),
                    dollDtoLeagueTableStanding(
                            "http://api.football-data.org/v1/teams/66", 5, "Manchester United FC", 38, 66, 49, 19, 9, 10),
                    dollDtoLeagueTableStanding(
                            "http://api.football-data.org/v1/teams/340", 6, "Southampton FC", 38, 63, 59, 18, 9, 11),
                    dollDtoLeagueTableStanding(
                            "http://api.football-data.org/v1/teams/563", 7, "West Ham United FC", 38, 62, 65, 16, 14, 8)
            };
        }

        private DtoLeagueTableStanding dollDtoLeagueTableStanding(String teamRef, int position, String teamName,
                                                                  int playedGames, int points,int goals,int wins,int draws,int losses){
            DtoLeagueTableStanding.DtoLinksTeam dtoLinksTeam =
                    new DtoLeagueTableStanding.DtoLinksTeam(teamRef);
            DtoLeagueTableStanding.DtoLinks dtoLinks = new DtoLeagueTableStanding.DtoLinks(dtoLinksTeam);

            return new DtoLeagueTableStanding( dtoLinks,position,teamName,playedGames,points,goals,wins,draws,losses);
        }

        private DtoLeague dollDtoLeague() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

            try {
                return dollDtoLeague(398,"Premier League 2015/16","PL","2015",38,38,20,380,sdf.parse("2016-05-19T15:12:55Z"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return null;
        }

        private DtoTeam dollDtoTeam() {
            //http://api.football-data.org/v1/teams/66
            return dollDtoTeam("http://api.football-data.org/v1/teams/66/players","Manchester United FC","MUFC","ManU",
                    "394,550,000 €","http://upload.wikimedia.org/wikipedia/de/d/da/Manchester_United_FC.svg");
        }

        private DtoTeam dollDtoTeam(String link, String name, String code, String shortName, String squadMarketValue, String crestUrl) {
            //http://api.football-data.org/v1/teams/66
            DtoTeam.DtoLinksPlayer linksToPLayers = new DtoTeam.DtoLinksPlayer(link);
            DtoTeam.DtoLinks links = new DtoTeam.DtoLinks(linksToPLayers);
            return new DtoTeam(links,name,code,shortName,squadMarketValue,crestUrl);
        }

        private Function<String,CompletableFuture<List<DtoPlayer>>> dollFunctionCfListDtoPlayers() {
            //http://api.football-data.org/v1/teams/66/players

            List<DtoPlayer> dtoPlayers = Arrays.asList(
                    new DtoPlayer("Bastian Schweinsteiger","Central Midfield",31,"1984-08-01","Germany","2018-06-30",null),
                    new DtoPlayer("David de Gea", "Keeper", 1, "1990-11-07", "Spain", "2019-06-30", null),
                    new DtoPlayer("Phil Jones", "Centre Back", 4, "1992-02-21", "England", "2019-06-30", null),
                    new DtoPlayer("Marcos Rojo", "Centre Back", 5, "1990-03-20", "Argentina", "2019-06-30", null),
                    new DtoPlayer("Chris Smalling","Centre Back",12,"1989-11-22","England","2019-06-30",null),
                    new DtoPlayer("Patrick McNair","Centre Back",33,"1995-04-27","Northern Ireland","2017-06-30",null),
                    new DtoPlayer("Luke Shaw","Left-Back",23,"1995-07-12","England","2018-06-30", null)
            );

            return (ref) -> CompletableFuture.supplyAsync(() -> dtoPlayers);
        }

        private DtoPlayersList dollDtoPlayersList(){
            return new DtoPlayersList(new DtoPlayer[]{
                    new DtoPlayer("Bastian Schweinsteiger","Central Midfield",31,"1984-08-01","Germany","2018-06-30",null),
                    new DtoPlayer("David de Gea", "Keeper", 1, "1990-11-07", "Spain", "2019-06-30", null),
                    new DtoPlayer("Phil Jones", "Centre Back", 4, "1992-02-21", "England", "2019-06-30", null),
                    new DtoPlayer("Marcos Rojo", "Centre Back", 5, "1990-03-20", "Argentina", "2019-06-30", null),
                    new DtoPlayer("Chris Smalling","Centre Back",12,"1989-11-22","England","2019-06-30",null),
                    new DtoPlayer("Patrick McNair","Centre Back",33,"1995-04-27","Northern Ireland","2017-06-30",null),
                    new DtoPlayer("Luke Shaw","Left-Back",23,"1995-07-12","England","2018-06-30", null)
            });
        }
    }
}