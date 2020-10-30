package test;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import unsw.gloriaromanus.Backend.*;
import unsw.gloriaromanus.*;

public class PlayerTest {

   
    @Test
    public void TestaddingPlayer() throws IOException {
        Database d = new Database();
        Player p = new Player("sara",d);
        String name = d.getPlayer(0).getUsername();
        assertEquals("sara",name);
        Player q = new Player ("Annie",d);
        assertEquals("Annie", d.getPlayer(1).getUsername());

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
    }
    
}
