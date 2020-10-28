package unsw.gloriaromanus.Backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Battleresolver {
    private Province playerProvince;
    private Province enemyProvince;
    private Database database;

    public Battleresolver(Province player, Province enemy, Database database) {

        playerProvince = player;
        enemyProvince = enemy;
        this.database = database;
        runSkirmish(player, enemy);
    }

     /**
      * selets units from each player and battles them against eachother until invasion result is determined
      * @param player
      * @param enemy
      */
    private void runSkirmish(Province player,Province enemy) {
        ArrayList<Unit> playerUnitList = player.getUnits();
        ArrayList<Unit> enemyUnitList = player.getUnits();
        startSkirmish(chooseUnit(playerUnitList), chooseUnit(enemyUnitList));

    }
    
    /**
    * randomly chooses a unit in the province for battle
    * @param u
    */

    private Unit chooseUnit(List<Unit> u) {

        Random r = new Random();
        return u.get(r.nextInt(u.size()));
        
    }

    /**
     * given two units, implements the logic of battle for two units
     * @param player
     * @param enemy
     */

    private void startSkirmish(Unit player, Unit enemy) {
        //TODO: implement the logic of skirmish 
    }
}
