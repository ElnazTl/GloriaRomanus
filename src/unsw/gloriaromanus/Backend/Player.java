package unsw.gloriaromanus.Backend;

import java.io.IOException;

public class Player {
    private String faction;
    public String username;
    Boolean turn;
    Database database;

   
    public Player(){}
    public Player(String username, Database database) {
        this.username = username;
        this.database = database;
        registerPlayer();

    }

    /**
     * Player choosing their faction 
     * 
     * @param faction
     */
    public void setDatabase(Database d) {
        this.database = d;
    }
    public void registerPlayer() {
        database.addPlayer(this);
    }
    public void chooseFaction(String faction) {
        // Faction f = new Faction(faction);
        setFaction(faction);
    }

    private void setFaction(String faction) {
        this.faction = faction;
    }

    public String getFaction() {
        return faction;
    }

    /**
     * setting the player turn
     */
    public void startTurn() {
        turn = true;
    }

    public void endTurn() {
        database.endTurn();
        turn = false;
    }

    /**
     * launching invasion 
     * 
     * @param human
     * @param enemy
     * @param d
     * @return
     * @throws IOException
     */
    public String invade(String human, String enemy) throws IOException {
        if (turn) {

            Province h = new Province(human, database);
            Province e = new Province(enemy, database);

            return h.invade(e, database);
        }
        return "It's not your turn";
    }

    /**
     * if enough gold, buying the required unit for the given province
     * 
     * @param name
     * @param category
     * @param province
     * @return
     */

    // implementing turn ?
    public String getUnit(String name, String province) throws IOException {
        if (turn) {
            return database.addUnit(name, province);
        }

        return "It's not your turn";
    }

    /**
     * given the unit and from and to if the provinces are adjacent the unit will be
     * moved
     * 
     * @param u
     * @param from
     * @param to
     * @return
     */

    public String moveTroop(Unit u, String from, String to) throws IOException {

        Province f = new Province(from, database);
        Province t = new Province(to, database);

        return f.moveTroopTo(t, u);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
