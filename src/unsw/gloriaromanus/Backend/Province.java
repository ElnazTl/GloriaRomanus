package unsw.gloriaromanus.Backend;

import java.util.List;

import unsw.gloriaromanus.Backend.Database.JSONObjectSerialiser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.json.JSONObject;
import java.util.ArrayList;

import unsw.gloriaromanus.Backend.tax.*;

public class Province {
    
    String name;
    int wealth;
    List<Unit> units;
    List<Unit> unitsTraining;
    List<Unit> selectedUnits;
    String taxStrategy;

    @JsonIgnore
    TaxRate taxRate;

    // JSON configs used to train troops
    @JsonIgnore
    JSONObject defaultUnitsConfig;
    @JsonIgnore
    JSONObject abilityConfig;
    

    public Province() {}

    /**
     * Initialises a province using the base values
     * @param name
     * @param unitsConfig
     * @param abilityConfig
     */
    public Province(String name, JSONObject unitsConfig, JSONObject abilityConfig) {
        this.name = name;
        this.units = new ArrayList<Unit>();
        this.unitsTraining = new ArrayList<Unit>(2);
        this.selectedUnits = new ArrayList<Unit>();
        this.defaultUnitsConfig = unitsConfig;
        this.abilityConfig = abilityConfig;
        setTaxStrategy(LowTax.TYPE);
    }


    // /**
    //  * Initialises a province from a saved game
    //  * @param name
    //  * @param unitsConfig
    //  * @param abilityConfig
    //  * @param initialUnits
    //  * @param initialUnitsTaining
    //  */
    // public Province(String name, JSONObject unitsConfig, JSONObject abilityConfig, List<Unit> initialUnits, List<Unit> initialUnitsTaining) {
    //     this.name = name;
    //     this.units = initialUnits;
    //     this.unitsTraining = initialUnitsTaining;
    //     this.selectedUnits = new ArrayList<Unit>();
    //     this.defaultUnitsConfig = unitsConfig;
    //     this.abilityConfig = abilityConfig;
    //     changeTaxRate(LowTax.TYPE);
    // }

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

    @JsonIgnore
    public TaxRate getTax() {
        return taxRate;
    }


    /**
     * Returns the gold value taxed by the faction
     * @return
     */
    public double taxProvince() {
        return wealth * taxRate.getTaxRate();
    }

    /**
     * Called at the end of a turn,
     * updates the province and units
     */
    public void endTurn() {
        deselectAllUnits();
        for (Unit u : this.units) {
            u.endTurn();
        }
        List<Unit> completedUnits = new ArrayList<Unit>();
        for (Unit u : this.unitsTraining) {
            u.endTurn();
            if (u.isTrained()) {
                completedUnits.add(u);
            }
        }
        units.addAll(completedUnits);
        unitsTraining.removeAll(completedUnits);
        wealth += taxRate.getTaxWealth();
        // Apply tax modifier
        deselectAllUnits();
    }

    
    /**
     * Returns the unit with given id
     *
     * @param id 
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
     * Removes all units, only called by battleresolver
     * when this province is the defending province
     * 
     * @param name
     */
    public void removeAllUnits() {
        units.removeAll(units);
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
     * Adds unit with given id to selected units in province
     * 
     * @param id Id of unit to select
     */
    public void selectUnit(Long id) {
        Unit u = findUnit(id);
        if (u == null) return;
        if (selectedUnits.contains(u)) {
            // Remove unit from selection
            selectedUnits.remove(u);
            units.add(u);
        } else {
            // Add unit to selection
            selectedUnits.add(u);
            units.remove(u);
        }
    }


    /**
     * Removes all units from selection
     * 
     */
    public void deselectAllUnits() {
        for (Unit u : selectedUnits) {
            units.add(u);
            selectedUnits.remove(u);
        }
    }


    /**
     * Removes all selected units, only called by
     * battleresolver when this province is the
     * attacking province
     */
    public void removeAllSelected() {
        selectedUnits.removeAll(selectedUnits);
    }


    /**
     * Returns list of the selected units
     * @return
     */
    public List<Unit> getSelectedUnits() {
        return selectedUnits;
    }


    /**
     * Adds all units from a given list to the province
     * @param unitsList
     */
    public void addUnits(List<Unit> unitsList) {
        units.addAll(unitsList);
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
    public boolean trainUnit(String name) {
        if (unitsTraining.size() == 2) return false;
        else {
            Unit u = new Unit(name, defaultUnitsConfig, abilityConfig);
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
    public Province conquerProvince(List<Unit> newUnits) {
        units.removeAll(units);
        unitsTraining.removeAll(unitsTraining);
        changeTaxRate(LowTax.TYPE);
        units.addAll(newUnits);
        return this;
    }


    public void loadConfigs(JSONObject unitsConfig, JSONObject abilityConfig) {
        defaultUnitsConfig = unitsConfig;
        this.abilityConfig = abilityConfig;
        for (Unit u : selectedUnits) {
            u.loadConfigs(unitsConfig, abilityConfig);
        }
        for (Unit u : units) {
            u.loadConfigs(unitsConfig, abilityConfig);
        }
        for (Unit u : unitsTraining) {
            u.loadConfigs(unitsConfig, abilityConfig);
        }
    }


    /**
     * Returns a string representation of the province state
     * @return
     */
    @JsonIgnore
    public String getProvinceState() {
        String state = "Province: \"" + name + "\"";
        state += "\n\t-> wealth: " + wealth;
        state += "\n\t-> tax rate: " + taxRate.toString();
        state += "\n\t-> selected units: ";
        for (Unit u : selectedUnits) {
            state += ("\n\t\t- " + u.toString());
        }
        state += "\n\t-> units: ";
        for (Unit u : units) {
            state += ("\n\t\t- " + u.toString());
        }
        state += "\n\t-> units training: ";
        for (Unit u : unitsTraining) {
            state += ("\n\t\t- " + u.toString());
        }
        return state;
    }



    @Override
    public String toString() {
        return this.name + " (province)";
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        Province p = (Province)obj;
        
        return name.equals(p.getName());
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

    public void setUnitsTraining(List<Unit> unitsTraining) {
        this.unitsTraining = unitsTraining;
    }

    public void setSelectedUnits(List<Unit> selectedUnits) {
        this.selectedUnits = selectedUnits;
    }

    public TaxRate getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(TaxRate taxRate) {
        this.taxRate = taxRate;
    }

    public JSONObject getDefaultUnitsConfig() {
        return defaultUnitsConfig;
    }

    public void setDefaultUnitsConfig(JSONObject defaultUnitsConfig) {
        this.defaultUnitsConfig = defaultUnitsConfig;
    }

    public JSONObject getAbilityConfig() {
        return abilityConfig;
    }

    public void setAbilityConfig(JSONObject abilityConfig) {
        this.abilityConfig = abilityConfig;
    }

    public String getTaxStrategy() {
        return taxStrategy;
    }

    public void setTaxStrategy(String taxStrategy) {
        this.taxStrategy = taxStrategy;
        changeTaxRate(taxStrategy);
    }
    

}
