package unsw.gloriaromanus.Backend.tax;

import org.json.JSONObject;

public interface TaxRate {
    public int getWealth();
    public double getRate();
    public JSONObject getMoraleModifier();
}
