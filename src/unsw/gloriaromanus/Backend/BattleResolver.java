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

        String engagementType = null;
        if (attackerUnit.isMelee() && defenderUnit.isMelee()) {
            engagementType = MELEE;
        } else if (attackerUnit.isRanged() && defenderUnit.isRanged()) {
            engagementType = RANGED;
        } else {
            // base 50% chance of either engagement
            // 10% x (speed of melee unit - speed of missile unit) (value of this formula can be negative)
        }
        runEngagement(engagementType, attackerUnit, defenderUnit);

    }
    
    /**
    * randomly chooses a unit in the province for battle
    * @param u
    */
    private static Unit randomUnit(List<Unit> u) {
        Random r = new Random();
        return u.get(r.nextInt(u.size()));
        
    }

    
    private static void runEngagement(String type, Unit attacker, Unit defender) {

        double N = RANDOMGEN.nextGaussian();
        double enemyTroops = defender.getNumTroops() * 0.1;
        if (type == RANGED) {
            if (!attacker.isMelee()) {
                double enemyValues = defender.getModifiedValue(modifier, type, who)
                int numInflict = (int)((N + 1) * enemyTroops);

            }
        } else {

        }
    }

    private static void killUnit(Province p, Unit u) {
        p.removeUnit(u);
    }

}
