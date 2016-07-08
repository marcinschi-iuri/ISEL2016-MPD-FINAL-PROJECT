package domain.mapping;

import domain.League;
import domain.Player;
import domain.Team;
import api.dto.DtoLeague;
import api.dto.DtoLeagueTableStanding;
import api.dto.DtoPlayer;
import api.dto.DtoTeam;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Created by imarcinschi on 7/2/2016.
 */
public class DtoToDomainMapperImplTest {

    DtoToDomainMapper mapper;
    @Before
    public void init(){
        mapper = new DtoToDomainMapperImpl();
    }

    @Test
    public void testDtoLeagueToLeague() throws Exception {

        List<DtoLeague> dtoLeagueList = dollDtoLeagueList();
        Function<Integer, CompletableFuture<List<DtoLeagueTableStanding>>>
                                                            standings = dollFunctionListDtoLeagueTableStanding();
        Function<String, CompletableFuture<DtoTeam>> teamByHref = dollFunctionCFDtoTeam();
        Function<String, CompletableFuture<List<DtoPlayer>>> playersByHref = dollFunctionCfListDtoPlayers();

        List<League> leagues = mapper.dtoLeagueToLeague(dtoLeagueList, standings, teamByHref, playersByHref);

        Assert.assertNotNull(leagues);
        Assert.assertTrue(leagues.size()==4);

        League league = leagues.get(0);
        Assert.assertNotNull(league);
        Assert.assertTrue(league.getId()==398);

        leagues.forEach(System.out::println);

    }

    @Test
    public void testDtoTeamToTeam() throws Exception {
        DtoTeam dtoTeam = dollDtoTeam();
        Function<String, CompletableFuture<List< DtoPlayer >>> f = dollFunctionCfListDtoPlayers();
        Team team = mapper.dtoTeamToTeam(dtoTeam, f);
        Assert.assertNotNull(team);
        Assert.assertTrue(team.getName().equals(dtoTeam.name));
        Assert.assertTrue(team.getCode().equals(dtoTeam.code));
        Assert.assertTrue(team.getCrestUrl().equals(dtoTeam.crestUrl));
        Assert.assertTrue(team.getShortName().equals(dtoTeam.shortName));
        Assert.assertTrue(team.getSquadMarketValue().equals(dtoTeam.squadMarketValue));

        System.out.println(team);
    }

    @Test
    public void testDtoPlayerToPlayer() throws Exception {
        DtoPlayer dtoPlayer = dollDtoPlayer();
        Player player = mapper.dtoPlayerToPlayer(dtoPlayer);

        Assert.assertNotNull(player);
        Assert.assertTrue(player.getName().equals(dtoPlayer.name));
        Assert.assertTrue(player.getContractUntil().equals(dtoPlayer.contractUntil));
        Assert.assertTrue(player.getDateOfBirth().equals(dtoPlayer.dateOfBirth));
        Assert.assertTrue(player.getJerseyNumber() == dtoPlayer.jerseyNumber);
        Assert.assertTrue(player.getNationality().equals(dtoPlayer.nationality));
        Assert.assertTrue(player.getPosition().equals(dtoPlayer.position));

        System.out.println(player);
    }

    @Test
    public void testListOfDtoTeamsToListOfTeams() throws Exception {
        List<DtoTeam> dtoTeamList = dollListDtoTeams();
        Function<String, CompletableFuture<List<DtoPlayer>>> playersByHref = dollFunctionCfListDtoPlayers();

        List<Team> teamList = mapper.listOfDtoTeamsToListOfTeams(dtoTeamList, playersByHref);

        Assert.assertNotNull(teamList);
        Assert.assertFalse(teamList.size() == 0);

        Team team = teamList.get(0);
        Assert.assertNotNull(team);
        Assert.assertTrue(team.getName().equals("Manchester United FC"));

        teamList.forEach(System.out::println);

    }

    @Test
    public void testListOfDtoPlayerToListOfPlayer() throws Exception {

        List<DtoPlayer> dtoPlayers = dollFunctionCfListDtoPlayers().apply("").get();

        List<Player> players = mapper.listOfDtoPlayerToListOfPlayer(dtoPlayers);

        Assert.assertNotNull(players);
        Assert.assertFalse(players.size() == 0);
        Assert.assertTrue(players.get(0).getName().equals("Bastian Schweinsteiger"));

        players.forEach(System.out::println);
    }



    /***************************************Helper methods**********************************************/

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

    private Function<String,CompletableFuture<DtoTeam>> dollFunctionCFDtoTeam() {
        return (ref) -> CompletableFuture.supplyAsync( () -> dollDtoTeam());
    }

    private DtoTeam dollDtoTeam() {
        //http://api.football-data.org/v1/teams/66
        return dollDtoTeam("http://api.football-data.org/v1/teams/66/players","Manchester United FC","MUFC","ManU",
                "394,550,000 €","http://upload.wikimedia.org/wikipedia/de/d/da/Manchester_United_FC.svg");
    }

    private List<DtoTeam> dollListDtoTeams() {

        //http://api.football-data.org/v1/soccerseasons/398/teams

        List<DtoTeam> dtoTeams = Arrays.asList(
                dollDtoTeam("http://api.football-data.org/v1/teams/66/players", "Manchester United FC", "MUFC", "ManU",
                        "394,550,000 €", "http://upload.wikimedia.org/wikipedia/de/d/da/Manchester_United_FC.svg"),
                dollDtoTeam("http://api.football-data.org/v1/teams/73/players", "Tottenham Hotspur FC", "THFC", "Spurs",
                        "278,000,000 €", "http://upload.wikimedia.org/wikipedia/de/b/b4/Tottenham_Hotspur.svg"),
                dollDtoTeam("http://api.football-data.org/v1/teams/1044/players", "AFC Bournemouth", "AFCB", "Bournemouth",
                        "88,800,000 €", "https://upload.wikimedia.org/wikipedia/de/4/41/Afc_bournemouth.svg"),
                dollDtoTeam("http://api.football-data.org/v1/teams/58/players", "Aston Villa FC", "AVFC", "Aston Villa",
                        "114,750,000 €", "http://upload.wikimedia.org/wikipedia/de/9/9f/Aston_Villa_logo.svg"),
                dollDtoTeam("http://api.football-data.org/v1/teams/62/players", "Everton FC", "EFC", "Everton",
                        "209,500,000 €", "http://upload.wikimedia.org/wikipedia/de/f/f9/Everton_FC.svg")
        );
        return dtoTeams;
    }

    private DtoTeam dollDtoTeam(String link, String name, String code, String shortName, String squadMarketValue, String crestUrl) {
        //http://api.football-data.org/v1/teams/66
        DtoTeam.DtoLinksPlayer linksToPLayers = new DtoTeam.DtoLinksPlayer(link);
        DtoTeam.DtoLinks links = new DtoTeam.DtoLinks(linksToPLayers);
        return new DtoTeam(links,name,code,shortName,squadMarketValue,crestUrl);
    }

    private Function<Integer,CompletableFuture<List<DtoLeagueTableStanding>>> dollFunctionListDtoLeagueTableStanding() {
        //http://api.football-data.org/v1/soccerseasons/398/leagueTable

        DtoLeagueTableStanding.DtoLinksTeam dtoLinksTeam =
                new DtoLeagueTableStanding.DtoLinksTeam("http://api.football-data.org/v1/teams/338");
        DtoLeagueTableStanding.DtoLinks dtoLinks = new DtoLeagueTableStanding.DtoLinks(dtoLinksTeam);

        List<DtoLeagueTableStanding> dtoLeagueTableStandingsList = Arrays.asList(
                dollDtoLeagueTableStanding(
                        "http://api.football-data.org/v1/teams/338",1,"Leicester City FC",38,81,68,23,12,3),
                dollDtoLeagueTableStanding(
                        "http://api.football-data.org/v1/teams/57",2,"Arsenal FC",38,71,65,20,11,7),
                dollDtoLeagueTableStanding(
                        "http://api.football-data.org/v1/teams/73",3,"Tottenham Hotspur FC",38,70,69,19,13,6),
                dollDtoLeagueTableStanding(
                        "http://api.football-data.org/v1/teams/65",4,"Manchester City FC",38,66,71,19,9,10),
                dollDtoLeagueTableStanding(
                        "http://api.football-data.org/v1/teams/66",5,"Manchester United FC",38,66,49,19,9,10),
                dollDtoLeagueTableStanding(
                        "http://api.football-data.org/v1/teams/340",6,"Southampton FC",38,63,59,18,9,11),
                dollDtoLeagueTableStanding(
                        "http://api.football-data.org/v1/teams/563",7,"West Ham United FC",38,62,65,16,14,8)
        );

        return (ref) -> CompletableFuture.supplyAsync(()->dtoLeagueTableStandingsList);
    }

    private DtoLeagueTableStanding dollDtoLeagueTableStanding(String teamRef, int position, String teamName,
                                            int playedGames, int points,int goals,int wins,int draws,int losses){
        DtoLeagueTableStanding.DtoLinksTeam dtoLinksTeam =
                new DtoLeagueTableStanding.DtoLinksTeam(teamRef);
        DtoLeagueTableStanding.DtoLinks dtoLinks = new DtoLeagueTableStanding.DtoLinks(dtoLinksTeam);

        return new DtoLeagueTableStanding( dtoLinks,position,teamName,playedGames,points,goals,wins,draws,losses);
    }

    private List<DtoLeague> dollDtoLeagueList() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        List<DtoLeague> dtoLeagueList = null;
        try {
            dtoLeagueList = Arrays.asList(
                    dollDtoLeague(398,"Premier League 2015/16","PL","2015",38,38,20,380,sdf.parse("2016-05-19T15:12:55Z")),
                    dollDtoLeague(424,"European Championships France 2016","EC","2016",5,7,24,48,sdf.parse("2016-07-01T21:11:39Z")),
                    dollDtoLeague(426,"Premiere League 2016/17","PL","2016",1,38,20,380,sdf.parse("2016-06-23T10:42:02Z")),
                    dollDtoLeague(427,"Championship 2016/17","ELC","2016",1,46,24,552,sdf.parse("2016-06-27T12:39:32Z"))
            );
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dtoLeagueList;
    }

    private DtoLeague dollDtoLeague(int id,String caption,String league,String year,int currentMatchday,
                                    int numberOfMatchdays,int numberOfTeams,int numberOfGames,Date lastUpdated) {

        return new DtoLeague(
                id,caption,league,year,currentMatchday,numberOfMatchdays,numberOfTeams,numberOfGames,lastUpdated);
    }

    private DtoPlayer dollDtoPlayer() {
        return  new DtoPlayer("Bastian Schweinsteiger","Central Midfield",31,"1984-08-01","Germany","2018-06-30",null);
    }




}