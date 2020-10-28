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
    }

     /**
     * randomly chooses a unit in the province for battle
     * @param u
     */

    private Unit chooseUnit(List<Unit> u) {

        Random r = new Random();
        return u.get(r.nextInt(u.size()));
        
    }
}
