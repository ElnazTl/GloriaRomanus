package unsw.gloriaromanus.Backend;

<<<<<<< HEAD
import java.util.List;
import java.util.ArrayList;
import org.json.JSONObject;
=======

import org.apache.commons.codec.net.QCodec;
import org.json.*;

import javafx.scene.control.ListCell;
import unsw.gloriaromanus.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.io.IOException;
import java.util.List;

>>>>>>> 4adaecd027a12cc72fc14b0f7a9d5b292a433e28

import unsw.gloriaromanus.Backend.tax.*;

public class Province {
    
    Database database;
    String faction;
    String name;
    int wealth;
    List<Unit> units;
    List<Unit> unitsTraining;
    TaxRate taxRate;



    public Province() {}
    public Province(String name, Database database) {
        this.name = name;
<<<<<<< HEAD
        this.units = new ArrayList<Unit>();
        this.unitsTraining = new ArrayList<Unit>(2);
        changeTaxRate(LowTax.TYPE);
    }
=======
        this.database = database;
        setFaciton();
        units = setUnit();
        unitsTraining = setTraining();
>>>>>>> 4adaecd027a12cc72fc14b0f7a9d5b292a433e28

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

<<<<<<< HEAD
    public TaxRate getTaxRate() {
        return taxRate;
    }

    /**
     * Called at start of a new turn
     * Changes anything that needs to be changed at start of a turn
     */
    public void newTurn() {
        for (Unit u : this.units) {
            u.newTurn();
        }
        for (Unit u : this.unitsTraining) {
            u.newTurn();
            if (u.isTrained()) {
                unitsTraining.remove(u);
                units.add(u);
            }
        }
        wealth = wealth + taxRate.getWealth();
=======
    public List<Unit> getUnits() {
        return units;
>>>>>>> 4adaecd027a12cc72fc14b0f7a9d5b292a433e28
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
<<<<<<< HEAD
    public Unit findUnit(Long id) {
        for (Unit u : units) {
            if (id == u.getUnitID()) {
                return u;
            }
        }
        return null;
=======
    public String invade(Province enemy, Database d) throws IOException {

        if (!confirmIfProvincesConnected(name, enemy.name))
            return ("Provinces not adjacent, cannot invade!");

        // TODO: return the result of Battle resolver
        Battleresolver br = new Battleresolver(this, enemy, database);
        return ("true");
>>>>>>> 4adaecd027a12cc72fc14b0f7a9d5b292a433e28
    }


    /**
     * Removes specified unit 
     * 
     * @param name
     */
    public void removeUnit(Long id) {
        Unit u = findUnit(id);
        if (u != null) {
            units.remove(u);
        }
    }


    /**
<<<<<<< HEAD
     * Changes the tax rate of the province
     * 
     * @param tax Name of Tax Rate
     */
    public void changeTaxRate(String tax) {
        //removeTaxMorale();
        taxRate = TaxFactory.newTaxRate(tax);
        //applyTaxMorale();
=======
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
>>>>>>> 4adaecd027a12cc72fc14b0f7a9d5b292a433e28
    }

    /**
     * given the game database adds the unit to the list of units present in the
     * province
     * 
     * @param d
     * @param u
     */

<<<<<<< HEAD
    // TODO 
    // public void applyTaxMorale() {
    //     // Change morale of units
    //     // Change morale of training units?
    // }

    //TODO
    // public void removeTaxMorale() {
    //     // Remove morale effect of units/training units
    // }
=======
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
>>>>>>> 4adaecd027a12cc72fc14b0f7a9d5b292a433e28

    }


    /**
     * moves troops between provinces, will fail if can't move the troop
     * 
<<<<<<< HEAD
     * @param name Name of unit to train
     * @return True if training unit, otherwise False
     */
    public boolean trainUnit(String name, JSONObject unitConfig, JSONObject abilityConfig) {
        if (unitsTraining.size() == 2) return false;
        else {
            Unit u = new Unit(name, unitConfig, abilityConfig);
            unitsTraining.add(u);
            // u.applyModifier(taxRate.getMoraleModifier());

            return true;
=======
     * @param to
     * @param u
     */

    public String moveTroopTo(Province to, String u) throws IOException {

        // TODO: find the shortes path + movement points to move to for DN

        // units can only move between adjacent provinces for pass mark need to update
        if (!to.getFaction().equals(faction)) return "can't move the unit to enemy territoy";

        if (!confirmIfProvincesConnected(to.name, this.name)) return "Provinces are not adjacent";
        for (int i = 0; i < units.size();i++) {
            if (units.get(i).getName().equals(u)) {

                to.addUnit(units.get(i));
                units.remove(i);
            }
>>>>>>> 4adaecd027a12cc72fc14b0f7a9d5b292a433e28
        }
           
      
        

        return "Successfully moved the unit";

    }
    /**
     * function return a list of the name of the units 
     * @return
     */


<<<<<<< HEAD
    /**
     * Resets the province's units and tax rate,
     * does not modify the wealth
     * 
     * @return Conquered Province
     */
    public Province conquerProvince() {
        units.removeAll(units);
        unitsTraining.removeAll(unitsTraining);
        changeTaxRate(LowTax.TYPE);
        return this;
    }


    // private boolean confirmIfProvincesConnected(String province1, String province2) throws IOException {
    //     String content = Files
    //         .readString(Paths.get("src/unsw/gloriaromanus/province_adjacency_matrix_fully_connected.json"));
    //     JSONObject provinceAdjacencyMatrix = new JSONObject(content);
    //     return provinceAdjacencyMatrix.getJSONObject(province1).getBoolean(province2);
    // }
   

    // public Unit chooseUnit(List<Unit> u) {

    //     Random r = new Random();
    //     return u.get(r.nextInt(u.size()));
        
    // }





    // public String moveTroopTo(Province to, Unit u) {
=======
    public ArrayList<String> ListOfUnitString() {
        ArrayList<String> result = new ArrayList<String> ();
        for (Unit u: getUnits()) {
            result.add(u.getName());
        }
        return result;
    }
>>>>>>> 4adaecd027a12cc72fc14b0f7a9d5b292a433e28

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
