package test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import unsw.gloriaromanus.Backend.*;

public class InvadeTest {

    
    @Test
    public void TestInvadeAdjacentNoEnemy() throws IOException {

        Database db = new Database("test");

        Player A = db.addNewPlayer("A", "Spain");
        Player B = db.addNewPlayer("B", "Numidia");

        A.startTurn();

        A.trainUnit("V", "soldier");
        A.trainUnit("V", "soldier");
        A.endTurn();
        B.startTurn();
        B.endTurn();

        List<Unit> unitsA = A.getUnitsFromProvince("V");
        System.out.println(unitsA);

        A.selectUnit("V", unitsA.get(0).getUnitID());

        int result = A.invade("V", "VI");
        assertEquals(1, result);

    }

    @Test
    public void TestInvadeNotAdjacent() throws IOException {

        Database db = new Database("test");

        Player A = db.addNewPlayer("A", "Spain");
        Player B = db.addNewPlayer("B", "Numidia");

        A.startTurn();

        A.trainUnit("V", "soldier");
        A.trainUnit("V", "soldier");
        A.endTurn();
        B.startTurn();
        B.endTurn();

        List<Unit> unitsA = A.getUnitsFromProvince("V");
        System.out.println(unitsA);

        A.selectUnit("V", unitsA.get(0).getUnitID());

        int result = A.invade("V", "XI");
        assertEquals(-1, result);

    }


    @Test
    public void TestInvadeAdjacentOneEnemy() throws IOException {

        Database db = new Database("test");

        Player A = db.addNewPlayer("A", "Spain");
        Player B = db.addNewPlayer("B", "Numidia");

        A.startTurn();

        A.trainUnit("V", "soldier");
        A.trainUnit("V", "soldier");
        A.endTurn();
        B.startTurn();
        B.trainUnit("VI", "soldier");
        B.endTurn();

        List<Unit> unitsA = A.getUnitsFromProvince("V");
        // System.out.println(unitsA);

        List<Unit> unitsB = B.getUnitsFromProvince("VI");
        // System.out.println(unitsB);

        A.selectUnit("V", unitsA.get(0).getUnitID());
        // System.out.println(unitsA);
        A.selectUnit("V", unitsA.get(0).getUnitID());

        int result = A.invade("V", "VI");
        assertEquals(1, result);

    }


    @Test
    public void TestEnemyInvadeLose() throws IOException {

        Database db = new Database("test");

        Player A = db.addNewPlayer("A", "Spain");
        Player B = db.addNewPlayer("B", "Numidia");

        A.startTurn();

        A.trainUnit("V", "soldier");
        A.trainUnit("V", "soldier");
        A.endTurn();
        B.startTurn();
        B.trainUnit("VI", "soldier");
        B.endTurn();
        A.trainUnit("V", "soldier");
        A.endTurn();

        List<Unit> unitsB = B.getUnitsFromProvince("VI");

        A.selectUnit("V", unitsB.get(0).getUnitID());

        int result = B.invade("VI", "V");
        assertEquals(-1, result);


    }
}

