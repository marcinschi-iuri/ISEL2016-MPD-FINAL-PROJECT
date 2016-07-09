package http;

import cache.HtmlCache;
import domain.League;
import domain.Standing;
import domain.mapping.DtoToDomainMapperImpl;
import domain.service.SoccerSeasonsService;
import api.SoccerWebApiImpl;
import html.HtmlHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Miguel Gamboa
 *         created on 03-06-2016
 */
public class HttpServerServerApp {

    public static void main(String[] args) throws Exception {

        SoccerSeasonsService service = new SoccerSeasonsService( new DtoToDomainMapperImpl(), new SoccerWebApiImpl());
        Controller controller = new Controller(service,new HtmlHelper());
        new HttpServer()
                    .addHandler("/", controller::leagues)
                    .addHandler("/soccerapp/leagues", controller::leagues)
                    .addHandler("/soccerapp/leagues/*", controller::leagueTable)
                    .addHandler("/soccerapp/teams/*", controller::team)
                    .addHandler("/soccerapp/players/*", controller::players)
                    .run();
    }

    static class Controller {
        private SoccerSeasonsService service;
        private HtmlHelper html;


        public Controller(SoccerSeasonsService service, HtmlHelper htmlHelper) {
            this.service = service;
            this.html = htmlHelper;
        }
        //@Path("/soccerapp/leagues") or @Path("/")
        public String leagues(HttpServletRequest req) {
            String leaguesListHtml = html.mapLeagues(service.getLeagues().join());
            return leaguesListHtml;
        }

        //@Path("/soccerapp/leagues/*")
        public String leagueTable(HttpServletRequest req) {

            String path = req.getPathInfo();
            String[] arr = path.split("/");
            int leagueId = Integer.valueOf(arr[arr.length-1]);

            CompletableFuture<String> cfHtml = service.getLeague(leagueId)
                    .thenApply(league -> league.getLeagueTable().join())
                    .thenApply(cfTable -> html.mapLeagueStandings(cfTable));

            return cfHtml.join();
        }

        //@Path("/soccerapp/teams/*")
        public String team(HttpServletRequest req) {

            String path = req.getPathInfo();
            String[] arr = path.split("/");
            int teamId = Integer.valueOf(arr[arr.length-1]);

            CompletableFuture<String> cfHtml = service
                    .getTeam(teamId)
                    .thenApply(team -> html.mapTeam(team));

            return cfHtml.join();
        }

        //@Path("/soccerapp/players/*")
        public String players(HttpServletRequest req) {
            String path = req.getPathInfo();
            String[] arr = path.split("/");
            int teamId = Integer.valueOf(arr[arr.length-1]);

            CompletableFuture<String> cfHtml = service
                    .getTeamPlayers(teamId)
                    .thenApply(playerList -> html.mapPlayers(playerList));

            return cfHtml.join();
        }
    }


}