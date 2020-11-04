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
    

    public boolean isTurn() {
        return db.isTurn(this);
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
    public boolean trainUnit(Province p, String unit) {
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

    public void selectProvince(String name) {
        Province p = findProvince(name); 
        if (p == null) return;
        if (selectedProvince == null) {
            provinces.remove(p);
            selectedProvince = p;
        } else if (selectedProvince.equals(p)) {
            provinces.add(p);
            selectedProvince = null;
        } else {
            provinces.add(selectedProvince);
            selectedProvince = p;
        }
    }

    public void deselectProvince() {
        if (selectedProvince == null) return;
        provinces.add(selectedProvince);
        selectedProvince = null;
    }

    /**
     * Adds unit to selected units for given province
     * 
     * @param province Province to add unit to
     * @param unit Unit to add to selection
     */
    public void selectUnit(Long unitID) {
        if (selectedProvince == null) return;
        selectedProvince.selectUnit(unitID);
    }



    public int invade(String ownedProvince, String enemyProvince) {
        Province p = findProvince(ownedProvince);
        if (p.getSelectedUnits().isEmpty()) return -1;
        return db.invade(p, enemyProvince);
    }

    public boolean trainUnit(String unit) {
        if (selectedProvince == null) {
            System.out.println("Player has not selected a province");
            return false;
        }
        return selectedProvince.trainUnit(unit);
    }

    public boolean moveUnits(String to) {
        Province pTo = findProvince(to);
        if (selectedProvince == null || pTo == null) {
            // One or both provinces not owned by faction
            // Use invade to move from owned province to enemy province
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
     * Loads the units availble to the faction
     * 
     * @throws IOException
     */
    private void loadFromConfig(JSONObject allowedUnits, JSONObject unitsConfig) {
        JSONArray config = allowedUnits.getJSONArray(this.name);
        for (Object o : config) {
            String s = (String)o;
            availableUnits.put(s, unitsConfig.getJSONObject(s).getInt("cost"));
        }
    }


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
