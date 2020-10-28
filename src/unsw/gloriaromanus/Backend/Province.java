package unsw.gloriaromanus.Backend;

import org.json.*;

import javafx.scene.control.ListCell;
import unsw.gloriaromanus.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.io.IOException;

public class Province {

    public String name;
    private Faction faction;
    private ArrayList<Unit> unitList;
    private Database database;

    public Province(String name, Database database) {
        this.name = name;
        this.database = database;
        setFaciton();
        database.getProvinceUnit();
        setUnit();

    }

    private void setUnit() {
        
        unitList = database.provinceUnit.get(name);
        System.out.println(unitList);
    }

    private void setFaciton() {
        faction = database.getFactionProvince().get(name);

    }

    public ArrayList<Unit> getUnits() {
        return unitList;
    }

    /**
     * checks if tow provinces are adjacent for invasion and chooses random units to
     * battle
     * 
     * @param enemy
     * @param d
     * @return
     * @throws IOException
     */
    public String battle(Province enemy, Database d) throws IOException {

        if (!confirmIfProvincesConnected(name, enemy.name))
            return ("Provinces not adjacent, cannot invade!");
        unitList = d.getProvinceUnit().get(this.name);

        // TODO: return the result of Battle resolver
        Battleresolver br = new Battleresolver(this, enemy, database);

        return "true";

    }

    /**
     * Function will check if two provinces are adjacent, implementation taken from
     * controller
     * 
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
     * given the game database adds the unit to the list of units present in the
     * province
     * 
     * @param d
     * @param u
     */
    public String addUnit(Database d, Unit u) {

        // TODO: complete the operation of buying/trining and troop availibility
        d.getProvinceUnit().get(name).add(u);
        setUnit();

        return "Successfully added the unit";
    }

    /**
     * moves troops between provinces, will fail if can't move the troop
     * 
     * @param to
     * @param u
     */

    public String moveTroopTo(Province to, Unit u) throws IOException {

        // TODO: find the shortes path + movement points to move to for DN

        // units can only move between adjacent provinces for pass mark need to update
        if (!confirmIfProvincesConnected(to.name, this.name))
            return "Provinces are not adjacent";
        database.getProvinceUnit().get(this).remove(u);
        database.getProvinceUnit().get(to).remove(u);

        return "Successfully moved the unit";

    }
    /**
     * function return a list of the name of the units 
     * @return
     */

    public ArrayList<String> ListOfUnitString() {
        ArrayList<String> result = new ArrayList<String> ();
        for (Unit u: getUnits()) {
            result.add(u.getName());
        }
        return result;
    }
    


}
