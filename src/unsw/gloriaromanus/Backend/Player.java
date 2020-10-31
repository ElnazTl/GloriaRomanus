package unsw.gloriaromanus.Backend;

import java.io.IOException;

public class Player {
    private String faction;
    public String username;
    Boolean turn;
    Database database;

   
    public Player(){}
    public Player(String username, Database database) throws IOException {
        this.username = username;
        this.database = database;
        registerPlayer();
        turn = false;

    }
    public Boolean getTurn() {
        return turn;
    }

    /**
     * Player choosing their faction 
     * 
     * @param faction
     */
    public void setDatabase(Database d) {
        this.database = d;
    }
    public void registerPlayer() throws IOException{
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
    public String startTurn() {
        
        for (Player p: database.getPlayers()) {
            if (p.getTurn())return "It's another player's turn";
        }
        turn = true;
        return "now it's your turn";
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
    public String invade(String attacker, String enemy) throws IOException {
        if (turn) {

            Province attackerProvince = new Province(attacker, database);
            var result = database.invade(attackerProvince, enemy) ;
            if (result == 0) return "You lost the battle";
            if (result == 1) return "You won";
            else return "the battle is a tie";
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

    
    public String getUnit(String name, String province) throws IOException {
        if (!database.getFactionProvince().get(province).getName().equals(faction)) return "can only get unit for the faction you belong to";
        if (turn) {
            if (!database.addUnit(name,faction,province)) return "can't add unit";
            return "successfully added the unit";
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

    // public String moveTroop(String u, String from, String to) throws IOException {

    //     Province f = new Province(from, database);
    //     Province t = new Province(to, database);

    //     return f.moveTroopTo(t, u);
    // }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
