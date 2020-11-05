package test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import unsw.gloriaromanus.Backend.*;

public class UnitTest {

    // Have to load config in manually
    // NOTE: if you change the config file,
    // you will have to manually change the values in the JSON's below
    static JSONObject unitJSON = new JSONObject("{\r\n    \"soldier\": {\r\n        \"type\" : \"infantry\",\r\n        \"attackType\" : \"melee\",\r\n        \"numTroops\" : 10,\r\n        \"cost\" : 5,\r\n        \"trainTime\" : 1,\r\n        \"attack\" : 15,\r\n        \"speed\" : 4,\r\n        \"morale\" : 5,\r\n        \"armour\" : 3,\r\n        \"shield\" : 2,\r\n        \"defence\" : 6,\r\n        \"ability\" : \"noAbility\"\r\n\r\n    },\r\n    \"horseArcher\": {\r\n        \"type\" : \"cavalry\",\r\n        \"attackType\" : \"ranged\",\r\n        \"numTroops\" : 8,\r\n        \"cost\" : 5,\r\n        \"trainTime\" : 2,\r\n        \"attack\" : 20,\r\n        \"speed\" : 7,\r\n        \"morale\" : 4,\r\n        \"armour\" : 2,\r\n        \"shield\" : 2,\r\n        \"charge\" : 5,\r\n        \"ability\" : \"noAbility\"\r\n    },\r\n    \"hoplite\": {\r\n        \"type\" : \"infantry\",\r\n        \"attackType\" : \"melee\",\r\n        \"numTroops\" : 8,\r\n        \"cost\" : 5,\r\n        \"trainTime\" : 2,\r\n        \"attack\" : 20,\r\n        \"speed\" : 2,\r\n        \"morale\" : 4,\r\n        \"armour\" : 2,\r\n        \"shield\" : 4,\r\n        \"defence\" : 6,\r\n        \"ability\" : \"phalanx\"\r\n    }\r\n}");
    static JSONObject abilityJSON = new JSONObject("{\r\n    \"noAbility\" : [\r\n    ],\r\n    \"phalanx\" : [\r\n        {\r\n            \"type\" : \"defence\",\r\n            \"value\" : 2,\r\n            \"strategy\" : \"multiply\",\r\n            \"who\" : \"friendly\"\r\n            \r\n        },\r\n        {\r\n            \"type\" : \"speed\",\r\n            \"value\" : 0.5,\r\n            \"strategy\" : \"multiply\",\r\n            \"who\" : \"friendly\"\r\n        }\r\n    ]\r\n}");


    @Test
    public void TestLoadsConfig() throws IOException {

        Unit u = new Unit("soldier", unitJSON, abilityJSON);
        assertEquals("infantry", u.getType());
        assertEquals(true, u.isMelee());
        assertEquals(false, u.isRanged());
        assertEquals(false, u.isTrained());
        assertEquals(10, u.getNumTroops());
        assertEquals(5, u.getCost());
        assertEquals(1, u.getTrainTime());
        assertEquals(15, u.getAttack());
        assertEquals(5, u.getMorale());
        assertEquals(2, u.getShield());
        assertEquals(6, u.getDefence());
        assertEquals(0, u.getCharge());
        assertEquals(4, u.getSpeed());
    }


    @Test
    public void TestSoldierTrains() throws IOException {

        Unit u = new Unit("soldier", unitJSON, abilityJSON);

        assertEquals(false, u.isTrained());
        u.endTurn();
        assertEquals(true, u.isTrained());
    }

    @Test
    public void TestHorseArcherTrains() throws IOException {

        Unit u = new Unit("horseArcher", unitJSON, abilityJSON);

        assertEquals(2, u.getTrainTime());
        assertEquals(false, u.isTrained());
        u.endTurn();
        assertEquals(false, u.isTrained());
        u.endTurn();
        assertEquals(true, u.isTrained());

        u.endTurn();
        assertEquals(true, u.isTrained());

    }

}

