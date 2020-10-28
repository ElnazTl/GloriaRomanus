package test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import unsw.gloriaromanus.Backend.*;

public class UnitTest {
    @Test
    public void loadsConfigTest() throws IOException {
        Unit u = new Unit("soldier");
        //assertEquals(10, u.getNumTroops());
    }

}

