package unsw.gloriaromanus.Backend;

public class Player {
    private Faction faction;
    public String username;
    Boolean turn;

    public Player(String username) {
        this.username = username;
    }

    /**
     * Player choosing their faction  tu7u
     * @param faction
     */

    public void chooseFaction(String faction) {
        Faction f = new Faction(faction);
        setFaction(f);
    }

    private void setFaction(Faction faction) {
        this.faction = faction;
    }

    public String getFaction() {
        return faction.name;
    }

    /**
     * setting the player turn
     */
    public void startTurn() {
        turn = true;
    }

    public void endTurn() {
        turn = false;
    }
    
}
