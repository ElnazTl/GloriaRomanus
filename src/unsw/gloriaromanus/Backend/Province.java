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

    /**
     * checks if tow provinces are adjacent for invasion and chooses random units to battle
     * @param enemy
     * @param d
     * @return
     * @throws IOException
     */
    public String battle(Province enemy, Database d) throws IOException {

         // TODO: implement battleresolve to return the result of the battle
        
        if(!confirmIfProvincesConnected(name, enemy.name)) return ("Provinces not adjacent, cannot invade!");
        unitList = d.getProvinceUnit().get(this.name);

        Unit humanUnit = chooseUnit(unitList);
        Unit EnemyUnit = chooseUnit(d.getProvinceUnit().get(enemy.name));


        return "true";

    }

    /**
     * Function will check if two provinces are adjacent, implementation taken from controller
     * @param province1
     * @param province2
     * @return
     * @throws IOException
     */

    private boolean confirmIfProvincesConnected(String province1, String province2) throws IOException {
        String content = Files
            .readString(Paths.get("src/unsw/gloriaromanus/province_adjacency_matrix_fully_connected.json"));
        JSONObject provinceAdjacencyMatrix = new JSONObject(content);
        return provinceAdjacencyMatrix.getJSONObject(province1).getBoolean(province2);
    }
   
    /**
     * randomly chooses a unit in the province for battle
     * @param u
     */

    public Unit chooseUnit(List<Unit> u) {

        Random r = new Random();
        return u.get(r.nextInt(u.size()));
        
    }

    /**
     * given the game database adds the unit to the list of units present in the province
     * @param d
     * @param u
     */
    public String addUnit(Database d, Unit u) {

        // TODO: complete the operation of buying/trining and troop availibility
        d.getProvinceUnit().get(this).add(u);

        return "Successfully added the unit";
    }

    /**
     * moves troops between provinces, will fail if can't move the troop
     * @param to
     * @param u
     */
    

    public String moveTroopTo(Province to, Unit u) throws IOException {

        
            // TODO: find the shortes path + movement points to move to for DN


            // units can only move between adjacent provinces for pass mark need to update
            if(!confirmIfProvincesConnected(to.name, this.name)) return "Provinces are not adjacent";
            database.getProvinceUnit().get(this).remove(u);
            database.getProvinceUnit().get(to).remove(u);

            return "Successfully moved the unit";


       
    }
    

}
