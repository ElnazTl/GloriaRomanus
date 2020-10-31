package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import unsw.gloriaromanus.Backend.*;
import unsw.gloriaromanus.*;

public class PlayerTest {
    Player p;
    Database d;

    

   @Test
    public void TestaddingPlayer() throws IOException {
        
        Database d  = new Database("test");
        Player q = new Player ("Annie",d);
        assertEquals("Annie", d.getPlayer(0).getUsername());
    }
    @Test
    public void TestFaction() throws IOException {
        Database d = new Database("test");
        Player p = new Player("sara",d);
        p.chooseFaction("Rome");
        assertEquals("Rome",d.getPlayer(0).getFaction());

    }

    /**
     * test the functionality of adding unit and the training period 
     * @throws IOException
     */
    @Test 
    public void TestgetUnit() throws IOException {
        Database d = new Database("test");
        Player p = new Player("Sara",d);
        p.chooseFaction("Rome");
        p.startTurn();
        assertEquals("can only get unit for the faction you belong to",p.getUnit("soldier","Achaia" ));
        assertEquals("successfully added the unit",p.getUnit("soldier","V" ));
        Province province = new Province("V",d);
        assertTrue(province.getUnits().isEmpty());
        assertTrue(province.getUnitsTraining().get(0).getName().equals("soldier"));
        d.endTurn();
        assertTrue(province.getUnits().get(0).getName().equals("soldier"));
        d.saveGame();

    }
    @Test 
    public void TestLoadPlayer() throws IOException {
        Database d = new Database("test");
        d.loadGame();
        assertTrue(d.getPlayer(0).username.equals("Sara"));

    }
    /**
     * testing units are added to the province according to their training time
     */
    @Test 
    public void TestTrainingTime () throws IOException {
        Database d = new Database("test");
        d.loadGame();
        Player p = d.getPlayer(0);
        p.startTurn();
        assertEquals("successfully added the unit", p.getUnit("horseArcher", "X"));
        p.endTurn();
        Province pr = new Province("X",d);
        assertTrue(pr.getUnits().isEmpty() == true);
        p.startTurn();
        p.endTurn();
        assertTrue(!pr.getUnits().isEmpty());
        assertTrue(pr.getUnits().get(0).getName().equals("horseArcher"));

    }
    
    @Test
    public void TestLoadUnit() throws IOException {
        Database d = new Database("test");
        d.loadGame();
        assertTrue(d.getProvinceUnit().get("V").get(0).getName().equals("soldier"));

    }
    /**
     * test "turn" feature for multiplayers
     */
    @Test
    public void TestMultiPlayer() throws IOException {
        Database d = new Database("test");
        Player p = new Player("suzi",d);
        p.chooseFaction("Rome");
        Player q = new Player ("sarah",d);
        q.chooseFaction("Gaul");
        p.startTurn();
        assertEquals("successfully added the unit", p.getUnit("horseArcher", "X"));
        assertEquals("can only get unit for the faction you belong to", q.getUnit("horseArcher", "X"));
        assertEquals("It's not your turn", q.getUnit("horseArcher", "Achaia"));
        assertEquals("It's another player's turn", q.startTurn());
        p.endTurn();
        q.startTurn();
        assertEquals("successfully added the unit", q.getUnit("horseArcher", "Achaia"));
        
    }
   
}
