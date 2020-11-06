package unsw.gloriaromanus.Backend.tax;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.json.JSONObject;
import unsw.gloriaromanus.Backend.Database.JSONObjectSerialiser;

public interface TaxRate {
    public int getTaxWealth();
    public double getTaxRate();
    @JsonSerialize(using = JSONObjectSerialiser.class)
    public JSONObject getMoraleModifier();
}
