package unsw.gloriaromanus.Backend;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;



public class Unit {
    
    private String name;
    private String type;
    private boolean melee;
    private int numTroops;
    private int cost;
    private int trainTime;
    private float attack;
    private float speed;
    private float morale;
    private float shield;
    private float defence;  // Melee units only
    private float charge;   // Cavalry units only
    private String ability;
    private JSONObject modifiers;

    
    public Unit(String name) throws IOException {
        this.name = name;
        loadUnitFromConfig(name);
    }


    public boolean isMelee() {
        return melee;
    }


    public boolean isRanged() {
        return !isMelee();
    }


    private void loadUnitFromConfig(String name) throws IOException {
        String configString = Files.readString(Paths.get("src/unsw/gloriaromanus/Backend/configs/units_config.json"));
        JSONObject unitsConfig = new JSONObject(configString);
        JSONObject config = new JSONObject(unitsConfig.getString(this.name));

        this.type = config.optString("type", "infantry");
        this.melee = "melee".equals(config.optString("attackType", "melee")) ? true : false;
        this.numTroops = config.optInt("numTroops", 1);
        this.cost = config.optInt("cost", 1);
        this.trainTime = config.optInt("trainTime", 1);
        this.attack = config.optFloat("attack", 1);
        this.morale = config.optFloat("morale", 1);
        this.shield = config.optFloat("shield", 1);
        this.ability = config.optString("ability", "noAbility");
        this.modifiers = getAbilityJSON(this.ability);
        this.charge = "cavalry".equals(this.type) ? config.optFloat("charge", 1) : 0;
        this.defence = isMelee() ? config.optFloat("defence", 1) : 0;
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

    private JSONObject getAbilityJSON(String ability) throws IOException {
        String abilityString = Files.readString(Paths.get("src/unsw/gloriaromanus/Backend/configs/ability_config.json"));
        JSONObject abilityConfig = new JSONObject(abilityString);
        JSONObject config = new JSONObject(abilityConfig.getString(this.name));
        return config;
    }


    @Override
    public String toString() {
        return "Unit: " + this.name + "(" + this.type + ") {\n\tmelee: " 
                + isMelee() + "\n\tnum: " + this.numTroops + "\n\tcost: "
                + this.cost + "\n\ttrain time: " + this.trainTime
                + "\n\tability: " + this.ability + "}";
    }
}
