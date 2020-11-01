package unsw.gloriaromanus.Backend;

import org.json.JSONObject;

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

    public Faction(){}
    
    public Faction(Database db, String name, List<Province> initialProvinces) throws IOException {
        this.db = db;
        this.name = name;
        this.treasury = 200;    // Starting gold
        this.provinces = initialProvinces;
        this.provincesConqueredOnTurn = new ArrayList<Province>();
        this.availableUnits = new HashMap<String, Integer>();
        loadFromConfig();
    }

    public void setDatabase(Database db) {
        this.db = db;
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
    

    public void endTurn() {
        for (Province p : provincesConqueredOnTurn) {
            provinces.add(p);
        }
        for (Province p : provinces) {
            p.endTurn();
            treasury += p.taxProvince();
        }
        db.endTurn(this);
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
     * Attempts to start training a unit with given name
     * as long as the player is allowed to train this unit
     * and if they have enough gold to buy the unit
     * 
     * @param p Province to train unit in
     * @param unit Name of unit to train
     * @return 
     * @throws IOException
     */
    public boolean trainUnit(String province, String unit) throws IOException {
        Province p = findProvince(province);
        if (p == null) {
            System.out.println("Could not find province");
            return false;
        }
        if (!availableUnits.containsKey(unit)) {
            // Unit not available to this faction
            System.out.println("Faction cannot train this unit");
            return false;
        }
        int cost = availableUnits.get(unit);
        if (cost > treasury) {
            // Faction does not have enough gold to buy unit
            System.out.println("Faction does not have enough gold to train this unit");
            return false;
        }
        boolean training = p.trainUnit(unit);
        if (training) {
            treasury = treasury - cost;
        }
        return training;
    }

    public boolean moveUnits(String from, String to) throws IOException {
        Province p1 = findProvince(from);
        Province p2 = findProvince(to);
        if (p1 == null || p2 == null) {
            // One or both provinces not owned by faction
            // Use invade to move from owned province to enemy province
            return false;
        } else if (!db.isAdjacentProvince(p1.getName(), p2.getName())) {
            // Provinces are not adjacent
            return false;
        } else if (p1.getSelectedUnits().isEmpty()) {
            // Player has not selected units to move
            return false;
        } else {
            p2.addUnits(p1.getSelectedUnits());
            p1.removeAllSelected();
            return true;
        }


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
     * Removes given province from list of owned provinces
     * 
     * @param p Province to remove
     */
    public void removeProvince(Province p) {
        if (provinces.contains(p)) provinces.remove(p);
    }


    /**
     * Adds unit to selected units for given province
     * 
     * @param province Province to add unit to
     * @param unit Unit to add to selection
     */
    public void addUnitSelection(String province, Long unit) {
        Province p = findProvince(province);
        p.selectUnit(unit);
    }



    public int invade(String ownedProvince, String enemyProvince) {
        Province p = findProvince(ownedProvince);
        if (p.getSelectedUnits().isEmpty()) return -1;
        return db.invade(p, enemyProvince);
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
    private void loadFromConfig() throws IOException {
        String unitConfigString = Files.readString(Paths.get("bin/unsw/gloriaromanus/Backend/configs/units_config.json"));
        JSONObject unitsConfig = new JSONObject(unitConfigString);

        String factionConfigString = Files.readString(Paths.get("bin/unsw/gloriaromanus/Backend/configs/faction_units_config.json"));
        JSONObject allowedUnitsConfig = new JSONObject(factionConfigString);

        List<Object> config = allowedUnitsConfig.getJSONArray(this.name).toList();
        for (Object o : config) {
            String s = (String)o;
            availableUnits.put(s, unitsConfig.getJSONObject(s).getInt("cost"));
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProvinces(List<Province> provinces) {
        this.provinces = provinces;
    }

    public List<Province> getProvincesConqueredOnTurn() {
        return provincesConqueredOnTurn;
    }

    public void setProvincesConqueredOnTurn(List<Province> provincesConqueredOnTurn) {
        this.provincesConqueredOnTurn = provincesConqueredOnTurn;
    }

    public void setAvailableUnits(Map<String, Integer> availableUnits) {
        this.availableUnits = availableUnits;
    }

    public int getTreasury() {
        return treasury;
    }

    public void setTreasury(int treasury) {
        this.treasury = treasury;
    }




    @Override
    public String toString() {
        return name + " Faction: " + (provinces.size() + provincesConqueredOnTurn.size()) + " provinces owned";
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Faction f = (Faction)obj;
        return name.equals(f.getName());
    }


}
