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
    private Map<String, Integer> availableUnits;
    private int treasury;

    public Faction(Database db, String name, int startingGold) throws IOException {
        this.db = db;
        this.name = name;
        this.treasury = startingGold;
        loadUnitsFromConfig();
        provinces = new ArrayList<Province>();
        availableUnits = new HashMap<String, Integer>();
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
        for (Province p : provinces) {
            p.newTurn();
        }
    }
    
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

        return trainUnit(p, unit);
    }


    public Province findProvince(String name) {
        for (Province p : provinces) {
            if (name.equals(p.getName())) {
                return p;
            }
        }
        return null;
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
