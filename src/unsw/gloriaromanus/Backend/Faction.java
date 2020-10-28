package unsw.gloriaromanus.Backend;

public class Faction {

    public String name;
    private Army army;
    // ArrayList <Province> provinceList;
    public Faction(String name) {
        this.name = name;
        army = new Army();
    }
    
}
