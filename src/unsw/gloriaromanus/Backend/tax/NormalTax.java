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
        return "Normal tax";
    }
}
