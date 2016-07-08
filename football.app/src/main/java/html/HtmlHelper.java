package html;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import domain.League;
import domain.Player;
import domain.Standing;
import domain.Team;
import api.SoccerWebApiImpl;
import api.dto.DtoLeague;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by imarcinschi on 6/26/2016.
 */
public class HtmlHelper {
    TemplateLoader templateLoader;
    public HtmlHelper(){
        templateLoader = new ClassPathTemplateLoader();
        templateLoader.setPrefix("/templates");
        templateLoader.setSuffix(".html");
    }

    public String mapLeagues(List<League> leagues) {
        String tmp = "";
        try {

            Handlebars handlebars = new Handlebars(templateLoader);

            Template template = handlebars.compile("leagues");

            tmp = template.apply(leagues);

        } catch (IOException e) { e.printStackTrace();  }

        return tmp;
    }


    public String mapLeagueStandings(List<Standing> listStandings) {
        String tmp = "";
        try {

            Handlebars handlebars = new Handlebars(templateLoader);

            Template template = handlebars.compile("standings");

            tmp = template.apply(listStandings);

        } catch (IOException e) { e.printStackTrace();  }


        return tmp;
    }


    public String mapTeam(Team team) {
        String tmp = "";
        try {

            Handlebars handlebars = new Handlebars(templateLoader);

            Template template = handlebars.compile("team");

            tmp = template.apply(team);

        } catch (IOException e) { e.printStackTrace();  }


        return tmp;
    }

    public String mapPlayers(List<Player> players) {
        String tmp = "";
        try {

            Handlebars handlebars = new Handlebars(templateLoader);

            Template template = handlebars.compile("players");

            tmp = template.apply(players);

        } catch (IOException e) { e.printStackTrace();  }


        return tmp;
    }


}
