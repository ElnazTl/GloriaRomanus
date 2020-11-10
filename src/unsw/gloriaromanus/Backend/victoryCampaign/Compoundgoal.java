package unsw.gloriaromanus.Backend.victoryCampaign;

import unsw.gloriaromanus.Backend.*;
import java.util.ArrayList;
import java.util.List;

public class Compoundgoal implements Goal {

    public Player player;
    private List<Goal> goals;

    public Compoundgoal (Player player) {
        this.player = player;
        goals = new ArrayList<Goal>();
    }

    public List<Goal> getGoals() {
        return goals;
    }
   
    public void addGoal(Goal g) {
        goals.add(g);
        
    }
    /**
     * or all of the leaf nodes in a compound
     */
    private boolean orGoal() {
        for (Goal g: goals) {
    
            if (g.victory()) return true;
        
        }
        return false;
    }
    /**
     * and leaf and compound
     */
    @Override
    public boolean victory() {
        for (Goal g: goals) {
            if (g.getClass().equals(this.getClass())) {
                if (!((Compoundgoal)g).orGoal()) return false;
            }
            else if (!g.victory()) return false;
        }
        return true;

    }

    @Override
    public boolean GameOver() {
        
        for (Goal g: goals) {
     
            if(g.GameOver()) return true;
        }
        
        return false;
    }
}
