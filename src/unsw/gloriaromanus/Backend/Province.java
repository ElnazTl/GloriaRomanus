package unsw.gloriaromanus.Backend;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import unsw.gloriaromanus.Backend.tax.*;

public class Province {
    
    String name;
    int wealth;
    List<Unit> units;
    TaxFactory taxFactory;
    TaxRate taxRate;
    List<Unit> unitsTraining;

    public Province(String name) {
        this.name = name;
        this.units = new ArrayList<Unit>();
        this.unitsTraining = new ArrayList<Unit>(2);
        this.taxFactory = new TaxFactory();
        changeTaxRate("low");
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
        }
        //wealth += taxRate.getWealth();
    }

    
    /**
     * Returns the first unit with specified name
     *
     * @param name 
     * @return
     */
    public Unit findUnit(String name) {
        for (Unit u : units) {
            if (name.equals(u.getName())) {
                return u;
            }
        }
        return null;
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


    public void changeTaxRate(String name) {
        taxRate = taxFactory.newTaxRate(name);
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


    /**
     * Attempts to train a unit
     * Returns true if training,
     * otherwise returns false
     * 
     * @param name Name of unit to train
     * @return True if training unit, otherwise False
     * @throws IOException
     */
    public boolean trainUnit(String name) throws IOException {
        if (unitsTraining.size() == 2) return false;
        else {
            unitsTraining.add(new Unit(name));
            return true;
        }
    }


    // public String moveTroopTo(Province to, Unit u) {

        
            
    //         // move from the shortest path
    //         // movement point
    //         database.getProvinceUnit().get(this).remove(u);
    //         database.getProvinceUnit().get(to).remove(u);

    //         return "Successfully moved the unit";


       
    // }
    

}
