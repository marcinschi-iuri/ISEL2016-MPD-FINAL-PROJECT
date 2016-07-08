package cache;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;

/**
 * Created by imarcinschi on 7/4/2016.
 */
public class HtmlCacheTest {

    private final String PROPERTIES_FILENAME= "mapper.properties";
    private String DIR_LEAGUES,DIR_LEAGUE_TABLES,DIR_TEAMS,DIR_PLAYERS,DIR_APP;
    private Path PATH_LEAGUES, PATH_LEAGUE_TABLE, PATH_TEAM, PATH_PLAYER;
    private long CACHE_SIZE;
    HtmlCache cache;

    @Before
    public void setUp() throws Exception {
        cache = new HtmlCache();
        loadProperties();
        loadCacheDirectories();
    }

    @Test
    public void testSaveLeagues() throws Exception {

        String filename = "leagues.html";
        String htmlToTest = getHtmlFromResources(filename);

        CompletableFuture<Void> voidCompletableFuture = cache.saveLeagues(htmlToTest);
        voidCompletableFuture.get();
        boolean cached = checkFileInCache(DIR_LEAGUES,filename);

        Assert.assertTrue(cached);
        String fileFromCache = getFileFromCache(DIR_LEAGUES, filename);
        Assert.assertTrue(htmlToTest.equals(fileFromCache));
    }


    @Test
    public void testGetLeagues() throws Exception {
        String htmlToTest = getHtmlFromResources("leagues.html");
        cache.saveLeagues(htmlToTest).get();

        CompletableFuture<String> leagues = cache.getLeagues();
        String leaguesHtml = leagues.get();

        Assert.assertTrue(htmlToTest.equals(leaguesHtml));
    }

    @Test
    public void testSaveLeagueTable() throws Exception {

        int leagueIdToTest = 123;
        String htmlToTest = getHtmlFromResources("standings.html");
        cache.saveLeagueTable(leagueIdToTest,htmlToTest).get();

        boolean cached = checkFileInCache(DIR_LEAGUE_TABLES,"leagueTable-123.html");
        Assert.assertTrue(cached);

        String fileFromCache = getFileFromCache(DIR_LEAGUE_TABLES, "leagueTable-123.html");
        Assert.assertTrue(htmlToTest.equals(fileFromCache));
    }

    @Test
    public void testGetLeagueTable() throws Exception {
        String htmlToTest = getHtmlFromResources("standings.html");
        cache.saveLeagueTable(123,htmlToTest).get();
        String htmlFromCache = cache.getLeagueTable(123).get();
        Assert.assertTrue(htmlToTest.equals(htmlFromCache));
    }

    @Test
    public void testSaveTeam() throws Exception {

        int teamId = 123;
        String htmlToTest = getHtmlFromResources("team.html");
        cache.saveTeam(teamId,htmlToTest).get();

        boolean cached = checkFileInCache(DIR_TEAMS,"team-123.html");
        Assert.assertTrue(cached);

        String fileFromCache = getFileFromCache(DIR_TEAMS, "team-123.html");
        Assert.assertTrue(htmlToTest.equals(fileFromCache));
    }

    @Test
    public void testGetTeam() throws Exception {
        String htmlToTest = getHtmlFromResources("team.html");
        cache.saveTeam(123,htmlToTest).get();
        String htmlFromCache = cache.getTeam(123).get();
        Assert.assertTrue(htmlToTest.equals(htmlFromCache));
    }

    @Test
    public void testSavePlayers() throws Exception {
        int teamId = 123;
        String htmlToTest = getHtmlFromResources("players.html");
        cache.savePlayers(teamId,htmlToTest).get();

        boolean cached = checkFileInCache(DIR_PLAYERS,"players-123.html");
        Assert.assertTrue(cached);

        String fileFromCache = getFileFromCache(DIR_PLAYERS, "players-123.html");
        Assert.assertTrue(htmlToTest.equals(fileFromCache));
    }

    @Test
    public void testGetPlayers() throws Exception {
        String htmlToTest = getHtmlFromResources("players.html");
        cache.savePlayers(123,htmlToTest).get();
        String htmlFromCache = cache.getPlayers(123).get();
        Assert.assertTrue(htmlToTest.equals(htmlFromCache));
    }

    private boolean checkFileInCache(String dir, String filename){
        String url = DIR_APP + dir + filename;
        Path path = Paths.get(url);
        boolean exists = Files.exists(path);
        return exists;
    }
    private String getFileFromCache(String dir, String filename){
        String url = DIR_APP + dir + filename;
        Path path = Paths.get(url);
        final StringBuffer[] buffer = {new StringBuffer()};

        try {
            if (Files.exists(path)) {
                Files.readAllLines(path).forEach(s -> buffer[0] = buffer[0].append(s));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer[0].toString();
    }

    private String getHtmlFromResources(String fileName){
        ClassLoader classLoader = getClass().getClassLoader();
        String pathString = classLoader.getResource("sampleHtml/"+fileName).getFile().substring(1);
        Path path = Paths.get(pathString);

        final StringBuffer[] buffer = {new StringBuffer()};

        try {
            if (Files.exists(path)) {
                Files.readAllLines(path).forEach(s -> buffer[0] = buffer[0].append(s));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer[0].toString();
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
        DIR_APP = System.getProperty("user.dir").replace( "\\", "/" );
        PATH_LEAGUES = Paths.get(DIR_APP + DIR_LEAGUES);
        PATH_LEAGUE_TABLE = Paths.get(DIR_APP + DIR_LEAGUE_TABLES);
        PATH_TEAM = Paths.get(DIR_APP + DIR_TEAMS);
        PATH_PLAYER = Paths.get(DIR_APP + DIR_PLAYERS);
    }
}