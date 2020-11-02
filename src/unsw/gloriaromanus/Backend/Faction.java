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

        // for (Province p: provinces) {
        //     p.setDatabase(db);
        // }
        // loadFromConfig();

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
            
            p.setDatabase(db);
            
            p.endTurn();

            // treasury += p.taxProvince();
        }
        db.endTurn(this);
    }
    
    public void selectUnit(String province, Long unit) {
        Province p = findProvince(province);
        p.selectUnit(unit);
    }

    public List<Unit> getUnitsFromProvince(String province) {
        Province p = findProvince(province);
        return p.getUnits();
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

    public String moveUnits(String from, String to) throws IOException {
        Province p1 = findProvince(from);
        Province p2 = findProvince(to);
        if (p1 == null || p2 == null) {
            // One or both provinces not owned by faction
            // Use invade to move from owned province to enemy province

            return "provinces not in the same faction";
        } else if (!db.isAdjacentProvince(p1.getName(), p2.getName())) {
            // Provinces are not adjacent
            return "not adjecant";
        } else if (p1.getSelectedUnits().isEmpty()) {
            // Player has not selected units to move
            return "no unit was selected to move";
        } else {
            p2.addUnits(p1.getSelectedUnits());
            p1.removeAllSelected();
            return "successfully moved";
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
        if (findProvince(enemyProvince) != null) return -1;
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
        // String unitConfigString = "{\r\n    \"soldier\": {\r\n        \"type\" : \"infantry\",\r\n        \"attackType\" : \"melee\",\r\n        \"numTroops\" : 10,\r\n        \"cost\" : 5,\r\n        \"trainTime\" : 1,\r\n        \"attack\" : 4,\r\n        \"speed\" : 4,\r\n        \"morale\" : 5,\r\n        \"armour\" : 3,\r\n        \"shield\" : 5,\r\n        \"defence\" : 6,\r\n        \"ability\" : \"noAbility\"\r\n\r\n    },\r\n    \"horseArcher\": {\r\n        \"type\" : \"cavalry\",\r\n        \"attackType\" : \"ranged\",\r\n        \"numTroops\" : 8,\r\n        \"cost\" : 5,\r\n        \"trainTime\" : 2,\r\n        \"attack\" : 6,\r\n        \"speed\" : 7,\r\n        \"morale\" : 4,\r\n        \"armour\" : 2,\r\n        \"shield\" : 2,\r\n        \"charge\" : 5,\r\n        \"ability\" : \"noAbility\"\r\n    },\r\n    \"hoplite\": {\r\n        \"type\" : \"infantry\",\r\n        \"attackType\" : \"melee\",\r\n        \"numTroops\" : 8,\r\n        \"cost\" : 5,\r\n        \"trainTime\" : 2,\r\n        \"attack\" : 8,\r\n        \"speed\" : 2,\r\n        \"morale\" : 4,\r\n        \"shield\" : 4,\r\n        \"defence\" : 6,\r\n        \"ability\" : \"phalanx\"\r\n    },\r\n    \"artillery\": {\r\n        \"type\" : \"artillery\",\r\n        \"attackType\" : \"ranged\",\r\n        \"numTroops\" : 3,\r\n        \"cost\" : 40,\r\n        \"trainTime\" : 3,\r\n        \"attack\" : 12,\r\n        \"speed\" : 1,\r\n        \"morale\" : 3,\r\n        \"shield\" : 6,\r\n        \"defence\" : 6,\r\n        \"ability\" : \"noAbility\"\r\n    }\r\n}\r\n";
        String unitConfigString = Files.readString(Paths.get("bin/unsw/gloriaromanus/Backend/configs/units_config.json"));
        JSONObject unitsConfig = new JSONObject(unitConfigString);

        // String factionConfigString = "{\r\n    \"Rome\": [\r\n        \"soldier\",\r\n        \"horseArcher\",\r\n        \"artillery\"\r\n    ],\r\n    \"Gaul\": [\r\n        \"soldier\",\r\n        \"horseArcher\",\r\n        \"artillery\"\r\n    ],\r\n    \"Carthage\": [\r\n        \"soldier\",\r\n        \"horseArcher\"\r\n    ],\r\n    \"Celtic Briton\": [\r\n        \"soldier\",\r\n        \"horseArcher\"\r\n    ],\r\n    \"Spain\": [\r\n        \"soldier\",\r\n        \"horseArcher\"\r\n    ],\r\n    \"Numidia\": [\r\n        \"soldier\",\r\n        \"horseArcher\"\r\n    ],\r\n    \"Egpyt\": [\r\n        \"soldier\",\r\n        \"horseArcher\"\r\n    ],\r\n    \"Seleucid\": [\r\n        \"soldier\",\r\n        \"horseArcher\"\r\n    ],\r\n    \"Pontus\": [\r\n        \"soldier\",\r\n        \"horseArcher\"\r\n    ],\r\n    \"Armenia\": [\r\n        \"soldier\",\r\n        \"horseArcher\"\r\n    ],\r\n    \"Parthian\": [\r\n        \"soldier\",\r\n        \"horseArcher\"\r\n    ],\r\n    \"Germanic\": [\r\n        \"soldier\",\r\n        \"horseArcher\"\r\n    ],\r\n    \"Greece\": [\r\n        \"soldier\",\r\n        \"horseArcher\"\r\n    ],\r\n    \"Macedonia\": [\r\n        \"soldier\",\r\n        \"horseArcher\"\r\n    ],\r\n    \"Trachia\": [\r\n        \"soldier\",\r\n        \"horseArcher\"\r\n    ],\r\n    \"Dacia\": [\r\n        \"soldier\",\r\n        \"horseArcher\"\r\n    ]\r\n}";
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
