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
        d.addNewPlayer("Annie", "Rome");
        assertEquals("Annie", d.getPlayer(0).getUsername());
    }
    @Test
    public void TestFaction() throws IOException {
        Database d = new Database("test");
        d.addNewPlayer("sara", "Rome");
        assertEquals("Rome",d.getPlayer(0).getFaction().getName());

    }

    /**
     * test the functionality of adding unit and the training period 
     * @throws IOException
     */
    @Test 
    public void TestgetUnit() throws IOException {
        Database d = new Database("test");

        Player p = d.addNewPlayer("sara", "Gaul");

        p.startTurn();

        assertEquals("can only get unit for the faction you belong to",p.getUnit("soldier","Achaia" ));
        assertEquals("successfully added the unit",p.getUnit("soldier","Noricum" ));

        Province province = new Province("Noricum" ,d);

        assertTrue(province.getUnits().isEmpty());
        assertTrue(province.getUnitsTraining().get(0).getName().equals("soldier"));

        p.endTurn();
        p.startTurn();
        assertEquals("successfully added the unit",p.getUnit("soldier","Noricum" ));

        p.endTurn();
        assertTrue(province.getUnitsTraining().isEmpty());
        assertTrue(province.getUnits().size() == 2);
        d.saveGame();

    }

    
    @Test 
    public void TestLoadPlayer() throws IOException {
        Database d = new Database("test");
        d.loadGame();
        assertTrue(d.getPlayer(0).username.equals("sara"));

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
        assertEquals("successfully added the unit", p.getUnit("soldier", "Noricum"));
        p.endTurn();
        Province pr = new Province("Noricum",d);
        assertTrue(pr.getUnits().size() == 3);
        p.startTurn();
        assertEquals("successfully added the unit", p.getUnit("horseArcher", "Noricum"));
        p.startTurn();
        assertTrue(pr.getUnits().size() == 3);
        p.endTurn();
        p.startTurn();
        assertTrue(pr.getUnits().size() == 3);
        p.endTurn();
        assertTrue(pr.getUnits().size() == 4);

        d.saveGame();
        

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
    public static void main(String[] args) throws IOException {
        Database d = new Database("");
        Player p = d.addNewPlayer("sara", "Gaul");
        p.startTurn();
        p.getUnit("soldier", "Narbonensis");
        Province pp = new Province("Narbonensis", d);
        System.out.println(pp.getUnitsTraining());
        p.endTurn();
        p.startTurn();
        d.saveGame();
        Database k = new Database("");
        k.loadGame();
        Player q = k.getPlayer(0);

        q.startTurn();
        System.out.println(q.getUnit("soldier", "Narbonensis"));
        Province x = new Province("Narbonensis", k);
        System.out.println("check the fnction"+k.getProvinceTraining().get("Narbonensis"));
        System.out.println("are we good"+x.getUnitsTraining());
        // x.setDatabase(k);



        q.endTurn();
        q.startTurn();
        q.endTurn();

        System.out.println("are we dont yet"+x.getUnits());
        System.out.println("well look what we have here"+k.getFaction().get("Gaul").getProvinces().get(0).getUnitsTraining());



    }
   
}
