package test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import unsw.gloriaromanus.Backend.*;
public class DatabaseTest {

    public static boolean gradleTest = false;
    
    @Test
    public void TestInitialiseDatabase() throws IOException {
        Database db = new Database(!gradleTest);
        
        assertEquals("200 BC", db.getGameYear());

    }

    @Test
    public void TestStartGame() throws IOException {
        Database db = new Database(!gradleTest);
        
        assertEquals("200 BC", db.getGameYear());
        Player A = db.addNewPlayer("A", "Rome");
        Player B = db.addNewPlayer("B", "Gaul");

        db.startGame();

        assertEquals("Rome", A.getFaction().getName());
        assertEquals("Gaul", B.getFaction().getName());
        
        assertEquals(true, A.isTurn());
        assertEquals(false, B.isTurn());

        A.endTurn();

        assertEquals(false, A.isTurn());
        assertEquals(true, B.isTurn());

        B.endTurn();

        assertEquals(true, A.isTurn());
        assertEquals(false, B.isTurn());

        // System.out.println("Player A provinces: " + A.getProvinces());
        // System.out.println();
        // System.out.println("Player B provinces: " + B.getProvinces());

    }


    @Test
    public void TestTrainUnit() throws IOException {
        Database db = new Database(!gradleTest);
        
        assertEquals("200 BC", db.getGameYear());
        Player A = db.addNewPlayer("A", "Spain");
        Player B = db.addNewPlayer("B", "Gaul");

        db.startGame();

        assertEquals("Spain", A.getFaction().getName());
        assertEquals("Gaul", B.getFaction().getName());
        
        assertEquals(true, A.isTurn());
        assertEquals(false, B.isTurn());

        System.out.println(A.getStateProvince("V"));

        A.selectProvince("V");
        A.trainUnit("soldier");
        A.trainUnit("soldier");
        System.out.println(A.getStateProvince("V"));
        A.endTurn();

        B.endTurn();

        A.selectProvince("V");
        A.trainUnit("soldier");
        A.trainUnit("soldier");
        A.trainUnit("soldier");
        System.out.println(A.getStateProvince("V"));
        A.endTurn();
        
        B.endTurn();

        System.out.println(A.getStateProvince("V"));

    }


    @Test
    public void TestMoveUnit() throws IOException {
        Database db = new Database(!gradleTest);
        
        assertEquals("200 BC", db.getGameYear());
        Player A = db.addNewPlayer("A", "Numidia");
        Player B = db.addNewPlayer("B", "Gaul");

        db.startGame();

        assertEquals("Numidia", A.getFaction().getName());
        assertEquals("Gaul", B.getFaction().getName());
        
        assertEquals(true, A.isTurn());
        assertEquals(false, B.isTurn());

        A.selectProvince("VI");
        A.trainUnit("soldier");
        A.trainUnit("soldier");
        A.endTurn();

        B.endTurn();

        A.selectProvince("VI");
        A.selectUnit(0L);
        System.out.println("BEFORE MOVE");
        System.out.println(A.getStateProvince("VI"));
        System.out.println(A.getStateProvince("VII"));
        A.moveUnits("VII");
        System.out.println("AFTER MOVE");
        System.out.println(A.getStateProvince("VI"));
        System.out.println(A.getStateProvince("VII"));

    }



    @Test
    public void TestMoveMultipleUnits() throws IOException {
        Database db = new Database(!gradleTest);
        
        assertEquals("200 BC", db.getGameYear());
        Player A = db.addNewPlayer("A", "Numidia");
        Player B = db.addNewPlayer("B", "Gaul");

        db.startGame();

        assertEquals("Numidia", A.getFaction().getName());
        assertEquals("Gaul", B.getFaction().getName());
        
        assertEquals(true, A.isTurn());
        assertEquals(false, B.isTurn());

        A.selectProvince("VI");
        A.trainUnit("soldier");
        A.trainUnit("soldier");
        A.endTurn();

        B.endTurn();

        A.selectProvince("VI");
        A.selectUnit(0L);
        A.selectUnit(1L);
        System.out.println("BEFORE MOVE");
        System.out.println(A.getStateProvince("VI"));
        System.out.println(A.getStateProvince("VII"));
        A.moveUnits("VII");
        System.out.println("AFTER MOVE");
        System.out.println(A.getStateProvince("VI"));
        System.out.println(A.getStateProvince("VII"));

    }
}
