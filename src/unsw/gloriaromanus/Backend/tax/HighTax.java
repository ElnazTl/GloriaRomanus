package unsw.gloriaromanus.Backend.tax;

import org.json.JSONObject;

public class HighTax implements TaxRate {
        
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
}
