package unsw.gloriaromanus.Backend;

import java.text.BreakIterator;

public class Unit {
    
    private String category;
    private String type;
    private int movementPoint;
    /**
     * Unit u = new Unit(artillery, "")
     */
    public Unit(String category, String type) {
        this.category = category;
        this.type = type;
        setmovementPoint();
    }

    private void setmovementPoint() {

        switch (type) {
            case "calvary":
                movementPoint = 15;
                break;

            case "infantry":
                movementPoint = 10;
                break;

            case "artillery":
                movementPoint = 4;
                break;
        }


    }

}
