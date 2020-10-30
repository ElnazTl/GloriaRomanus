package unsw.gloriaromanus.Backend;

import java.util.List;
import java.util.ArrayList;
import org.json.JSONObject;

import unsw.gloriaromanus.Backend.tax.*;

public class Province {
    
    String faction;
    String name;
    int wealth;
    List<Unit> units;
    List<Unit> unitsTraining;
    TaxRate taxRate;
    Database database;



    public Province() {}
    public Province(String name, Database database) {
        this.database = database;
        this.name = name;
        this.units = getUnit();
        this.unitsTraining = getTraining();
        changeTaxRate(LowTax.TYPE);
        this.faction = setFaction();

    }


    /**
     * Called at start of a new turn
     * Changes anything that needs to be changed at start of a turn
     */
    private String setFaction() {
        // System.out.println(database.getFactionProvince().get(name));
        return database.getFactionProvince().get(name).name;
    }
    public void newTurn() {
        for (Unit u : this.units) {
            u.newTurn();
        }
        ArrayList<Integer> indexs = new ArrayList<Integer> ();
        for (int i = 0; i < unitsTraining.size() ;i++) {
            Unit u = unitsTraining.get(i);
            u.newTurn();
            if (u.isTrained()) {
                indexs.add(i);
                units.add(u);
            }
        }
        for (int i: indexs) {
            unitsTraining.remove(i);
        }
        
        
        wealth = wealth + taxRate.getWealth();

    }
    public void setDatabase(Database d) {
        this.database = d;
    }

    private List<Unit> getUnit() {
        return database.getProvinceUnit().get(name);
    }
    private List<Unit> getTraining() {
        return database.getProvinceTraining().get(name);

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

    public void removeUnit(Unit u) {
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

    /**
     * given the game database adds the unit to the list of units present in the
     * province
     * 
     * @param d
     * @param u
     */


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
     * moves troops between provinces, will fail if can't move the troop
     * 
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

    public String getFaction() {
        return faction;
    }


    public String getName() {
        return name;
    }

   

    public int getWealth() {
        return wealth;
    }

   
    // public TaxRate getTaxRate() {
    //     return taxRate;
    // }

    

    public List<Unit> getUnitsTraining() {
        return unitsTraining;
    }

    public void setFaction(String faction) {
        this.faction = faction;
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

    // public void setTaxRate(TaxRate taxRate) {
    //     this.taxRate = taxRate;
    // }

    public void setUnitsTraining(List<Unit> unitsTraining) {
        this.unitsTraining = unitsTraining;
    }

  

    // public Province desirilise(JSONObject jo) {
    //     jo.getString("wealth")
    // }

    


  

       
    // }
    


}
