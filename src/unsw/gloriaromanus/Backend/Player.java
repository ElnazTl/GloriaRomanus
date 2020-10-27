package unsw.gloriaromanus.Backend;

import java.io.IOException;

public class Player {
    private Faction faction;
    public String username;
    Boolean turn;
    Database database;

    public Player(String username, Database database) {
        this.username = username;
        this.database = database;

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

    /**
     * given the player province 
     * @param human
     * @param enemy
     * @param d
     * @return
     * @throws IOException
     */
    public String invade(String human, String enemy ) throws IOException {
        if (turn) {
            Province h = new Province(human);
            Province e = new Province(enemy);

            return h.battle(e, database);
        }
        return "It's not your turn";
    }

    
    /**
     * if enough gold, buying the required unit for the given province
     * @param name
     * @param category
     * @param province
     * @return
     */

    public String getUnit(String name, String category, String province) {
        if (turn) {
            Unit u = new Unit (category, name);
            Province p = new Province(province);

            p.addUnit(database, u);

            // if successful returning message otherwise returning the fail message
            return "Added unit";


        }

        return "It's not your turn";       
    }

    

}
