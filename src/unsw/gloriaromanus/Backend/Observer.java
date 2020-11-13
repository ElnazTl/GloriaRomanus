package unsw.gloriaromanus.Backend;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface Observer {
    public void update(Province p) throws JsonParseException, JsonMappingException, IOException;
}
