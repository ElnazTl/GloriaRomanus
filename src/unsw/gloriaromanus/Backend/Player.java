package unsw.gloriaromanus.Backend;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class Player {

    private Faction faction;
    public String username;
    Boolean turn;

   
    public Player(){}
    public Player(String username) {
        this.username = username;
    }




    public void setFaction(Faction faction) {
        this.faction = faction;
    }

    public Faction getFaction() {
        return faction;
    }

    /**
     * setting the player turn
     */
    public void startTurn() {
        turn = true;
    }

    public void endTurn() {
        if (!turn) {
            // Not player turn, cant end turn
        }
        faction.endTurn();
        turn = false;
    }


    public void invade(String ownedProvince, String enemyProvince) {
        faction.invade(ownedProvince, enemyProvince);
    }


    /**
     * Attempts to train a given unit in a given province
     * 
     * @param province Province to train unit in
     * @param unit Unit to train
     * @throws IOException
     */
    public void trainUnit(String province, String unit) throws IOException {
        boolean training = faction.trainUnit(province, unit);
        if (training) {
            // Unit is training
            System.out.println("Trained unit successfully");
        } else {
            // Unit training failed
            System.out.println("Could not train unit");
        }
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

    public boolean moveUnits(String from, String to) throws IOException {
        boolean move = faction.moveUnits(from, to);
        if (move) {
            // Unit moved successfully
            System.out.println("Move units successfully");
        } else {
            // Unit could not move
            System.out.println("Could not move units");
        }
        return move;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    @Override
    public String toString() {
        return "Player: " + username;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        Player p = (Player)obj;

        return username.equals(p.getUsername());
    }
}
