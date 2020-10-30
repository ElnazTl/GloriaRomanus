package unsw.gloriaromanus.Backend;

import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.List;
import java.util.ArrayList;

public class Faction {

<<<<<<< HEAD
    String name;
    List <Province> provinces;
    List <String> availableUnits;

    public Faction(String name) throws IOException {
        this.name = name;
        loadUnitsFromConfig();
    }


    public String getName() {
        return name;
    }


    public List<Province> getProvinces() {
        return provinces;
    }


    public List<String> getAvailableUnits() {
        return availableUnits;
=======
    public String name;
    private Army army;
    // ArrayList <Province> provinceList;
    public Faction(String name) {
        this.name = name;
        army = new Army();
>>>>>>> 4adaecd027a12cc72fc14b0f7a9d5b292a433e28
    }
    

    public void newTurn() {
        for (Province p : provinces) {
            p.newTurn();
        }
    }



    /**
     * Loads the units availble to the faction
     * 
     * @throws IOException
     */
    private void loadUnitsFromConfig() throws IOException {
        String configString = Files.readString(Paths.get("src/unsw/gloriaromanus/Backend/configs/faction_units_config.json"));
        JSONObject unitsConfig = new JSONObject(configString);
        JSONArray config = unitsConfig.getJSONArray(this.name);
        availableUnits = ArrayUtil.convert(config);
    }
}
