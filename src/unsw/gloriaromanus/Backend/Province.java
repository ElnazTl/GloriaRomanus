package unsw.gloriaromanus.Backend;


import org.apache.commons.codec.net.QCodec;
import org.json.*;

import javafx.scene.control.ListCell;
import unsw.gloriaromanus.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.io.IOException;
import java.util.List;


import unsw.gloriaromanus.Backend.tax.*;

public class Province {
    
    Database database;
    String faction;
    String name;
    int wealth;
    List<Unit> units;
    TaxFactory taxFactory;
    TaxRate taxRate;
    List<Unit> unitsTraining;


    public Province() {}
    public Province(String name, Database database) {
        this.name = name;
        this.database = database;
        setFaciton();
        units = setUnit();
        unitsTraining = setTraining();

    }

    public void setDatabase(Database d){
        this.database = d;
    }
    

    private List<Unit> setUnit() {
        
        return database.getProvinceUnit().get(name);
    }
    private List<Unit> setTraining() {
        return database.getProvinceTraining().get(name);
    }

    private void setFaciton() {
        faction = database.getFactionProvince().get(name).name;

    }

    public List<Unit> getUnits() {
        return units;
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
    public String invade(Province enemy, Database d) throws IOException {

        if (!confirmIfProvincesConnected(name, enemy.name))
            return ("Provinces not adjacent, cannot invade!");

        // TODO: return the result of Battle resolver
        Battleresolver br = new Battleresolver(this, enemy, database);
        return ("true");
    }


    /**
     * Removes specified unit and returns it
     * 
     * @param name
     * @return
     */
    public Unit popUnit(Unit u) {
        if (u != null) {
            units.remove(u);
            return u;
        }
        return null;
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

    // private void setWealth(int cost) {
    //     this.wealth = wealth-cost;
    // }

    private void setTraining(Unit u) {
        unitsTraining.add(u);
    }
    
    /**
     * if enough wealth would get the unit and add it to list of trining units
     * @param u
     * @return
     */

    public String getUnit(Unit u) {

        /**
         * gold implementation not required for pass
         * if (wealth >= u.getCost()) {

            database.getProvinceTraining().get(name).add(u);
            setWealth(u.getCost());
            setTraining(u);
            return "Successfully added the unit";
        }
        **/

        if (unitsTraining.size() >=2 ) {
            System.out.println("Already training two troops");
        }
        setTraining(u);
        return "unit started traiing ";

        
    }
    /**
     * will add the unit to unitlist when training is over
     * @param u
     */
    private void addUnit(Unit u) {
        database.getProvinceUnit().get(name).add(u);
        units.add(u);

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

    public String getFaction() {
        return faction;
    }


    public String getName() {
        return name;
    }

   

    public int getWealth() {
        return wealth;
    }

   

    public TaxFactory getTaxFactory() {
        return taxFactory;
    }

   
    public TaxRate getTaxRate() {
        return taxRate;
    }

    

    public List<Unit> getUnitsTraining() {
        return unitsTraining;
    }

    public void setFaction(Faction faction) {
        this.faction = faction.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWealth(int wealth) {
        this.wealth = wealth;
    }

    public void setUnits(List<Unit> units) {
        this.units = units;
    }

    public void setTaxFactory(TaxFactory taxFactory) {
        this.taxFactory = taxFactory;
    }

    public void setTaxRate(TaxRate taxRate) {
        this.taxRate = taxRate;
    }

    public void setUnitsTraining(List<Unit> unitsTraining) {
        this.unitsTraining = unitsTraining;
    }

  

    // public Province desirilise(JSONObject jo) {
    //     jo.getString("wealth")
    // }

    


  

       
    // }
    


}
