package unsw.gloriaromanus.Backend;

import java.io.IOException;

public class Player {

    private Faction faction;
    public String username;

   
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


    public void endTurn() {
        if (!isTurn()) {
            System.out.println("Not your turn");
            return;
        }
        faction.endTurn();
    }

    public boolean isTurn() {
        return faction.isTurn();
    }


    public void invade(String ownedProvince, String enemyProvince) {
        if (!isTurn()) {
            System.out.println("Not your turn");
            return;
        }
        faction.invade(ownedProvince, enemyProvince);
    }


    /**
     * Attempts to train a given unit in a given province
     * 
     * @param province Province to train unit in
     * @param unit Unit to train
     * @throws IOException
     */
    public void trainUnit(String unit) throws IOException {
        if (!isTurn()) {
            System.out.println("Not your turn");
            return;
        }
        boolean training = faction.trainUnit(unit);
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

    public boolean moveUnits(String to) throws IOException {
        if (!isTurn()) {
            System.out.println("Not your turn");
            return false;
        }
        boolean move = faction.moveUnits(to);
        if (move) {
            // Unit moved successfully
            System.out.println("Move units successfully");
        } else {
            // Unit could not move
            System.out.println("Could not move units");
        }
        return move;
    }


    public void selectUnit(Long unitID) {
        if (!isTurn()) {
            System.out.println("Not your turn");
            return;
        }
        faction.selectUnit(unitID);
    }

    public String getProvinces() {
        return faction.getProvinces().toString();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void selectProvince(String name) {
        faction.selectProvince(name);
    }

    public String getStateProvince(String name) {
        return faction.getStateProvince(name);
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
