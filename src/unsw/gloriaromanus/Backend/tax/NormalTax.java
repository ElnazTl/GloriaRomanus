package unsw.gloriaromanus.Backend.tax;

import org.json.JSONObject;

public class NormalTax implements TaxRate {
    
    final public static String TYPE = "Normal";
    private int wealth;
    private double taxRate;

    public NormalTax() {
        wealth = 10;
        taxRate = 0.1;
    }

    public int getTaxWealth() {
        return wealth;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public JSONObject getMoraleModifier() {
        return new JSONObject();
    }

    @Override
    public String toString() {
        return "Normal tax";
    }
}
