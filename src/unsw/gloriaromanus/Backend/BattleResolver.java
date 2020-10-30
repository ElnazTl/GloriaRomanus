package unsw.gloriaromanus.Backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class BattleResolver {

    final public static String MELEE = "melee";
    final public static String RANGED = "ranged";

    final public static Random RANDOMGEN = new Random();

    public static Province battle(Province attacker, Province defender) {
        
        List<Unit> attackerArmy = attacker.getUnits();
        List<Unit> defenderArmy = defender.getUnits();

        Province battleWinner = null;
        int numSkirmishes = 0;
        while (numSkirmishes < 200) {
            if (attackerArmy.isEmpty() && defenderArmy.isEmpty()) {
                break;
            } else if (!attackerArmy.isEmpty() && defenderArmy.isEmpty()) {
                battleWinner = attacker;
            } else {
                battleWinner = defender;
            }

            runSkirmish(attackerArmy, defenderArmy);


            numSkirmishes++;
        }

        return battleWinner;
    }

     /**
      * selets units from each player and battles them against eachother until invasion result is determined
      * @param player
      * @param enemy
      */
    private static void runSkirmish(List<Unit> attackerArmy, List<Unit> defenderArmy) {

        Unit attackerUnit = randomUnit(attackerArmy);
        Unit defenderUnit = randomUnit(defenderArmy);


        runEngagement(attackerUnit, defenderUnit);

    }
    
    /**
    * randomly chooses a unit in the province for battle
    * @param u
    */
    private static Unit randomUnit(List<Unit> u) {
        Random r = new Random();
        return u.get(r.nextInt(u.size()));
        
    }

    
    private static void runEngagement(Unit attacker, Unit defender) {
        String type = null;
        if (attacker.isMelee() && defender.isMelee()) {
            type = MELEE;
        } else if (attacker.isRanged() && defender.isRanged()) {
            type = RANGED;
        } else {
            // base 50% chance of either engagement
            // 10% x (speed of melee unit - speed of missile unit) (value of this formula can be negative)
        }

        boolean attackerBreaks = false;
        boolean defenderBreaks = false;
        boolean attackerRoutes = false;
        boolean defenderRoutes = false;

        int attackerNumTroops = attacker.getNumTroops();
        int defenderNumTroops = defender.getNumTroops();

        while (defender.isAlive() && attacker.isAlive()) {
            if (!attackerRoutes && defenderRoutes)
            defender.inflictCasualties(calculateCasualties(type, attacker, defender));
            if (defender.isAlive()) {
                attacker.inflictCasualties(calculateCasualties(type, defender, attacker));
            }
        }
        
        
    }

    private static int calculateCasualties (String type, Unit attacker, Unit defender) {
        int numInflict = 0;
        double N = RANDOMGEN.nextGaussian();
        double enemyTroops = defender.getNumTroops() * 0.1;
        double playerAttack = attacker.getFriendlyModifiedValue("attack")
                            + attacker.getFriendlyModifiedValue("charge");
        double enemyValues = defender.getFriendlyModifiedValue("armour")
                           + defender.getFriendlyModifiedValue("shield");
        if (enemyValues == 0) enemyValues = 10;
        if (type == RANGED && attacker.isMelee()) return 0;
        if (type == MELEE) enemyValues = enemyValues + defender.getFriendlyModifiedValue("defence"); 
        numInflict = (int)((N + 1) * enemyTroops * (playerAttack / enemyValues));
        if (numInflict < 0) numInflict = 0;
        if (numInflict > defender.getNumTroops()) numInflict = defender.getNumTroops();
        return numInflict;
    }

    private static void killUnit(Province p, Unit u) {
        p.removeUnit(u);
    }

}
