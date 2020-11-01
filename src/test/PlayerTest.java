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


    

   @Test
    public void TestaddingPlayer() throws IOException {
        
        Database d  = new Database("test");
        d.addNewPlayer("Annie", "Rome");
        assertEquals("Annie", d.getPlayer(0).getUsername());
        d.saveGame();
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
        Province pro = new Province("Noricum" ,d);

        assertTrue(province.getUnitsTraining().isEmpty());
        assertTrue(pro.getUnits().size() == 2);
        d.saveGame();

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
        // assertTrue(pr.getUnits().size() == 3);
        p.startTurn();
         assertEquals("successfully added the unit", p.getUnit("horseArcher", "Noricum"));
        p.endTurn();
        // // assertTrue(pr.getUnits().size() == 3);
        
         p.startTurn();
         p.endTurn();
         assertTrue(pr.getUnits().size() == 4);

        d.saveGame();
        

    }
    
    
    /**
     * test "turn" feature for multiplayers
     */
    @Test
    public void TestMultiPlayer() throws IOException {
        Database d = new Database("test");
        Player p = d.addNewPlayer("andi", "Rome");
        
        Player q = d.addNewPlayer("sara", "Gaul");
        p.startTurn();

        assertEquals("successfully added the unit", p.getUnit("horseArcher", "Lugdunensis"));
        assertEquals("can only get unit for the faction you belong to", q.getUnit("horseArcher", "Lugdunensis"));
        assertEquals("It's not your turn", q.getUnit("horseArcher", "Narbonensis"));
        assertEquals("It's another player's turn", q.startTurn());
        p.endTurn();
        q.startTurn();
        assertEquals("successfully added the unit", q.getUnit("horseArcher", "Narbonensis"));
        
    }
    @Test
    public void testMoveTroop() throws IOException {
        Database d = new Database("test");
        d.loadGame();
        Player p = d.getPlayer(0);
        p.startTurn();
        assertEquals("successfully added the unit", p.getUnit("soldier", "Narbonensis"));
        p.endTurn();
        Province pro = new Province("Narbonensis",d);
        Long id  = pro.getUnits().get(0).getUnitID();
        p.selectUnit("Narbonensis", id);
        
        assertEquals("not adjecant",p.moveUnits("Narbonensis","Noricum"));
        assertEquals("successfully moved",p.moveUnits("Narbonensis","Numidia"));
        assertEquals("provinces not in the same faction",p.moveUnits("Narbonensis","Lugdunensis"));
        // assertEquals("Move units successfully",p.moveUnits("Narbonensis","Alpes Graiae et Poeninae"));
    }


    



    
}
