package unsw.gloriaromanus.Backend.victoryCampaign;
import unsw.gloriaromanus.Backend.*;

public class Treasury implements Goal {

    int treasure;
    int treasureGoal;
    Player player;
    public Treasury(Player player, int treasureGoal ) {
        this.treasureGoal = treasureGoal;
        this.player = player;
        treasure = player.getFaction().getTreasury();
    }

    @Override
    public boolean GameOver() {
        return false;
    }

    @Override
    public boolean victory() {
        return (treasure== treasureGoal);
    }
    
    
}
