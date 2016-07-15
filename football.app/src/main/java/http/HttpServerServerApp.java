package http;

import cache.HtmlCache;
import domain.League;
import domain.Standing;
import domain.Team;
import domain.mapping.DtoToDomainMapperImpl;
import domain.service.SoccerSeasonsService;
import api.SoccerWebApiImpl;
import html.HtmlHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author Miguel Gamboa
 *         created on 03-06-2016
 */
public class HttpServerServerApp {

    public static void main(String[] args) throws Exception {

        SoccerSeasonsService service = new SoccerSeasonsService( new DtoToDomainMapperImpl(), new SoccerWebApiImpl());

        Controller controller = new Controller(service,new HtmlHelper(), new HtmlCache());

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
        private HtmlCache cache;


        public Controller(SoccerSeasonsService service, HtmlHelper htmlHelper, HtmlCache htmlCache) {
            this.service = service;
            this.html = htmlHelper;
            this.cache = htmlCache;
        }
        //@Path("/soccerapp/leagues") or @Path("/")
        public String leagues(HttpServletRequest req) {
            return cache.getLeagues()
                    .thenApply(
                           html -> {
                               if (html != null && html.length() != 0) return html;
                               html = this.html.mapLeagues(service.getLeagues().join());
                               cache.saveLeagues(html).join();
                               return html;
                           }
                    ).join();
        }

        //@Path("/soccerapp/leagues/*")
        public String leagueTable(HttpServletRequest req) {
            String path = req.getPathInfo();
            String[] arr = path.split("/");
            int leagueId = Integer.valueOf(arr[arr.length - 1]);
            return cache.getLeagueTable(leagueId)
                    .thenApply(html -> {
                        if (html != null && html.length() != 0) return html;
                        try {
                            html = service.getLeague(leagueId)
                                    .thenApply(league -> league.getLeagueTable().join())
                                    .thenApply(cfTable -> this.html.mapLeagueStandings(cfTable)).get();
                            cache.saveLeagueTable(leagueId, html).join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        return html;
                    }).join();
        }

        //@Path("/soccerapp/teams/*")
        public String team(HttpServletRequest req) {

            String path = req.getPathInfo();
            String[] arr = path.split("/");
            int teamId = Integer.valueOf(arr[arr.length-1]);

            return cache.getTeam(teamId).thenApply(teamHtml -> {

                if (teamHtml != null && teamHtml.length() != 0)
                            return teamHtml;

                teamHtml = html.mapTeam(service.getTeam(teamId).join());
                cache.saveTeam(teamId,teamHtml).join();

                return teamHtml;
            }).join();
        }

        //@Path("/soccerapp/players/*")
        public String players(HttpServletRequest req) {
            String path = req.getPathInfo();
            String[] arr = path.split("/");
            int teamId = Integer.valueOf(arr[arr.length-1]);

            return cache.getPlayers(teamId).thenApply(
                    playersHtml -> {
                        if (playersHtml != null && playersHtml.length() != 0)
                            return playersHtml;
                        playersHtml = this.html.mapPlayers(service.getTeamPlayers(teamId).join());
                        cache.savePlayers(teamId,playersHtml);
                        return playersHtml;
                    }
            ).join();
        }
    }


}