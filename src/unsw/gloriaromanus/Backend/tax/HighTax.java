package unsw.gloriaromanus.Backend.tax;

import org.json.JSONObject;

public class HighTax implements TaxRate {
        
    final public static String TYPE = "HighTax";
    private int wealth;
    private double taxRate;

    public HighTax() {
        wealth = 0;
        taxRate = 0.15;
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
        return "High tax";
    }
}
