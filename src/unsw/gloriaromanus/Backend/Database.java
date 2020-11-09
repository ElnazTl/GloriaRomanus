package unsw.gloriaromanus.Backend;

import unsw.gloriaromanus.Backend.victoryCampaign.*;
import org.json.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Database {

    final private static String path = "src/unsw/gloriaromanus/";

    private List<String> factionsTaken;
    private List<String> factions;
    private List<Player> players;
    private Map<String, Player> intermediatePlayerFactions;
    private List<String> provinceNames;
    private boolean[][] provAdjMatrix;

    private int numProvicnes;
    private int numPlayers = 0;
    private int turnNumber = 0;
    private String currentPlayer = null;
    private int gameYear = 200 + turnNumber;

    @JsonIgnore
    private JSONObject defaultUnitsConfig;
    @JsonIgnore
    private JSONObject initialProvincesConfig;
    @JsonIgnore
    private JSONObject factionAllowedUnits;
    @JsonIgnore
    private JSONObject abilityConfig;

    /**
     * 
     * @throws IOException
     */
    public Database() throws IOException {
        factionsTaken = new ArrayList<String>();
        factions = new ArrayList<String>();
        players = new ArrayList<Player>();
        //playerFactions = new HashMap<Player, Faction>();
        intermediatePlayerFactions = new HashMap<String, Player>();
        provinceNames = new ArrayList<String>();
        loadDefaultConfigs();
        numProvicnes = provinceNames.size();
    }



    private void loadDefaultConfigs() throws IOException {
        // Load initial provinces
        String initialProvincesString = Files.readString(Paths.get(path + "initial_province_ownership.json"));
        initialProvincesConfig = new JSONObject(initialProvincesString);

        // Load default units config
        String defaultUnitsString = Files.readString(Paths.get(path + "Backend/configs/units_config.json"));
        defaultUnitsConfig = new JSONObject(defaultUnitsString);

        // Load factions allowed units
        String factionUnitsString = Files.readString(Paths.get(path + "Backend/configs/faction_units_config.json"));
        factionAllowedUnits = new JSONObject(factionUnitsString);

        // Load adjacency matrix
        String adjacencyString = Files.readString(Paths.get(path + "province_adjacency_matrix_fully_connected.json"));
        JSONObject provMatrixJSON = new JSONObject(adjacencyString);
        loadProvMatrix(provMatrixJSON);

        // Load ability config
        String abilityString = Files.readString(Paths.get(path + "Backend/configs/ability_config.json"));
        abilityConfig = new JSONObject(abilityString);

        // Add all Factions to list
        factions.addAll(initialProvincesConfig.keySet());
    }

    public Player addNewPlayer(String player, String name) {
        for (Player p : players) {
            if (player.equals(p.getUsername())) {
                // Username taken
                return null;
            }
        }
        if (!factionsTaken.contains(name)) {
            Player p = new Player(player);
            intermediatePlayerFactions.put(name, p);
            factionsTaken.add(name);
            numPlayers++;
            if (currentPlayer == null)
                currentPlayer = p.getUsername();
            return p;
        }
        return null;
    }

    public void startGame() {
        turnNumber = 1;
        if (numPlayers < 2) {
            System.out.println("Not enough players");
            return;
        } else if (numPlayers > 16) {
            System.out.println("Too many players");
        }
        assignProvinces();

    }

    // Assign provinces correctly
    private void assignProvinces() {
        List<Province> provincesAvailable = new ArrayList<Province>();
        for (String faction : initialProvincesConfig.keySet()) {
            if (!factionsTaken.contains(faction)) {
                // Faction names not been taken by a player
                JSONArray provincesJSON = initialProvincesConfig.getJSONArray(faction);
                for (Object o : provincesJSON) {
                    provincesAvailable.add(new Province((String) o, defaultUnitsConfig, abilityConfig));
                }

            }
        }

        Collections.shuffle(provincesAvailable);
        int numPerFaction = provincesAvailable.size() / factionsTaken.size();
        int remainder = provincesAvailable.size() % factionsTaken.size();
        for (String fString : factionsTaken) {
            List<Province> list = new ArrayList<Province>();
            JSONArray initialProvinces = initialProvincesConfig.getJSONArray(fString);

            // Add initial provinces
            for (Object o : initialProvinces.toList()) {
                Province p = new Province((String) o, defaultUnitsConfig, abilityConfig);
                list.add(p);
            }
            List<Province> sublist = provincesAvailable.subList(0, numPerFaction);
            if (remainder != 0) {
                sublist.add(provincesAvailable.get(numPerFaction));
                remainder--;
            }
            list.addAll(sublist);
            provincesAvailable.removeAll(sublist);
            Faction f = new Faction(this, fString, list, factionAllowedUnits, defaultUnitsConfig);
            Player p = intermediatePlayerFactions.get(fString);
            p.setFaction(f);
            // playerFactions.put(p, f);
            players.add(p);
        }
    }

    public Province findProvince(String name) {
        Province province = null;
        for (Player p : players) {
            province = p.getFaction().findProvince(name);
            if (province != null) break;
        }
        return province;
    }

    public int invade(Province attacker, String enemy) {
        if (!isAdjacentProvince(attacker.getName(), enemy)) {
            System.out.println("Provinces are not adjacent");
            return -1;
        }
        Province defender = findProvince(enemy);
        int result = BattleResolver.battle(attacker, defender);

        Faction aFaction = getFactionOfProvince(attacker);
        Faction dFaction = getFactionOfProvince(defender);

        if (result == 1) {
            // Attacker conquered province
            aFaction.addConqueredProvince(defender);
            dFaction.removeProvince(defender);
            return 1;
        }
        return result;
    }
    /**
     * sets the number of provinces in the game
     * @param x
     */
    public void setNumProvinces(int x) {
        numProvicnes = x;

    }
    /**
     * Function called after each invasion 
     * @param p
     */
    public int StateOfPlayer(Player p) {
        return VictoryCampaign.state(p, numProvicnes, 100000);
    }

    public Faction getFactionOfProvince(Province p) {
        for (Player player : players) {
            Faction f = player.getFaction();
            if (f.findProvince(p.getName()) != null) {
                return f;
            }
        }
        return null;
    }

    public boolean isAdjacentProvince(String province1, String province2) {
        return provAdjMatrix[provinceNames.indexOf(province1)][provinceNames.indexOf(province2)];
    }

    private Player getPlayerOfFaction(Faction f) {
        for (Player p : players) {
            if (f.equals(p.getFaction())) {
                return p;
            }
        }
        return null;
    }

    public void endTurn(Faction f) {
        Player p = getPlayerOfFaction(f);
        currentPlayer = nextPlayer(p).getUsername();
        turnNumber++;

    }

    private Player nextPlayer(Player player) {
        for (int i = 0; i < players.size(); i++) {
            if (player.equals(players.get(i))) {
                if (i == players.size() - 1)
                    return players.get(0);
                else
                    return players.get(i + 1);
            }
        }
        return null;
    }

    public boolean isTurn(Faction f) {
        return currentPlayer.equals(getPlayerOfFaction(f).getUsername());
    }

    public String getGameYear() {
        return gameYear + " BC";
    }

    public Player getCurrentPlayer() {
        for (Player player : players) {
            if (currentPlayer.equals(player.getUsername())) {
                return player;
            }
        }
        return null;
    }


    private void loadProvMatrix(JSONObject provMatrixJSON) {
        provinceNames = new ArrayList<String>(provMatrixJSON.keySet());
        provAdjMatrix = new boolean[provinceNames.size()][provinceNames.size()];
        for (int i = 0; i < provinceNames.size(); i++) {
            String provFrom = provinceNames.get(i);
            JSONObject provFromJSON = provMatrixJSON.getJSONObject(provFrom);
            for (int j = 0; j < provinceNames.size(); j++) {
                String provTo = provinceNames.get(j);
                provAdjMatrix[i][j] = provFromJSON.getBoolean(provTo);
            }
        }
    }


    public boolean moveUnits(Province from, Province to, List<String> provinces, int maxPoints) {
        if (from.getSelectedUnits().isEmpty()) {
            System.out.println("No units selected to move");
        }
        List<Integer> ownedProvinces = new ArrayList<Integer>();
        for (String p : provinces) {
            ownedProvinces.add(provinceNames.indexOf(p));
        }
        List<String> path = shortestPath(from.getName(), to.getName(), ownedProvinces, maxPoints);
        if (path == null) return false;
        System.out.println(path);
        to.addUnits(from.getSelectedUnits());
        from.removeAllSelected();
        return true;
    }

    public List<String> shortestPath(String from, String to, List<Integer> ownedProvinces, int maxPoints) {
        // num points used to move between a province = 2
        int pointsPerProvince = 2;

        List<String> path = new ArrayList<String>();
        int maxLength = maxPoints / pointsPerProvince;
        int numProv = provinceNames.size();

        int source = provinceNames.indexOf(from);
        int target = provinceNames.indexOf(to);

        List<Integer> q = new ArrayList<Integer>();
        int[] dist = new int[numProv];
        int[] prev = new int[numProv];
        
        for (int i = 0; i < numProv; i++) {
            dist[i] = Integer.MAX_VALUE;
            prev[i] = -1;
            // q.add(i);
        }

        for (int i : ownedProvinces) {
            q.add(i);
        }

        dist[source] = 0;

        while (!q.isEmpty()) {
            int index = minList(q, dist);
            if (index == -1) return null;
            int u = q.remove(index);
            if (u == target) break;
            List<Integer> adj = findAdjacent(u, ownedProvinces);
            for (int i : adj) {
                if (i == u) continue;
                int alt = dist[u] + pointsPerProvince;
                if (alt < dist[i]) {
                    dist[i] = alt;
                    prev[i] = u;
                }
            }
        }

        int u = target;
        if (prev[u] != -1 || u == source) {
            while (u != -1) {
                path.add(0, provinceNames.get(u));
                u = prev[u];
            }
        }

        if (path.isEmpty() || path.size() - 1 > maxLength) return null;
        return path;
    }



    private List<Integer> findAdjacent(int u, List<Integer> ownedProvs) {
        List<Integer> list = new ArrayList<Integer>();
        boolean[] provs = provAdjMatrix[u];
        for (int i = 0; i < provinceNames.size(); i++) {
            if (u == i) continue;
            if (provs[i] && ownedProvs.contains(i)) list.add(i);
        }
        return list;
    }

    private int minList(List<Integer> list, int[] dist) {
        int min = Integer.MAX_VALUE;
        int i = -1;
        for (int s : list) {
            if (dist[s] < min) {
                min = dist[s];
                i = list.indexOf(s);
            }
        }
        return i;
    }


    public void saveGame() throws IOException {
        ObjectMapper om = new ObjectMapper();
        FileOutputStream saveOS = new FileOutputStream(path + "Backend/save/save.json");
        ObjectWriter writer = om.writerWithDefaultPrettyPrinter();
        // saveOS.write(writer.writeValueAsBytes(playerFactions.entrySet()));
        saveOS.write(writer.writeValueAsBytes(players));
        saveOS.close();

        saveDatabase();
    }

    private void saveDatabase() throws IOException {
        JSONObject json = new JSONObject();
        json.put("numPlayers", numPlayers);
        json.put("turnNumber", turnNumber);
        json.put("currentPlayer", currentPlayer);
        FileWriter fw = new FileWriter(path + "Backend/save/db.json");
        fw.write(json.toString());
        fw.close();
    }

    public void loadGame() throws IOException {
        ObjectMapper om = new ObjectMapper();
        FileInputStream loadIS = new FileInputStream(path + "Backend/save/save.json");
        JsonFactory jf = new JsonFactory();
        players = om.readValue(jf.createParser(loadIS), new TypeReference<List<Player>>(){});
        // System.out.println(players);
        loadDatabase();

    }

    private void loadDatabase() throws IOException {
        String dbString = Files.readString(Paths.get(path + "Backend/save/db.json"));
        JSONObject dbConfig = new JSONObject(dbString);
        numPlayers = dbConfig.getInt("numPlayers");
        turnNumber = dbConfig.getInt("turnNumber");
        currentPlayer = dbConfig.getString("currentPlayer");
        loadConfigs();
    }


    private void loadConfigs() {
        for (Player p : players) {
            Faction f = p.getFaction();
            f.loadConfigs(defaultUnitsConfig, abilityConfig);
        }
    }

   

}


