package unsw.gloriaromanus.Backend;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONObject;

import unsw.gloriaromanus.Backend.tax.*;

public class Province {
    
    String name;
    int wealth;
    List<Unit> units;
    List<Unit> unitsTraining;
    TaxRate taxRate;


    public Province(String name) {
        this.name = name;
        this.units = new ArrayList<Unit>();
        this.unitsTraining = new ArrayList<Unit>(2);
        changeTaxRate(LowTax.TYPE);
    }

    public String getName() {
        return name;
    }

    public int getWealth() {
        return wealth;
    }

    public List<Unit> getUnits() {
        return units;
    }

    public List<Unit> getUnitsTraining() {
        return unitsTraining;
    }

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
    }

    
    /**
     * Returns the first unit with specified name
     *
     * @param name 
     * @return
     */
    public Unit findUnit(Long id) {
        for (Unit u : units) {
            if (id == u.getUnitID()) {
                return u;
            }
        }
        return null;
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
     * Changes the tax rate of the province
     * 
     * @param tax Name of Tax Rate
     */
    public void changeTaxRate(String tax) {
        //removeTaxMorale();
        taxRate = TaxFactory.newTaxRate(tax);
        //applyTaxMorale();
    }


    // TODO 
    // public void applyTaxMorale() {
    //     // Change morale of units
    //     // Change morale of training units?
    // }

    //TODO
    // public void removeTaxMorale() {
    //     // Remove morale effect of units/training units
    // }



    /**
     * Attempts to train a unit
     * Returns true if training,
     * otherwise returns false
     * 
     * @param name Name of unit to train
     * @return True if training unit, otherwise False
     */
    public boolean trainUnit(String name, JSONObject unitConfig) throws IOException {
        if (unitsTraining.size() == 2) return false;
        else {
            Unit u = new Unit(name, unitConfig);
            unitsTraining.add(u);
            // u.applyModifier(taxRate.getMoraleModifier());

            return true;
        }
    }


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

        
            
    //         // move from the shortest path
    //         // movement point
    //         database.getProvinceUnit().get(this).remove(u);
    //         database.getProvinceUnit().get(to).remove(u);

    //         return "Successfully moved the unit";


       
    // }
    

}
