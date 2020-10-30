package unsw.gloriaromanus.Backend;

import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Faction {

    private Database db;
    private String name;
    private List<Province> provinces;
    private List<Province> provincesConqueredOnTurn;
    private Map<String, Integer> availableUnits;
    private int treasury;

    public Faction(Database db, String name, int startingGold, List<Province> initialProvinces) throws IOException {
        this.db = db;
        this.name = name;
        this.treasury = startingGold;
        this.provinces = initialProvinces;
        this.provincesConqueredOnTurn = new ArrayList<Province>();
        this.availableUnits = new HashMap<String, Integer>();
        loadUnitsFromConfig();
    }


    public String getName() {
        return name;
    }


    public List<Province> getProvinces() {
        return provinces;
    }


    public Map<String, Integer> getAvailableUnits() {
        return availableUnits;
    }
    

    public void newTurn() {
        for (Province p : provincesConqueredOnTurn) {
            provinces.add(p);
        }
        for (Province p : provinces) {
            p.newTurn();
        }
    }
    

    /**
     * Attempts to start training a unit with given name
     * as long as the player is allowed to train this unit
     * and if they have enough gold to buy the unit
     * 
     * @param p Province to train unit in
     * @param unit Name of unit to train
     * @return 
     * @throws IOException
     */
    public boolean trainUnit(Province p, String unit) throws IOException {
        if (!availableUnits.containsKey(unit)) {
            // Unit not available to this faction
            return false;
        }
        int cost = availableUnits.get(unit);
        
        if (cost > treasury) {
            // Faction does not have enough gold to buy unit
            return false;
        }

        return p.trainUnit(unit);
    }


    /**
     * Finds province with given name
     * 
     * @param name Name of province to find
     * @return Province with given name, otherwise null
     */
    public Province findProvince(String name) {
        for (Province p : provinces) {
            if (name.equals(p.getName())) {
                return p;
            }
        }
        return null;
    }


    /**
     * Adds given province to list of owned
     * provinces if not already in the list
     * 
     * @param p Province to add
     */
    public void addProvince(Province p) {
        if (!provinces.contains(p)) provinces.add(p);
    }


    /**
     * Adds given province to list of conquered
     * provinces during current turn if not already
     * in the list
     * 
     * @param p Province to add
     */
    public void addConqueredProvince(Province p) {
        if (!conqueredDuringTurn(p)) provincesConqueredOnTurn.add(p);
    }


    /**
     * Returns True if given province was conquered
     * during the players current turn
     * 
     * @param p Province to check
     * @return True if in list, otherwise False
     */
    public boolean conqueredDuringTurn(Province p) {
        return provincesConqueredOnTurn.contains(p);
    }


    /**
     * Loads the units availble to the faction
     * 
     * @throws IOException
     */
    private void loadUnitsFromConfig() throws IOException {
        String configString = Files.readString(Paths.get("bin/unsw/gloriaromanus/Backend/configs/faction_units_config.json"));
        JSONObject unitsConfig = new JSONObject(configString);
        List<Object> config = unitsConfig.getJSONArray(this.name).toList();
        for (Object o : config) {
            String s = (String)o;
            availableUnits.put(s, unitsConfig.getJSONObject(s).getInt("cost"));
        }
    }
}
