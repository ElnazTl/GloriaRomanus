package unsw.gloriaromanus.Backend.tax;

import org.json.JSONObject;

public class LowTax implements TaxRate {
    
    final public static String TYPE = "Low";
    private int wealth;
    private double taxRate;

    public LowTax() {
        wealth = 10;
        taxRate = 0.1;
    }

    public int getWealth() {
        return wealth;
    }

    public double getRate() {
        return taxRate;
    }

    public JSONObject getMoraleModifier() {
        return new JSONObject();
    }

    @Override
    public String toString() {
        return "Low tax";
    }
}
