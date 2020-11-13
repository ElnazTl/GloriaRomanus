package unsw.gloriaromanus.Backend;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
public interface FactionObserver {
    public void update(Faction faction) throws JsonParseException, JsonMappingException, IOException;
}
