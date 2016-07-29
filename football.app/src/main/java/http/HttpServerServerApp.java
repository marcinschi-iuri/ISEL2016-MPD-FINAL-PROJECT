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
                    .addHandler("/soccerapp/leagueTable/*", controller::leagueTable)
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

/*            String cache_html_leagues = cache.getLeagues().join();

            if (cache_html_leagues != null && cache_html_leagues.length() != 0) return cache_html_leagues;

            List<League> service_leagues = service.getLeagues().join();

            String service_html_leagues = this.html.mapLeagues(service_leagues);

            cache.saveLeagues(service_html_leagues).join();

            return service_html_leagues;
*/
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

        //@Path("/soccerapp/leagueTable/*")
        public String leagueTable(HttpServletRequest req) {
            String path = req.getPathInfo();
            String[] arr = path.split("/");
            int leagueId = Integer.valueOf(arr[arr.length - 1]);

 /*           String cache_html_leagueTable = cache.getLeagueTable(leagueId).join();

            if (cache_html_leagueTable != null && cache_html_leagueTable.length() != 0) return cache_html_leagueTable;

            League service_league = service.getLeague(leagueId).join();

            List<Standing> service_leagueTable = service_league.getLeagueTable().join();

            String service_html_leagueTable = this.html.mapLeagueStandings(service_leagueTable);

            cache.saveLeagueTable(leagueId, service_html_leagueTable).join();

            return service_html_leagueTable;
*/

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