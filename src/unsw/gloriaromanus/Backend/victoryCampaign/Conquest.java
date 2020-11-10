/**
 * goal of conquering all of the territories
 */

package unsw.gloriaromanus.Backend.victoryCampaign;

import unsw.gloriaromanus.Backend.*;


public class Conquest implements Goal {
    Player player;
    int nProvincesGoal;
    int nProvinces;
    public Conquest(Player player, int goalNumber) {
        this.player = player;
        this.nProvincesGoal = goalNumber;
        setProvinces();
    }

    private void setProvinces() {
        Faction f = player.getFaction();
        nProvinces = f.getProvinces().size()+f.getProvincesConqueredOnTurn().size();
    }

    @Override
    public boolean victory() {
        return (nProvinces == nProvincesGoal);
    }


    @Override
    public boolean GameOver() {
        return (nProvinces == 0);  
    }
    
}
