package unsw.gloriaromanus.Backend;

import org.json.JSONArray;
import org.json.JSONObject;

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
    private Province selectedProvince;


    /**
     * Initialises a faction with the given provinces, allowed units
     * and default config for units
     * @param db
     * @param name
     * @param provincesOwned
     * @param allowedUnits
     * @param unitsConfig
     */
    public Faction(Database db, String name, List<Province> provincesOwned, JSONObject allowedUnits, JSONObject unitsConfig) {
        this.db = db;
        this.name = name;
        this.treasury = 200;    // Starting gold
        this.provinces = provincesOwned;
        this.provincesConqueredOnTurn = new ArrayList<Province>();
        this.availableUnits = new HashMap<String, Integer>();
        this.selectedProvince = null;
        loadFromConfig(allowedUnits, unitsConfig);
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
    

    /**
     * Updates the faction after the turn,
     * then ends turn for the provinces it owns
     */
    public void endTurn() {
        deselectProvince();
        for (Province p : provincesConqueredOnTurn) {
            provinces.add(p);
        }
        for (Province p : provinces) {
            treasury += p.taxProvince();
            p.endTurn();
        }
        
        db.endTurn(this);
    }
    

    /**
     * Returns if it is the players turn
     * @return
     */
    public boolean isTurn() {
        return db.isTurn(this);
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
        if (name.equals(selectedProvince.getName())) return selectedProvince;
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
     * Removes given province from list of owned provinces
     * 
     * @param p Province to remove
     */
    public void removeProvince(Province p) {
        if (provinces.contains(p)) provinces.remove(p);
    }

    /**
     * Selects an owned province with given name
     * If province aleady selected, it is deselected
     * @param name
     */
    public void selectProvince(String name) {
        Province p = findProvince(name); 
        if (p == null) return;
        if (selectedProvince == null) {
            provinces.remove(p);
            selectedProvince = p;
        } else if (selectedProvince.equals(p)) {
            deselectProvince();
        } else {
            provinces.add(selectedProvince);
            selectedProvince = p;
        }
    }


    /**
     * Deselects selected province
     */
    public void deselectProvince() {
        if (selectedProvince == null) return;
        provinces.add(selectedProvince);
        selectedProvince = null;
    }


    /**
     * Adds unit with given id from selected province
     * @param unitID
     */
    public void selectUnit(Long unitID) {
        if (selectedProvince == null) return;
        selectedProvince.selectUnit(unitID);
    }


    /**
     * Attempts to invade specified enemy province
     * with units selected in selected province
     * @param ownedProvince
     * @param enemyProvince
     * @return
     */
    public int invade(String enemyProvince) {
        if (findProvince(enemyProvince) != null) return -1;        // Enemy province is owned by this faction
        if (selectedProvince == null) return -1;        // No selected province
        if (selectedProvince.getSelectedUnits().isEmpty()) return -1;      // No selected units to invade with
        return db.invade(selectedProvince, enemyProvince);
    }

    /**
     * Attempts to train unit in selected province with
     * given unit name
     * @param unit
     * @return
     */
    public boolean trainUnit(String unit) {
        if (selectedProvince == null) {
            System.out.println("Player has not selected a province");
            return false;
        }
        int cost = availableUnits.get(unit);
        if (cost > treasury) {
            // Faction does not have enough gold to buy unit
            return false;
        }
        boolean trained = selectedProvince.trainUnit(unit);
        if (!trained) return false;
        treasury -= cost;
        return true;
    }

    
    /**
     * Moves selected units from selected province to
     * specfied province
     * @param to
     * @return
     */
    public boolean moveUnits(String to) {
        Province pTo = findProvince(to);
        if (selectedProvince == null) {
            // No selected province to move troops from
            return false;
        } else if (pTo == null) {
            // Province selected to move troops to is not
            // owned by this factions, use invade to move
            // troops from owned province to enemy province
            return false;
        } else if (conqueredDuringTurn(pTo)) {
            // Cant move to a province conquered on turn
            return false;
        } else if (!db.isAdjacentProvince(selectedProvince.getName(), pTo.getName())) {
            // Provinces are not adjacent
            return false;
        } else if (selectedProvince.getSelectedUnits().isEmpty()) {
            // Player has not selected units to move
            return false;
        } else {
            pTo.addUnits(selectedProvince.getSelectedUnits());
            selectedProvince.removeAllSelected();
            return true;
        }


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
     * Loads faction from specified configs
     * @param allowedUnits
     * @param unitsConfig
     */
    private void loadFromConfig(JSONObject allowedUnits, JSONObject unitsConfig) {
        JSONArray config = allowedUnits.getJSONArray(this.name);
        for (Object o : config) {
            String s = (String)o;
            availableUnits.put(s, unitsConfig.getJSONObject(s).getInt("cost"));
        }
    }

    
    /**
     * Gets the string representation of the specified province
     * @param name
     * @return
     */
    public String getStateProvince(String name) {
        Province p = findProvince(name);
        if (p != null) return p.getState();
        return null;
    }

    @Override
    public String toString() {
        return name + " (faction)";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        Faction f = (Faction)obj;
        return name.equals(f.getName());
    }

}
