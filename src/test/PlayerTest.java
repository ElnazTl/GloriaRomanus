package test;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        
        // assertEquals("sara",name);
        // Player q = new Player ("Annie",d);
        // assertEquals("Annie", d.getPlayer(1).getUsername());
        p.chooseFaction("Rome");
    }
    @Test
    public void TestFaction() throws IOException {
        Database d = new Database();
        Player p = new Player("sara",d);
        p.chooseFaction("Rome");
        assertEquals("Rome",d.getPlayer(0).getFaction());

    }

    @Test 
    public void TestgetUnit() throws IOException {
        Database d = new Database();
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
    public void TestLoad() throws IOException {
        Database d = new Database();
        d.loadGame();
        assertTrue(d.getPlayer(0).username.equals("sara"));

    }

        public static void main(String[] args) throws IOException {
            Database d = new Database();
            Player x= new Player("ani",d);
            x.startTurn();
            x.chooseFaction("Rome");
            x.getUnit("soldier", "V");

            // d.saveGame();
          
            // d.loadGame();
            Database k = new Database();
            k.loadGame();


        }
    
}
