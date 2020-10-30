package unsw.gloriaromanus.Backend;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

    @JsonIgnoreProperties
    public Unit(){}
    public Unit(String name, JSONObject unitConfig, JSONObject abilityConfig) throws IOException {
        this.name = name;
        loadUnitFromConfig(name, unitConfig, abilityConfig);
    }

    public void endTurn() {
        trainTime = trainTime-1;
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
    private void loadUnitFromConfig(String name, JSONObject unitsConfig, JSONObject abilityConfig) throws IOException {
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
    private JSONObject getAbilityJSON(String ability, JSONObject abilityConfig) throws IOException {
        JSONObject config = abilityConfig.getJSONObject(this.ability);
        return config;
    }


    @Override
    public String toString() {
        return "Unit: " + this.name + " (" + this.type + ") {\n\tmelee: " 
                + isMelee() + "\n\tnumTroops: " + this.numTroops + "\n\tcost: "
                + this.cost + "\n\ttime to train: " + this.trainTime
                + "\n\tability: " + this.ability + " }";
    }

    public void setName(String name) {
        this.name = name;
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

    public void setAbility(String ability) {
        this.ability = ability;
    }

    public void setModifiers(JSONObject modifiers) {
        this.modifiers = modifiers;
    }
    
}
