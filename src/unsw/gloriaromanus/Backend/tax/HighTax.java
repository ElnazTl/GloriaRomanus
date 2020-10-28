package unsw.gloriaromanus.Backend.tax;

import org.json.JSONObject;

public class HighTax implements TaxRate {
        
    private int wealth;
    private double taxRate;

    public HighTax() {
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
}
