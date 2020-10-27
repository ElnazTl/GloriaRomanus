package unsw.gloriaromanus.Backend;

import org.json.*;
import unsw.gloriaromanus.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class Province {
    
    String name;
    Faction faction;
    ArrayList<Unit> unitList;
    Database database;

    public Province(String name, Database database) {
        this.name = name;
        this.database = database;
        setFaciton();
        
    }

    private void setFaciton() {
        faction = database.getFactionProvince().get(this);
    }

    // choose a random unit to invade
    public String battle(Province enemy, Database d) throws IOException {
        
        if(!confirmIfProvincesConnected(name, enemy.name)) return ("Provinces not adjacent, cannot invade!");
        unitList = d.getProvinceUnit().get(this.name);

        Unit humanUnit = chooseUnit(unitList);
        Unit EnemyUnit = chooseUnit(d.getProvinceUnit().get(enemy.name));


        return "true";

    }

    private boolean confirmIfProvincesConnected(String province1, String province2) throws IOException {
        String content = Files
            .readString(Paths.get("src/unsw/gloriaromanus/province_adjacency_matrix_fully_connected.json"));
        JSONObject provinceAdjacencyMatrix = new JSONObject(content);
        return provinceAdjacencyMatrix.getJSONObject(province1).getBoolean(province2);
    }
   

    public Unit chooseUnit(List<Unit> u) {

        Random r = new Random();
        return u.get(r.nextInt(u.size()));
        
    }

    /**
     * given the game database adds the unit to the list of units present in the province
     * @param d
     * @param u
     */
    public void addUnit(Database d, Unit u) {
        d.getProvinceUnit().get(this).add(u);
    }

    public String moveTroopTo(Province to, Unit u) {

        
            
            // move from the shortest path
            // movement point
            database.getProvinceUnit().get(this).remove(u);
            database.getProvinceUnit().get(to).remove(u);

            return "Successfully moved the unit";


       
    }
    

}