package unsw.gloriaromanus.Backend;

import org.json.*;
// import org.junit.jupiter.api.condition.OS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.StackWalker.Option;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import com.esri.arcgisruntime.internal.security.Token;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Database {

    // private Map<String,List<Unit>> provinceUnit;
    // private Map<String,Faction> provinceList;
    // private String address;
    // private Map<String, ArrayList<Province>> factionList;
    
    


    
    private List<String> factionsTaken;
    private List<String> factions;
    private Map<Player, Faction> playerFactions;
    private Map<String, Player> intermediatePlayerFactions;
    private int numPlayers = 0;
    private int turnNumber = 0;
    private Player currentPlayer = null;
    private String gameYear = (200 + turnNumber) + " BC";

    private String path;
    
    private JSONObject provinceAdjacencyMatrix;
    private JSONObject defaultUnitsConfig;
    private JSONObject initialProvincesConfig;
    private JSONObject factionAllowedUnits;
    private JSONObject abilityConfig;




    /**
     * 
     * @throws IOException
     */
    public Database(boolean test) throws IOException {
        factionsTaken = new ArrayList<String>();
        factions = new ArrayList<String>();
        playerFactions = new HashMap<Player, Faction>();
        intermediatePlayerFactions = new HashMap<String, Player>();
        loadPath(test);
        loadDefaultConfigs();

        
    }



    private void loadPath(boolean test) {
        if (test) {
            path = "bin/unsw/gloriaromanus/";
        } else {
            path = "src/unsw/gloriaromanus/";
        }
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
        provinceAdjacencyMatrix = new JSONObject(adjacencyString);

        // Load ability config
        String abilityString = Files.readString(Paths.get(path + "Backend/configs/ability_config.json"));
        abilityConfig = new JSONObject(abilityString);

        // Add all Factions to list
        factions.addAll(initialProvincesConfig.keySet());
    }

    public Player addNewPlayer(String player, String name) {
        for (Player p : playerFactions.keySet()) {
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
            if (currentPlayer == null) currentPlayer = p;
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
                    provincesAvailable.add(new Province((String)o, defaultUnitsConfig, abilityConfig));
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
                Province p = new Province((String)o, defaultUnitsConfig, abilityConfig);
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
            playerFactions.put(p, f);
        }
    }


    public Province findProvince(String name) {
        Province p = null;
        for (Faction f : playerFactions.values()) {
            p = f.findProvince(name);
            if (p != null) break;
        }
        return p;
    }


    public int invade(Province attacker, String enemy) {
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


    public Faction getFactionOfProvince(Province p) {
        for (Faction f : playerFactions.values()) {
            if (f.findProvince(p.getName()) != null) return f;
        }
        return null;
    }


    public boolean isAdjacentProvince(String province1, String province2) {
        return provinceAdjacencyMatrix.getJSONObject(province1).getBoolean(province2);
    }

    private Player getPlayerOfFaction(Faction f) {
        for (Map.Entry<Player, Faction> entry : playerFactions.entrySet()) {
            if (f.equals(entry.getValue())) {
                return entry.getKey(); 
            }
        }
        return null;
    }

    public void endTurn(Faction f) {
        Player p = getPlayerOfFaction(f);
        currentPlayer = nextPlayer(p);
        turnNumber++;

    }


    private Player nextPlayer(Player player) {
        List<Player> playerList = new ArrayList<Player>(playerFactions.keySet());

        for (int i = 0; i < playerList.size(); i++) {
            if (player.equals(playerList.get(i))) {
                if (i == playerList.size() - 1) return playerList.get(0);
                else return playerList.get(i + 1);
            }
        }
        return null;
    }


    public boolean isTurn(Faction f) {
        return currentPlayer.equals(getPlayerOfFaction(f));
    }


    public String getGameYear() {
        return gameYear;
    }






















    // public Map<String,List<Unit>> getProvinceUnit() {
    //     return provinceUnit;
    // }

    // public Map<String,Faction > getFactionProvince() {
    //     return provinceList;
    // }


    // public void addFaction(String faction) throws IOException {
    //     addtoFile(faction, "F", " ");
    // }

    // public void addProvince(String province, String faction) throws IOException {
    //     addtoFile(faction, "P", province);
    // }

    // /**
    //  * adds faction/province to the database
    //  * @param faction
    //  * @param option
    //  * @param province
    //  * @throws IOException
    //  */
    // public void addtoFile(String faction, String option, String province) throws IOException {

    //     String content = Files.readString(Paths.get(address));
    //     JSONObject ownership = new JSONObject(content);

    //     if (option.equals("F")) {
    //         JSONArray empty = new JSONArray();
    //         ownership.put(faction, empty);
    //     }

    //     else {
    //         Object object = ownership.get(faction);
    //         JSONArray list = (JSONArray) object;
    //         list.put(province);
    //     }
    //         // Files.write(path, bytes, options)
    //         Files.writeString(Paths.get(address), ownership.toString());


        
    // }

    // /**
    //  * Function initilises the map of province to faction and set the province list for each faction 
    //  * @return map of province to faction 
    //  * @throws IOException
    //  */

    // private Map<String, List<Unit>> setOwningUnit() {
    //     Map<String, List<Unit>> m = new HashMap<String, List<Unit>>();
    //     for (String provinceName : provinceList.keySet()) {
    //         ArrayList<Unit> u = new ArrayList<Unit> ();
    //         m.put(provinceName, u);
    //       }

    //       return m;
    // }

    // private Map<String,Faction> setProvinceToOwningFactionMap() throws IOException {
    //     ArrayList<Province> ps = new ArrayList<Province> ();
    //     Map<String,Faction> m = new HashMap<String,Faction>();
    //     String content = Files.readString(Paths.get(address));
    //     JSONObject ownership = new JSONObject(content);
    //     for (String key : ownership.keySet()) {
    //       // key will be the faction name
    //       JSONArray ja = ownership.getJSONArray(key);
    //       // value is province name
    //       for (int i = 0; i < ja.length(); i++) {
    //         String value = ja.getString(i);

    //         Faction f = new Faction(key);
    //         m.put(value,f);

    //       }
          
    //     }
    //     return m;
    //   }

    //   private Map<String,ArrayList<Province>> setOwningProvince() throws IOException {
    //     Map<String,ArrayList<Province>> m = new HashMap<String,ArrayList<Province>>();
    //     String content = Files.readString(Paths.get(address));
    //     JSONObject ownership = new JSONObject(content);
    //     for (String key : ownership.keySet()) {
    //         ArrayList<Province> ps = new ArrayList<Province> ();

    //       // key will be the faction name
    //       JSONArray ja = ownership.getJSONArray(key);
    //       // value is province name
    //       for (int i = 0; i < ja.length(); i++) {
    //         String value = ja.getString(i);

    //         Faction f = new Faction(key);
    //         ps.add(new Province(value,this));

    //       }
    //       m.put(key,ps);
          
    //     }
    //     return m;
    //   }


    

    // public void saveGame() throws IOException {
    //     OutputStream os = new FileOutputStream("bin/unsw/gloriaromanus/Backend/configs/load.json");
    //     ObjectMapper om = new ObjectMapper();
    //     OutputStream os1 = new FileOutputStream("bin/unsw/gloriaromanus/Backend/configs/loadPlayer.json");
    //     JsonGenerator g = om.getFactory().createGenerator(os);
    //     JsonGenerator g1 = om.getFactory().createGenerator(os1);


    //     for (Player player:players) {
    //         om.writeValue(g1,player);
            
    //     }
    //     for (String f: factionList.keySet()) {
    //         for (Province p: factionList.get(f)) {
    //             saveProvince(f,p, om,g);
    //         }
    //     }
      
    // }



    // public void loadGame() throws IOException {
    //     loadPlayer();
    //     loadProvince();
    // }



    // /**
    //  * Will load the saved players
    //  */

    // public void loadPlayer() throws IOException {
    //     FileReader fis = new FileReader("bin/unsw/gloriaromanus/Backend/configs/loadPlayer.json");
    //     JsonFactory jf = new JsonFactory();
    //     ObjectMapper mapper = new ObjectMapper();
    //     Iterator<Player> value = mapper.readValues( jf.createParser(fis), Player.class);

    //     while(value.hasNext()) {
    //         Player p = value.next();
    //         p.setDatabase(this);
    //         addPlayer(p);

    //     }

    // }

    // public void loadProvince() throws IOException {

    //     FileReader fis = new FileReader("bin/unsw/gloriaromanus/Backend/configs/load.json");
    //     JsonFactory jf = new JsonFactory();
    //     ObjectMapper mapper = new ObjectMapper();
    //     mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
    //     Iterator<Province> value = mapper.readValues( jf.createParser(fis), Province.class);

    //     while(value.hasNext()) {
    //         Province p = value.next();
    //         List<Unit> u = p.getUnits();
    //         provinceUnit.put(p.getName(), u);
    //         provinceList.put(p.getName(), new Faction(p.getFaction()));
    //         factionList.get(p.getFaction()).add(p);
    //         provinceTraining.put(p.getName(), p.getUnitsTraining());
    //         // p.setDatabase(this);
    //         if (!p.getUnits().isEmpty()) System.out.println(p.getUnits().get(0).getCost());
    //     }


    // }

       
    
    // public void saveProvince(String f, Province p, ObjectMapper om, JsonGenerator g) throws IOException {

    //     //TODO: save other features of the province in the file 
    //     om.writeValue(g,p);


    // } 

   
   
    
}








