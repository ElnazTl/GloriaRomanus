package unsw.gloriaromanus.Backend.victoryCampaign;
import unsw.gloriaromanus.Backend.*;

public class VictoryCampaign {
  
    private static Compoundgoal cg;
    

  
    public static int state(Player player,int nProvinces, int treasury) {
        setUp(player, nProvinces, treasury);
        if( cg.victory()) return 1;
        if (cg.GameOver()) return -1;
        return 0;
    }
    


    private static void setUp(Player player, int nProvinces, int treasury) {
        Conquest conquest = new Conquest(player,nProvinces);
        Treasury t = new Treasury(player, treasury);
        Compoundgoal or = new Compoundgoal(player);
        or.addGoal(t);
        cg = new Compoundgoal(player);
        cg.addGoal(conquest);
        cg.addGoal(or);
    }
}
