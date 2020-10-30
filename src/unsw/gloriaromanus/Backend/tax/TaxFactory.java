package unsw.gloriaromanus.Backend.tax;

public class TaxFactory {

    public static TaxRate newTaxRate(String tax) {
        switch(tax) {
            case LowTax.TYPE:
                return new LowTax();
            case NormalTax.TYPE:
                return new NormalTax();
            case HighTax.TYPE:
                return new HighTax();
            case VeryHighTax.TYPE:
                return new VeryHighTax();
            default:
                return null;
        }
    }
}
