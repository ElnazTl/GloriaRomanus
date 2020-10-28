package unsw.gloriaromanus.Backend;

import java.io.IOException;

import org.json.JSONObject;


public class Unit {
    
    private String name;
    private String type;
    private boolean melee;
    private int numTroops;
    private int cost;
    private int trainTime;
    private double attack;
    private double speed;
    private double morale;
    private double shield;
    private double defence;  // Melee units only
    private double charge;   // Cavalry units only
    private String ability;
    private JSONObject modifiers;

    
    public Unit(String name, JSONObject unitConfig, JSONObject abilityConfig) {
        this.name = name;
        loadUnitFromConfig(name, unitConfig, abilityConfig);
    }


    public String getName() {
        return name;
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


    public double getMorale() {
        return morale;
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


    public String getAbility() {
        return ability;
    }


    public JSONObject getModifiers() {
        return modifiers;
    }
    

    public boolean isMelee() {
        return melee;
    }


    public boolean isRanged() {
        return !isMelee();
    }

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
    public boolean isAlive() {
        return numTroops != 0;
    }


    /**
     * Called at start of a new turn
     * Changes anything that needs to be changed at start of a turn
     */
    public void newTurn() {
        if (trainTime != 0) {
            trainTime = trainTime - 1;
        }
    }

    
    /**
     * Loads the base config values for the specified unit
     * from the configs/unit_config.json file
     * 
     * @param name of unit to train
     * @throws IOException
     */
    private void loadUnitFromConfig(String name, JSONObject unitsConfig, JSONObject abilityConfig) {
        JSONObject config = unitsConfig.getJSONObject(this.name);
        
        this.type = config.optString("type", "infantry");
        this.melee = "melee".equals(config.optString("attackType", "melee")) ? true : false;
        this.numTroops = config.optInt("numTroops", 1);
        this.cost = config.optInt("cost", 1);
        this.trainTime = config.optInt("trainTime", 1);
        this.attack = config.optDouble("attack", 1);
        this.morale = config.optDouble("morale", 1);
        this.shield = config.optDouble("shield", 1);
        this.ability = config.optString("ability", "noAbility");
        this.modifiers = getAbilityJSON(this.ability, abilityConfig);
        this.charge = "cavalry".equals(this.type) ? config.optDouble("charge", 1) : 0;
        this.defence = isMelee() ? config.optDouble("defence", 1) : 0;
        switch (this.type) {
            case "cavalry":
                this.speed = 15;
                break;
            case "infantry":
                this.speed = 10;
                break;
            case "artillery":
                this.speed = 4;
                break;
            default:
                this.speed = 1;
        }
    }


    /**
     * Loads the modifiers JSONObject
     * for the specified ability
     * 
     * @param ability
     * @return JSONObject of specified ability
     * @throws IOException
     */
    private JSONObject getAbilityJSON(String ability, JSONObject abilityConfig) {
        JSONObject config = abilityConfig.getJSONObject(this.ability);
        return config;
    }


    @Override
    public String toString() {
        return name + " unit (" + numTroops + ", " + ability + ")";
    }
}
