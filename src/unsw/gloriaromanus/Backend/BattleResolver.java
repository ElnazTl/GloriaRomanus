package unsw.gloriaromanus.Backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class BattleResolver {

    final public static String MELEE = "melee";
    final public static String RANGED = "ranged";

    
    private static List<Unit> attackerArmy;
    private static List<Unit> defenderArmy;
    private static List<Unit> attackerRouted;
    private static List<Unit> defenderRouted;


    /**
     * Battle an attacker province against a defender province
     * 
     * @param attacker Attacking province
     * @param defender Defending province
     * @return Returns -1 if draw, 0 if defender won, 1 if attacker won
     */
    public static int battle(Province attacker, Province defender) {

        attackerArmy = new ArrayList<Unit>();
        defenderArmy = new ArrayList<Unit>();
        attackerRouted = new ArrayList<Unit>();
        defenderRouted = new ArrayList<Unit>();

        attackerArmy.addAll(attacker.getSelectedUnits());
        attacker.clearAllSelected();
        defenderArmy.addAll(defender.getUnits());
        defender.clearAllSelected();




        int battleWinner = -1;
        int numSkirmishes = 0;
        while (numSkirmishes < 200) {
            if (attackerArmy.isEmpty() && defenderArmy.isEmpty()) {
                // Both armies have routed
                break;
            } else if (!attackerArmy.isEmpty() && defenderArmy.isEmpty()) {
                // Attacking army wins
                battleWinner = 1;
                attackerArmy.addAll(attackerRouted);
                defender.conquerProvince(attackerArmy);
                defenderRouted.removeAll(defenderRouted);
                break;
            } else if (attackerArmy.isEmpty() && !defenderArmy.isEmpty()) {
                // Defending army wins
                battleWinner = 0;
                attacker.addUnits(attackerRouted);
                defenderArmy.addAll(defenderRouted);
                break;
            }

            runSkirmish();


            numSkirmishes++;
        }
        if (numSkirmishes == 200) {
            attacker.addUnits(attackerArmy);
            defender.addUnits(defenderArmy);
        }

        attackerArmy.removeAll(attackerArmy);
        defenderArmy.removeAll(defenderArmy);
        attackerRouted.removeAll(attackerRouted);
        defenderRouted.removeAll(defenderRouted);

        return battleWinner;
    }


     /**
      * Selects units from each player and battles them against
      * each other until invasion result is determined
      *
      * @param player
      * @param enemy
      */
    private static void runSkirmish() {

        Unit attacker = randomUnit(attackerArmy);
        Unit defender = randomUnit(defenderArmy);

        runEngagements(attacker, defender);
        
    }
    
    /**
    * Randomly chooses a unit in the province for battle
    *
    * @param u
    */
    private static Unit randomUnit(List<Unit> u) {
        Random r = new Random();
        return u.get(r.nextInt(u.size()));
        
    }

    
    /**
     * Runs all engagements for a skirmish
     * 
     * @param attacker
     * @param defender
     */
    private static void runEngagements(Unit attacker, Unit defender) {
        String type = null;
        if (attacker.isMelee() && defender.isMelee()) {
            type = MELEE;
        } else if (attacker.isRanged() && defender.isRanged()) {
            type = RANGED;
        } else {
            // base 50% chance of either engagement
            // 10% x (speed of melee unit - speed of missile unit) (value of this formula can be negative)
        }


        boolean attackerRoutes = false;
        boolean defenderRoutes = false;



        // Run engagements
        while (true) {
            int numAttackerTroops = attacker.getNumTroops();
            int numDefenderTroops = defender.getNumTroops();

            // Attacker defeats defender
            if (attacker.isAlive() && !defender.isAlive()) break;   
            // Defender defeats attacker
            if (!attacker.isAlive() && defender.isAlive()) break;   
            
            // Attacker escapes engagement
            if (attackerRoutes) {  
                attackerRouted.add(attacker);
                break;
            }
            // Defender escapes battle
            if (defenderRoutes) {
                defenderRouted.add(defender);
                break;
            }

            int attackCasualtiesInflicted = calculateCasualties(type, attacker, defender);
            defender.inflictCasualties(attackCasualtiesInflicted);

            if (defender.isAlive()) {
                int defenderCasualtiesInflicted = calculateCasualties(type, defender, attacker);
                attacker.inflictCasualties(defenderCasualtiesInflicted);

                if (!attacker.isAlive()) {
                    // Defender killed all attacker troops
                    attackerArmy.remove(attacker);
                    break;
                } else {
                    // Both units are still alive
                    boolean attackerBreaks = unitBreaks(attacker, attackCasualtiesInflicted, defenderCasualtiesInflicted, numAttackerTroops, numDefenderTroops);
                    boolean defenderBreaks = unitBreaks(defender, defenderCasualtiesInflicted, attackCasualtiesInflicted, numDefenderTroops, numAttackerTroops);

                    if (attackerBreaks && !defenderBreaks) {
                        // Defender wins
                        // Attacker breaks, defender inflicts casualties
                        while (attacker.isAlive()) {
                            attacker.inflictCasualties(calculateCasualties(type, defender, attacker));
                            attackerRoutes = unitRoutes(attacker, defender);
                            if (attackerRoutes) {
                                attackerRouted.add(attacker);
                                break;
                            }
                        }
                        attackerArmy.remove(attacker);
                        break;

                    } else if (!attackerBreaks && defenderBreaks) {
                        // Attacker wins
                        // Defender breaks, attacker inflicts casualties
                        while (defender.isAlive()) {
                            defender.inflictCasualties(calculateCasualties(type, attacker, defender));
                            defenderRoutes = unitRoutes(defender, attacker);
                            if (defenderRoutes) {
                                defenderRouted.add(defender);
                                break;
                            }
                        }
                        defenderArmy.remove(defender);
                        break;
                    } else if (!attackerBreaks && !defenderBreaks) {
                        // Neither unit breaks, new engagement
                        continue;
                    }
                    break;
                }

            } else {
                // Attacker killed all defender troops
                defenderArmy.remove(defender);
                break;
            }
        }
        
    }



    /**
     * Calculates the number of casualties to inflict on an enemy
     * 
     * @param type Type of engagement
     * @param attacker Unit that attacks
     * @param defender Unit that casualties are inflicted on
     * @return
     */
    private static int calculateCasualties (String type, Unit attacker, Unit defender) {
        Random RGen = new Random();
        
        int numInflict = 0;
        double N = RGen.nextGaussian();
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


    /**
     * Calculates chance for a unit to break, and return if unit breaks
     * 
     * @param unit Unit to calculate break chance of
     * @param aCasualties Number of casualties inflicted by attacker during engagement
     * @param dCasualties Number of casualties inflicted by defender during engagement
     * @param aTroops Number of attacker troops before engagement
     * @param dTroops Number of defender troops before engagement
     * @return True if unit breaks, otherwise False
     */
    private static boolean unitBreaks(Unit unit, int aCasualties, int dCasualties, int aTroops, int dTroops) {
        Random RGen = new Random();

        double r = RGen.nextDouble();
        double a = aCasualties / aTroops;
        double d = dCasualties / dTroops;
        if (d == 0) d = Double.MIN_VALUE;
        double chance = a/d * 0.1 + (1 - unit.getFriendlyModifiedValue("morale") * 0.1);

        if (chance < 0.05) chance = 0.05;
        else if (chance > 1) chance = 1;

        return r < chance;
    }


    /**
     * Calculates chance for unit to route, and returns if routed or not
     * 
     * @param router
     * @param pursuer
     * @return
     */
    private static boolean unitRoutes(Unit router, Unit pursuer) {

        if (!router.isAlive()) return false;

        Random RGen = new Random();

        double r = RGen.nextDouble();

        double chance = 0.5 + 0.1 * (router.getFriendlyModifiedValue("speed") 
                                     - pursuer.getFriendlyModifiedValue("speed"));

        if (chance < 0.1) chance = 0.1;
        else if (chance > 1) chance = 1;

        return r < chance;
    }

}
