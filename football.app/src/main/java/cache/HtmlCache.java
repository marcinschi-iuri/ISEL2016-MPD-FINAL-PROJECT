package cache;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

/**
 * Created by imarcinschi on 7/4/2016.
 */
public class HtmlCache {

    private final String PROPERTIES_FILENAME= "mapper.properties";
    private String DIR_LEAGUES,DIR_LEAGUE_TABLES,DIR_TEAMS,DIR_PLAYERS,DIR_APP;
    private Path PATH_LEAGUES, PATH_LEAGUE_TABLE, PATH_TEAM, PATH_PLAYER;
    private long CACHE_SIZE;

    public HtmlCache(){
        loadProperties();
        loadCacheDirectories();
    }

    public CompletableFuture<Void> saveLeagues(String html) {
        return saveHtml(Paths.get(DIR_APP+DIR_LEAGUES + "/leagues.html"),html);
    }

    public CompletableFuture<String> getLeagues() {
        return getHtml(Paths.get(DIR_APP + DIR_LEAGUES + "/leagues.html"));
    }

    public CompletableFuture<Void> saveLeagueTable(int leagueId, String html) {
        return saveHtml(Paths.get(DIR_APP+DIR_LEAGUE_TABLES + "/leagueTable-"+leagueId+".html"),html);
    }

    public  CompletableFuture<String> getLeagueTable(int leagueId){
        return getHtml(Paths.get(DIR_APP+DIR_LEAGUE_TABLES + "/leagueTable-"+leagueId+".html"));
    }

    public  CompletableFuture<String> getTeam(int teamId) {
        return getHtml(Paths.get(DIR_APP+DIR_TEAMS + "/team-"+teamId+".html"));
    }

    public  CompletableFuture<Void> saveTeam(int teamId, String html) {
        return saveHtml(Paths.get(DIR_APP+DIR_TEAMS + "/team-"+teamId+".html"),html);
    }

    public CompletableFuture<String> getPlayers(int teamId) {
        return getHtml(Paths.get(DIR_APP+DIR_PLAYERS + "/playersOfTeam-"+teamId+".html"));
    }

    public CompletableFuture<Void> savePlayers(int teamId, String html) {
        return saveHtml(Paths.get(DIR_APP+DIR_PLAYERS + "/players-"+teamId+".html"),html);


    }


    private CompletableFuture<Void> saveHtml(Path file,String html){
        return CompletableFuture.runAsync(() -> {
            try {
                Files.write(file, html.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private CompletableFuture<String> getHtml(Path file){
        return CompletableFuture.supplyAsync(() -> {
            final StringBuffer[] buffer = {new StringBuffer()};
            try {
                if (Files.exists(file)) {
                    Files.readAllLines(file).forEach(s -> buffer[0] = buffer[0].append(s));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return buffer[0].toString();
        });
    }

    private void loadProperties(){
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(PROPERTIES_FILENAME));
            prop.forEach((key, value) -> assignProperty((String) key, (String)value));
        } catch (FileNotFoundException e) { e.printStackTrace();
        } catch (IOException e) {e.printStackTrace();}
    }

    private void assignProperty(String key, String value) {
        if (key.startsWith("cache.")) {
            String key2 = key.replace("cache.", "");
            if (key2.startsWith("path.")) {
                String key3 = key2.replace("path.", "");
                if (key3.equals("LEAGUES")) {
                    DIR_LEAGUES = value;
                } else if (key3.equals("LEAGUE_TABLES")) {
                    DIR_LEAGUE_TABLES = value;
                } else if (key3.equals("TEAMS")) {
                    DIR_TEAMS = value;
                } else if (key3.equals("PLAYERS")) {
                    DIR_PLAYERS = value;
                }
            } else if (key2.equals("size")) {
                CACHE_SIZE = Long.valueOf(value);
            }
        }
    }

    private void loadCacheDirectories() {
        try {
            DIR_APP = System.getProperty("user.dir").replace( "\\", "/" );
            PATH_LEAGUES = Paths.get(DIR_APP + DIR_LEAGUES);
            PATH_LEAGUE_TABLE = Paths.get(DIR_APP + DIR_LEAGUE_TABLES);
            PATH_TEAM = Paths.get(DIR_APP + DIR_TEAMS);
            PATH_PLAYER = Paths.get(DIR_APP + DIR_PLAYERS);

            Files.createDirectories(PATH_LEAGUES);
            Files.createDirectories(PATH_LEAGUE_TABLE);
            Files.createDirectories(PATH_TEAM);
            Files.createDirectories(PATH_PLAYER);

        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
