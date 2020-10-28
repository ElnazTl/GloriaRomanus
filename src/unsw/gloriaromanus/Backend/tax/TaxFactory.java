package unsw.gloriaromanus.Backend.tax;

public class TaxFactory {

    public TaxRate newTaxRate(String tax) {
        switch(tax) {
            case "low":
                return new LowTax();
            case "normal":
                return new NormalTax();
            case "high":
                return new HighTax();
            case "veryHigh":
                return new VeryHighTax();
            default:
                return null;
        }
    }
}
