package unsw.gloriaromanus.Backend;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONPropertyIgnore;
//import org.junit.Ignore;


public class Unit {
    private static Long ID = 0L;
    
    private String name;
    private Long unitID;
    private String type;
    private boolean melee;
    private int numTroops;
    private int cost;
    private int trainTime;
    private double attack;
    private double speed;
    private int movePoints;
    private double armour;
    private double morale;
    private double shield;
    private double defence;  // Melee units only
    private double charge;   // Cavalry units only
    private String abilityType;
    @JsonIgnoreProperties
    private JSONArray ability;
    
    @JsonIgnoreProperties private JSONArray modifiers;
    @JsonIgnoreProperties private JSONObject baseValues;


    public Unit() {}

    public Unit(String name) throws IOException {
        this.name = name;
        this.unitID = ID;
        ID = ID + 1;
        loadFromConfig(name);
    }

    public Unit(String name, JSONObject unitConfig) throws IOException {
        this.name = name;
        this.unitID = ID;
        ID = ID + 1;
        loadFromConfig(name, unitConfig);
    }


    public String getName() {
        return name;
    }

    public Long getUnitID() {
        return unitID;
    }


    public String getType() {
        return type;
    }


    public int getNumTroops() {
        return numTroops;
    }


    public int getCost() {
        return cost;
    }


    public int getTrainTime() {
        return trainTime;
    }


    public double getAttack() {
        return attack;
    }


    public double getSpeed() {
        return speed;
    }


    public int getMovePoints() {
        return movePoints;
    }


    public double getMorale() {
        return morale;
    }


    public double getArmour() {
        return armour;
    }


    public double getShield() {
        return shield;
    }


    public double getDefence() {
        return defence;
    }


    public double getCharge() {
        return charge;
    }


    public String getAbilityType() {
        return abilityType;
    }

    @JsonIgnore
    public JSONArray getAbility() {
        return ability;
    }

    @JsonIgnore
    public JSONArray getModifiers() {
        return modifiers;
    }
    
    @JsonIgnore
    public boolean isMelee() {
        return melee;
    }

    @JsonIgnore
    public boolean isRanged() {
        return !isMelee();
    }
    @JsonIgnore
    public boolean isTrained() {
        return trainTime == 0;
    }


    /**
     * Inflicts given number of casualties on unit,
     * minimum of 0 troops remaining
     * 
     * @param num Number of troops to 'kill'
     */
    public void inflictCasualties(int num) {
        if (numTroops - num < 0) {
            numTroops = 0;
        } else {
            numTroops = numTroops - num;
        }
    }


    /**
     * Returns True if unit has troops remaining,
     * otherwise False
     * 
     * @return True if unit is alive, otherwise False
     */
    @JsonIgnore
    public boolean isAlive() {
        return numTroops != 0;
    }


    /**
     * Called at start of a new turn
     * Changes anything that needs to be changed at start of a turn
     */
    public void endTurn() {
        if (trainTime != 0) {
            trainTime = trainTime - 1;
        }
    }

    
    /**
     * Adds a new modifier to Unit
     * 
     * @param modifier New modifier object
     * @param who Apply to friendly of enemy unit
     */
    public void addModifier(JSONObject modifier) {
        modifiers.put(modifier);
    }


    /**
     * Removes given modifier from modifiers,
     * if that modifier is in the JSONArray
     * 
     * @param modifier Modifier to remove
     */
    public void removeModifier(JSONObject modifier) {
        if (!modifiers.isEmpty()) {
            for (int i = 0; i < modifiers.length(); i++) {
                JSONObject mod = modifiers.getJSONObject(i);
                if (mod.equals(modifier)) {
                    modifiers.remove(i);
                }
            }
        }
    }



    /**
     * Returns the value of specified attribute after applying
     * the friendly modifiers to it
     * 
     * @param type Value to modifiy
     * @param who Friendly or enemy modifiers
     * @return Modified value
     */
    public double getFriendlyModifiedValue(String type) {
        Iterator<Object> json = modifiers.iterator();
        double val = baseValues.optDouble(type, 0);
        while (json.hasNext()) {
            JSONObject mod = (JSONObject)json.next();
            if (type.equals(mod.getString(type)) && "friendly".equals(mod.getString("who"))) {
                if ("add".equals(mod.getString("strategy"))) {
                    val = val + mod.optDouble("value", 0);
                } else if ("multiply".equals(mod.getString("strategy"))) {
                    val = val * mod.optDouble("value", 1);
                }
            }
        }
        return val;
    }


    /**
     * Returns the value of specified attribute after applying
     * the enemy modifiers to it
     * 
     * @param type Value to modifiy
     * @return Modified value
     */
    public double getEnemyModifiedValue(String type) {
        Iterator<Object> json = modifiers.iterator();
        double val = baseValues.optDouble(type, 0);
        while (json.hasNext()) {
            JSONObject mod = (JSONObject)json.next();
            if (type.equals(mod.getString(type)) && "enemy".equals(mod.getString("who"))) {
                if ("add".equals(mod.getString("strategy"))) {
                    val = val + mod.optDouble("value", 0);
                } else if ("multiply".equals(mod.getString("strategy"))) {
                    val = val * mod.optDouble("value", 1);
                }
            }
        }
        return val;
    }


    /**
     * Loads base config values for specified unit
     * from the configs/unit_config.json file
     * 
     * @param name
     * @throws IOException
     */
    private void loadFromConfig(String name) throws IOException {
        // String defaultString = "{\r\n    \"soldier\": {\r\n        \"type\" : \"infantry\",\r\n        \"attackType\" : \"melee\",\r\n        \"numTroops\" : 10,\r\n        \"cost\" : 5,\r\n        \"trainTime\" : 1,\r\n        \"attack\" : 4,\r\n        \"speed\" : 4,\r\n        \"morale\" : 5,\r\n        \"armour\" : 3,\r\n        \"shield\" : 5,\r\n        \"defence\" : 6,\r\n        \"ability\" : \"noAbility\"\r\n\r\n    },\r\n    \"horseArcher\": {\r\n        \"type\" : \"cavalry\",\r\n        \"attackType\" : \"ranged\",\r\n        \"numTroops\" : 8,\r\n        \"cost\" : 5,\r\n        \"trainTime\" : 2,\r\n        \"attack\" : 6,\r\n        \"speed\" : 7,\r\n        \"morale\" : 4,\r\n        \"armour\" : 2,\r\n        \"shield\" : 2,\r\n        \"charge\" : 5,\r\n        \"ability\" : \"noAbility\"\r\n    },\r\n    \"hoplite\": {\r\n        \"type\" : \"infantry\",\r\n        \"attackType\" : \"melee\",\r\n        \"numTroops\" : 8,\r\n        \"cost\" : 5,\r\n        \"trainTime\" : 2,\r\n        \"attack\" : 8,\r\n        \"speed\" : 2,\r\n        \"morale\" : 4,\r\n        \"shield\" : 4,\r\n        \"defence\" : 6,\r\n        \"ability\" : \"phalanx\"\r\n    },\r\n    \"artillery\": {\r\n        \"type\" : \"artillery\",\r\n        \"attackType\" : \"ranged\",\r\n        \"numTroops\" : 3,\r\n        \"cost\" : 40,\r\n        \"trainTime\" : 3,\r\n        \"attack\" : 12,\r\n        \"speed\" : 1,\r\n        \"morale\" : 3,\r\n        \"shield\" : 6,\r\n        \"defence\" : 6,\r\n        \"ability\" : \"noAbility\"\r\n    }\r\n}\r\n";
        String defaultString = Files.readString(Paths.get("bin/unsw/gloriaromanus/Backend/configs/units_config.json"));
        JSONObject unitsConfig = new JSONObject(defaultString);
        loadFromConfig(name, unitsConfig);
    }


    
    /**
     * Loads the given config values for the specified unit
     * 
     * 
     * @param name Name of unit to train
     * @param unitsConfig JSONObject of config file
     * @throws IOException
     */
    private void loadFromConfig(String name, JSONObject unitsConfig) throws IOException {
        JSONObject config = unitsConfig.getJSONObject(this.name);
        
        this.type = config.optString("type", "infantry");
        this.melee = "melee".equals(config.optString("attackType", "melee")) ? true : false;
        this.numTroops = config.optInt("numTroops", 1);
        this.cost = config.optInt("cost", 1);
        this.trainTime = config.optInt("trainTime", 1);
        this.attack = config.optDouble("attack", 1);
        this.speed = config.optDouble("speed", 1);
        this.morale = config.optDouble("morale", 1);
        this.armour = config.optDouble("armour", 1);
        this.shield = config.optDouble("shield", 1);
        this.abilityType = config.optString("ability", "noAbility");
        this.ability = getAbilityJSON(this.abilityType);
        this.charge = "cavalry".equals(this.type) ? config.optDouble("charge", 1) : 0;
        this.defence = isMelee() ? config.optDouble("defence", 1) : 0;
        switch (this.type) {
            case "cavalry":
                this.movePoints= 15;
                break;
            case "infantry":
                this.movePoints = 10;
                break;
            case "artillery":
                this.movePoints = 4;
                break;
            default:
                this.movePoints = 1;
        }
        this.modifiers = new JSONArray();
        this.baseValues = config;
    }


    /**
     * Loads the modifiers JSONObject
     * for the specified ability
     * 
     * @param ability
     * @return JSONObject of specified ability
     * @throws IOException
     */
    private JSONArray getAbilityJSON(String ability) throws IOException {
        String configString = Files.readString(Paths.get("bin/unsw/gloriaromanus/Backend/configs/ability_config.json"));
        JSONObject abilityConfig = new JSONObject(configString);
        JSONArray config = abilityConfig.getJSONArray(ability);
        return config;
    }


    @Override
    public String toString() {
        return "unit (" + name + ", id: " + unitID + ")";
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        
        Unit u = (Unit)obj;
        return unitID == u.getUnitID();
    }

    public static Long getID() {
        return ID;
    }

    public static void setID(Long iD) {
        ID = iD;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUnitID(Long unitID) {
        this.unitID = unitID;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMelee(boolean melee) {
        this.melee = melee;
    }

    public void setNumTroops(int numTroops) {
        this.numTroops = numTroops;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setTrainTime(int trainTime) {
        this.trainTime = trainTime;
    }

    public void setAttack(double attack) {
        this.attack = attack;
    }

    public void setMovePoints(int movePoints) {
        this.movePoints = movePoints;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setMorale(double morale) {
        this.morale = morale;
    }

    public void setShield(double shield) {
        this.shield = shield;
    }

    public void setDefence(double defence) {
        this.defence = defence;
    }

    public void setCharge(double charge) {
        this.charge = charge;
    }
    // @Ignore
    public void setAbilityType(String abilityType) {
        this.abilityType = abilityType;
    }

    // public void setAbility(JSONArray ability) {
    //     this.ability = ability;
    // }
    // @Ignore
    // public void setModifiers(JSONArray modifiers) {
    //     this.modifiers = modifiers;
    // }

    // public JSONObject getBaseValues() {
    //     return baseValues;
    // }

    // public void setBaseValues(JSONObject baseValues) {
    //     this.baseValues = baseValues;
    // }

    
}
