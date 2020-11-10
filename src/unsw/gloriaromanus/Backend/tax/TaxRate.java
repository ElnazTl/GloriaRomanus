package unsw.gloriaromanus.Backend.tax;

import org.json.JSONObject;

public interface TaxRate {
    public int getTaxWealth();
    public double getTaxRate();
    public JSONObject getMoraleModifier();
    
}
