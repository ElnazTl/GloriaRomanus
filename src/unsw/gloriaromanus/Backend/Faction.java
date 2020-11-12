package unsw.gloriaromanus.Backend;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnore;
// import java.io.IOException;
public class Faction {

    @JsonIgnore
    private Database db;
    private String name;
    private List<Province> provinces;
    private List<Province> provincesConqueredOnTurn;
    private Map<String, Integer> availableUnits;
    private int treasury;
    private Province selectedProvince;
    private List <FactionObserver> observor;


    /**
     * Default constructor used for deserialisation
     */
    public Faction() {}


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
        observor = new ArrayList<FactionObserver>();
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
    
    public void subscribe(FactionObserver o) {
        observor.add(o);
    }

    public void notifysub() throws JsonParseException, JsonMappingException, IOException {
        for (FactionObserver o: observor) {
            o.update(this);
        }
    }
    /**
     *
     * Updates the faction after the turn,
     * then ends turn for the provinces it owns
     */
    public void endTurn()  throws JsonParseException, JsonMappingException, IOException {
        deselectProvince();
        for (Province p : provincesConqueredOnTurn) {
            provinces.add(p);
        }
        for (Province p : provinces) {
            treasury += p.taxProvince();
            p.endTurn();
        }
        notifysub();
        db.endTurn(this);
    }
    

    /**
     * Returns if it is the players turn
     * @return
     */
    @JsonIgnore
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
        for (Province p : provincesConqueredOnTurn) {
            if (name.equals(p.getName())) {
                return p;
            }
        }
        if (selectedProvince == null) return null;
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

    public List<String> getMoveableProvinces() {
        List<String> provs = new ArrayList<String>();
        for (Province p : provinces) {
            provs.add(p.getName());
        }
        if (selectedProvince != null) provs.add(selectedProvince.getName());
        return provs;
    }


    /**
     * Attempts to invade specified enemy province
     * with units selected in selected province
     * @param ownedProvince
     * @param enemyProvince
     * @return
     */
    public int invade(String enemyProvince) {
        if (findProvince(enemyProvince) != null) {
            System.out.println("Cannot invade province owned by attacker");   
            return -1;
        }
        if (selectedProvince == null) {
            System.out.println("Player has not selected a province to invade from");
            return -1;
        }
        if (selectedProvince.getSelectedUnits().isEmpty()) {
            System.out.println("Player has not selected any units in selected province to invade with");
            return -1;
        }
        return db.invade(selectedProvince, enemyProvince);
    }

    /**
     * Attempts to train unit in selected province with
     * given unit name
     * @param unit
     * @return
     */
    public boolean trainUnit(String unit)  {
        if (selectedProvince == null) {
            System.out.println("Player has not selected a province");
            return false;
        }
        int cost = getUnitCost(unit);
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
            System.out.println("No selected province to move troops from");
            return false;
        } else if (selectedProvince.getSelectedUnits().isEmpty()) {
            // No units selected
            System.out.println("No units are selected to move");
            return false;
        } else if (pTo == null) {
            // Province selected to move troops to is not
            // owned by this factions, use invade to move
            // troops from owned province to enemy province
            System.out.println("Can only move troops between owned provinces");
            return false;
        } else if (conqueredDuringTurn(pTo)) {
            // Cant move to a province conquered on turn
            System.out.println("Cannot move troops into a province conquered this turn");
            return false;
        } else {
            // pTo.addUnits(selectedProvince.getSelectedUnits());
            // selectedProvince.removeAllSelected();
            int minMovePoints = selectedProvince.minMoveUnits();
            return db.moveUnits(selectedProvince, pTo, getMoveableProvinces(), minMovePoints);
        }


    }




    /**
     * Returns the cost of the specified unit
     * If unit is not available to the faction,
     * returns -1
     */
    public int getUnitCost(String unit) {
        if (!availableUnits.containsKey(unit)) return -1;
        else return availableUnits.get(unit);
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
     * Loads default configs
     * @param unitsConfig
     * @param abilityConfig
     */
    public void loadConfigs(JSONObject unitsConfig, JSONObject abilityConfig) {
        for (Province p : provinces) {
            p.loadConfigs(unitsConfig, abilityConfig);
        }
        for (Province p : provincesConqueredOnTurn) {
            p.loadConfigs(unitsConfig, abilityConfig);
        }
        if (selectedProvince != null) {
            selectedProvince.loadConfigs(unitsConfig, abilityConfig);
        }
    }
    
    /**
     * Gets the string representation of the specified province
     * @param name
     * @return
     */
    @JsonIgnore
    public String getProvinceState(String name) {
        Province p = findProvince(name);
        if (p != null) return p.getProvinceState();
        return null;
    }


    /**
     * Returns a string representation of the state of the faction
     */
    @JsonIgnore
    public String getFactionState() {
        String state = "Faction: \"" + name + "\"";
        state += "\n\t-> treasury: " + treasury;
        state += "\n\t-> selected province: " + selectedProvince;
        state += "\n\t-> provinces: ";
        for (Province p : provinces) {
            state += ("\n\t\t- " + p.toString());
        }
        state += "\n\t-> provinces conquered this turn: ";
        for (Province p : provincesConqueredOnTurn) {
            state += ("\n\t\t- " + p.toString());
        }
        return state;
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

    public Database getDb() {
        return db;
    }

    public void setDb(Database db) {
        this.db = db;
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

    public Province getSelectedProvince() {
        return selectedProvince;
    }

    public void setSelectedProvince(Province selectedProvince) {
        this.selectedProvince = selectedProvince;
    }

}
